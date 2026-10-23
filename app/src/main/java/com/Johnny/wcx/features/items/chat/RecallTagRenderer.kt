package com.Johnny.wcx.features.items.chat

import android.app.Activity
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.Johnny.wcx.preferences.WePrefs
import com.Johnny.wcx.utils.WeLogger
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import dev.ujhhgtg.reflekt.utils.toClass

/**
 * 防撤回「气泡右侧圆角标签」的渲染器（样式二）。
 *
 * ## 为什么不用 ChattingItemContainer
 * 8.0.79 的 release 包里 `com.tencent.mm.ui.chatting.viewitems.ChattingItemContainer` 只剩字符串
 * （混淆后的日志文本），**没有类定义**，按类名 hook 一定落空。这里改用两个都保留原名的锚点：
 * `com.tencent.mm.ui.chatting.ChattingUI`（有定义）+ RecyclerView 的可见子项。
 *
 * ## 匹配方式
 * 1. 优先从 item 的 tag 里反射读 `field_msgSvrId`（微信 storage 对象保留 field_ 前缀），
 *    这样文字、图片、语音、视频等**所有消息类型**都能匹配；
 * 2. 读不到再退化为遍历子 TextView 的文本比对（只覆盖文本消息）。
 *
 * ## 已知限制
 * 只在进入聊天页（ChattingUI.onResume）后渲染一次可见区域；上滑加载出来的历史消息不会再补标签。
 * 这是为了不常驻 RecyclerView 的滑动回调（那会明显影响滚动流畅度）。
 */
internal object RecallTagRenderer {

    private const val TAG = "RecallTagRenderer"

    /** 被拦下的撤回消息 msgSvrId 集合。与 AntiMessageRecall 共用。 */
    internal const val KEY_BLOCKED = "recall_blocked_msgs"

    private const val MAX_ENTRIES = 300

    private val LABEL_TAG = "wcx_recall_label".hashCode()

    private const val CHATTING_UI = "com.tencent.mm.ui.chatting.ChattingUI"

    // 与截图接近的柔和配色
    private const val BG_COLOR = 0xFFFDE8F0.toInt()
    private const val TEXT_COLOR = 0xFFB0455F.toInt()

    private var installed = false

    internal var labelText: String = "已撤回"

    internal fun blockedEntries(): Set<String> = WePrefs.getStringSetOrDef(KEY_BLOCKED, emptySet())

    internal fun rememberBlocked(msgSvrId: String) {
        if (msgSvrId.isBlank()) return
        runCatching {
            val entries = blockedEntries().toMutableSet()
            entries.add(msgSvrId)
            val trimmed = if (entries.size <= MAX_ENTRIES) entries
            else entries.toList().takeLast(MAX_ENTRIES).toSet()
            WePrefs.putStringSet(KEY_BLOCKED, trimmed)
        }.onFailure { WeLogger.w(TAG, "记录被撤回消息失败", it) }
    }

    internal fun install() {
        if (installed) return
        val clazz = runCatching { CHATTING_UI.toClass() }.getOrElse { ex ->
            WeLogger.w(TAG, "ChattingUI 未找到，圆角标签不可用（不影响防撤回）", ex)
            return
        }
        val onResume = runCatching { clazz.getDeclaredMethod("onResume") }.getOrElse { ex ->
            WeLogger.w(TAG, "ChattingUI.onResume 不存在，圆角标签不可用", ex)
            return
        }
        runCatching {
            XposedBridge.hookMethod(onResume, object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val activity = param.thisObject as? Activity ?: return
                    runCatching {
                        val decor = activity.window?.decorView ?: return@runCatching
                        // 等列表完成一次布局再扫，onResume 时 RecyclerView 往往还是空的。
                        decor.postDelayed({ runCatching { decorate(activity) } }, 300L)
                    }.onFailure { WeLogger.w(TAG, "渲染圆角标签失败", it) }
                }
            })
            installed = true
            WeLogger.i(TAG, "圆角标签渲染已安装")
        }.onFailure { WeLogger.w(TAG, "hook ChattingUI.onResume 失败", it) }
    }

    // ── 渲染 ────────────────────────────────────────────────────────────────

    private fun decorate(activity: Activity) {
        val list = bestRecyclerView(activity.window?.decorView)
        val ids = blockedEntries()
        WeLogger.i(
            TAG,
            "decorate: recycler=${list != null} children=${list?.childCount ?: -1} blockedIds=${ids.size}",
        )
        if (list == null || ids.isEmpty()) return

        for (index in 0 until list.childCount) {
            val item = list.getChildAt(index) as? ViewGroup ?: continue
            // 两条路取 msgSvrId：先看 item 子树的 tag，再退回 adapter.getItem(position)。
            val msgId = runCatching { findMsgSvrId(item) }.getOrNull()
                ?: runCatching { msgIdAtPosition(list, item) }.getOrNull()
            val hit = if (msgId != null) msgId in ids else false
            WeLogger.d(TAG, "item#$index msgSvrId=$msgId hit=$hit")
            val existing = item.findViewWithTag<View>(LABEL_TAG)

            if (!hit) {
                existing?.let { runCatching { item.removeView(it) } }
                continue
            }
            if (existing != null) continue

            val label = buildLabel(item.context) ?: continue
            runCatching { attach(item, label) }
                .onFailure { WeLogger.w(TAG, "addView 标签失败", it) }
        }
    }

    private fun matchesBlocked(item: ViewGroup, ids: Set<String>): Boolean {
        findMsgSvrId(item)?.let { return it in ids }
        // 退化：文本匹配（只有文本消息有效）
        val texts = mutableListOf<String>()
        collectTexts(item, texts, 0)
        return texts.any { it.trim() in ids }
    }

    /** 递归找 item 子树上 tag 里的 msgSvrId。 */
    private fun findMsgSvrId(view: View): String? {
        val tag = view.tag
        if (tag != null && tag != LABEL_TAG) {
            readLongField(tag, "field_msgSvrId")?.let { return it.toString() }
            readLongField(tag, "field_msgId")?.let { return it.toString() }
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                findMsgSvrId(view.getChildAt(i))?.let { return it }
            }
        }
        return null
    }

    /**
     * 兜底：通过 RecyclerView 的 adapter 取该位置的 item 对象，再读它的 msgSvrId。
     *
     * 微信的消息适配器是混淆类，但 `getItem(int)` 这种 public 方法名通常保留；取不到就返回 null，
     * 不影响上一条路径。
     */
    private fun msgIdAtPosition(list: RecyclerView, item: View): String? {
        val adapter = list.adapter ?: return null
        val position = list.getChildAdapterPosition(item)
        if (position < 0) return null
        val getter = adapter.javaClass.methods.firstOrNull {
            it.name == "getItem" && it.parameterCount == 1 &&
                it.parameterTypes[0] == Int::class.javaPrimitiveType
        } ?: return null
        val obj = runCatching { getter.invoke(adapter, position) }.getOrNull() ?: return null
        return (readLongField(obj, "field_msgSvrId") ?: readLongField(obj, "field_msgId"))?.toString()
    }

    private fun readLongField(obj: Any, fieldName: String): Long? {
        var clazz: Class<*>? = obj.javaClass
        while (clazz != null && clazz != Any::class.java) {
            try {
                val field = clazz.getDeclaredField(fieldName)
                field.isAccessible = true
                return (field.get(obj) as? Number)?.toLong()
            } catch (_: NoSuchFieldException) {
                // 继续往父类找
            } catch (_: Throwable) {
                return null
            }
            clazz = clazz.superclass
        }
        return null
    }

    private fun collectTexts(view: View, out: MutableList<String>, depth: Int) {
        if (depth > 6) return
        if (view is TextView && view.tag != LABEL_TAG) {
            view.text?.toString()?.let { out.add(it) }
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) collectTexts(view.getChildAt(i), out, depth + 1)
        }
    }

    private fun attach(parent: ViewGroup, label: TextView) {
        when (parent) {
            is LinearLayout -> {
                val lp = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                )
                lp.gravity = Gravity.CENTER_VERTICAL
                lp.marginStart = dp(parent.context, 4)
                parent.addView(label, lp)
            }
            is FrameLayout -> {
                val lp = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                )
                lp.gravity = Gravity.END or Gravity.BOTTOM
                lp.marginEnd = dp(parent.context, 6)
                parent.addView(label, lp)
            }
            else -> parent.addView(label)
        }
    }

    private fun buildLabel(context: Context?): TextView? {
        context ?: return null
        val text = labelText.ifBlank { "已撤回" }
        return TextView(context).apply {
            this.text = text
            textSize = 10f
            setTextColor(TEXT_COLOR)
            includeFontPadding = false
            setPadding(dp(context, 6), dp(context, 2), dp(context, 6), dp(context, 2))
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = dp(context, 8).toFloat()
                setColor(BG_COLOR)
            }
            tag = LABEL_TAG
        }
    }

    /** 聊天页里可能同时有消息列表、表情面板等多个 RecyclerView，取「子项最多」的那个。 */
    private fun bestRecyclerView(view: View?): RecyclerView? {
        val all = mutableListOf<RecyclerView>()
        collectRecyclerViews(view, all, 0)
        if (all.isEmpty()) return null
        return all.maxByOrNull { it.childCount }
    }

    private fun collectRecyclerViews(view: View?, out: MutableList<RecyclerView>, depth: Int) {
        if (view == null || depth > 12) return
        if (view is RecyclerView) out.add(view)
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) collectRecyclerViews(view.getChildAt(i), out, depth + 1)
        }
    }

    private fun dp(context: Context, value: Int): Int =
        (value * context.resources.displayMetrics.density).toInt()
}
