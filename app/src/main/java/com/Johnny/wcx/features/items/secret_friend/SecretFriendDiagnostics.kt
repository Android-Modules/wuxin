package com.Johnny.wcx.features.items.secret_friend

import android.app.AlertDialog
import androidx.activity.ComponentActivity
import com.Johnny.wcx.features.core.ClickableFeature
import com.Johnny.wcx.features.core.Feature
import com.Johnny.wcx.utils.WeLogger

/**
 * 密友功能诊断：把「哪一层没隐藏掉」变成可见信息。
 *
 * 密友由 ~25 个独立开关组成（会话 / 通讯录 / 搜索 / 聊天 / 资料 / 朋友圈 / 外围界面…），
 * 任一开关关闭，或某个依赖的类/方法在当前微信版本上失配，该面就会静默失效——用户只看到
 * 「还能搜到 / 还能点进去」，却无从判断是哪一层。本页集中展示总开关、名单、临时解除、
 * 每个隐藏面的开关状态，以及**关键锚点是否解析成功**（类/方法是否存在），
 * 据此可直接判断是「开关没开」还是「当前微信版本锚点漂移」。
 */
@Feature(
    name = "密友功能诊断",
    categories = ["密友功能"],
    description = "查看密友各隐藏面的实际生效状态：总开关、名单人数、临时解除、每个面的开关，以及关键锚点是否解析成功"
)
object SecretFriendDiagnostics : ClickableFeature() {

    private const val TAG = "SecretFriendDiagnostics"

    /** 诊断本身不需要开关，常驻可点。 */
    override val alwaysEnabled: Boolean = true

    override fun onClick(context: ComponentActivity) {
        val text = runCatching { buildReportText() }.getOrElse { error ->
            WeLogger.e(TAG, "build report failed", error)
            "诊断信息生成失败：" + (error.message ?: error.javaClass.simpleName)
        }
        runCatching {
            AlertDialog.Builder(context)
                .setTitle("密友功能诊断")
                .setMessage(text)
                .setPositiveButton("知道了", null)
                .show()
        }.onFailure { WeLogger.e(TAG, "show diagnostics failed", it) }
    }

    /** 结构化诊断行（label -> value），供密友诊断页面渲染。 */
    fun collectRows(): List<Pair<String, String>> {
        val rows = mutableListOf<Pair<String, String>>()

        val masterOn = SecretFriendState.isSecretFriendMasterOn()
        val count = SecretFriendState.getStoredWxIds().size
        val tempShown = SecretFriendState.isTemporarilyShown()

        rows += "总开关（密友名单管理）" to if (masterOn) "开启" else "关闭（所有隐藏已失效）"
        rows += "名单人数" to if (count == 0) "0（名单为空，全部隐藏不生效）" else "$count"
        rows += "临时解除" to if (tempShown) "生效中（名单内所有人当前可见）" else "未激活"

        rows += "── 主页会话隐藏 ──" to faceState(HideConversations.isEnabled)
        rows += "── 通讯录隐藏 ──" to faceState(HideSecretContacts.isEnabled)
        rows += "── 标签内隐藏 ──" to faceState(HideTagMembers.isEnabled)
        rows += "── 主页搜索隐藏 ──" to faceState(HideSearch.isEnabled)
        rows += "── 禁止进入聊天 ──" to faceState(BlockChat.isEnabled)
        rows += "── 禁止查看资料 ──" to faceState(BlockProfile.isEnabled)
        rows += "── 朋友圈隐藏 ──" to faceState(HideMoments.isEnabled)
        rows += "── 我的朋友圈隐藏 ──" to faceState(HideMyMoments.isEnabled)
        rows += "── 最近转发隐藏 ──" to faceState(HideRecentForward.isEnabled)
        rows += "── 状态页隐藏 ──" to faceState(HideStatusPage.isEnabled)
        rows += "── 存储空间记录隐藏 ──" to faceState(HideStorageRecords.isEnabled)
        rows += "── 存储空间缓存隐藏 ──" to faceState(HideStorageCache.isEnabled)
        rows += "── 免打扰（消息静音）──" to faceState(MuteSecretFriend.isEnabled)

        rows += "── 锚点·聊天页 ChattingUI ──" to probeClass(CHATTING_UI)
        rows += "── 锚点·资料页 ContactInfoUI ──" to probeClass(CONTACT_INFO_UI)
        rows += "── 锚点·通讯录 AddressLiveList ──" to probeClass(ADDRESS_LIVE_LIST)
        rows += "── 锚点·ChattingUI.onCreate ──" to probeMethod(CHATTING_UI, "onCreate")
        rows += "── 锚点·ContactInfoUI.onCreate ──" to probeMethod(CONTACT_INFO_UI, "onCreate")
        rows += "── 锚点·wcdb SQLiteDatabase ──" to probeClass(WCDB_DB)
        rows += "── 锚点·wcdb compat SQLiteDatabase ──" to probeClass(WCDB_DB_COMPAT)

        return rows
    }

    /** 整段文本（供 ClickableFeature 的弹窗展示）。 */
    fun buildReportText(): String =
        collectRows().joinToString("\n") { it.first + "：" + it.second }

    private fun faceState(enabled: Boolean): String =
        if (enabled) "开启" else "关闭（该层未隐藏）"

    private fun probeClass(className: String): String {
        val clazz = loadClass(className)
        return if (clazz != null) "已找到" else "未找到（该面锚点失配）"
    }

    private fun probeMethod(className: String, methodName: String): String = runCatching {
        val clazz = loadClass(className) ?: return@runCatching "未找到类（该面无法拦截）"
        val found = clazz.declaredMethods.any { it.name == methodName } ||
            clazz.methods.any { it.name == methodName }
        if (found) "已找到" else "未找到方法（该面无法拦截）"
    }.getOrElse { "探测异常：" + (it.message ?: it.javaClass.simpleName) }

    /** 用模块 ClassLoader 解析宿主类；解析不到（类不存在/CL 不可见）返回 null，不抛异常。 */
    private fun loadClass(className: String): Class<*>? = runCatching {
        Class.forName(className, false, SecretFriendDiagnostics::class.java.classLoader)
    }.getOrNull()

    private const val CHATTING_UI = "com.tencent.mm.ui.chatting.ChattingUI"
    private const val CONTACT_INFO_UI = "com.tencent.mm.plugin.profile.ui.ContactInfoUI"
    private const val ADDRESS_LIVE_LIST = "com.tencent.mm.ui.contact.address.AddressLiveList"
    private const val WCDB_DB = "com.tencent.wcdb.database.SQLiteDatabase"
    private const val WCDB_DB_COMPAT = "com.tencent.wcdb.compat.SQLiteDatabase"
}
