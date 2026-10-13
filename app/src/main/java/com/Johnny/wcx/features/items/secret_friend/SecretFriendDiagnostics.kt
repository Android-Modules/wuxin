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
        val text = runCatching { buildReport() }.getOrElse { error ->
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

    private fun buildReport(): String {
        val sb = StringBuilder()

        // ── 全局状态 ──
        val masterOn = SecretFriendState.isSecretFriendMasterOn()
        val count = SecretFriendState.getStoredWxIds().size
        val tempShown = SecretFriendState.isTemporarilyShown()

        sb.append("【全局状态】\n")
        sb.append("总开关（密友名单管理）：").append(if (masterOn) "开启" else "关闭 ❌ 所有隐藏已失效").append('\n')
        sb.append("名单人数：").append(count)
        if (count == 0) sb.append("（❌ 名单为空，全部隐藏不生效）")
        sb.append('\n')
        sb.append("临时解除：")
        if (tempShown) sb.append("生效中 ❌ 名单内所有人当前可见") else sb.append("未激活")
        sb.append("\n\n")

        // ── 各隐藏面 ──
        sb.append("【隐藏面生效状态】\n")
        appendFace(sb, "主页会话隐藏", HideConversations.isEnabled)
        appendFace(sb, "通讯录隐藏", HideSecretContacts.isEnabled)
        appendFace(sb, "标签内隐藏", HideTagMembers.isEnabled)
        appendFace(sb, "主页搜索隐藏", HideSearch.isEnabled)
        appendFace(sb, "禁止进入聊天", BlockChat.isEnabled)
        appendFace(sb, "禁止查看资料", BlockProfile.isEnabled)
        appendFace(sb, "朋友圈隐藏", HideMoments.isEnabled)
        appendFace(sb, "我的朋友圈隐藏", HideMyMoments.isEnabled)
        appendFace(sb, "最近转发隐藏", HideRecentForward.isEnabled)
        appendFace(sb, "状态页隐藏", HideStatusPage.isEnabled)
        appendFace(sb, "存储空间记录隐藏", HideStorageRecords.isEnabled)
        appendFace(sb, "存储空间缓存隐藏", HideStorageCache.isEnabled)
        appendFace(sb, "免打扰（密友消息静音）", MuteSecretFriend.isEnabled)
        sb.append('\n')

        // ── 关键锚点自检 ──
        sb.append("【关键锚点自检】\n")
        appendClass(sb, "聊天页 ChattingUI", CHATTING_UI)
        appendClass(sb, "资料页 ContactInfoUI", CONTACT_INFO_UI)
        appendClass(sb, "通讯录 LiveList", ADDRESS_LIVE_LIST)
        appendClass(sb, "wcdb SQLiteDatabase", WCDB_DB)
        appendClass(sb, "wcdb compat SQLiteDatabase", WCDB_DB_COMPAT)
        appendMethod(sb, "ChattingUI.onCreate", CHATTING_UI, "onCreate")
        appendMethod(sb, "ContactInfoUI.onCreate", CONTACT_INFO_UI, "onCreate")
        sb.append('\n')

        // ── 提示 ──
        sb.append("【说明】\n")
        sb.append("· 显示「关闭」的面 = 该层没有隐藏，请到列表里打开对应开关\n")
        sb.append("· 名单为空 / 临时解除生效时，所有面都会放行（正常行为）\n")
        sb.append("· 锚点显示 ❌ = 当前微信版本该面的类/方法已漂移，该面隐藏会失效\n")
        sb.append("· 若开关已开、锚点也 ✅ 却仍能看到密友，请把本页截图反馈\n")

        return sb.toString()
    }

    private fun appendFace(sb: StringBuilder, label: String, enabled: Boolean) {
        sb.append(if (enabled) "✅ " else "❌ ")
        sb.append(label).append("：").append(if (enabled) "开启" else "关闭").append('\n')
    }

    private fun appendClass(sb: StringBuilder, label: String, className: String) {
        val found = loadClass(className) != null
        sb.append(if (found) "✅ " else "❌ ")
        sb.append(label).append("：").append(if (found) "已找到" else "未找到（该面锚点失配）").append('\n')
    }

    private fun appendMethod(sb: StringBuilder, label: String, className: String, methodName: String) {
        val found = runCatching {
            val clazz = loadClass(className) ?: return@runCatching false
            clazz.declaredMethods.any { it.name == methodName } || clazz.methods.any { it.name == methodName }
        }.getOrDefault(false)
        sb.append(if (found) "✅ " else "❌ ")
        sb.append(label).append("：").append(if (found) "已找到" else "未找到（该面无法拦截）").append('\n')
    }

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
