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
 * 任一开关关闭或某个 DexKit 锚点在当前微信版本上失配，该面就会静默失效——用户只看到
 * 「还能搜到 / 还能点进去」，却无从判断是哪一层。本页集中展示总开关、名单、临时解除，
 * 以及每个隐藏面的开关状态，并给出「名单为空 / 临时解除」两类最常见的「整体失效」提示。
 */
@Feature(
    name = "密友功能诊断",
    categories = ["密友功能"],
    description = "查看密友各隐藏面的实际生效状态：总开关、名单人数、临时解除，以及每个面是开还是关——哪一层没隐藏掉一眼可见"
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

        // ── 提示 ──
        sb.append("【说明】\n")
        sb.append("· 显示「关闭」的面 = 该层没有隐藏，请到列表里打开对应开关\n")
        sb.append("· 名单为空 / 临时解除生效时，所有面都会放行（正常行为）\n")
        sb.append("· 若某面已开启却仍能看到密友，多因当前微信版本该锚点失配，模块日志会记录 [SecretFriend] 相关警告\n")

        return sb.toString()
    }

    private fun appendFace(sb: StringBuilder, label: String, enabled: Boolean) {
        sb.append(if (enabled) "✅ " else "❌ ")
        sb.append(label).append("：").append(if (enabled) "开启" else "关闭").append('\n')
    }
}
