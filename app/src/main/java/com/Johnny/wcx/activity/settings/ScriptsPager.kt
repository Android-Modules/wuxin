package com.Johnny.wcx.activity.settings

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Johnny.wcx.features.hchat.HchatScriptBootstrap
import com.Johnny.wcx.utils.HostInfo
import com.Johnny.wcx.utils.TargetProcesses
import com.Johnny.wcx.utils.WeLogger
import h.Hchat.hooks.api.core.WeChatApis
import h.Hchat.hooks.items.script.ScriptPluginRuntime
import h.Hchat.ui.SettingsUI
import h.Hchat.ui.miuix.HchatMiuixTheme
import h.Hchat.ui.miuix.ScriptPluginSettingsMiuixContent

private const val SCRIPTS_TAG = "ScriptsPager"

/**
 * 「脚本」页。
 *
 * 直接内嵌渲染 Hchat 的脚本插件内容页（与 Hchat 主设置页的「脚本插件」Tab 一致）：
 * 插件总开关 + 本地插件列表 + 插件 Agent / 在线插件 / 本地插件管理入口。
 *
 * 顶部额外放了一张「运行时诊断」卡片（临时，便于定位「数据库未就绪」），
 * 会显示当前进程、微信版本、引导层走到哪一步、数据库是否就绪、插件目录。
 */
@Composable
fun ScriptsPager() {
    val context = LocalContext.current
    var readmePlugin by remember { mutableStateOf<ScriptPluginRuntime.ScriptPlugin?>(null) }
    var diagTick by remember { mutableStateOf(0) }

    HchatMiuixTheme(context) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            RuntimeDiagnosticsCard(context, diagTick) {
                diagTick++
                HchatScriptBootstrap.reinstall()
            }
            Spacer(Modifier.height(10.dp))
            ScriptPluginSettingsMiuixContent.ScriptPluginSettingsContent(
                context = context,
                onOpenReadme = { readmePlugin = it },
                onOpenMarket = { safeOpen("打开在线插件") { SettingsUI.showScriptPluginMarket(context) } },
                onOpenAgent = { safeOpen("打开插件 Agent") { SettingsUI.showScriptPluginAgent(context) } },
                onOpenManager = { safeOpen("打开本地插件管理") { SettingsUI.showScriptPluginManager(context) } }
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

private inline fun safeOpen(what: String, block: () -> Unit) {
    runCatching { block() }.onFailure { WeLogger.e(SCRIPTS_TAG, "$what 失败", it) }
}

@Composable
private fun RuntimeDiagnosticsCard(context: Context, tick: Int, onReinstall: () -> Unit) {
    val databaseReady = remember(tick) {
        runCatching { WeChatApis.database() != null }.getOrDefault(false)
    }
    val scriptDir = remember(tick) {
        runCatching { ScriptPluginRuntime.scriptDir(context).absolutePath }.getOrDefault("?")
    }
    val lines = listOf(
        "进程: ${TargetProcesses.currentName}（主进程=${TargetProcesses.isInMain}）",
        "微信: ${HostInfo.versionName} (${HostInfo.versionCode})",
        "引导: ${HchatScriptBootstrap.status}",
        "数据库: ${if (databaseReady) "就绪" else "未就绪"}",
        "插件目录: $scriptDir"
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x1F607D8B), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Text(text = "脚本运行时诊断", color = Color(0xFF2196F3), fontSize = 14.sp)
        lines.forEach { line ->
            Text(text = line, color = Color(0xFF888888), fontSize = 12.sp)
        }
        TextButton(onClick = onReinstall) {
            Text(text = "重新初始化运行时", fontSize = 13.sp)
        }
    }
}
