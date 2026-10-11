package com.Johnny.wcx.activity.settings

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
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
import com.Johnny.wcx.utils.WeLogger
import h.Hchat.hooks.items.script.ScriptPluginFeature
import h.Hchat.hooks.items.script.ScriptPluginSettingsProvider
import h.Hchat.ui.SettingsUI
import h.Hchat.ui.UIRegistry

private const val SCRIPTS_PAGER_TAG = "ScriptsPager"

/**
 * 「脚本」页。
 *
 * 脚本子系统（总开关 / 插件列表 / 插件管理 / 在线插件市场 / 脚本 Agent / README）
 * 已从 Hchat 完整迁移，其设置界面由 Hchat 侧 MiuixSettingsPage 以嵌入式 Compose
 * 浮层的形式叠加在当前 Activity 上。
 *
 * 进入脚本页时直接请求打开 Hchat 的脚本设置页（featureId = "script_plugin"）。
 * 由于 `SettingsUI.showFeature` 依赖 `UIRegistry` 中已注册的 provider，而引导层
 * （[com.Johnny.wcx.features.hchat.HchatScriptBootstrap]）可能因 DexKit 初始化失败、
 * 或与设置页不在同一进程等原因未能注册，这里做一次「兜底注册」，确保设置页一定能打开。
 */
@Composable
fun ScriptsPager() {
    val context = LocalContext.current
    val activity: Context = context.findActivity() ?: context

    fun ensureProviderRegistered() {
        runCatching {
            val registry = UIRegistry.get()
            val registered = registry.getAllProviders().any { it.featureId() == ScriptPluginFeature.ID }
            if (!registered) {
                registry.registerProvider(ScriptPluginSettingsProvider())
                WeLogger.i(SCRIPTS_PAGER_TAG, "已补注册脚本插件设置 provider（引导层未就绪）")
            }
        }.onFailure {
            WeLogger.e(SCRIPTS_PAGER_TAG, "补注册脚本插件设置 provider 失败", it)
        }
    }

    fun openScriptSettings() {
        ensureProviderRegistered()
        val opened = runCatching { SettingsUI.showFeature(activity, ScriptPluginFeature.ID) }
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

private fun Context.findActivity(): Activity? {
    var current: Context? = this
    while (current is ContextWrapper) {
        if (current is Activity) return current
        current = current.baseContext
    }
    return null
}
