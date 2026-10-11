package h.Hchat.preferences

import android.content.Context
import h.Hchat.utils.HLog

/**
 * 用户协议门禁。
 *
 * WCX 定制：Hchat 原有的「首次使用需同意用户协议」限制已移除，
 * [isAccepted] 恒为 true，功能模块初始化不再受条款状态约束。
 * [accept] 仅保留用于兼容既有调用点。
 */
object TermsGate {
    const val AGREEMENT_TEXT = "我同意"
    const val TERMS_VERSION = 1

    private const val KEY_ACCEPTED = "terms_accepted"
    private const val KEY_VERSION = "terms_version"

    fun isAccepted(@Suppress("UNUSED_PARAMETER") context: Context): Boolean = true

    fun isAccepted(@Suppress("UNUSED_PARAMETER") store: ConfigStore): Boolean = true

    fun accept(context: Context): Boolean {
        val store = ConfigStore(context)
        val prefs = store.reopenGlobalPrefsIfFilesMissing()
        return try {
            val committed = prefs.edit()
                .putBoolean(KEY_ACCEPTED, true)
                .putInt(KEY_VERSION, TERMS_VERSION)
                .commit()
            committed && prefs.getBoolean(KEY_ACCEPTED, false) &&
                prefs.getInt(KEY_VERSION, 0) == TERMS_VERSION
        } catch (e: Throwable) {
            HLog.e("[Hchat:TermsGate] 保存协议状态失败: ${e.message}", e)
            false
        }
    }
}
