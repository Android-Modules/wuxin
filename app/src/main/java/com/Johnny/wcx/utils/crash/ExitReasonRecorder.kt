package com.Johnny.wcx.utils.crash

import android.app.ActivityManager
import android.content.Context
import com.Johnny.wcx.utils.WeLogger

/**
 * 记录「上一次进程是怎么没的」。
 *
 * 用来区分：系统内存不足回收 / Java 或原生崩溃 / ANR / 用户手动强行停止 / 应用更新…
 * 结论会落到 SharedPreferences，并在「模块自检」页展示一行摘要。
 *
 * 说明：`ApplicationExitInfo` 自 Android 11(API 30) 起提供，
 * `ActivityManager.getHistoricalProcessExitInfos()` 未在公开 SDK 导出，必须反射调用。
 */
object ExitReasonRecorder {

    private const val TAG = "ExitReasonRecorder"

    private const val PREFS = "wcx_exit_reason"
    private const val KEY_TIME = "time"
    private const val KEY_REASON = "reason"
    private const val KEY_DESC = "desc"
    private const val KEY_PROCESS = "process"

    private val REASON_NAMES = mapOf(
        1 to "UNKNOWN 未知",
        2 to "EXIT_SELF 主动退出",
        3 to "SIGNALED 被信号杀死",
        4 to "LOW_MEMORY 系统内存不足回收",
        5 to "CRASH Java 崩溃",
        6 to "CRASH_NATIVE 原生崩溃",
        7 to "ANR 无响应被系统结束",
        8 to "INITIALIZATION_FAILURE 初始化失败",
        9 to "PERMISSION_CHANGE 权限变更",
        10 to "EXCESSIVE_RESOURCE_USAGE 资源占用过高被系统结束",
        11 to "USER_REQUESTED 用户手动强行停止",
        12 to "USER_STOPPED 用户停止",
        13 to "DEPENDENCY_DIED 依赖进程死亡",
        14 to "OTHER 其他",
        15 to "FREEZER 被冻结",
        16 to "PACKAGE_STATE_CHANGE 包状态变化",
        17 to "PACKAGE_UPDATED 应用更新",
    )

    /** 模块初始化早期调用：把「上一次退出」的原因落盘，供诊断页展示。 */
    fun recordOnStartup(context: Context) {
        runCatching {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager ?: return
            val method = am.javaClass.getMethod("getHistoricalProcessExitInfos")
            val list = method.invoke(am) as? List<*> ?: return
            if (list.isEmpty()) return

            val selfPackage = context.packageName
            var picked: Any? = null
            var pickedTime = 0L
            for (item in list) {
                if (item == null) continue
                val cls = item.javaClass
                val processName = runCatching {
                    cls.getMethod("getProcessName").invoke(item) as? String
                }.getOrNull()
                if (processName != null && !processName.startsWith(selfPackage)) continue
                val ts = runCatching {
                    cls.getMethod("getTimestamp").invoke(item) as? Long ?: 0L
                }.getOrDefault(0L)
                if (ts > pickedTime) {
                    pickedTime = ts
                    picked = item
                }
            }
            val info = picked ?: return
            val cls = info.javaClass
            val reason = runCatching { cls.getMethod("getReason").invoke(info) as? Int ?: -1 }
                .getOrDefault(-1)
            val desc = runCatching { cls.getMethod("getDescription").invoke(info) as? String }
                .getOrNull().orEmpty()
            val processName = runCatching { cls.getMethod("getProcessName").invoke(info) as? String }
                .getOrNull().orEmpty()

            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                .putLong(KEY_TIME, pickedTime)
                .putInt(KEY_REASON, reason)
                .putString(KEY_DESC, desc)
                .putString(KEY_PROCESS, processName)
                .apply()
            WeLogger.i(TAG, "last exit: reason=$reason desc=$desc process=$processName ts=$pickedTime")
        }.onFailure { WeLogger.w(TAG, "record last exit reason failed", it) }
    }

    /** 供诊断页展示的一行摘要；从未记录过时返回 null。 */
    fun summary(context: Context): String? {
        return runCatching {
            val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            val time = prefs.getLong(KEY_TIME, 0L)
            if (time <= 0L) return null
            val reason = prefs.getInt(KEY_REASON, -1)
            val desc = prefs.getString(KEY_DESC, "").orEmpty()
            val process = prefs.getString(KEY_PROCESS, "").orEmpty()
            val fmt = java.text.SimpleDateFormat("MM-dd HH:mm", java.util.Locale.getDefault())
            val name = REASON_NAMES[reason] ?: "reason=$reason"
            buildString {
                append(name)
                append("｜").append(fmt.format(java.util.Date(time)))
                if (process.isNotEmpty()) append("｜").append(process.substringAfterLast(':'))
                if (desc.isNotEmpty()) append("｜").append(desc.take(60))
            }
        }.getOrNull()
    }
}
