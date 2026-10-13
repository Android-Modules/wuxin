package com.Johnny.wcx.activity.settings

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Johnny.wcx.features.api.core.WeConversationApi
import com.Johnny.wcx.features.core.FeaturesProvider
import com.Johnny.wcx.features.hchat.HchatScriptBootstrap
import com.Johnny.wcx.features.items.secret_friend.HideConversations
import com.Johnny.wcx.preferences.WePrefs
import com.Johnny.wcx.utils.android.showToast
import com.Johnny.wcx.utils.reflection.ClassLoaders
import h.Hchat.hooks.api.core.WeChatApis
import h.Hchat.hooks.core.DexInstallScheduler
import h.Hchat.hooks.core.HookRegistry
import h.Hchat.hooks.items.script.ScriptPluginRuntime
import h.Hchat.hooks.items.script.ScriptPluginSettings
import h.Hchat.preferences.HchatStorage
import h.Hchat.ui.miuix.ActionRow
import h.Hchat.ui.miuix.InsetDivider
import h.Hchat.ui.miuix.PageScaffold
import h.Hchat.ui.miuix.SettingsCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.BasicComponentDefaults
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 模块自检（最外层）：把整个模块的公共依赖（微信 API 层 / 脚本运行时 / Hook 安装任务 / 关键类锚点）
 * 与各功能分类启用概况逐项扫描出来，并支持手动「开始检测」与「一键修复」。
 *
 * - 进入页面自动检测一次（静默填充）；
 * - 手动「开始检测」带进度条逐项推进；
 * - 每一项都可点开查看详情（含义 + 恢复建议）；
 * - 「一键修复」重建脚本引导链路 / 推进就绪阶段 / 重排 Hook 任务 / 重载插件 / 密友对账。
 */
@Composable
fun ModuleHealthScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollBehavior = MiuixScrollBehavior()

    var items by remember { mutableStateOf(listOf<HealthItem>()) }
    var lastScan by remember { mutableStateOf("尚未检测") }
    var running by remember { mutableStateOf(false) }
    var runTitle by remember { mutableStateOf("") }
    var runProgress by remember { mutableFloatStateOf(0f) }
    var runSubtitle by remember { mutableStateOf("") }
    var runLog by remember { mutableStateOf(listOf<Pair<String, Boolean>>()) }

    fun launchRun(
        title: String,
        stepsProvider: () -> List<HealthStep>,
        showOverlay: Boolean,
        reportTitle: String?,
        replaceItems: Boolean,
    ) {
        if (running) return
        if (showOverlay) {
            running = true
            runTitle = title
            runProgress = 0f
            runSubtitle = "准备中…"
            runLog = emptyList()
        }
        scope.launch {
            val steps = runCatching { stepsProvider() }.getOrElse { emptyList() }
            val collected = mutableListOf<HealthItem>()
            steps.forEachIndexed { index, step ->
                if (showOverlay) runSubtitle = "正在检测：${step.title}"
                val outcome = withContext(Dispatchers.IO) {
                    runCatching { step.run() }.getOrElse { error ->
                        listOf(
                            HealthItem(
                                label = step.title,
                                section = step.title,
                                value = "检测异常",
                                ok = false,
                                detail = "执行「${step.title}」时抛出异常：\n" + (error.toString()),
                            )
                        )
                    }
                }
                collected += outcome
                if (showOverlay) {
                    runLog = runLog + (step.title to outcome.all { it.ok })
                    runProgress = (index + 1).toFloat() / steps.size.toFloat().coerceAtLeast(1f)
                    delay(120)
                }
            }
            if (replaceItems) items = collected
            running = false
            lastScan = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            if (reportTitle != null) showReportDialog(context, reportTitle, collected)
        }
    }

    // 进入页面自动静默检测一次
    LaunchedEffect(Unit) {
        launchRun("模块自检", { moduleHealthSteps(context) }, showOverlay = false, reportTitle = null, replaceItems = true)
    }

    fun openDetail(item: HealthItem) {
        runCatching {
            AlertDialog.Builder(context)
                .setTitle(item.label)
                .setMessage("状态：${item.value}\n\n${item.detail}")
                .setPositiveButton("知道了", null)
                .show()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                item {
                    Text(
                        text = "最后检测：$lastScan　·　点击任意一项可查看详情",
                        fontSize = 12.sp,
                        color = BasicComponentDefaults.summaryColor().color,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }

                val grouped = items.groupBy { it.section }
                grouped.forEach { (section, list) ->
                    item { SmallTitle(modifier = Modifier.padding(top = 6.dp), text = section) }
                    item {
                        SettingsCard {
                            list.forEachIndexed { index, item ->
                                if (index > 0) InsetDivider()
                                HealthRow(item) { openDetail(item) }
                            }
                        }
                    }
                }

                item { SmallTitle(modifier = Modifier.padding(top = 10.dp), text = "检测与修复") }
                item {
                    SettingsCard {
                        ActionRow("开始检测", "逐项扫描模块公共依赖与锚点，带进度条") {
                            launchRun("模块自检", { moduleHealthSteps(context) }, showOverlay = true, reportTitle = "模块自检结果", replaceItems = true)
                        }
                        InsetDivider()
                        ActionRow("一键修复", "重建脚本引导链路 / 推进就绪阶段 / 重排 Hook 任务 / 重载插件 / 密友对账") {
                            launchRun("一键修复", { moduleRepairSteps(context) }, showOverlay = true, reportTitle = "一键修复结果", replaceItems = false)
                        }
                        InsetDivider()
                        ActionRow("复制检测结果", "复制到剪贴板，便于反馈") {
                            val text = if (items.isEmpty()) {
                                "（尚无检测结果）"
                            } else {
                                items.joinToString("\n") { "${it.label}：${it.value}" }
                            }
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                            clipboard?.setPrimaryClip(ClipData.newPlainText("WCX 模块自检", text))
                            showToast("检测结果已复制")
                        }
                    }
                }

                item {
                    Text(
                        text = "说明：本页检测模块级公共依赖（微信 API / 脚本运行时 / Hook 安装任务 / 关键类锚点）" +
                            "与各分类启用情况。标红或标 ❌ 的项，通常就是对应功能失效的原因；点开每一项可看含义与恢复建议。",
                        fontSize = 12.sp,
                        color = BasicComponentDefaults.summaryColor().color,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                    )
                }
            }
        }

        if (running) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xCC000000)),
                contentAlignment = Alignment.Center,
            ) {
                Box(modifier = Modifier.fillMaxWidth(0.88f).padding(16.dp)) {
                    SettingsCard {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = runTitle,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = MiuixTheme.colorScheme.onSurface,
                            )
                            Spacer(Modifier.height(14.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MiuixTheme.colorScheme.onSurface.copy(alpha = 0.12f)),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(runProgress.coerceIn(0f, 1f))
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFF2E7DFF)),
                                )
                            }
                            Spacer(Modifier.height(10.dp))
                            Text(
                                text = "${(runProgress * 100).toInt()}%  $runSubtitle",
                                fontSize = 12.sp,
                                color = BasicComponentDefaults.summaryColor().color,
                            )
                            Spacer(Modifier.height(12.dp))
                            runLog.takeLast(6).forEach { entry ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (entry.second) "✅ " else "❌ ",
                                        fontSize = 12.sp,
                                    )
                                    Text(
                                        text = entry.first,
                                        fontSize = 12.sp,
                                        color = if (entry.second) BasicComponentDefaults.summaryColor().color
                                        else Color(0xFFD93025),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
//  数据模型
// ---------------------------------------------------------------------------

/** 一个检测项。[section] 用于分组显示；[detail] 是点开后的含义 + 恢复建议。 */
private data class HealthItem(
    val label: String,
    val section: String,
    val value: String,
    val ok: Boolean,
    val detail: String,
)

/** 一个检测步骤：产出一批 [HealthItem]。 */
private class HealthStep(val title: String, val run: () -> List<HealthItem>)

@Composable
private fun HealthRow(item: HealthItem, onOpenDetail: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenDetail() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = item.label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = BasicComponentDefaults.titleColor().color,
            modifier = Modifier.fillMaxWidth(0.46f),
        )
        Text(
            text = item.value,
            fontSize = 12.sp,
            color = if (item.ok) BasicComponentDefaults.summaryColor().color else Color(0xFFD93025),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

private fun showReportDialog(context: Context, title: String, items: List<HealthItem>) {
    val okCount = items.count { it.ok }
    val sb = StringBuilder()
    sb.append("共 ").append(items.size).append(" 项，通过 ").append(okCount)
        .append("，问题 ").append(items.size - okCount).append("。\n\n")
    items.forEach { item ->
        sb.append(if (item.ok) "✅ " else "❌ ").append(item.label).append("：").append(item.value).append('\n')
    }
    runCatching {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(sb.toString())
            .setPositiveButton("知道了", null)
            .show()
    }
}

// ---------------------------------------------------------------------------
//  检测步骤
// ---------------------------------------------------------------------------

private fun moduleHealthSteps(context: Context): List<HealthStep> = listOf(
    HealthStep("功能清单与分类启用概况") { featureOverview() },
    HealthStep("微信公共 API 层") { apiLayer() },
    HealthStep("脚本运行时") { scriptRuntime(context) },
    HealthStep("Hook 安装任务与就绪阶段") { hookTasks() },
    HealthStep("关键类锚点") { anchors() },
)

private fun featureOverview(): List<HealthItem> {
    val items = FeaturesProvider.ALL_HOOK_ITEMS
    val out = mutableListOf<HealthItem>()
    out += HealthItem(
        "功能总数", "功能清单与分类启用概况", "${items.size} 个", true,
        "模块共注册 ${items.size} 个功能项（含各类开关与可点击功能）。",
    )
    items.groupBy { it.categories.firstOrNull() ?: "未分类" }
        .toSortedMap()
        .forEach { (category, list) ->
            val on = list.count { WePrefs.getBoolOrFalse(it.name) }
            val detail = buildString {
                append("该分类共 ${list.size} 个功能，当前已启用 $on 个。")
                if (on == 0) append("\n该分类目前没有任何功能被启用——若你期望其中某功能生效，请到「功能」页对应分类手动开启。")
            }
            // 分类启用数为 0 不代表失败，仅作信息展示，不标红。
            out += HealthItem("分类·$category", "功能清单与分类启用概况", "${list.size} 个，已启用 $on", true, detail)
        }
    return out
}

private fun apiLayer(): List<HealthItem> {
    val section = "微信公共 API 层"
    return listOf(
        apiItem(section, "微信 API·数据库", runCatching { WeChatApis.database() }.getOrNull()),
        apiItem(section, "微信 API·账号", runCatching { WeChatApis.account() }.getOrNull()),
        apiItem(section, "微信 API·联系人", runCatching { WeChatApis.contacts() }.getOrNull()),
        apiItem(section, "微信 API·会话", runCatching { WeChatApis.conversations() }.getOrNull()),
    )
}

private fun apiItem(section: String, label: String, obj: Any?): HealthItem =
    if (obj != null) {
        HealthItem(label, section, "已解析", true, "已成功取得该 API 对象：${obj.javaClass.name}。")
    } else {
        HealthItem(
            label, section, "未解析", false,
            "未取得该 API 对象。\n通常是 DexKit 预热未完成，或当前微信版本未适配。\n可尝试「一键修复」，或在脚本诊断页点「重新初始化脚本子系统」后重启微信。",
        )
    }

private fun scriptRuntime(context: Context): List<HealthItem> {
    val section = "脚本运行时"
    val out = mutableListOf<HealthItem>()

    val readinessDetail = runCatching { ScriptPluginRuntime.scriptApiReadinessDetail() }.getOrNull()
    val ready = readinessDetail == null
    out += HealthItem(
        "脚本运行时就绪", section,
        if (ready) "已就绪" else "未就绪",
        ready,
        if (ready) {
            "脚本运行时依赖的微信数据库（rcontact/chatroom）可读，插件可正常加载。"
        } else {
            "未就绪原因：$readinessDetail\n这会导致「插件总开关」打不开、插件全部加载失败。\n点「一键修复」重建引导链路可尝试恢复。"
        },
    )

    val globalOn = runCatching {
        HchatStorage.preferences(context, ScriptPluginSettings.PREFS_NAME)
            .getBoolean(ScriptPluginSettings.KEY_ENABLE, ScriptPluginSettings.DEFAULT_ENABLE)
    }.getOrDefault(false)
    out += HealthItem(
        "插件总开关", section,
        if (globalOn) "已开启" else "已关闭",
        globalOn,
        if (globalOn) "插件总开关已开启，启动时会自动加载已启用的插件。"
        else "插件总开关未开启，所有脚本插件都不会工作。可在「脚本」页打开，或点「一键修复」。",
    )

    val hookCount = runCatching { HookRegistry.get().size }.getOrDefault(0)
    out += HealthItem(
        "脚本 Hook 注册", section,
        "$hookCount 个", hookCount > 0,
        if (hookCount > 0) "已注册 $hookCount 个 Hook（脚本的消息/菜单等监听）。"
        else "0 个：脚本引导链路可能未完成。点「一键修复」重建引导链路。",
    )

    val plugins = runCatching { ScriptPluginRuntime.listPlugins(context) }.getOrDefault(emptyList())
    val enabledPlugins = plugins.count {
        runCatching { ScriptPluginRuntime.isPluginEnabled(context, it.id) }.getOrDefault(false)
    }
    val pluginsOk = plugins.isEmpty() || enabledPlugins > 0
    out += HealthItem(
        "本地插件", section,
        "已装 ${plugins.size} 个，已启用 $enabledPlugins 个", pluginsOk,
        when {
            plugins.isEmpty() -> "尚未安装任何脚本插件。可到「脚本」页的在线插件市场安装。"
            enabledPlugins == 0 -> "已安装 ${plugins.size} 个插件，但一个都没启用——请在「脚本」页逐个开启。"
            else -> "已安装 ${plugins.size} 个插件，已启用 $enabledPlugins 个。"
        },
    )

    val loadErrors = collectPluginLoadErrors(context)
    out += HealthItem(
        "插件加载错误", section,
        if (loadErrors.isEmpty()) "无" else loadErrors.joinToString(" ｜ "),
        loadErrors.isEmpty(),
        if (loadErrors.isEmpty()) "最近一次插件加载没有报错。"
        else "以下插件最近一次加载失败：\n" + loadErrors.joinToString("\n") + "\n可到「脚本」页查看插件详情或重装。",
    )

    return out
}

private fun hookTasks(): List<HealthItem> {
    val section = "Hook 安装任务与就绪阶段"
    val snapshot = runCatching { DexInstallScheduler.snapshot() }.getOrDefault(emptyList())
    val pending = snapshot.count { !it.installed }
    val out = mutableListOf<HealthItem>()
    out += HealthItem(
        "Hook 安装任务", section,
        "共 ${snapshot.size} 个，待装 $pending", pending == 0,
        "模块共有 ${snapshot.size} 个需要 DexKit 解析后安装的 Hook 任务，当前待装 $pending 个。\n待装/失败的任务会让对应功能暂时失效，点「一键修复」可重排重试。",
    )
    snapshot.forEach { task ->
        val ok = task.installed
        val value = if (ok) "✅ 已安装" else "⚠ ${if (task.paused) "已暂停" else "待重试"}（第 ${task.retryIndex} 次）"
        out += HealthItem(
            "· ${task.label}", section, value, ok,
            "key=${task.key}\n阶段=${task.stage}\n运行中=${task.running}\n已暂停=${task.paused}\n重试次数=${task.retryIndex}",
        )
    }
    val level = runCatching { DexInstallScheduler.readyLevelName() }.getOrDefault("未知")
    out += HealthItem(
        "就绪阶段", section, level, level.contains("WARMUP"),
        "阶段顺序：EARLY < BRIDGE < WARMUP。\n只有推进到 WARMUP 后，脚本级 Hook（菜单/消息监听等）才会执行。当前为 $level。",
    )
    return out
}

private fun anchors(): List<HealthItem> = listOf(
    anchorItem("聊天页 ChattingUI", "com.tencent.mm.ui.chatting.ChattingUI"),
    anchorItem("资料页 ContactInfoUI", "com.tencent.mm.plugin.profile.ui.ContactInfoUI"),
    anchorItem("通讯录 AddressLiveList", "com.tencent.mm.ui.contact.address.AddressLiveList"),
    anchorItem("朋友圈评论 SnsCommentFooter", "com.tencent.mm.plugin.sns.ui.SnsCommentFooter"),
    anchorItem("主界面 LauncherUI", "com.tencent.mm.ui.LauncherUI"),
    anchorItem("wcdb SQLiteDatabase", "com.tencent.wcdb.database.SQLiteDatabase"),
    anchorItem("wcdb compat SQLiteDatabase", "com.tencent.wcdb.compat.SQLiteDatabase"),
)

private fun anchorItem(name: String, className: String): HealthItem {
    val found = runCatching { Class.forName(className, false, ClassLoaders.MODULE) }.isSuccess
    return HealthItem(
        "锚点·$name", "关键类锚点",
        if (found) "已找到" else "未找到",
        found,
        "类 $className\n" + if (found) {
            "在当前微信版本中存在，依赖它的功能有锚点可用。"
        } else {
            "未找到——依赖它的功能在当前微信版本可能已漂移/失效，需要适配。"
        },
    )
}

// ---------------------------------------------------------------------------
//  修复步骤
// ---------------------------------------------------------------------------

private fun moduleRepairSteps(context: Context): List<HealthStep> = listOf(
    HealthStep("重建脚本引导链路") {
        val lines = runCatching { HchatScriptBootstrap.reinitializeFully() }
            .getOrElse { listOf("异常：" + (it.message ?: it.javaClass.simpleName)) }
        listOf(
            HealthItem(
                "引导链路", "重建脚本引导链路", lines.joinToString("；"), true,
                lines.joinToString("\n"),
            )
        )
    },
    HealthStep("推进就绪阶段并重排全部 Hook 任务") {
        DexInstallScheduler.ensureWarmupReady()
        DexInstallScheduler.retryAllPending()
        listOf(
            HealthItem(
                "Hook 任务", "推进就绪阶段并重排全部 Hook 任务",
                "阶段=" + DexInstallScheduler.readyLevelName() + "，待装 " + DexInstallScheduler.pendingCount() + " 个", true,
                "已强制推进到 WARMUP，并把所有未装任务的计数清零后重排重试。",
            )
        )
    },
    HealthStep("重新加载全部已启用插件") {
        val plugins = runCatching { ScriptPluginRuntime.listPlugins(context) }.getOrDefault(emptyList())
        val enabled = plugins.filter {
            runCatching { ScriptPluginRuntime.isPluginEnabled(context, it.id) }.getOrDefault(false)
        }
        var ok = 0
        enabled.forEach { plugin ->
            runCatching { ScriptPluginRuntime.reloadPluginAsync(context, plugin.id) }.onSuccess { ok++ }
        }
        listOf(
            HealthItem(
                "已启用插件", "重新加载全部已启用插件",
                "已重载 $ok/${enabled.size} 个", true,
                "重新加载了所有已启用的脚本插件。",
            )
        )
    },
    HealthStep("密友隐藏面重新对账") {
        val ok1 = runCatching { HideConversations.reconcileOnListChange() }.isSuccess
        val ok2 = runCatching { WeConversationApi.reloadConversations() }.isSuccess
        listOf(
            HealthItem(
                "密友名单", "密友隐藏面重新对账",
                "对账=${if (ok1) "成功" else "失败"}，刷新会话=${if (ok2) "成功" else "失败"}", ok1,
                "按当前密友名单重新隐藏/恢复会话行，并刷新会话列表。",
            )
        )
    },
)

/** 采集各插件最近一次加载失败原因（读插件目录 log.txt 尾部）。 */
private fun collectPluginLoadErrors(context: Context): List<String> {
    val plugins = runCatching { ScriptPluginRuntime.listPlugins(context) }.getOrDefault(emptyList())
    return runCatching {
        plugins.mapNotNull { plugin ->
            val message = runCatching {
                val logFile = java.io.File(plugin.dir, "log.txt")
                if (!logFile.isFile) {
                    null
                } else {
                    val lines = logFile.readLines().takeLast(120)
                    val index = lines.indexOfLast { it.contains("ERROR") }
                    val okIndex = lines.indexOfLast { it.contains("插件加载成功") }
                    if (index < 0 || index < okIndex) {
                        null
                    } else {
                        val head = lines[index].substringAfter("ERROR", lines[index]).trim()
                        val detail = lines.getOrNull(index + 1)?.trim().orEmpty()
                        "$head $detail".trim()
                    }
                }
            }.getOrNull()
            message?.takeIf { it.isNotBlank() }?.let { "${plugin.displayName ?: plugin.id}: $it" }
        }
    }.getOrDefault(emptyList())
}
