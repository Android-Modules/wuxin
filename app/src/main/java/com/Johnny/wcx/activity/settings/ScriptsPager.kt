package com.Johnny.wcx.activity.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import h.Hchat.hooks.items.script.ScriptPluginRuntime
import h.Hchat.ui.SettingsUI
import h.Hchat.ui.miuix.HchatMiuixTheme
import h.Hchat.ui.miuix.ScriptPluginSettingsMiuixContent

/**
 * 「脚本」页。
 *
 * 直接内嵌渲染 Hchat 的脚本插件内容页（与 Hchat 主设置页的「脚本插件」Tab 一致）：
 * 插件总开关 + 本地插件列表 + 插件 Agent / 在线插件 / 本地插件管理入口。
 *
 * 点击「插件 Agent / 在线插件 / 本地插件管理」时，以 Hchat 的浮层页形式打开对应子页面。
 */
@Composable
fun ScriptsPager() {
    val context = LocalContext.current
    var readmePlugin by remember { mutableStateOf<ScriptPluginRuntime.ScriptPlugin?>(null) }

    HchatMiuixTheme(context) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            ScriptPluginSettingsMiuixContent.ScriptPluginSettingsContent(
                context = context,
                onOpenReadme = { readmePlugin = it },
                onOpenMarket = { SettingsUI.showScriptPluginMarket(context) },
                onOpenAgent = { SettingsUI.showScriptPluginAgent(context) },
                onOpenManager = { SettingsUI.showScriptPluginManager(context) }
            )
        }

        readmePlugin?.let { plugin ->
            ScriptPluginSettingsMiuixContent.ScriptPluginReadmeDialog(
                context = context,
                plugin = plugin,
                onClose = { readmePlugin = null }
            )
        }
    }
}
