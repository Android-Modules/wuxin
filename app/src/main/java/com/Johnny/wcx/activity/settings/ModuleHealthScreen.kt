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
import com.Johnny.wcx.features.core.FeaturesProvider
import com.Johnny.wcx.preferences.WePrefs
import com.Johnny.wcx.utils.reflection.ClassLoaders
import com.Johnny.wcx.utils.android.showToast
import h.Hchat.hooks.api.core.WeChatApis
import h.Hchat.hooks.core.DexInstallScheduler
import h.Hchat.ui.miuix.ActionRow
import h.Hchat.ui.miuix.InsetDivider
import h.Hchat.ui.miuix.PageScaffold
import h.Hchat.ui.miuix.SettingsCard
import top.yukonga.miuix.kmp.basic.BasicComponentDefaults
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text

/**
 * 模块自检（最外层）：检测整个模块的公共依赖是否就绪——微信公共 API 层、Hook 安装任务、
 * 关键锚点——以及各功能分类的启用概况。用于快速判断「哪些功能可能失效」。
 */
@Composable
fun ModuleHealthScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var refreshVersion by remember { mutableStateOf(0) }
    val rows = remember(refreshVersion) {
        runCatching { collectModuleHealth() }.getOrDefault(listOf("检测失败" to "请重试"))
    }
    val scrollBehavior = MiuixScrollBehavior()

    PageScaffold(
        title = "模块自检",
        largeTitle = "模块自检",
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
            item { SmallTitle(modifier = Modifier.padding(top = 6.dp), text = "公共依赖与锚点") }
            item {
                SettingsCard {
                    rows.forEachIndexed { index, row ->
                        if (index > 0) InsetDivider()
                        ModuleHealthRow(row.first, row.second)
                    }
                }
            }
            item { SmallTitle(modifier = Modifier.padding(top = 10.dp), text = "操作") }
            item {
                SettingsCard {
                    ActionRow("重新检测", "重新读取模块公共依赖状态") {
                        refreshVersion++
                        showToast("已重新检测")
                    }
                    InsetDivider()
                    ActionRow("复制检测结果", "复制到剪贴板，便于反馈") {
                        val text = rows.joinToString("\n") { "${it.first}：${it.second}" }
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                        clipboard?.setPrimaryClip(ClipData.newPlainText("WCX 模块自检", text))
                        showToast("检测结果已复制")
                    }
                }
            }
            item {
                Text(
                    text = "说明：本页检测模块级公共依赖（微信 API / Hook 安装任务 / 关键锚点）与各分类启用情况。" +
                        "标 ❌ 的锚点或未装成的 Hook 任务，通常就是对应功能失效的原因。",
                    fontSize = 12.sp,
                    color = BasicComponentDefaults.summaryColor().color,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                )
            }
        }
    }
}

/** 采集模块级健康信息（label -> value）。 */
private fun collectModuleHealth(): List<Pair<String, String>> {
    val rows = mutableListOf<Pair<String, String>>()

    // —— 功能分类概况 ——
    val items = FeaturesProvider.ALL_HOOK_ITEMS
    rows += "功能总数" to "${items.size} 个"
    items.groupBy { it.categories.firstOrNull() ?: "未分类" }
        .toSortedMap()
        .forEach { (category, list) ->
            val on = list.count { WePrefs.getBoolOrFalse(it.name) }
            rows += "分类·$category" to "${list.size} 个，已启用 $on"
        }

    // —— 微信公共 API 层 ——
    rows += "微信 API·数据库" to apiState(runCatching { WeChatApis.database() }.getOrNull())
    rows += "微信 API·账号" to apiState(runCatching { WeChatApis.account() }.getOrNull())
    rows += "微信 API·联系人" to apiState(runCatching { WeChatApis.contacts() }.getOrNull())
    rows += "微信 API·会话" to apiState(runCatching { WeChatApis.conversations() }.getOrNull())

    // —— Hook 安装任务 ——
    val total = runCatching { DexInstallScheduler.snapshot().size }.getOrDefault(0)
    val pending = runCatching { DexInstallScheduler.pendingCount() }.getOrDefault(0)
    val failed = runCatching { DexInstallScheduler.failedCount() }.getOrDefault(0)
    rows += "Hook 安装任务" to "共 $total 个，待装 $pending，失败暂停 $failed"
    runCatching { DexInstallScheduler.snapshot() }.getOrDefault(emptyList())
        .filter { !it.installed }.take(8).forEach { task ->
            rows += ("      · " + task.label) to task.summary
        }
    rows += "就绪阶段" to runCatching { DexInstallScheduler.readyLevelName() }.getOrDefault("未知")

    // —— 关键锚点（微信内部类，均在本项目代码中被真实引用）——
    rows += "锚点·聊天页 ChattingUI" to probeClass("com.tencent.mm.ui.chatting.ChattingUI")
    rows += "锚点·资料页 ContactInfoUI" to probeClass("com.tencent.mm.plugin.profile.ui.ContactInfoUI")
    rows += "锚点·通讯录 AddressLiveList" to probeClass("com.tencent.mm.ui.contact.address.AddressLiveList")
    rows += "锚点·朋友圈评论 SnsCommentFooter" to probeClass("com.tencent.mm.plugin.sns.ui.SnsCommentFooter")
    rows += "锚点·主界面 LauncherUI" to probeClass("com.tencent.mm.ui.LauncherUI")
    rows += "锚点·wcdb SQLiteDatabase" to probeClass("com.tencent.wcdb.database.SQLiteDatabase")

    return rows
}

private fun apiState(api: Any?): String =
    if (api != null) "已解析" else "未解析（DexKit 未完成或当前微信版本未适配）"

private fun probeClass(className: String): String = runCatching {
    Class.forName(className, false, ClassLoaders.MODULE)
}.isSuccess.let { if (it) "已找到" else "未找到（该功能可能失效）" }

@Composable
private fun ModuleHealthRow(label: String, value: String) {
    val warn = value.contains("未找到") || value.contains("未解析") ||
        value.contains("失败") || value.contains("待装") && !value.startsWith("共 0")
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
