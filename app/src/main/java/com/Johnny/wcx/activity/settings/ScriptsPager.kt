package com.Johnny.wcx.activity.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import h.Hchat.hooks.items.script.ScriptPluginRuntime
import h.Hchat.ui.miuix.PluginMarketUi
import h.Hchat.ui.miuix.ScriptPluginManagerTabPage
import h.Hchat.ui.miuix.ScriptPluginMiuixTabContent
import h.Hchat.ui.miuix.ScriptPluginReadmeTabDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * 「脚本」独立页 —— WCX 专属脚本插件中心：
 * 插件总开关 / 插件列表（启用开关 + 操作菜单）/ 本地插件管理 / 在线插件市场 / README 弹窗。
 *
 * 子页面（在线插件、本地插件管理）用全屏 Dialog 呈现：
 * 设置页底部 5 个 Tab 会盖住子页面底部的操作区，Hchat 里这些子页面本身是整页跳转，
 * 这里用全屏宿主对齐同样的观感。
 */
@Composable
fun ScriptsPager() {
    val context = LocalContext.current
    var showManager by remember { mutableStateOf(false) }
    var showMarket by remember { mutableStateOf(false) }
    var readmePlugin by remember { mutableStateOf<ScriptPluginRuntime.ScriptPlugin?>(null) }

    MiuixListScaffold(title = "脚本", blurTopBar = false) {
        item {
            // 父级 Pager 与内层 TopAppBar 各有一层 backdrop；内容滚动越界会被采样成顶部灰块，这里裁剪掉溢出绘制。
            Box(modifier = Modifier.fillMaxWidth().clipToBounds()) {
                ScriptPluginMiuixTabContent(
                    context = context,
                    onOpenManager = { showManager = true },
                    onOpenReadme = { plugin -> readmePlugin = plugin },
                    onOpenMarket = { showMarket = true },
                )
            }
        }
        item {
            Spacer(Modifier.height(CONTENT_BOTTOM_INSET))
        }
    }

    if (showMarket || showManager) {
        Dialog(
            onDismissRequest = {
                showMarket = false
                showManager = false
            },
            properties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MiuixTheme.colorScheme.background),
            ) {
                if (showMarket) {
                    PluginMarketUi.ScriptPluginMarketPage(
                        context = context,
                        onBack = { showMarket = false },
                    )
                } else {
                    ScriptPluginManagerTabPage(
                        context = context,
                        onBack = { showManager = false },
                    )
                }
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
