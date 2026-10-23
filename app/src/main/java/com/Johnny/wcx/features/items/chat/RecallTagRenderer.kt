package com.Johnny.wcx.features.items.chat

import android.app.Activity
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
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

    /** 挂在 label 上的第二枚 tag，用于取回 OnPreDrawListener 以便移除。 */
    private val LISTENER_TAG = "wcx_recall_label_listener".hashCode()

    /** 被撤回消息的原始透明度（与上游一致：变暗表示"已删除"）。 */
    private const val RECALL_ALPHA = 0.4f

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
                        // 首屏加载有快有慢，分几档多扫几次，避免只扫到空列表就再不补标签。
                        for (delay in longArrayOf(300L, 1200L, 2500L)) {
                            decor.postDelayed({
                                runCatching { decorate(activity) }
                                    .onFailure { WeLogger.w(TAG, "渲染圆角标签失败", it) }
                            }, delay)
                        }
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
                existing?.let {
                    removeListener(item, it)
                    runCatching { item.removeView(it) }
                }
                // 视图被回收复用后原来的变暗要还原，否则正常消息会一直是半透明的。
                runCatching { restoreAlpha(item, 0) }
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

    /**
     * 把标签挂到消息行上，并用绝对定位放到「气泡右侧」。
     *
     * ## 为什么不再用容器自带的 LayoutParams
     * 微信消息行的根节点是水平 LinearLayout，之前把标签当行内子项加进去、靠 gravity 摆位，
     * 结果标签被布局挤到屏幕外（真机上就是「气泡右侧什么都没有」）。这里改成：
     * 先按容器默认 LayoutParams 加入（addView 单参走 generateDefaultLayoutParams，任何容器都安全），
     * 再在每帧绘制前按「气泡视图」的真实屏幕坐标算 translationX/Y —— 相当于浮在行上，不参与布局。
     */
    private fun attach(parent: ViewGroup, label: TextView) {
        val content = findMessageContent(parent)
        parent.addView(label)
        if (content == null) {
            WeLogger.d(TAG, "attach: 未找到气泡视图，标签不可见")
            return
        }
        // 变暗表示这条消息已被撤回（标签浮在变暗层之外，自己也要设 alpha）。
        runCatching { content.alpha = RECALL_ALPHA }
        label.alpha = 1f

        val rowLoc = IntArray(2)
        val contentLoc = IntArray(2)
        val listener = ViewTreeObserver.OnPreDrawListener {
            if (!label.isAttachedToWindow || !parent.isAttachedToWindow) return@OnPreDrawListener true
            if (parent.width == 0 || label.measuredWidth == 0) return@OnPreDrawListener true
            parent.getLocationOnScreen(rowLoc)
            content.getLocationOnScreen(contentLoc)
            val contentLeft = (contentLoc[0] - rowLoc[0]).toFloat()
            val contentRight = contentLeft + content.width
            val contentBottom = (contentLoc[1] - rowLoc[1] + content.height).toFloat()
            val gap = dp(parent.context, 4).toFloat()
            val maxX = (parent.width - label.measuredWidth).toFloat()
            val targetX = (contentRight + gap).coerceIn(0f, maxOf(0f, maxX))
            val maxY = (parent.height - label.measuredHeight).toFloat()
            val targetY = (contentBottom - label.measuredHeight).coerceIn(0f, maxOf(0f, maxY))
            if (label.translationX != targetX) label.translationX = targetX
            if (label.translationY != targetY) label.translationY = targetY
            true
        }
        runCatching {
            parent.viewTreeObserver.addOnPreDrawListener(listener)
            label.setTag(LISTENER_TAG, listener)
        }.onFailure { WeLogger.w(TAG, "addOnPreDrawListener 失败", it) }
    }

    /** 把子树里被压暗的视图还原成不透明（只动 alpha < 1 的，避免误伤微信自己的动画）。 */
    private fun restoreAlpha(view: View, depth: Int) {
        if (depth > 8) return
        if (view.tag != LABEL_TAG && view.alpha < 1f) view.alpha = 1f
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) restoreAlpha(view.getChildAt(i), depth + 1)
        }
    }

    private fun removeListener(parent: ViewGroup, label: View) {
        runCatching {
            val listener = label.getTag(LISTENER_TAG) as? ViewTreeObserver.OnPreDrawListener
                ?: return@runCatching
            parent.viewTreeObserver.removeOnPreDrawListener(listener)
        }
    }

    /**
     * 找消息行里的「气泡/内容」视图：优先用 holder 的 getMainContainerView()（微信 holder 基类方法，
     * 各消息类型都覆写成自己的气泡），失败再退化为「tag 类名以 viewitems 开头的可见视图」。
     */
    private fun findMessageContent(row: View): View? {
        val holder = row.tag
        if (holder != null) {
            val getter = runCatching {
                holder.javaClass.methods.firstOrNull {
                    it.name == "getMainContainerView" && it.parameterCount == 0
                }
            }.getOrNull()
            if (getter != null) {
                runCatching { getter.invoke(holder) as? View }
                    .getOrNull()
                    ?.takeIf { it.width > 0 }
                    ?.let { return it }
            }
        }
        val found = mutableListOf<View>()
        collectTaggedBubble(row, found, 0)
        return found.firstOrNull { it.width > 0 } ?: found.firstOrNull()
    }

    private fun collectTaggedBubble(view: View, out: MutableList<View>, depth: Int) {
        if (depth > 8) return
        val tagName = view.tag?.javaClass?.name
        if (tagName != null && tagName.startsWith("com.tencent.mm.ui.chatting.viewitems")) {
            out.add(view)
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) collectTaggedBubble(view.getChildAt(i), out, depth + 1)
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
