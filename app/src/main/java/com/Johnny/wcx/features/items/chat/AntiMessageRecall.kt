package com.Johnny.wcx.features.items.chat

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.core.view.isVisible
import de.robv.android.xposed.XC_MethodHook
import dev.ujhhgtg.reflekt.reflekt
import com.Johnny.wcx.dexkit.abc.IResolveDex
import com.Johnny.wcx.dexkit.dsl.dexConstructor
import com.Johnny.wcx.dexkit.dsl.dexMethod
import com.Johnny.wcx.features.api.core.WeDatabaseApi
import com.Johnny.wcx.features.api.core.WeMessageApi
import com.Johnny.wcx.features.api.core.WeXmlParserApi
import com.Johnny.wcx.features.api.core.models.MessageInfo
import com.Johnny.wcx.features.api.core.models.MessageType
import com.Johnny.wcx.features.api.ui.WeChatMessageViewApi
import com.Johnny.wcx.features.core.ClickableFeature
import com.Johnny.wcx.features.core.Feature
import com.Johnny.wcx.preferences.WePrefs
import com.Johnny.wcx.preferences.WePrefs.Companion.prefOption
import com.Johnny.wcx.ui.content.AlertDialogContent
import com.Johnny.wcx.ui.content.TextButton
import com.Johnny.wcx.ui.content.m3.BaseWidget
import com.Johnny.wcx.ui.content.m3.DropDownMenuWidget
import com.Johnny.wcx.ui.content.m3.DropdownOption
import com.Johnny.wcx.ui.content.m3.SegmentedColumn
import com.Johnny.wcx.ui.content.m3.SwitchWidget
import com.Johnny.wcx.ui.utils.VectorPathDrawable
import com.Johnny.wcx.ui.utils.dpToPx
import com.Johnny.wcx.ui.utils.findViewWhich
import com.Johnny.wcx.ui.utils.showComposeDialog
import com.Johnny.wcx.utils.WeLogger
import com.Johnny.wcx.utils.android.isDarkMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Collections
import java.util.WeakHashMap

@Feature(name = "防撤回", categories = ["聊天"], description = "阻止撤回消息")
object AntiMessageRecall : ClickableFeature(), IResolveDex, WeXmlParserApi.IAfterParseListener,
    WeChatMessageViewApi.ICreateViewListener {

    private const val TAG = "AntiMessageRecall"

    private var recallOutgoing by prefOption("recall_outgoing", false)
    private var badgeType by prefOption("recall_badge_type", BADGE_TYPE_ICON)
    // WeChat only marks a message as revoked AFTER overwriting its row in place (destroying the
    // content). Our blocked recalls leave the row intact and unmarked, so the recalled list must be
    // tracked here, persisted as "<talker>\u001F<msgSvrId>" entries.
    private var recalledRecords by prefOption("recall_records", emptySet())

    // In-memory mirror of [recalledRecords] for the per-bind hot path.
    @Volatile
    private var recalledKeyCache: Set<String> = emptySet()

    // Content views currently dimmed by us, keyed by their bound row, so a rebind restores the
    // exact view we changed instead of clobbering other features' animations (e.g.
    // MessageEntranceAnimation).
    private val dimmedContentViews =
        Collections.synchronizedMap(WeakHashMap<View, View>())
    private val badges = Collections.synchronizedMap(WeakHashMap<View, RecallBadge>())

    // Scenes currently recalling a self-sent message, keyed by the NetSceneRevokeMsg instance, so
    // the scene-end hook can tell which message row the host is about to destroy.
    private val pendingRecallMsgs = Collections.synchronizedMap(WeakHashMap<Any, MessageInfo>())

    // Unique to NetSceneRevokeMsg's notice converter across the supported host range; anchors the
    // scene class without hard-coding its obfuscated name.
    private const val REVOKE_INVOKE_MESSAGE_XML =
        "<sysmsg type=\"invokeMessage\"><invokeMessage><text><![CDATA[%s]]></text><timestamp><![CDATA[%s]]></timestamp><link><text><![CDATA[%s]]></text></link><preContent><![CDATA[%s]]></preContent><type><![CDATA[%s]]></type><msgSource><![CDATA[%s]]></msgSource></invokeMessage></sysmsg>"

    private const val TYPE_KEY = "\$.sysmsg.\$type"
    private const val RECORD_SEPARATOR = "\u001F"
    private const val RECALL_ALPHA = 0.4f
    private const val BADGE_SIZE_DP = 28
    private const val BADGE_PADDING_DP = 6
    private const val BADGE_GAP_DP = 4

    private const val BADGE_TYPE_ICON = "icon"
    private const val BADGE_TYPE_TEXT = "text"

    // Badge label; upstream reads it from a string resource.
    private const val BADGE_TEXT = "已撤回"

    // Material "delete" glyph on the same 960-unit viewport as the other vector icons.
    private const val DELETE_ICON_PATH =
        "M280,840 Q247,840 223.5,816.5 T200,760 V240 H160 V160 H360 V120 H600 V160 H800 V240 H760 V760 Q760,793 736.5,816.5 T680,840 H280 Z M680,240 H280 V760 H680 Z M360,680 H440 V320 H360 Z M520,680 H600 V320 H520 Z"

    private val badgeIconLight = "#C62828".toColorInt()
    private val badgeCircleLight = "#FFCDD2".toColorInt()
    private val badgeIconDark = "#EF9A9A".toColorInt()
    private val badgeCircleDark = "#3E1C1C".toColorInt()

    private class RecallBadge(
        val view: View,
        val isSelfSender: Boolean,
        var anchor: View,
        val preDrawListener: ViewTreeObserver.OnPreDrawListener,
        val rowLocation: IntArray = IntArray(2),
        val anchorLocation: IntArray = IntArray(2),
    )

    private fun encodeKey(talker: String, serverId: Long) = "$talker$RECORD_SEPARATOR$serverId"

    // NetSceneRevokeMsg (scene 594, /cgi-bin/micromsg-bin/revokemsg): recalling a message sent from
    // this device never parses a revoke sysmsg — the scene rewrites the message row in place at
    // scene end (notice converter, isSend reset, row update), so the XML parser hook never sees it.
    // The constructor receives the target message; capture it for the scene-end hook.
    private val methodRevokeMsgSceneInit by dexConstructor {
        matcher {
            declaredClass {
                usingEqStrings(REVOKE_INVOKE_MESSAGE_XML)
            }
            paramCount = 3
        }
    }

    private val methodRevokeMsgSceneEnd by dexMethod {
        matcher {
            declaredClass {
                usingEqStrings(REVOKE_INVOKE_MESSAGE_XML)
            }
            name = "onGYNetEnd"
            paramCount = 6
        }
    }

    override fun onEnable() {
        recalledKeyCache = recalledRecords
        WeXmlParserApi.addListener(this)
        WeChatMessageViewApi.addListener(this)

        methodRevokeMsgSceneInit.hookBefore {
            val captured = MessageInfo(args[0]!!)
            pendingRecallMsgs[thisObject!!] = captured
        }

        methodRevokeMsgSceneEnd.hookBefore {
            if (!recallOutgoing) {
                WeLogger.i(TAG, "revoke scene end skipped: recall outgoing disabled")
                return@hookBefore
            }
            val msgInfo = pendingRecallMsgs.remove(thisObject!!)
            if (msgInfo == null) {
                WeLogger.i(TAG, "revoke scene end skipped: no captured message")
                return@hookBefore
            }
            // Recall rejected (e.g. the 2-minute window expired): keep WeChat's own failure path,
            // the message row was never rewritten anyway.
            val errType = args[1] as Int
            val errCode = args[2] as Int
            if (errType != 0 || errCode != 0) {
                WeLogger.i(TAG, "revoke scene end skipped: scene failed")
                return@hookBefore
            }
            if (!msgInfo.isSelfSender) {
                WeLogger.i(TAG, "revoke scene end skipped: not self sender")
                return@hookBefore
            }

            // Resolve the scene queue wrapper first: if that fails we bail out without recording,
            // so the original body keeps running and the recall just behaves unblocked.
            val sceneEndField = thisObject!!.reflekt().firstField {
                type { it.isInterface && it.declaredMethods.any { m -> m.name == "onSceneEnd" } }
            }
            val sceneWrapper = sceneEndField.get(thisObject!!)!!
            @Suppress("UNCHECKED_CAST")
            val sceneWrapperType = sceneEndField.self.type as Class<Any>
            val sceneEnd = sceneWrapperType.reflekt()
                .firstMethod { name = "onSceneEnd"; parameterCount = 4 }

            // Replicate the trailing queue completion of the skipped body so the NetScene queue and
            // the chat UI (progress dialog dismissal, refresh) finish the scene normally.
            sceneEnd.invoke(sceneWrapper, errType, errCode, args[3], thisObject)

            val key = encodeKey(msgInfo.talker, msgInfo.serverId)
            recalledKeyCache = recalledKeyCache + key
            recalledRecords = recalledKeyCache
            WeLogger.i(TAG, "kept recalled self message: $key")
            refreshBoundViews(msgInfo)
            result = null
        }
    }

    override fun onDisable() {
        WeXmlParserApi.removeListener(this)
        WeChatMessageViewApi.removeListener(this)
    }

    override fun onParse(param: XC_MethodHook.MethodHookParam, result: MutableMap<String, Any?>) {
        val args = param.args
        val xmlContent = args[0] as? String ?: ""
        val rootTag = args[1] as? String ?: ""

        if (rootTag != "sysmsg" || !xmlContent.contains("revokemsg")) {
            return
        }

        if (result[TYPE_KEY] == "revokemsg") {
            val cursor = WeDatabaseApi.rawQuery(
                "SELECT type,content,talker,createTime,lvbuffer,msgId,msgSvrId,isSend FROM message WHERE msgSvrId = ?",
                arrayOf(result[".sysmsg.revokemsg.newmsgid"] as? String? ?: return)
            )

            cursor.use { cursor ->
                if (cursor.moveToFirst()) {
                    val msgInfo = MessageInfo(WeMessageApi.convertMsgInfoInstanceFromCursor(cursor))

                    if (msgInfo.isSelfSender && !recallOutgoing) {
                        WeLogger.i(TAG, "sender is self and not recall outgoing, skipping")
                        return
                    }

                    result[TYPE_KEY] = null

                    val key = encodeKey(msgInfo.talker, msgInfo.serverId)
                    recalledKeyCache = recalledKeyCache + key
                    recalledRecords = recalledKeyCache

                    WeLogger.i(TAG, "recorded message revoke: $key")
                    refreshBoundViews(msgInfo)
                }
            }
        }
    }

    // The recall usually arrives while the message row is on screen; restyle it immediately
    // instead of waiting for the next rebind.
    private fun refreshBoundViews(msgInfo: MessageInfo) {
        WeChatMessageViewApi.findBoundViews {
            it.talker == msgInfo.talker && it.serverId == msgInfo.serverId
        }.forEach { (view, boundMsgInfo) ->
            // onParse may run off the main thread; re-check the binding inside the posted block so
            // a view recycled in between is never styled for the wrong message.
            view.post {
                val current = WeChatMessageViewApi.getBoundMessage(view) ?: return@post
                if (current.instance === boundMsgInfo.instance) {
                    applyRecallStyle(view, current)
                }
            }
        }
    }

    override fun onCreateView(param: XC_MethodHook.MethodHookParam, view: View) {
        val msgInfo = WeChatMessageViewApi.getMsgInfoFromParam(param)
        clearRecallStyle(view)
        if (isRecalled(msgInfo)) {
            applyRecallStyle(view, msgInfo)
        }
    }

    private fun isRecalled(msgInfo: MessageInfo): Boolean =
        encodeKey(msgInfo.talker, msgInfo.serverId) in recalledKeyCache

    // ── recalled-message styling ──────────────────────────────────────────────

    private fun applyRecallStyle(view: View, msgInfo: MessageInfo) {
        val contentView = findMessageContent(view) ?: return
        val previous = dimmedContentViews.put(view, contentView)
        if (previous != null && previous !== contentView) previous.alpha = 1f
        contentView.alpha = RECALL_ALPHA

        attachRecallBadge(view, msgInfo.isSelfSender, contentView)
    }

    private fun clearRecallStyle(view: View) {
        dimmedContentViews.remove(view)?.alpha = 1f
        removeRecallBadge(view)
    }

    // Every classic chat holder in the supported host range inherits getMainContainerView(), and
    // most holders override it with the semantic bubble/card container. Some Mvvm holders leave
    // the inherited clickArea null and attach their ItemDataTag to the dynamically-created content
    // view instead, so fall back to the first visible tagged view across the whole row. Searching
    // the whole row avoids depending on message-specific LinearLayout ordering while visibility
    // excludes hidden send-status views that can carry the same tag.
    private fun findMessageContent(view: View): View? {
        val holder = view.tag!!
        val mainContainer = holder.reflekt()
            .firstMethod {
                name = "getMainContainerView"
                parameterCount = 0
                superclass()
            }
            .invoke() as? View
        return mainContainer ?: view.findViewWhich {
            it.isVisible && isTaggedBubbleView(it)
        }
    }

    private fun isTaggedBubbleView(view: View): Boolean =
        view.tag?.javaClass?.name?.startsWith("com.tencent.mm.ui.chatting.viewitems") == true

    private fun attachRecallBadge(row: View, isSelfSender: Boolean, anchor: View) {
        if (badges.containsKey(row)) return

        val rowGroup = row as? ViewGroup ?: return
        val context = row.context
        val dark = context.isDarkMode
        val iconColor = if (dark) badgeIconDark else badgeIconLight
        val circleColor = if (dark) badgeCircleDark else badgeCircleLight
        val badgeView: View = if (badgeType == BADGE_TYPE_TEXT) {
            TextView(context).apply {
                text = BADGE_TEXT
                textSize = 12f
                setTextColor(iconColor)
                includeFontPadding = false
                val hPad = BADGE_PADDING_DP.dpToPx(context)
                val vPad = (BADGE_PADDING_DP / 2).dpToPx(context)
                setPadding(hPad, vPad, hPad, vPad)
                background = GradientDrawable().apply {
                    cornerRadius = (BADGE_SIZE_DP / 2).dpToPx(context).toFloat()
                    setColor(circleColor)
                }
            }
        } else {
            RecallBadgeIconView(context, iconColor, circleColor)
        }
        // Not a child of the dimmed content row, so it must dim itself.
        badgeView.alpha = RECALL_ALPHA
        val size = BADGE_SIZE_DP.dpToPx(context)
        val badgeWidth =
            if (badgeType == BADGE_TYPE_TEXT) ViewGroup.LayoutParams.WRAP_CONTENT else size
        val badgeHeight =
            if (badgeType == BADGE_TYPE_TEXT) ViewGroup.LayoutParams.WRAP_CONTENT else size
        // The chat row root is a RelativeLayout on the supported host versions; fall back to the
        // parent's own LayoutParams type elsewhere, since a mismatched type crashes on measure.
        rowGroup.addView(badgeView, badgeLayoutParams(rowGroup, badgeWidth, badgeHeight))

        val preDrawListener = ViewTreeObserver.OnPreDrawListener {
            badges[row]?.let { positionRecallBadge(row, it) }
            true
        }
        badges[row] = RecallBadge(badgeView, isSelfSender, anchor, preDrawListener)
        row.viewTreeObserver.addOnPreDrawListener(preDrawListener)
    }

    private fun badgeLayoutParams(parent: ViewGroup, width: Int, height: Int): ViewGroup.LayoutParams =
        when (parent) {
            is RelativeLayout -> RelativeLayout.LayoutParams(width, height)
            is LinearLayout -> LinearLayout.LayoutParams(width, height)
            is FrameLayout -> FrameLayout.LayoutParams(width, height)
            else -> ViewGroup.LayoutParams(width, height)
        }

    private fun removeRecallBadge(row: View) {
        val badge = badges.remove(row) ?: return
        row.viewTreeObserver.removeOnPreDrawListener(badge.preDrawListener)
        (row as? ViewGroup)?.removeView(badge.view)
    }

    // The badge sits fully outside the bubble, bottom-aligned with it, clamped inside the row:
    // on the bubble's right for received messages, mirrored to the bubble's left for self-sent
    // ones (bubble and avatar are right-aligned there). Reconciled before every draw from the
    // anchor's absolute position — never from cached field offsets, because other features move
    // the content column within the row (avatar/name show-hide) without touching the anchor's
    // own coordinates. The per-frame cost is two matrix walks over preallocated arrays plus a
    // couple of float compares; the write only happens on an actual change, and the DFS
    // re-anchor only runs when the current anchor view went away.
    private fun positionRecallBadge(row: View, badge: RecallBadge) {
        if (!row.isAttachedToWindow || row.width == 0) return
        var anchor = badge.anchor
        if (!anchor.isAttachedToWindow || anchor.width == 0) {
            anchor = findMessageContent(row) ?: return
            badge.anchor = anchor
        }
        val badgeView = badge.view
        val badgeWidth = badgeView.measuredWidth
        val badgeHeight = badgeView.measuredHeight
        if (badgeWidth == 0 || badgeHeight == 0) return

        row.getLocationOnScreen(badge.rowLocation)
        anchor.getLocationOnScreen(badge.anchorLocation)
        val anchorLeft = badge.anchorLocation[0] - badge.rowLocation[0]
        val anchorRight = anchorLeft + anchor.width
        val anchorBottom = badge.anchorLocation[1] - badge.rowLocation[1] + anchor.height
        val gap = BADGE_GAP_DP.dpToPx(row.context).toFloat()
        val rowRightLimit = (row.width - badgeWidth).toFloat()
        val targetX = if (badge.isSelfSender) {
            (anchorLeft - gap - badgeWidth).coerceIn(0f, rowRightLimit)
        } else {
            (anchorRight + gap).coerceIn(0f, rowRightLimit)
        }
        val targetY = (anchorBottom - badgeHeight)
            .toFloat()
            .coerceIn(0f, (row.height - badgeHeight).toFloat())
        if (badgeView.translationX != targetX || badgeView.translationY != targetY) {
            badgeView.translationX = targetX
            badgeView.translationY = targetY
        }
    }

    // Same icon style as SwipeMessageOperations' swipe action icons: a tinted vector glyph on a
    // circular backdrop, in red to mark the message as deleted.
    @SuppressLint("AppCompatCustomView")
    private class RecallBadgeIconView(
        context: Context,
        iconColor: Int,
        circleColor: Int,
    ) : ImageView(context) {
        init {
            scaleType = ScaleType.CENTER_INSIDE
            // A fresh drawable per badge, so the tint never leaks into another badge.
            val drawable = object : VectorPathDrawable(DELETE_ICON_PATH) {}
            drawable.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)
            setImageDrawable(drawable)
            imageTintList = null
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(circleColor)
            }
            val pad = BADGE_PADDING_DP.dpToPx(context)
            setPadding(pad, pad, pad, pad)
        }
    }

    // ── legacy notice migration ────────────────────────────────────────────────

    private sealed interface MigrationState {
        data object Idle : MigrationState
        data object Running : MigrationState
        data class Done(val migrated: Int, val unmatched: Int) : MigrationState
        data object Failed : MigrationState
    }

    private data class LegacyNotice(val msgId: Long, val talker: String, val createTime: Long)

    /**
     * Finds the system-message rows rendered from the old notice template, records their original
     * messages into the new persisted list, and deletes the notice rows.
     */
    private fun migrateLegacyNotices(): Pair<Int, Int> {
        // The 提示格式 option was removed; the last stored template survives in MMKV and is what
        // old notices were rendered with. The MMKV key only exists if the user ever saved a custom
        // value (defaults are applied at read time), so fall back to the historical default.
        val template = WePrefs.getString("recall_pattern")
            ?: "「\$sender」尝试撤回上一条消息 (已阻止)"
        val regex = buildLegacyNoticeRegex(template)
            ?: throw IllegalStateException("invalid stored recall pattern")

        val notices = mutableListOf<LegacyNotice>()
        WeDatabaseApi.rawQuery(
            "SELECT msgId, talker, content, createTime FROM message WHERE type = ${MessageType.SYSTEM.code}",
        ).use { cursor ->
            while (cursor.moveToNext()) {
                val content = cursor.getString(2) ?: continue
                if (!regex.matches(content)) continue
                notices += LegacyNotice(
                    msgId = cursor.getLong(0),
                    talker = cursor.getString(1) ?: continue,
                    createTime = cursor.getLong(3),
                )
            }
        }

        var migrated = 0
        var unmatched = 0
        val newKeys = mutableSetOf<String>()
        for (notice in notices) {
            val originalServerId = findOriginalMessage(notice)
            if (originalServerId == null) {
                unmatched++
                continue
            }
            newKeys += encodeKey(notice.talker, originalServerId)
            WeDatabaseApi.delete("message", "msgId = ?", arrayOf(notice.msgId.toString()))
            migrated++
        }

        if (newKeys.isNotEmpty()) {
            recalledKeyCache = recalledKeyCache + newKeys
            recalledRecords = recalledKeyCache
        }
        return migrated to unmatched
    }

    // Legacy notices were injected at original.createTime + 1 (createTime is in seconds), so the
    // original lives one second before the notice in the same chat; fall back to the nearest
    // earlier regular message when that exact second has no surviving row.
    @Suppress("DEPRECATION")
    private fun findOriginalMessage(notice: LegacyNotice): Long? {
        val systemTypes = listOf(
            MessageType.SYSTEM.code,
            MessageType.SYSTEM_NOTICE.code,
            MessageType.RECALL.code,
            MessageType.SYSTEM_LOCATION.code,
        ).joinToString(",")
        val beforeNotice = (notice.createTime - 1).toString()

        WeDatabaseApi.rawQuery(
            "SELECT msgSvrId FROM message WHERE talker = ? AND createTime = ? AND type NOT IN ($systemTypes) ORDER BY msgId DESC LIMIT 1",
            arrayOf(notice.talker, beforeNotice)
        ).use { cursor ->
            if (cursor.moveToFirst()) return cursor.getLong(0).takeIf { it != 0L }
        }
        WeDatabaseApi.rawQuery(
            "SELECT msgSvrId FROM message WHERE talker = ? AND createTime < ? AND type NOT IN ($systemTypes) ORDER BY createTime DESC, msgId DESC LIMIT 1",
            arrayOf(notice.talker, beforeNotice)
        ).use { cursor ->
            if (cursor.moveToFirst()) return cursor.getLong(0).takeIf { it != 0L }
        }
        return null
    }

    private fun buildLegacyNoticeRegex(template: String): Regex? {
        if (template.isBlank()) return null

        val placeholders = listOf("\$sender", "\$sendTime", "\$recallTime", "\$content")
        val builder = StringBuilder("^")
        var rest = template
        while (rest.isNotEmpty()) {
            val match = placeholders
                .mapNotNull { placeholder ->
                    rest.indexOf(placeholder).takeIf { it >= 0 }?.let { placeholder to it }
                }
                .minByOrNull { it.second }
            if (match == null) {
                builder.append(Regex.escape(rest))
                break
            }
            val (placeholder, index) = match
            if (index > 0) builder.append(Regex.escape(rest.substring(0, index)))
            builder.append(if (placeholder == "\$content") """[\s\S]*""" else """[\s\S]+?""")
            rest = rest.substring(index + placeholder.length)
        }
        builder.append('$')

        return runCatching { Regex(builder.toString()) }.getOrNull()
    }

    // ── config dialog ──────────────────────────────────────────────────────────

    override fun onClick(context: ComponentActivity) {
        showComposeDialog(context) {
            var recallOutgoingInput by remember { mutableStateOf(recallOutgoing) }
            var badgeTypeInput by remember { mutableStateOf(badgeType) }
            var migrationState by remember { mutableStateOf<MigrationState>(MigrationState.Idle) }

            AlertDialogContent(
                    title = { Text("防撤回") },
                    text = {
                        SegmentedColumn(contentPadding = PaddingValues(0.dp)) {
                            item {
                                SwitchWidget(
                                    iconPlaceholder = false,
                                    title = "防撤回自己的消息",
                                    description = "是否对自己发出的消息也生效",
                                    checked = recallOutgoingInput,
                                    onCheckedChange = {
                                        recallOutgoingInput = it
                                        recallOutgoing = it
                                    },
                                )
                            }
                            item {
                                DropDownMenuWidget(
                                    iconPlaceholder = false,
                                    title = "徽标类型",
                                    description = null,
                                    value = badgeTypeInput,
                                    options = listOf(
                                        DropdownOption(BADGE_TYPE_ICON, "图标"),
                                        DropdownOption(BADGE_TYPE_TEXT, "文本"),
                                    ),
                                    onValueChange = {
                                        badgeTypeInput = it
                                        badgeType = it
                                    },
                                )
                            }
                            item {
                                val migrating = migrationState == MigrationState.Running
                                BaseWidget(
                                    iconPlaceholder = false,
                                    title = "迁移旧版撤回提示",
                                    description = when (val state = migrationState) {
                                        MigrationState.Idle -> "将旧版以系统消息插入的撤回提示迁移为新样式，并删除旧提示；重新进入会话后生效"
                                        MigrationState.Running -> "正在迁移…"
                                        is MigrationState.Done ->
                                            "已迁移 ${state.migrated} 条提示，${state.unmatched} 条未能定位原消息"
                                        MigrationState.Failed -> "迁移失败"
                                    },
                                    isError = migrationState is MigrationState.Failed,
                                    enabled = !migrating,
                                    onClick = {
                                        migrationState = MigrationState.Running
                                        CoroutineScope(Dispatchers.IO).launch {
                                            migrationState = runCatching { migrateLegacyNotices() }
                                                .fold(
                                                    onSuccess = { (migrated, unmatched) ->
                                                        MigrationState.Done(migrated, unmatched)
                                                    },
                                                    onFailure = {
                                                        WeLogger.e(TAG, "legacy notice migration failed", it)
                                                        MigrationState.Failed
                                                    },
                                                )
                                        }
                                    },
                                    trailingContent = {
                                        if (migrating) {
                                            CircularProgressIndicator(modifier = Modifier.size(18.dp))
                                        }
                                    },
                                )
                            }
                        }
                    },
                    dismissButton = {
                        TextButton(onDismiss) { Text("关闭") }
                    },
            )
        }
    }
}
