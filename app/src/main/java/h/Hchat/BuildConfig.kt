package h.Hchat

/**
 * Hchat → WCX 迁移兼容层。
 *
 * Hchat 源码（crash / keepalive / script / ui.miuix 等多个包）通过
 * `import h.Hchat.BuildConfig` 读取模块版本信息。WCX 的模块包名是 com.Johnny.wcx，
 * AGP 只为其生成 com.Johnny.wcx.BuildConfig，因此这里转发一份同名常量，
 * 使迁移过来的 Hchat 源码无需逐文件改动。
 */
object BuildConfig {
    @JvmField
    val VERSION_NAME: String = com.Johnny.wcx.BuildConfig.VERSION_NAME

    @JvmField
    val VERSION_CODE: Int = com.Johnny.wcx.BuildConfig.VERSION_CODE

    @JvmField
    val APPLICATION_ID: String = com.Johnny.wcx.BuildConfig.APPLICATION_ID

    @JvmField
    val DEBUG: Boolean = com.Johnny.wcx.BuildConfig.DEBUG
}
