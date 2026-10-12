package com.Johnny.wcx.activity.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.materialsymbols.MaterialSymbols
import com.composables.icons.materialsymbols.outlined.Arrow_back
import com.Johnny.wcx.features.hchat.HchatScriptBootstrap
import com.Johnny.wcx.utils.HostInfo
import com.Johnny.wcx.utils.TargetProcesses
import com.Johnny.wcx.utils.android.showToast
import h.Hchat.hooks.api.core.WeChatApis
import h.Hchat.hooks.items.script.ScriptPluginRuntime
import h.Hchat.hooks.items.script.ScriptPluginSettings
import h.Hchat.preferences.HchatStorage
import h.Hchat.ui.miuix.ActionRow
import h.Hchat.ui.miuix.InsetDivider
import h.Hchat.ui.miuix.PageScaffold
import h.Hchat.ui.miuix.PluginMarketUi
import h.Hchat.ui.miuix.ScriptPluginManagerTabPage
import h.Hchat.ui.miuix.SettingsCard
import top.yukonga.miuix.kmp.basic.BasicComponentDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

/** 在线插件市场（Hchat 原版 UI），整页跳转。 */
@Composable
fun ScriptMarketScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MiuixTheme.colorScheme.background),
    ) {
        PluginMarketUi.ScriptPluginMarketPage(context = context, onBack = onBack)
    }
}

/** 本地插件管理，整页跳转。 */
@Composable
fun ScriptManagerScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MiuixTheme.colorScheme.background),
    ) {
        ScriptPluginManagerTabPage(context = context, onBack = onBack)
    }
}

/**
 * 脚本功能诊断页。
 *
 * 目标：脚本开关开不起来 / 脚本不工作时，一眼能看出卡在引导链路的哪一步、微信 API 是否解析。
 */
@Composable
fun ScriptDiagnosticsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var refreshVersion by remember { mutableStateOf(0) }
    val rows = remember(refreshVersion) { collectScriptDiagnostics(context) }
    val scrollBehavior = MiuixScrollBehavior()

    PageScaffold(
        title = "脚本功能诊断",
        largeTitle = "脚本功能诊断",
        scrollBehavior = scrollBehavior,
        onBack = onBack,
    ) { padding ->
        androidx.compose.foundation.lazy.LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                top = padding.calculateTopPadding() + 8.dp,
                bottom = padding.calculateBottomPadding() + 84.dp,
            ),
        ) {
            item { SmallTitle(modifier = Modifier.padding(top = 6.dp), text = "运行时状态") }
            item {
                SettingsCard {
                    rows.forEachIndexed { index, row ->
                        if (index > 0) InsetDivider()
                        DiagnosticRow(row.first, row.second, row.second.startsWith("✗") || row.second.contains("未"))
                    }
                }
            }
            item { SmallTitle(modifier = Modifier.padding(top = 10.dp), text = "操作") }
            item {
                SettingsCard {
                    ActionRow("重新初始化脚本子系统", "重跑引导链路：WechatApiFeature + ScriptPluginFeature") {
                        HchatScriptBootstrap.retry()
                        refreshVersion++
                        showToast("已重新初始化，请稍候再试开关")
                    }
                    InsetDivider()
                    ActionRow("刷新诊断信息", "重新读取当前运行时状态") { refreshVersion++ }
                    InsetDivider()
                    ActionRow("复制诊断信息", "把上面的内容复制到剪贴板，便于反馈") {
                        val text = rows.joinToString("\n") { "${it.first}：${it.second}" }
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                        clipboard?.setPrimaryClip(ClipData.newPlainText("WCX 脚本诊断", text))
                        showToast("诊断信息已复制")
                    }
                }
            }
            item {
                Text(
                    text = "提示：脚本依赖微信公共 API（DexKit 解析数据库/账号/消息等）。" +
                        "若「微信数据库 API」为未解析，通常是 DexKit 预热未完成或当前微信版本未适配。",
                    fontSize = 12.sp,
                    color = BasicComponentDefaults.summaryColor().color,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                )
            }
        }
    }
}

@Composable
private fun DiagnosticRow(label: String, value: String, warn: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = BasicComponentDefaults.titleColor().color,
            modifier = Modifier.fillMaxWidth(0.42f),
        )
        Text(
            text = value,
            fontSize = 12.sp,
            color = if (warn) androidx.compose.ui.graphics.Color(0xFFD93025)
            else BasicComponentDefaults.summaryColor().color,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/** 采集脚本运行时诊断信息（label -> value）。 */
private fun collectScriptDiagnostics(context: Context): List<Pair<String, String>> {
    val rows = mutableListOf<Pair<String, String>>()

    val enabled = runCatching {
        HchatStorage.preferences(context, ScriptPluginSettings.PREFS_NAME)
            .getBoolean(ScriptPluginSettings.KEY_ENABLE, ScriptPluginSettings.DEFAULT_ENABLE)
    }.getOrDefault(false)
    rows += "插件总开关" to if (enabled) "已开启" else "已关闭"

    val database = runCatching { WeChatApis.database() }.getOrNull()
    val account = runCatching { WeChatApis.account() }.getOrNull()
    rows += "微信数据库 API" to if (database != null) "已解析" else "✗ 未解析（DexKit 未完成/未适配）"
    rows += "微信账号 API" to if (account != null) "已解析" else "✗ 未解析（DexKit 未完成/未适配）"

    rows += "引导链路" to runCatching { HchatScriptBootstrap.status }.getOrDefault("未知")
    rows += "未就绪原因" to runCatching { ScriptPluginRuntime.scriptApiReadinessDetail() }
        .getOrNull().orEmpty().ifBlank { "无（已就绪）" }

    val pluginCount = runCatching { ScriptPluginRuntime.listPlugins(context).size }.getOrDefault(-1)
    rows += "已安装插件" to if (pluginCount >= 0) "$pluginCount 个" else "读取失败"
    rows += "插件目录" to runCatching { ScriptPluginRuntime.scriptDir(context).absolutePath }
        .getOrDefault("未知")

    rows += "宿主" to "${HostInfo.packageName} ${HostInfo.versionName}(${HostInfo.versionCode})"
    rows += "进程" to TargetProcesses.currentName
    return rows
}
