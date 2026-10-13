package com.Johnny.wcx.activity.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Johnny.wcx.features.items.secret_friend.SecretFriendDiagnostics
import com.Johnny.wcx.utils.android.showToast
import h.Hchat.ui.miuix.ActionRow
import h.Hchat.ui.miuix.InsetDivider
import h.Hchat.ui.miuix.PageScaffold
import h.Hchat.ui.miuix.SettingsCard
import top.yukonga.miuix.kmp.basic.BasicComponentDefaults
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text

/**
 * 密友功能诊断页：逐面显示开关状态与关键锚点是否解析成功，
 * 一眼区分「开关没开」与「当前微信版本锚点漂移」。
 */
@Composable
fun SecretFriendDiagnosticsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var refreshVersion by remember { mutableStateOf(0) }
    val rows = remember(refreshVersion) {
        runCatching { SecretFriendDiagnostics.collectRows() }.getOrDefault(emptyList())
    }
    val scrollBehavior = MiuixScrollBehavior()

    PageScaffold(
        title = "密友功能诊断",
        largeTitle = "密友功能诊断",
        scrollBehavior = scrollBehavior,
        onBack = onBack,
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = padding.calculateTopPadding() + 8.dp,
                bottom = padding.calculateBottomPadding() + 84.dp,
            ),
        ) {
            item { SmallTitle(modifier = Modifier.padding(top = 6.dp), text = "生效状态与锚点自检") }
            item {
                SettingsCard {
                    rows.forEachIndexed { index, row ->
                        if (index > 0) InsetDivider()
                        SecretDiagRow(row.first, row.second)
                    }
                }
            }
            item { SmallTitle(modifier = Modifier.padding(top = 10.dp), text = "操作") }
            item {
                SettingsCard {
                    ActionRow("刷新诊断", "重新读取当前状态") {
                        refreshVersion++
                        showToast("密友诊断已刷新")
                    }
                    InsetDivider()
                    ActionRow("复制诊断信息", "复制到剪贴板，便于反馈") {
                        val text = rows.joinToString("\n") { "${it.first}：${it.second}" }
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                        clipboard?.setPrimaryClip(ClipData.newPlainText("WCX 密友诊断", text))
                        showToast("诊断信息已复制")
                    }
                }
            }
            item {
                Text(
                    text = "说明：若某隐藏面显示「开启」却仍能看到密友，多为该面依赖的类/方法在当前微信版本已漂移" +
                        "（标「未找到」的锚点）。把本页截图反馈即可定位。",
                    fontSize = 12.sp,
                    color = BasicComponentDefaults.summaryColor().color,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                )
            }
        }
    }
}

@Composable
private fun SecretDiagRow(label: String, value: String) {
    val warn = value.contains("未找到") || value.contains("关闭") ||
        value.contains("失效") || value.contains("生效中") || value.contains("异常") ||
        value.contains("为空")
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = BasicComponentDefaults.titleColor().color,
            modifier = Modifier.fillMaxWidth(0.46f),
        )
        Text(
            text = value,
            fontSize = 12.sp,
            color = if (warn) Color(0xFFD93025) else BasicComponentDefaults.summaryColor().color,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
