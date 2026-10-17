package com.Johnny.wcx.utils.crash

import android.app.ActivityManager
import android.app.ApplicationExitInfo
import android.content.Context
import android.os.Build
import com.Johnny.wcx.utils.WeLogger
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 记录「上一次进程是怎么没的」。
 *
 * 用来区分：系统内存不足回收 / Java 或原生崩溃 / ANR / 用户或厂商强行停止。
 * 结论落到 SharedPreferences，并在「模块自检」页展示。
 *
 * 说明：Android 11(API 30) 起 [ActivityManager.getHistoricalProcessExitReasons] 是公开 API，
 * 可以直接调用（只能查本包）。低版本系统没有该能力，静默跳过，不要刷日志。
 */
object ExitReasonRecorder {

    private const val TAG = "ExitReasonRecorder"

    private const val PREFS = "wcx_exit_reason"
    private const val KEY_TIME = "time"
    private const val KEY_REASON = "reason"
    private const val KEY_DESC = "desc"
    private const val KEY_PROCESS = "process"
    private const val KEY_IMPORTANCE = "importance"
    private const val KEY_STATUS = "status"

    /** [ApplicationExitInfo] 的 reason 常量（API 30 起固定）。 */
    private val REASON_NAMES = mapOf(
        ApplicationExitInfo.REASON_UNKNOWN to "UNKNOWN 未知原因",
        ApplicationExitInfo.REASON_EXIT_SELF to "EXIT_SELF 进程自己退出",
        ApplicationExitInfo.REASON_SIGNALED to "SIGNALED 被系统信号杀死",
        ApplicationExitInfo.REASON_LOW_MEMORY to "LOW_MEMORY 内存不足被系统回收",
        ApplicationExitInfo.REASON_CRASH to "CRASH Java 崩溃",
        ApplicationExitInfo.REASON_CRASH_NATIVE to "CRASH_NATIVE 原生崩溃",
        ApplicationExitInfo.REASON_ANR to "ANR 无响应被系统结束",
        ApplicationExitInfo.REASON_INITIALIZATION_FAILURE to "INITIALIZATION_FAILURE 初始化失败",
        ApplicationExitInfo.REASON_PERMISSION_CHANGE to "PERMISSION_CHANGE 权限变更",
        ApplicationExitInfo.REASON_EXCESSIVE_RESOURCE_USAGE to "EXCESSIVE_RESOURCE_USAGE 资源占用过高被系统结束",
        ApplicationExitInfo.REASON_USER_REQUESTED to "USER_REQUESTED 被强行停止",
        ApplicationExitInfo.REASON_USER_STOPPED to "USER_STOPPED 被用户停止",
        ApplicationExitInfo.REASON_DEPENDENCY_DIED to "DEPENDENCY_DIED 依赖进程死亡",
        ApplicationExitInfo.REASON_OTHER to "OTHER 其他原因",
        ApplicationExitInfo.REASON_FREEZER to "FREEZER 被系统冻结",
        ApplicationExitInfo.REASON_PACKAGE_STATE_CHANGE to "PACKAGE_STATE_CHANGE 包状态变化",
        ApplicationExitInfo.REASON_PACKAGE_UPDATED to "PACKAGE_UPDATED 应用被更新",
    )

    private val timeFormat = SimpleDateFormat("MM-dd HH:mm:ss", Locale.getDefault())

    /** 同一进程只读一次，避免多进程引导时反复写盘、互相覆盖。 */
    private val recorded = java.util.concurrent.atomic.AtomicBoolean(false)

    /** 模块初始化早期调用：把「上一次退出」的原因落盘，供诊断页展示。 */
    fun recordOnStartup(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return
        if (!recorded.compareAndSet(false, true)) return
        runCatching {
            val am = context.getSystemService(ActivityManager::class.java) ?: return
            val list = am.getHistoricalProcessExitReasons(context.packageName, 0, 8)
            if (list.isNullOrEmpty()) return

            val selfPackage = context.packageName
            // 优先取「本人进程名 == 包名」的那条（微信主进程），其余进程（:push 等）作次选。
            var picked: ApplicationExitInfo? = null
            var pickedTime = 0L
            for (info in list) {
                if (info == null) continue
                val name = info.processName.orEmpty()
                if (name.isNotEmpty() && !name.startsWith(selfPackage)) continue
                // 主进程（无冒号后缀）优先；同为主进程时取最近一次。
                val isMainProcess = name == selfPackage
                val isPickedMain = picked?.processName == selfPackage
                val better = when {
                    picked == null -> true
                    isMainProcess && !isPickedMain -> true
                    isMainProcess == isPickedMain -> info.timestamp > pickedTime
                    else -> false
                }
                if (better) {
                    picked = info
                    pickedTime = info.timestamp
                }
            }
            val info = picked ?: return

            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                .putLong(KEY_TIME, info.timestamp)
                .putInt(KEY_REASON, info.reason)
                .putString(KEY_DESC, info.description.orEmpty().take(400))
                .putString(KEY_PROCESS, info.processName.orEmpty())
                .putInt(KEY_IMPORTANCE, info.importance)
                .putInt(KEY_STATUS, info.status)
                .apply()
            WeLogger.i(
                TAG,
                "last exit: reason=" + info.reason + "(" + reasonName(info.reason) + ")" +
                    " importance=" + info.importance +
                    " process=" + info.processName +
                    " ts=" + info.timestamp
            )
        }.onFailure {
            // 仅一行，不带堆栈：这个探针失败不影响任何功能，不该污染用户日志。
            WeLogger.w(TAG, "read exit reason unavailable: " + (it.message ?: it.javaClass.simpleName))
        }
    }

    /** reason 码 → 简短中文名。 */
    fun reasonName(reason: Int): String = REASON_NAMES[reason] ?: "reason=$reason"

    /** 供诊断页展示的一行摘要；从未记录过时返回 null。 */
    fun summary(context: Context): String? {
        val data = read(context) ?: return null
        return buildString {
            append(data.reasonName)
            append("｜").append(timeFormat.format(Date(data.time)))
            if (data.process.isNotEmpty() && data.process != context.packageName) {
                append("｜").append(data.process.substringAfterLast(':'))
            }
            if (data.desc.isNotEmpty()) {
                append("｜").append(data.desc.replace('\n', ' ').take(80))
            }
        }
    }

    /**
     * 根据退出原因给出「该由谁负责、下一步查什么」的结论。
     * 供诊断页直接展示，避免用户看到一堆英文常量还得自己猜。
     */
    fun diagnosis(context: Context): String? {
        val data = read(context) ?: return null
        return when (data.reason) {
            ApplicationExitInfo.REASON_LOW_MEMORY ->
                "结论：微信是被系统按内存不足回收的（不是崩溃、也不是模块主动杀进程）。\n" +
                    "方向：模块侧的常驻内存（dex 解析、Hook、脚本运行时）会让微信更容易被选中回收。"
            ApplicationExitInfo.REASON_CRASH ->
                "结论：微信发生了 Java 崩溃。可到「日志 → 崩溃日志」查看具体堆栈。"
            ApplicationExitInfo.REASON_CRASH_NATIVE ->
                "结论：微信发生了原生层崩溃（native/SIGSEGV）。常见于 dex 解析库、脚本引擎等 native 组件。" +
                    "若稳定复现，需要定位到具体 .so。"
            ApplicationExitInfo.REASON_ANR ->
                "结论：微信主线程阻塞被系统判为无响应后结束。方向：检查主线程上是否有耗时操作。"
            ApplicationExitInfo.REASON_USER_REQUESTED, ApplicationExitInfo.REASON_USER_STOPPED ->
                "结论：进程是被「强行停止 / 停止」结束的，这类操作只能由用户或系统省电策略发起，" +
                    "第三方模块在技术上无法强停其它进程。\n" +
                    "方向：检查系统「应用省电策略 / 后台运行限制 / 异常耗电自动管理」是否对微信做了限制。"
            ApplicationExitInfo.REASON_SIGNALED ->
                "结论：进程被系统信号杀死（kill -9 一类），通常来自系统低内存回收或厂商后台清理。"
            ApplicationExitInfo.REASON_EXCESSIVE_RESOURCE_USAGE ->
                "结论：系统判定该应用资源占用过高而结束它。方向：降低后台 CPU / 唤醒频率。"
            ApplicationExitInfo.REASON_FREEZER ->
                "结论：进程被系统冻结（后台冻结机制），随后被回收。属于系统省电行为。"
            ApplicationExitInfo.REASON_EXIT_SELF ->
                "结论：进程自己调用了 exit 退出。若配合崩溃日志为空，需要排查模块内是否有主动退出路径。"
            ApplicationExitInfo.REASON_PACKAGE_UPDATED ->
                "结论：微信被更新/覆盖安装，进程随包变化结束，属正常现象。"
            else -> "结论：原因码 " + data.reason + "，暂无对应建议。"
        }
    }

    private data class ExitData(
        val time: Long,
        val reason: Int,
        val reasonName: String,
        val desc: String,
        val process: String,
    )

    private fun read(context: Context): ExitData? {
        return runCatching {
            val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            val time = prefs.getLong(KEY_TIME, 0L)
            if (time <= 0L) return null
            val reason = prefs.getInt(KEY_REASON, -1)
            ExitData(
                time = time,
                reason = reason,
                reasonName = reasonName(reason),
                desc = prefs.getString(KEY_DESC, "").orEmpty(),
                process = prefs.getString(KEY_PROCESS, "").orEmpty(),
            )
        }.getOrNull()
    }
}
