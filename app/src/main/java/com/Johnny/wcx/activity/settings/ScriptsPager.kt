package com.Johnny.wcx.activity.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import h.Hchat.hooks.items.script.ScriptPluginRuntime
import h.Hchat.ui.miuix.PluginMarketUi
import h.Hchat.ui.miuix.ScriptPluginAgentUi
import h.Hchat.ui.miuix.ScriptPluginManagerTabPage
import h.Hchat.ui.miuix.ScriptPluginMiuixTabContent
import h.Hchat.ui.miuix.ScriptPluginReadmeTabDialog

/**
 * 「脚本」独立页（WCX 自建版本，对齐 v274~v276 那套"能用"的实现）。
 *
 * 结构：
 * - route 0：内容页（插件总开关 / 插件 Agent / 在线插件 / 本地插件管理 / 本地插件列表）
 * - route 1：本地插件管理页（`ScriptPluginManagerTabPage`）
 * - route 2：在线插件市场页（Hchat `PluginMarketUi.ScriptPluginMarketPage`，走 Hchat 市场服务器）
 * - route 3：脚本 Agent 工作台（Hchat `ScriptPluginAgentUi.ScriptPluginAgentWorkspacePage`）
 *
 * 子页面全部**内联渲染**（而不是"浮层叠加到 Activity"），避免浮层机制在 WCX 里点击无反应。
 * 数据与逻辑走已迁移的 Hchat 脚本子系统（ScriptPluginRuntime / ScriptPluginManager）。
 */
@Composable
fun ScriptsPager() {
    val context = LocalContext.current
    var route by remember { mutableStateOf(0) }
    var readmePlugin by remember { mutableStateOf<ScriptPluginRuntime.ScriptPlugin?>(null) }

    when (route) {
        1 -> ScriptPluginManagerTabPage(
            context = context,
            onBack = { route = 0 },
        )
        2 -> PluginMarketUi.ScriptPluginMarketPage(
            context = context,
            onBack = { route = 0 },
        )
        3 -> ScriptPluginAgentUi.ScriptPluginAgentWorkspacePage(
            context = context,
            onBack = { route = 0 },
            onExitHandlerChanged = {},
        )
        else -> MiuixListScaffold(title = "脚本", blurTopBar = false) {
            item {
                // 父级 Pager 与内层 TopAppBar 各有一层 backdrop；内容滚动越界会被采样成顶部灰块，这里裁剪掉溢出绘制。
                Box(modifier = Modifier.fillMaxWidth().clipToBounds()) {
                    ScriptPluginMiuixTabContent(
                        context = context,
                        onOpenManager = { route = 1 },
                        onOpenReadme = { plugin -> readmePlugin = plugin },
                        onOpenMarket = { route = 2 },
                        onOpenAgent = { route = 3 },
                    )
                }
            }
            item {
                Spacer(Modifier.height(CONTENT_BOTTOM_INSET))
            }
        }
    }

    val pending = readmePlugin
    if (pending != null) {
        ScriptPluginReadmeTabDialog(
            context = context,
            plugin = pending,
            onClose = { readmePlugin = null },
        )
    }
}
