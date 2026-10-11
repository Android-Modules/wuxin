package com.Johnny.wcx.activity.settings

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import h.Hchat.hooks.items.script.ScriptPluginFeature
import h.Hchat.ui.SettingsUI

/**
 * 「脚本」页。
 *
 * 脚本子系统（总开关 / 插件列表 / 插件管理 / 在线插件市场 / 脚本 Agent / README）
 * 已从 Hchat 完整迁移，其设置界面由 Hchat 侧 MiuixSettingsPage 以嵌入式 Compose
 * 浮层的形式叠加在当前 Activity 上。
 *
 * 进入脚本页时直接请求打开 Hchat 的脚本设置页（featureId = "script_plugin"），
 * 同时保留一个手动入口作为兜底；打开失败时给出可见提示，避免出现「点了没反应」。
 */
@Composable
fun ScriptsPager() {
    val context = LocalContext.current

    fun openScriptSettings() {
        val opened = runCatching { SettingsUI.showFeature(context, ScriptPluginFeature.ID) }
            .getOrDefault(false)
        if (!opened) {
            Toast.makeText(context, "脚本设置页打开失败：脚本插件未就绪", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        openScriptSettings()
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "脚本插件")
            Button(
                onClick = { openScriptSettings() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "打开脚本插件设置")
            }
        }
    }
}
