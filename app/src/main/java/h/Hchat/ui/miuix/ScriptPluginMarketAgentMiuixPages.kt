package h.Hchat.ui.miuix

import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.ContextWrapper
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.provider.Settings
import android.view.WindowManager
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.SideEffect
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import h.Hchat.hooks.api.core.WeChatApis
import h.Hchat.hooks.items.script.ScriptPluginRuntime
import h.Hchat.hooks.items.script.market.PluginMarketHistoryVersion
import h.Hchat.hooks.items.script.market.PluginMarketInstaller
import h.Hchat.hooks.items.script.market.PluginMarketException
import h.Hchat.hooks.items.script.market.PluginMarketFile
import h.Hchat.hooks.items.script.market.PluginMarketComment
import h.Hchat.hooks.items.script.market.PluginMarketNotification
import h.Hchat.hooks.items.script.market.PluginMarketPlugin
import h.Hchat.hooks.items.script.market.PluginMarketRepository
import h.Hchat.hooks.items.script.market.PluginMarketReviewStatus
import h.Hchat.hooks.items.script.market.PluginMarketUserIdentity
import h.Hchat.hooks.items.script.agent.CodexSettings
import h.Hchat.hooks.items.script.agent.ScriptPluginAgentClient
import h.Hchat.hooks.items.script.agent.ScriptPluginAgentAttachment
import h.Hchat.hooks.items.script.agent.ScriptPluginAgentChatMessage
import h.Hchat.hooks.items.script.agent.ScriptPluginAgentConfig
import h.Hchat.hooks.items.script.agent.ScriptPluginAgentCancellation
import h.Hchat.hooks.items.script.agent.ScriptPluginAgentContext
import h.Hchat.hooks.items.script.agent.ScriptPluginAgentDiff
import h.Hchat.hooks.items.script.agent.ScriptPluginAgentDraft
import h.Hchat.hooks.items.script.agent.ScriptPluginAgentEventIds
import h.Hchat.hooks.items.script.agent.ScriptPluginAgentLocalFiles
import h.Hchat.hooks.items.script.agent.ScriptPluginAgentMcpServer
import h.Hchat.hooks.items.script.agent.ScriptPluginAgentReasoning
import h.Hchat.hooks.items.script.agent.ScriptPluginAgentRequest
import h.Hchat.hooks.items.script.agent.ScriptPluginAgentResumeState
import h.Hchat.hooks.items.script.agent.ScriptPluginAgentSession
import h.Hchat.hooks.items.script.agent.ScriptPluginAgentSessionStore
import h.Hchat.hooks.items.script.agent.ScriptPluginAgentSettings
import h.Hchat.hooks.items.script.agent.ScriptPluginAgentProfile
import h.Hchat.hooks.items.script.agent.ScriptPluginAgentQuotedMessage
import h.Hchat.hooks.items.script.agent.ScriptPluginAgentSpeech
import h.Hchat.hooks.items.script.agent.ScriptPluginAgentTextMerge
import h.Hchat.hooks.items.script.agent.ScriptPluginAgentToolEvent
import h.Hchat.hooks.items.script.agent.ScriptPluginAgentToolResultStore
import h.Hchat.hooks.items.script.agent.ScriptPluginAgentValidator
import h.Hchat.hooks.items.script.agent.ScriptPluginAgentWorkspaceChange
import h.Hchat.hooks.items.script.agent.ScriptPluginAgentWorkspaceToolConfirmation
import h.Hchat.hooks.items.script.agent.ScriptPluginAgentWorkspaceWriteDecision
import h.Hchat.hooks.items.script.agent.ScriptPluginAgentWorkspaceTools
import h.Hchat.hooks.items.script.agent.ScriptPluginAgentWriter
import h.Hchat.preferences.HchatStorage
import h.Hchat.ui.FeatureSettingsProvider
import org.json.JSONObject
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.PullToRefresh
import top.yukonga.miuix.kmp.basic.TabRow
import top.yukonga.miuix.kmp.basic.CircularProgressIndicator
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.window.WindowDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.io.File
import java.net.URL
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.LinkedHashSet
import java.util.Locale
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.abs
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.RowScope
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.BasicComponentDefaults
import android.content.Intent
import android.net.Uri


private object PluginMarketUi {

private enum class PluginMarketUploadPhase {
    QUEUED,
    UPLOADING,
    SUCCESS,
    PENDING_REVIEW,
    FAILED
}

private data class PluginMarketUploadStatus(
    val phase: PluginMarketUploadPhase,
    val message: String
)

@Composable
fun ScriptPluginMarketPage(
    context: Context,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val scrollBehavior = MiuixScrollBehavior()
    var query by rememberSaveable { mutableStateOf("") }
    var sort by rememberSaveable { mutableStateOf("latest") }
    var refreshVersion by remember { mutableStateOf(0) }
    var loading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf("") }
    var plugins by remember { mutableStateOf<List<PluginMarketPlugin>>(emptyList()) }
    var pluginCount by remember { mutableStateOf(0) }
    var selectedPlugin by remember { mutableStateOf<PluginMarketPlugin?>(null) }
    var detailPlugin by remember { mutableStateOf<PluginMarketPlugin?>(null) }
    var detailLoading by remember { mutableStateOf(false) }
    var detailError by remember { mutableStateOf("") }
    var detailRetryVersion by remember { mutableStateOf(0) }
    var historyVersions by remember { mutableStateOf<List<PluginMarketHistoryVersion>>(emptyList()) }
    var historyLoading by remember { mutableStateOf(false) }
    var historyError by remember { mutableStateOf("") }
    var historyInstallVersionId by remember { mutableStateOf<String?>(null) }
    var comments by remember { mutableStateOf<List<PluginMarketComment>>(emptyList()) }
    var commentsLoading by remember { mutableStateOf(false) }
    var commentsError by remember { mutableStateOf("") }
    var commentsRetryVersion by remember { mutableStateOf(0) }
    var currentUserIdentity by remember { mutableStateOf<PluginMarketUserIdentity?>(null) }
    var interactionError by remember { mutableStateOf("") }
    var likedByCurrentUser by remember { mutableStateOf(false) }
    var followingByCurrentUser by remember { mutableStateOf(false) }
    var likeCountLoaded by remember { mutableStateOf(false) }
    var commentCountLoaded by remember { mutableStateOf(false) }
    var updatingLike by remember { mutableStateOf(false) }
    var updatingFollow by remember { mutableStateOf(false) }
    var commentDraft by remember { mutableStateOf("") }
    var submittingComment by remember { mutableStateOf(false) }
    var deletingCommentId by remember { mutableStateOf<String?>(null) }
    var detailTargetExists by remember { mutableStateOf(false) }
    var installingRemoteId by remember { mutableStateOf<String?>(null) }
    var deletingRemoteId by remember { mutableStateOf<String?>(null) }
    var installMessage by remember { mutableStateOf("") }
    var pendingOverwrite by remember { mutableStateOf<PluginMarketPlugin?>(null) }
    var showUploadDialog by remember { mutableStateOf(false) }
    var notifications by remember { mutableStateOf<List<PluginMarketNotification>>(emptyList()) }
    var notificationUnreadCount by remember { mutableStateOf(0L) }
    var notificationsLoading by remember { mutableStateOf(true) }
    var notificationsError by remember { mutableStateOf("") }
    var notificationRefreshVersion by remember { mutableStateOf(0) }
    var showNotifications by remember { mutableStateOf(false) }

    fun updateSocialCounts(
        remotePluginId: String,
        likeCount: Long? = null,
        commentCount: Long? = null
    ) {
        fun updated(plugin: PluginMarketPlugin): PluginMarketPlugin = plugin.copy(
            likeCount = likeCount ?: plugin.likeCount,
            commentCount = commentCount ?: plugin.commentCount
        )
        detailPlugin = detailPlugin?.let { if (it.remotePluginId == remotePluginId) updated(it) else it }
        selectedPlugin = selectedPlugin?.let { if (it.remotePluginId == remotePluginId) updated(it) else it }
        plugins = plugins.map { if (it.remotePluginId == remotePluginId) updated(it) else it }
    }

    LaunchedEffect(query, sort, refreshVersion) {
        delay(250L)
        loading = true
        loadError = ""
        val result = withContext(Dispatchers.IO) {
            PluginMarketRepository.list(
                context = context,
                query = query.trim(),
                sort = sort,
                limit = Int.MAX_VALUE
            )
        }
        result.fold(
            onSuccess = { page ->
                plugins = page.items
                pluginCount = page.count
            },
            onFailure = { error ->
                loadError = pluginMarketErrorMessage(error)
                if (plugins.isEmpty()) pluginCount = 0
            }
        )
        loading = false
    }

    LaunchedEffect(notificationRefreshVersion) {
        notificationsLoading = true
        notificationsError = ""
        val result = withContext(Dispatchers.IO) {
            PluginMarketRepository.notifications(context, limit = Int.MAX_VALUE)
        }
        result.fold(
            onSuccess = { page ->
                notifications = page.items
                notificationUnreadCount = page.unreadCount
            },
            onFailure = { error ->
                notificationsError = pluginMarketErrorMessage(error)
                if (notifications.isEmpty()) notificationUnreadCount = 0L
            }
        )
        notificationsLoading = false
    }

    LaunchedEffect(showNotifications, notificationsLoading) {
        if (!showNotifications || notificationsLoading) return@LaunchedEffect
        val unreadIds = notifications.filterNot { it.read }.map { it.notificationId }
        if (unreadIds.isEmpty()) return@LaunchedEffect
        val result = withContext(Dispatchers.IO) {
            PluginMarketRepository.markNotificationsRead(context, unreadIds)
        }
        result.onSuccess { readResult ->
            notificationUnreadCount = readResult.unreadCount
            val markedIds = unreadIds.toHashSet()
            notifications = notifications.map { item ->
                if (item.notificationId in markedIds) item.copy(read = true) else item
            }
        }
    }

    LaunchedEffect(selectedPlugin?.remotePluginId, detailRetryVersion) {
        val selected = selectedPlugin ?: return@LaunchedEffect
        detailLoading = true
        detailError = ""
        detailPlugin = null
        historyLoading = true
        historyError = ""
        historyVersions = emptyList()
        detailTargetExists = false
        installMessage = ""
        val (detailResult, historyResult) = withContext(Dispatchers.IO) {
            val loadedDetail = PluginMarketRepository.detail(context, selected.remotePluginId).map { detail ->
                val exists = PluginMarketInstaller.existingLocalPluginId(context, detail) != null
                detail to exists
            }
            loadedDetail to PluginMarketRepository.history(context, selected.remotePluginId)
        }
        detailResult.fold(
            onSuccess = { (detail, exists) ->
                val currentSummary = selectedPlugin?.takeIf {
                    it.remotePluginId == selected.remotePluginId
                }
                detailPlugin = detail.copy(
                    likeCount = if (likeCountLoaded) {
                        currentSummary?.likeCount ?: detail.likeCount
                    } else {
                        detail.likeCount
                    },
                    commentCount = if (commentCountLoaded) {
                        currentSummary?.commentCount ?: detail.commentCount
                    } else {
                        detail.commentCount
                    }
                )
                detailTargetExists = exists
            },
            onFailure = { error ->
                detailError = pluginMarketErrorMessage(error)
                detailTargetExists = false
            }
        )
        historyResult.fold(
            onSuccess = { historyVersions = it },
            onFailure = { historyError = pluginMarketErrorMessage(it) }
        )
        detailLoading = false
        historyLoading = false
    }

    LaunchedEffect(selectedPlugin?.remotePluginId, commentsRetryVersion) {
        val selected = selectedPlugin ?: return@LaunchedEffect
        commentsLoading = true
        commentsError = ""
        interactionError = ""
        comments = emptyList()
        currentUserIdentity = null
        likedByCurrentUser = false
        likeCountLoaded = false
        commentCountLoaded = false
        val identityResult = withContext(Dispatchers.IO) { PluginMarketRepository.currentUserIdentity(context) }
        val commentsResult = withContext(Dispatchers.IO) { PluginMarketRepository.comments(context, selected.remotePluginId) }
        val likedResult = withContext(Dispatchers.IO) { PluginMarketRepository.likeStatus(context, selected.remotePluginId) }
        val followedResult = withContext(Dispatchers.IO) { PluginMarketRepository.followStatus(context, selected.remotePluginId) }
        identityResult.fold(
            onSuccess = { currentUserIdentity = it },
            onFailure = { interactionError = pluginMarketErrorMessage(it) }
        )
        commentsResult.fold(
            onSuccess = { page ->
                comments = page.items
                commentCountLoaded = true
                updateSocialCounts(selected.remotePluginId, commentCount = page.total)
            },
            onFailure = { commentsError = pluginMarketErrorMessage(it) }
        )
        likedResult.fold(
            onSuccess = { likeResult ->
                likedByCurrentUser = likeResult.liked
                likeCountLoaded = true
                updateSocialCounts(selected.remotePluginId, likeCount = likeResult.likeCount)
            },
            onFailure = {
                if (interactionError.isBlank()) interactionError = pluginMarketErrorMessage(it)
            }
        )
        followedResult.fold(
            onSuccess = { followingByCurrentUser = it },
            onFailure = { if (interactionError.isBlank()) interactionError = pluginMarketErrorMessage(it) }
        )
        commentsLoading = false
    }

    fun performInstall(plugin: PluginMarketPlugin, overwrite: Boolean) {
        if (installingRemoteId != null) return
        installingRemoteId = plugin.remotePluginId
        installMessage = ""
        scope.launch {
            val result = withContext(Dispatchers.IO) {
                PluginMarketRepository.install(context, plugin, overwrite)
            }
            result.fold(
                onSuccess = { installed ->
                    detailTargetExists = true
                    installed.downloadCount?.let { count ->
                        detailPlugin = detailPlugin?.copy(downloadCount = count)
                        selectedPlugin = selectedPlugin?.copy(downloadCount = count)
                        plugins = plugins.map { item ->
                            if (item.remotePluginId == plugin.remotePluginId) {
                                item.copy(downloadCount = count)
                            } else {
                                item
                            }
                        }
                    }
                    installMessage = if (installed.replacedExisting) {
                        "更新成功，插件已保持禁用"
                    } else {
                        "安装成功，插件默认禁用"
                    }
                    Toast.makeText(context, installMessage, Toast.LENGTH_SHORT).show()
                },
                onFailure = { error ->
                    installMessage = "安装失败: ${pluginMarketErrorMessage(error)}"
                }
            )
            installingRemoteId = null
        }
    }

    fun requestInstall(plugin: PluginMarketPlugin) {
        if (installingRemoteId != null) return
        installingRemoteId = plugin.remotePluginId
        installMessage = ""
        scope.launch {
            val exists = withContext(Dispatchers.IO) {
                PluginMarketInstaller.existingLocalPluginId(context, plugin) != null
            }
            installingRemoteId = null
            if (exists) {
                pendingOverwrite = plugin
            } else {
                performInstall(plugin, overwrite = false)
            }
        }
    }

    fun requestHistoryInstall(version: PluginMarketHistoryVersion) {
        val selected = selectedPlugin ?: return
        if (historyInstallVersionId != null || installingRemoteId != null) return
        historyInstallVersionId = version.versionId
        scope.launch {
            val result = withContext(Dispatchers.IO) {
                PluginMarketRepository.historyDetail(
                    context,
                    selected.remotePluginId,
                    version.versionId
                )
            }
            historyInstallVersionId = null
            result.fold(
                onSuccess = ::requestInstall,
                onFailure = {
                    Toast.makeText(
                        context,
                        "读取历史版本失败: ${pluginMarketErrorMessage(it)}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
        }
    }

    fun deleteOwnedPlugin(plugin: PluginMarketPlugin) {
        if (deletingRemoteId != null || installingRemoteId != null) return
        deletingRemoteId = plugin.remotePluginId
        scope.launch {
            val result = withContext(Dispatchers.IO) {
                PluginMarketRepository.deleteOwnedRemote(context, plugin.remotePluginId)
            }
            deletingRemoteId = null
            result.fold(
                onSuccess = {
                    selectedPlugin = null
                    detailPlugin = null
                    historyVersions = emptyList()
                    plugins = plugins.filterNot { it.remotePluginId == plugin.remotePluginId }
                    pluginCount = (pluginCount - 1).coerceAtLeast(0)
                    refreshVersion++
                    Toast.makeText(context, "线上插件已删除，本地插件不受影响", Toast.LENGTH_SHORT).show()
                },
                onFailure = { error ->
                    Toast.makeText(
                        context,
                        "删除失败: ${pluginMarketErrorMessage(error)}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
        }
    }

    fun togglePluginLike(plugin: PluginMarketPlugin) {
        if (updatingLike) return
        if (currentUserIdentity == null) {
            Toast.makeText(
                context,
                interactionError.ifBlank { "当前微信账号资料尚未就绪" },
                Toast.LENGTH_LONG
            ).show()
            return
        }
        val removeLike = likedByCurrentUser
        updatingLike = true
        interactionError = ""
        scope.launch {
            val result = withContext(Dispatchers.IO) {
                if (removeLike) {
                    PluginMarketRepository.unlike(context, plugin.remotePluginId)
                } else {
                    PluginMarketRepository.like(context, plugin.remotePluginId)
                }
            }
            result.fold(
                onSuccess = { likeResult ->
                    likedByCurrentUser = likeResult.liked
                    likeCountLoaded = true
                    updateSocialCounts(plugin.remotePluginId, likeCount = likeResult.likeCount)
                },
                onFailure = { error ->
                    interactionError = pluginMarketErrorMessage(error)
                    Toast.makeText(context, "操作失败: $interactionError", Toast.LENGTH_LONG).show()
                }
            )
            updatingLike = false
        }
    }

    fun togglePluginFollow(plugin: PluginMarketPlugin) {
        if (updatingFollow || currentUserIdentity == null) return
        updatingFollow = true
        scope.launch {
            val result = withContext(Dispatchers.IO) {
                PluginMarketRepository.toggleFollow(context, plugin.remotePluginId, followingByCurrentUser)
            }
            result.fold(
                onSuccess = { followingByCurrentUser = it },
                onFailure = { interactionError = pluginMarketErrorMessage(it) }
            )
            updatingFollow = false
        }
    }
    fun submitPluginComment(plugin: PluginMarketPlugin) {
        if (submittingComment) return
        val content = commentDraft.trim()
        if (content.isBlank()) {
            Toast.makeText(context, "评论内容不能为空", Toast.LENGTH_SHORT).show()
            return
        }
        if (currentUserIdentity == null) {
            Toast.makeText(
                context,
                interactionError.ifBlank { "当前微信账号资料尚未就绪" },
                Toast.LENGTH_LONG
            ).show()
            return
        }
        submittingComment = true
        interactionError = ""
        scope.launch {
            val result = withContext(Dispatchers.IO) {
                PluginMarketRepository.addComment(context, plugin.remotePluginId, content)
            }
            result.fold(
                onSuccess = { mutation ->
                    mutation.comment?.let { added ->
                        comments = (listOf(added) + comments.filterNot { it.commentId == added.commentId })
                    }
                    commentDraft = ""
                    commentCountLoaded = true
                    updateSocialCounts(plugin.remotePluginId, commentCount = mutation.commentCount)
                },
                onFailure = { error ->
                    interactionError = pluginMarketErrorMessage(error)
                    Toast.makeText(context, "评论失败: $interactionError", Toast.LENGTH_LONG).show()
                }
            )
            submittingComment = false
        }
    }

    fun submitPluginReply(
        plugin: PluginMarketPlugin,
        parentComment: PluginMarketComment,
        content: String,
        onCompleted: (Boolean) -> Unit
    ) {
        if (submittingComment) {
            onCompleted(false)
            return
        }
        if (currentUserIdentity == null) {
            Toast.makeText(
                context,
                interactionError.ifBlank { "当前微信账号资料尚未就绪" },
                Toast.LENGTH_LONG
            ).show()
            onCompleted(false)
            return
        }
        submittingComment = true
        interactionError = ""
        scope.launch {
            val result = withContext(Dispatchers.IO) {
                PluginMarketRepository.replyComment(
                    context,
                    plugin.remotePluginId,
                    parentComment,
                    content
                )
            }
            var succeeded = false
            result.fold(
                onSuccess = { mutation ->
                    mutation.comment?.let { added ->
                        comments = (listOf(added) + comments.filterNot {
                            it.commentId == added.commentId
                        })
                    }
                    commentCountLoaded = true
                    updateSocialCounts(plugin.remotePluginId, commentCount = mutation.commentCount)
                    succeeded = true
                },
                onFailure = { error ->
                    interactionError = pluginMarketErrorMessage(error)
                    Toast.makeText(context, "回复失败: $interactionError", Toast.LENGTH_LONG).show()
                }
            )
            submittingComment = false
            onCompleted(succeeded)
        }
    }

    fun openNotifications() {
        showNotifications = true
        notificationRefreshVersion++
    }

    fun deletePluginComment(plugin: PluginMarketPlugin, comment: PluginMarketComment) {
        if (deletingCommentId != null) return
        if (!comment.canDelete) {
            Toast.makeText(context, "只能删除自己的评论", Toast.LENGTH_SHORT).show()
            return
        }
        deletingCommentId = comment.commentId
        interactionError = ""
        scope.launch {
            val result = withContext(Dispatchers.IO) {
                PluginMarketRepository.deleteOwnComment(context, plugin.remotePluginId, comment)
            }
            result.fold(
                onSuccess = { mutation ->
                    comments = comments.filterNot { it.commentId == comment.commentId }
                    commentCountLoaded = true
                    updateSocialCounts(plugin.remotePluginId, commentCount = mutation.commentCount)
                    if (mutation.commentCount != comments.size.toLong()) commentsRetryVersion++
                },
                onFailure = { error ->
                    interactionError = pluginMarketErrorMessage(error)
                    Toast.makeText(context, "删除评论失败: $interactionError", Toast.LENGTH_LONG).show()
                }
            )
            deletingCommentId = null
        }
    }

    PageScaffold(
        title = "在线插件",
        largeTitle = "在线插件",
        scrollBehavior = scrollBehavior,
        topBarActions = {
            PluginMarketNotificationBell(
                unreadCount = notificationUnreadCount,
                onClick = ::openNotifications
            )
        },
        bottomBar = {
            BottomActionBar(
                primaryText = "上传本地插件",
                onPrimaryClick = { showUploadDialog = true },
                secondaryText = "返回",
                onSecondaryClick = onBack
            )
        }
    ) { padding ->
        PullToRefresh(
            isRefreshing = loading,
            onRefresh = { if (!loading) refreshVersion++ },
            modifier = Modifier.fillMaxSize(),
            topAppBarScrollBehavior = scrollBehavior,
            refreshTexts = listOf("下拉刷新", "松开刷新", "正在刷新", "刷新完成"),
            contentPadding = PaddingValues(0.dp)
        ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
            state = listState,
            contentPadding = PaddingValues(
                top = padding.calculateTopPadding() + 8.dp,
                bottom = padding.calculateBottomPadding() + 84.dp
            )
        ) {
            item {
                SearchBarSurface(
                    query = query,
                    placeholder = "搜索插件、作者或目录名",
                    modifier = Modifier.padding(horizontal = 12.dp),
                    onQueryChange = { query = it }
                )
            }
            item { SmallTitle(modifier = Modifier.padding(top = 10.dp), text = "浏览") }
            item {
                SettingsCard {
                    PopupChoiceRow(
                        title = "排序方式",
                        summary = if (sort == "downloads") "热门（按下载量）" else "最新发布",
                        options = listOf(
                            PopupChoice("最新", "latest"),
                            PopupChoice("热门", "downloads")
                        ),
                        currentValue = sort,
                        onValueChanged = { sort = it }
                    )
                    InsetDivider()
                    ActionRow(
                        title = "刷新",
                        summary = if (loading) "正在加载在线插件" else "重新获取当前列表"
                    ) {
                        refreshVersion++
                    }
                }
            }
            item {
                SettingsCard(modifier = Modifier.padding(top = 10.dp)) {
                    Text(
                        text = "社区插件由用户上传，安装前请核对作者、说明和文件内容。下载后的插件默认禁用。",
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        fontSize = 13.sp,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp)
                    )
                }
            }
            item {
                SmallTitle(
                    modifier = Modifier.padding(top = 10.dp),
                    text = "在线插件(${if (loading && plugins.isEmpty()) "-" else pluginCount})"
                )
            }
            if (loadError.isNotBlank()) {
                item {
                    SettingsCard {
                        Column {
                            EmptyText(loadError)
                            TextButton(
                                text = "重试",
                                onClick = { refreshVersion++ },
                                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
                                colors = ButtonDefaults.textButtonColorsPrimary()
                            )
                        }
                    }
                }
            } else if (loading && plugins.isEmpty()) {
                item { SettingsCard { EmptyText("正在加载在线插件...") } }
            } else if (plugins.isEmpty()) {
                item {
                    SettingsCard {
                        EmptyText(if (query.isBlank()) "暂无在线插件" else "没有匹配的在线插件")
                    }
                }
            } else {
                item {
                    SettingsCard {
                        plugins.forEachIndexed { index, plugin ->
                            PluginMarketListRow(plugin) {
                                selectedPlugin = plugin
                            }
                            if (index != plugins.lastIndex) InsetDivider()
                        }
                    }
                }
            }
        }
        }
    }

    selectedPlugin?.let { summary ->
        PluginMarketDetailDialog(
            summary = summary,
            detail = detailPlugin,
            loading = detailLoading,
            error = detailError,
            historyVersions = historyVersions,
            historyLoading = historyLoading,
            historyError = historyError,
            historyInstallVersionId = historyInstallVersionId,
            installed = detailTargetExists,
            installing = installingRemoteId == summary.remotePluginId,
            deleting = deletingRemoteId == summary.remotePluginId,
            owned = PluginMarketRepository.ownsRemotePlugin(context, summary.remotePluginId),
            installMessage = installMessage,
            likedByCurrentUser = likedByCurrentUser,
            followingByCurrentUser = followingByCurrentUser,
            updatingLike = updatingLike,
            updatingFollow = updatingFollow,
            comments = comments,
            commentsLoading = commentsLoading,
            commentsError = commentsError,
            currentUserWxId = currentUserIdentity?.wxId.orEmpty(),
            interactionError = interactionError,
            commentDraft = commentDraft,
            submittingComment = submittingComment,
            deletingCommentId = deletingCommentId,
            onRetry = { detailRetryVersion++ },
            onRetryComments = { commentsRetryVersion++ },
            onInstall = ::requestInstall,
            onInstallHistory = ::requestHistoryInstall,
            onToggleLike = { togglePluginLike(summary) },
            onToggleFollow = { togglePluginFollow(summary) },
            onCommentDraftChanged = { value -> commentDraft = value.take(1000) },
            onSubmitComment = { submitPluginComment(summary) },
            onDeleteComment = { comment -> deletePluginComment(summary, comment) },
            onSubmitReply = { comment, content, onCompleted ->
                submitPluginReply(summary, comment, content, onCompleted)
            },
            onDelete = { deleteOwnedPlugin(summary) },
            onDismiss = {
                if (
                    installingRemoteId == null &&
                    deletingRemoteId == null &&
                    !updatingLike &&
                    !submittingComment &&
                    deletingCommentId == null
                ) {
                    selectedPlugin = null
                    detailPlugin = null
                    detailError = ""
                    historyVersions = emptyList()
                    historyError = ""
                    comments = emptyList()
                    commentsError = ""
                    currentUserIdentity = null
                    interactionError = ""
                    commentDraft = ""
                    installMessage = ""
                }
            }
        )
    }

    pendingOverwrite?.let { plugin ->
        WindowDialog(
            show = true,
            title = "覆盖本地插件",
            onDismissRequest = { pendingOverwrite = null },
            content = {
                Column {
                    Text(
                        text = "本地已存在目录“${PluginMarketInstaller.defaultLocalPluginId(plugin)}”。更新会覆盖 main.java、main.java.bshs、info.prop 和 README.md，并保留其它本地配置文件。更新后插件保持禁用。",
                        color = MiuixTheme.colorScheme.onSurface,
                        fontSize = 14.sp
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TextButton(
                            text = "取消",
                            onClick = { pendingOverwrite = null },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColorsPrimary()
                        )
                        TextButton(
                            text = "确认覆盖",
                            onClick = {
                                pendingOverwrite = null
                                performInstall(plugin, overwrite = true)
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColorsPrimary()
                        )
                    }
                }
            }
        )
    }

    if (showUploadDialog) {
        PluginMarketUploadDialog(
            context = context,
            onDismiss = { uploaded ->
                showUploadDialog = false
                if (uploaded) refreshVersion++
            }
        )
    }

    if (showNotifications) {
        PluginMarketNotificationDialog(
            notifications = notifications,
            loading = notificationsLoading,
            error = notificationsError,
            onRetry = { notificationRefreshVersion++ },
            onNotificationClick = { notification ->
                showNotifications = false
                val target = plugins.firstOrNull {
                    it.remotePluginId == notification.remotePluginId
                } ?: PluginMarketPlugin(
                    remotePluginId = notification.remotePluginId,
                    sourcePluginId = "",
                    name = notification.pluginName,
                    author = "",
                    version = "",
                    versionId = "",
                    updateTime = "",
                    downloadCount = 0L,
                    likeCount = 0L,
                    commentCount = 0L,
                    description = "",
                    files = emptyList()
                )
                val activity = context as? Activity
                    ?: WeChatApis.currentActivity()?.currentActivity() as? Activity
                if (activity == null) {
                    selectedPlugin = target
                } else {
                    activity.window?.decorView?.postOnAnimation {
                        if (!activity.isFinishing && !activity.isDestroyed) selectedPlugin = target
                    }
                }
            },
            onDismiss = { showNotifications = false }
        )
    }
}

@Composable
private fun PluginMarketNotificationBell(
    unreadCount: Long,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier.size(40.dp).responsiveTap(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            imageVector = NavIcons.Bell,
            contentDescription = "回复通知",
            colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onSurface),
            modifier = Modifier.size(24.dp)
        )
        if (unreadCount > 0L) {
            Box(
                modifier = Modifier.align(Alignment.TopEnd)
                    .height(16.dp)
                    .sizeIn(minWidth = 16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFD93025))
                    .padding(horizontal = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (unreadCount > 99L) "99+" else unreadCount.toString(),
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun PluginMarketNotificationDialog(
    notifications: List<PluginMarketNotification>,
    loading: Boolean,
    error: String,
    onRetry: () -> Unit,
    onNotificationClick: (PluginMarketNotification) -> Unit,
    onDismiss: () -> Unit
) {
    WindowDialog(
        show = true,
        title = "回复通知",
        onDismissRequest = onDismiss,
        content = {
            Column(modifier = Modifier.fillMaxWidth().heightIn(max = 520.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "共 ${notifications.size} 条",
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        fontSize = 12.sp
                    )
                    TextButton(
                        text = if (loading) "正在刷新" else "刷新",
                        onClick = onRetry,
                        enabled = !loading,
                        colors = ButtonDefaults.textButtonColorsPrimary()
                    )
                }
                Column(
                    modifier = Modifier.fillMaxWidth().weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                ) {
                    when {
                        loading && notifications.isEmpty() -> EmptyText("正在加载回复通知...")
                        error.isNotBlank() && notifications.isEmpty() -> {
                            EmptyText(error)
                            TextButton(
                                text = "重试",
                                onClick = onRetry,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.textButtonColorsPrimary()
                            )
                        }
                        notifications.isEmpty() -> EmptyText("暂无回复通知")
                        else -> notifications.forEachIndexed { index, notification ->
                            PluginMarketNotificationRow(
                                notification = notification,
                                onClick = { onNotificationClick(notification) }
                            )
                            if (index != notifications.lastIndex) Spacer(Modifier.height(8.dp))
                        }
                    }
                }
                TextButton(
                    text = "关闭",
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                    colors = ButtonDefaults.textButtonColorsPrimary()
                )
            }
        }
    )
}

@Composable
private fun PluginMarketNotificationRow(
    notification: PluginMarketNotification,
    onClick: () -> Unit
) {
    var pressed by remember(notification.notificationId) { mutableStateOf(false) }
    val feedbackColor = rememberPressFeedbackColor(pressed)
    Column(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(
                if (pressed) feedbackColor else MiuixTheme.colorScheme.secondaryVariant
            )
            .responsiveTap(onClick = onClick, onPressedChange = { pressed = it })
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(
            text = when (notification.type) {
                "plugin_comment" -> "${notification.actorNickname.ifBlank { "微信用户" }} 评论了你的插件"
                "plugin_update" -> "你关注的插件发布了新版本"
                else -> "${notification.actorNickname.ifBlank { "微信用户" }} 回复了你的评论"
            },
            color = MiuixTheme.colorScheme.onSurface,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = notification.pluginName.ifBlank { notification.remotePluginId },
            color = MiuixTheme.colorScheme.primary,
            fontSize = 12.sp,
            modifier = Modifier.fillMaxWidth().padding(top = 2.dp)
        )
        Text(
            text = notification.content,
            color = MiuixTheme.colorScheme.onSurface,
            fontSize = 13.sp,
            modifier = Modifier.fillMaxWidth().padding(top = 7.dp)
        )
        Text(
            text = "原评论：${notification.originalContent}",
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            fontSize = 12.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth().padding(top = 5.dp)
        )
        Text(
            text = formatPluginMarketTime(notification.createdAt),
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            fontSize = 11.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun PluginMarketListRow(
    plugin: PluginMarketPlugin,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val pressFeedbackColor = rememberPressFeedbackColor(pressed)
    Row(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(pressFeedbackColor)
            .responsiveTap(onClick = onClick, onPressedChange = { pressed = it })
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = plugin.name.ifBlank { plugin.remotePluginId },
                color = MiuixTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = buildString {
                    append("作者: ").append(plugin.author.ifBlank { "未知" })
                    append(" | 版本: ").append(plugin.version.ifBlank { "未知" })
                    append(" | 下载: ").append(plugin.downloadCount)
                    append("\n点赞: ").append(plugin.likeCount)
                    append(" | 评论: ").append(plugin.commentCount)
                    append("\n更新: ").append(formatPluginMarketTime(plugin.updateTime))
                },
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                fontSize = 12.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            text = "›",
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            fontSize = 22.sp,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
private fun PluginMarketDetailDialog(
    summary: PluginMarketPlugin,
    detail: PluginMarketPlugin?,
    loading: Boolean,
    error: String,
    historyVersions: List<PluginMarketHistoryVersion>,
    historyLoading: Boolean,
    historyError: String,
    historyInstallVersionId: String?,
    installed: Boolean,
    installing: Boolean,
    deleting: Boolean,
    owned: Boolean,
    installMessage: String,
    likedByCurrentUser: Boolean,
    followingByCurrentUser: Boolean,
    updatingLike: Boolean,
    updatingFollow: Boolean,
    comments: List<PluginMarketComment>,
    commentsLoading: Boolean,
    commentsError: String,
    currentUserWxId: String,
    interactionError: String,
    commentDraft: String,
    submittingComment: Boolean,
    deletingCommentId: String?,
    onRetry: () -> Unit,
    onRetryComments: () -> Unit,
    onInstall: (PluginMarketPlugin) -> Unit,
    onInstallHistory: (PluginMarketHistoryVersion) -> Unit,
    onToggleLike: () -> Unit,
    onToggleFollow: () -> Unit,
    onCommentDraftChanged: (String) -> Unit,
    onSubmitComment: () -> Unit,
    onDeleteComment: (PluginMarketComment) -> Unit,
    onSubmitReply: (PluginMarketComment, String, (Boolean) -> Unit) -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var confirmDelete by remember(summary.remotePluginId) { mutableStateOf(false) }
    var pendingDeleteComment by remember(summary.remotePluginId) {
        mutableStateOf<PluginMarketComment?>(null)
    }
    var replyTarget by remember(summary.remotePluginId) {
        mutableStateOf<PluginMarketComment?>(null)
    }
    var replyDraft by remember(summary.remotePluginId) { mutableStateOf("") }
    WindowDialog(
        show = true,
        title = summary.name.ifBlank { "插件详情" },
        onDismissRequest = onDismiss,
        content = {
            Column(modifier = Modifier.fillMaxWidth().heightIn(max = 560.dp).imePadding()) {
                Column(
                    modifier = Modifier.fillMaxWidth().weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                ) {
                    val shownPlugin = detail ?: summary
                    PluginMarketDialogInfoRow("作者", shownPlugin.author.ifBlank { "未知" })
                    PluginMarketDialogInfoRow("版本", shownPlugin.version.ifBlank { "未知" })
                    PluginMarketDialogInfoRow("更新时间", formatPluginMarketTime(shownPlugin.updateTime))
                    PluginMarketDialogInfoRow("下载量", shownPlugin.downloadCount.toString())
                    PluginMarketDialogInfoRow("点赞数", shownPlugin.likeCount.toString())
                    PluginMarketDialogInfoRow("评论数", shownPlugin.commentCount.toString())
                    PluginMarketDialogInfoRow("插件 ID", summary.remotePluginId)
                    if (detail?.description?.isNotBlank() == true) {
                        Text(
                            text = detail.description,
                            color = MiuixTheme.colorScheme.onSurface,
                            fontSize = 14.sp,
                            modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                        )
                    }
                    if (detail != null) {
                        Text(
                            text = "包含文件",
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                        Text(
                            text = detail.files.joinToString("、") { it.name }.ifBlank { "未知" },
                            color = MiuixTheme.colorScheme.onSurface,
                            fontSize = 13.sp,
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                        )
                        detail.files.firstOrNull { it.name.equals("README.md", ignoreCase = true) }
                            ?.content?.takeIf { it.isNotBlank() }
                            ?.let { readme ->
                                Text(
                                    text = "插件说明",
                                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 12.dp)
                                )
                                Box(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                                    MarkdownUi.Content(
                                        context = context,
                                        markdown = readme,
                                        contentPadding = PaddingValues(0.dp)
                                    )
                                }
                            }
                    }
                    PluginMarketSocialSection(
                        likeCount = shownPlugin.likeCount,
                        commentCount = shownPlugin.commentCount,
                        likedByCurrentUser = likedByCurrentUser,
                        followingByCurrentUser = followingByCurrentUser,
                        updatingLike = updatingLike,
                        updatingFollow = updatingFollow,
                        comments = comments,
                        commentsLoading = commentsLoading,
                        commentsError = commentsError,
                        currentUserWxId = currentUserWxId,
                        interactionError = interactionError,
                        commentDraft = commentDraft,
                        submittingComment = submittingComment,
                        deletingCommentId = deletingCommentId,
                        enabled = !installing && !deleting,
                        onToggleLike = onToggleLike,
                        onToggleFollow = onToggleFollow,
                        onCommentDraftChanged = onCommentDraftChanged,
                        onSubmitComment = onSubmitComment,
                        onRetryComments = onRetryComments,
                        onDeleteComment = { pendingDeleteComment = it },
                        onReplyComment = { comment ->
                            replyTarget = comment
                            replyDraft = ""
                        }
                    )
                    PluginMarketHistoryUi.HistoryList(
                        versions = historyVersions,
                        loading = historyLoading,
                        error = historyError,
                        installingVersionId = historyInstallVersionId,
                        enabled = !installing,
                        onRetry = onRetry,
                        onInstall = onInstallHistory
                    )
                    Text(
                        text = "社区插件可能执行敏感操作，请确认来源可信。安装或更新完成后插件保持禁用。",
                        color = Color(0xFFD93025),
                        fontSize = 13.sp,
                        modifier = Modifier.fillMaxWidth().padding(top = 14.dp)
                    )
                    when {
                        loading -> EmptyText("正在加载插件详情...")
                        error.isNotBlank() -> {
                            EmptyText(error)
                            TextButton(
                                text = "重新加载",
                                onClick = onRetry,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.textButtonColorsPrimary()
                            )
                        }
                    }
                    if (installMessage.isNotBlank()) {
                        Text(
                            text = installMessage,
                            color = if (installMessage.startsWith("安装失败")) {
                                Color(0xFFD93025)
                            } else {
                                MiuixTheme.colorScheme.primary
                            },
                            fontSize = 13.sp,
                            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                val activeReply = replyTarget
                if (activeReply != null) {
                    val focusRequester = remember(activeReply.commentId) { FocusRequester() }
                    val keyboardController = LocalSoftwareKeyboardController.current
                    LaunchedEffect(activeReply.commentId) {
                        focusRequester.requestFocus()
                        keyboardController?.show()
                    }
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MiuixTheme.colorScheme.secondaryVariant)
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "回复 ${activeReply.userNickname.ifBlank { "微信用户" }}",
                            color = MiuixTheme.colorScheme.onSurface,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = activeReply.content.replace('\n', ' ').trim(),
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            fontSize = 12.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth().padding(top = 2.dp)
                        )
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(MiuixTheme.colorScheme.background)
                                .padding(horizontal = 10.dp, vertical = 9.dp)
                        ) {
                            if (replyDraft.isBlank()) {
                                Text(
                                    text = "回复 ${activeReply.userNickname.ifBlank { "微信用户" }}",
                                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                                    fontSize = 14.sp
                                )
                            }
                            BasicTextField(
                                value = replyDraft,
                                onValueChange = { replyDraft = it.take(1000) },
                                enabled = !submittingComment,
                                minLines = 1,
                                maxLines = 4,
                                textStyle = TextStyle(
                                    color = MiuixTheme.colorScheme.onSurface,
                                    fontSize = 14.sp
                                ),
                                cursorBrush = SolidColor(MiuixTheme.colorScheme.primary),
                                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${replyDraft.length}/1000",
                                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                                fontSize = 11.sp
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                TextButton(
                                    text = "取消",
                                    onClick = {
                                        replyTarget = null
                                        replyDraft = ""
                                    },
                                    enabled = !submittingComment,
                                    colors = ButtonDefaults.textButtonColorsPrimary()
                                )
                                TextButton(
                                    text = if (submittingComment) "正在回复" else "发送",
                                    onClick = {
                                        val content = replyDraft.trim()
                                        if (content.isNotBlank()) {
                                            onSubmitReply(activeReply, content) { succeeded ->
                                                if (succeeded) {
                                                    replyTarget = null
                                                    replyDraft = ""
                                                }
                                            }
                                        }
                                    },
                                    enabled = replyDraft.isNotBlank() && !submittingComment,
                                    colors = ButtonDefaults.textButtonColorsPrimary()
                                )
                            }
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TextButton(
                            text = "关闭",
                            onClick = onDismiss,
                            enabled = !installing && !deleting && !updatingLike &&
                                !submittingComment && deletingCommentId == null,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColorsPrimary()
                        )
                        if (owned) {
                            TextButton(
                                text = if (deleting) "正在删除" else "删除",
                                onClick = { confirmDelete = true },
                                enabled = !installing && !deleting && !updatingLike &&
                                    !submittingComment && deletingCommentId == null,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.textButtonColorsPrimary()
                            )
                        }
                        if (installing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                size = 18.dp
                            )
                        }
                        TextButton(
                            text = when {
                                installing -> "正在处理"
                                installed -> "更新"
                                else -> "安装"
                            },
                            onClick = { detail?.let(onInstall) },
                            enabled = detail != null && !loading && error.isBlank() && !installing &&
                                !deleting && !updatingLike && !submittingComment && deletingCommentId == null,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColorsPrimary()
                        )
                    }
                }
            }
        }
    )
    if (confirmDelete) {
        WindowDialog(
            show = true,
            title = "删除线上插件",
            onDismissRequest = { confirmDelete = false },
            content = {
                Column {
                    Text(
                        text = "确定删除“${summary.name.ifBlank { summary.remotePluginId }}”吗？线上插件及全部历史版本都会被删除，本地插件不会删除。此操作无法撤销。",
                        color = MiuixTheme.colorScheme.onSurface,
                        fontSize = 14.sp
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TextButton(
                            text = "取消",
                            onClick = { confirmDelete = false },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColorsPrimary()
                        )
                        TextButton(
                            text = "确认删除",
                            onClick = {
                                confirmDelete = false
                                onDelete()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColorsPrimary()
                        )
                    }
                }
            }
        )
    }
    pendingDeleteComment?.let { comment ->
        WindowDialog(
            show = true,
            title = "删除评论",
            onDismissRequest = { pendingDeleteComment = null },
            content = {
                Column {
                    Text(
                        text = "确定删除这条评论吗？此操作无法撤销。",
                        color = MiuixTheme.colorScheme.onSurface,
                        fontSize = 14.sp
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TextButton(
                            text = "取消",
                            onClick = { pendingDeleteComment = null },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColorsPrimary()
                        )
                        TextButton(
                            text = "确认删除",
                            onClick = {
                                pendingDeleteComment = null
                                onDeleteComment(comment)
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColorsPrimary()
                        )
                    }
                }
            }
        )
    }
}

@Composable
private fun PluginMarketSocialSection(
    likeCount: Long,
    commentCount: Long,
    likedByCurrentUser: Boolean,
    followingByCurrentUser: Boolean,
    updatingLike: Boolean,
    updatingFollow: Boolean,
    comments: List<PluginMarketComment>,
    commentsLoading: Boolean,
    commentsError: String,
    currentUserWxId: String,
    interactionError: String,
    commentDraft: String,
    submittingComment: Boolean,
    deletingCommentId: String?,
    enabled: Boolean,
    onToggleLike: () -> Unit,
    onToggleFollow: () -> Unit,
    onCommentDraftChanged: (String) -> Unit,
    onSubmitComment: () -> Unit,
    onRetryComments: () -> Unit,
    onDeleteComment: (PluginMarketComment) -> Unit,
    onReplyComment: (PluginMarketComment) -> Unit
) {
    Text(
        text = "互动",
        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
        fontSize = 12.sp,
        modifier = Modifier.padding(top = 16.dp)
    )
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            text = when {
                updatingLike -> "正在处理"
                likedByCurrentUser -> "取消点赞"
                else -> "点赞"
            },
            onClick = onToggleLike,
            enabled = enabled && currentUserWxId.isNotBlank() && !updatingLike,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.textButtonColorsPrimary()
        )
        TextButton(
            text = when {
                updatingFollow -> "正在处理"
                followingByCurrentUser -> "取消关注"
                else -> "关注插件"
            },
            onClick = onToggleFollow,
            enabled = enabled && currentUserWxId.isNotBlank() && !updatingFollow,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.textButtonColorsPrimary()
        )
        Text(
            text = "$likeCount 个赞 · $commentCount 条评论",
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            fontSize = 13.sp,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
    Text(
        text = "发表评论",
        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
        fontSize = 12.sp,
        modifier = Modifier.padding(top = 10.dp)
    )
    BasicTextField(
        value = commentDraft,
        onValueChange = onCommentDraftChanged,
        enabled = enabled && currentUserWxId.isNotBlank() && !submittingComment,
        minLines = 2,
        maxLines = 4,
        textStyle = TextStyle(color = MiuixTheme.colorScheme.onSurface, fontSize = 14.sp),
        cursorBrush = SolidColor(MiuixTheme.colorScheme.primary),
        decorationBox = { innerTextField ->
            Box(contentAlignment = Alignment.TopStart) {
                if (commentDraft.isBlank()) {
                    Text(
                        text = if (currentUserWxId.isBlank()) "当前微信账号资料尚未就绪" else "填写评论内容",
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        fontSize = 14.sp
                    )
                }
                innerTextField()
            }
        },
        modifier = Modifier.fillMaxWidth().padding(top = 5.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MiuixTheme.colorScheme.secondaryVariant)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    )
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${commentDraft.length}/1000",
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            fontSize = 12.sp
        )
        TextButton(
            text = if (submittingComment) "正在发表" else "发表",
            onClick = onSubmitComment,
            enabled = enabled && currentUserWxId.isNotBlank() &&
                commentDraft.isNotBlank() && !submittingComment,
            colors = ButtonDefaults.textButtonColorsPrimary()
        )
    }
    if (interactionError.isNotBlank()) {
        Text(
            text = interactionError,
            color = Color(0xFFD93025),
            fontSize = 12.sp,
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
        )
    }
    Text(
        text = "评论 ($commentCount)",
        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
        fontSize = 12.sp,
        modifier = Modifier.padding(top = 10.dp)
    )
    val commentThreads = remember(comments) { buildPluginMarketCommentThreads(comments) }
    when {
        commentsLoading -> EmptyText("正在加载评论...")
        commentsError.isNotBlank() -> {
            EmptyText(commentsError)
            TextButton(
                text = "重试",
                onClick = onRetryComments,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColorsPrimary()
            )
        }
        comments.isEmpty() -> EmptyText("暂无评论")
        else -> commentThreads.forEachIndexed { index, thread ->
            PluginMarketCommentRow(
                comment = thread.root,
                deleting = deletingCommentId == thread.root.commentId,
                enabled = enabled && deletingCommentId == null,
                onDelete = { onDeleteComment(thread.root) },
                onReply = { onReplyComment(thread.root) }
            )
            if (thread.replies.isNotEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(start = 14.dp, bottom = 8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(MiuixTheme.colorScheme.secondaryVariant)
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    thread.replies.forEachIndexed { replyIndex, reply ->
                        PluginMarketReplyRow(
                            comment = reply,
                            deleting = deletingCommentId == reply.commentId,
                            enabled = enabled && deletingCommentId == null,
                            onDelete = { onDeleteComment(reply) },
                            onReply = { onReplyComment(reply) }
                        )
                        if (replyIndex != thread.replies.lastIndex) InsetDivider()
                    }
                }
            }
            if (index != commentThreads.lastIndex) InsetDivider()
        }
    }
}

@Composable
private fun PluginMarketCommentRow(
    comment: PluginMarketComment,
    deleting: Boolean,
    enabled: Boolean,
    onDelete: () -> Unit,
    onReply: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.weight(1f)
                .clip(RoundedCornerShape(6.dp))
                .clickable(enabled = enabled, onClick = onReply)
                .padding(vertical = 7.dp)
        ) {
            Text(
                text = comment.userNickname.ifBlank { "微信用户" },
                color = MiuixTheme.colorScheme.onSurface,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = formatPluginMarketTime(comment.createdAt),
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                fontSize = 11.sp
            )
            Text(
                text = comment.content,
                color = MiuixTheme.colorScheme.onSurface,
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
            )
        }
        if (comment.canDelete) {
            TextButton(
                text = if (deleting) "正在删除" else "删除",
                onClick = onDelete,
                enabled = enabled && !deleting,
                colors = ButtonDefaults.textButtonColorsPrimary()
            )
        }
    }
}

@Composable
private fun PluginMarketReplyRow(
    comment: PluginMarketComment,
    deleting: Boolean,
    enabled: Boolean,
    onDelete: () -> Unit,
    onReply: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.weight(1f)
                .clip(RoundedCornerShape(4.dp))
                .clickable(enabled = enabled, onClick = onReply)
                .padding(vertical = 6.dp)
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Medium)) {
                        append(comment.userNickname.ifBlank { "微信用户" })
                    }
                    append(" 回复 ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Medium)) {
                        append(comment.replyToNickname.ifBlank { "微信用户" })
                    }
                    append("：")
                    append(comment.content)
                },
                color = MiuixTheme.colorScheme.onSurface,
                fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = formatPluginMarketTime(comment.createdAt),
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        if (comment.canDelete) {
            TextButton(
                text = if (deleting) "正在删除" else "删除",
                onClick = onDelete,
                enabled = enabled && !deleting,
                colors = ButtonDefaults.textButtonColorsPrimary()
            )
        }
    }
}

private data class PluginMarketCommentThread(
    val root: PluginMarketComment,
    val replies: List<PluginMarketComment>
)

private fun buildPluginMarketCommentThreads(
    comments: List<PluginMarketComment>
): List<PluginMarketCommentThread> {
    val commentsById = comments.associateBy { it.commentId }
    val roots = comments.filter { comment ->
        comment.parentCommentId.isBlank() || comment.parentCommentId !in commentsById
    }.sortedWith(
        compareByDescending<PluginMarketComment> { it.createdAt }
            .thenByDescending { it.commentId }
    )
    val repliesByRoot = linkedMapOf<String, MutableList<PluginMarketComment>>()
    comments.filter { it.parentCommentId.isNotBlank() }.forEach { comment ->
        var cursor = comment
        val visited = mutableSetOf(comment.commentId)
        while (cursor.parentCommentId.isNotBlank()) {
            val parent = commentsById[cursor.parentCommentId] ?: break
            if (!visited.add(parent.commentId)) break
            cursor = parent
        }
        if (cursor.commentId != comment.commentId) {
            repliesByRoot.getOrPut(cursor.commentId) { mutableListOf() }.add(comment)
        }
    }
    return roots.map { root ->
        PluginMarketCommentThread(
            root = root,
            replies = repliesByRoot[root.commentId].orEmpty().sortedWith(
                compareBy<PluginMarketComment> { it.createdAt }.thenBy { it.commentId }
            )
        )
    }
}

@Composable
private fun PluginMarketDialogInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            fontSize = 13.sp
        )
        Text(
            text = value,
            color = MiuixTheme.colorScheme.onSurface,
            fontSize = 13.sp,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f).padding(start = 16.dp)
        )
    }
}

@Composable
private fun PluginMarketUploadDialog(
    context: Context,
    onDismiss: (Boolean) -> Unit
) {
    val scope = rememberCoroutineScope()
    var loading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf("") }
    var localPlugins by remember { mutableStateOf<List<ScriptPluginRuntime.ScriptPlugin>>(emptyList()) }
    var query by rememberSaveable { mutableStateOf("") }
    var selectedIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    var remoteNames by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var releaseNotes by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var extraFiles by remember { mutableStateOf<Map<String, List<PluginMarketFile>>>(emptyMap()) }
    var statuses by remember { mutableStateOf<Map<String, PluginMarketUploadStatus>>(emptyMap()) }
    var uploading by remember { mutableStateOf(false) }
    var uploadedAny by remember { mutableStateOf(false) }
    var reloadVersion by remember { mutableStateOf(0) }

    LaunchedEffect(reloadVersion) {
        loading = true
        loadError = ""
        val result = runCatching {
            withContext(Dispatchers.IO) { ScriptPluginRuntime.listPlugins(context) }
        }
        result.fold(
            onSuccess = { plugins ->
                localPlugins = plugins
                remoteNames = plugins.associate { plugin ->
                    plugin.id to (plugin.displayName ?: plugin.name).ifBlank { plugin.id }
                }
                releaseNotes = plugins.associate { plugin ->
                    plugin.id to releaseNotes[plugin.id].orEmpty()
                }
                extraFiles = extraFiles.filterKeys { id -> plugins.any { it.id == id } }
                selectedIds = selectedIds.intersect(plugins.mapTo(LinkedHashSet()) { it.id })
            },
            onFailure = { error -> loadError = pluginMarketErrorMessage(error) }
        )
        loading = false
    }

    val normalizedQuery = query.trim().lowercase(Locale.US)
    val filteredPlugins = localPlugins.filter { plugin ->
        normalizedQuery.isEmpty() ||
            plugin.id.lowercase(Locale.US).contains(normalizedQuery) ||
            plugin.name.lowercase(Locale.US).contains(normalizedQuery) ||
            plugin.author.lowercase(Locale.US).contains(normalizedQuery)
    }
    val allFilteredSelected = filteredPlugins.isNotEmpty() && filteredPlugins.all { it.id in selectedIds }

    fun startUpload() {
        if (uploading || selectedIds.isEmpty()) return
        val ordered = localPlugins.filter { it.id in selectedIds }
        if (ordered.any { remoteNames[it.id].orEmpty().trim().isBlank() }) {
            Toast.makeText(context, "在线插件名不能为空", Toast.LENGTH_SHORT).show()
            return
        }
        statuses = ordered.associate { plugin ->
            plugin.id to PluginMarketUploadStatus(PluginMarketUploadPhase.QUEUED, "等待上传")
        }
        uploading = true
        scope.launch {
            var publishedCount = 0
            var pendingCount = 0
            ordered.forEach { plugin ->
                statuses = statuses + (
                    plugin.id to PluginMarketUploadStatus(PluginMarketUploadPhase.UPLOADING, "正在上传")
                )
                val result = withContext(Dispatchers.IO) {
                    PluginMarketRepository.upload(
                        context = context,
                        localPluginId = plugin.id,
                        remoteName = remoteNames[plugin.id].orEmpty().trim(),
                        releaseNotes = releaseNotes[plugin.id].orEmpty().trim(),
                        extraFiles = extraFiles[plugin.id].orEmpty()
                    )
                }
                result.fold(
                    onSuccess = { ownership ->
                        uploadedAny = true
                        when (ownership.reviewStatus) {
                            PluginMarketReviewStatus.PENDING -> {
                                pendingCount++
                                statuses = statuses + (
                                    plugin.id to PluginMarketUploadStatus(
                                        PluginMarketUploadPhase.PENDING_REVIEW,
                                        "上传成功，待审核 · ${ownership.remotePluginId}"
                                    )
                                )
                            }
                            PluginMarketReviewStatus.APPROVED -> {
                                publishedCount++
                                statuses = statuses + (
                                    plugin.id to PluginMarketUploadStatus(
                                        PluginMarketUploadPhase.SUCCESS,
                                        "上传成功 · ${ownership.remotePluginId}"
                                    )
                                )
                            }
                        }
                    },
                    onFailure = { error ->
                        statuses = statuses + (
                            plugin.id to PluginMarketUploadStatus(
                                PluginMarketUploadPhase.FAILED,
                                "上传失败: ${pluginMarketErrorMessage(error)}"
                            )
                        )
                    }
                )
            }
            uploading = false
            Toast.makeText(
                context,
                "上传完成：已发布 $publishedCount，待审核 $pendingCount，失败 ${ordered.size - publishedCount - pendingCount}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    WindowDialog(
        show = true,
        title = "上传本地插件",
        onDismissRequest = { if (!uploading) onDismiss(uploadedAny) },
        content = {
            Column(modifier = Modifier.fillMaxWidth().heightIn(max = 620.dp)) {
                SearchBarSurface(
                    query = query,
                    placeholder = "搜索本地插件",
                    onQueryChange = { if (!uploading) query = it }
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "已选 ${selectedIds.size} / ${localPlugins.size}",
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        fontSize = 12.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = if (allFilteredSelected) "取消当前筛选" else "全选当前筛选",
                        color = if (filteredPlugins.isEmpty() || uploading) {
                            MiuixTheme.colorScheme.onSurfaceVariantSummary
                        } else {
                            MiuixTheme.colorScheme.primary
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable(enabled = filteredPlugins.isNotEmpty() && !uploading) {
                            val filteredIds = filteredPlugins.mapTo(LinkedHashSet()) { it.id }
                            selectedIds = if (allFilteredSelected) {
                                selectedIds - filteredIds
                            } else {
                                selectedIds + filteredIds
                            }
                        }.padding(horizontal = 8.dp, vertical = 6.dp)
                    )
                }
                LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f, fill = false)) {
                    when {
                        loading -> item { EmptyText("正在读取本地插件...") }
                        loadError.isNotBlank() -> item {
                            Column {
                                EmptyText(loadError)
                                TextButton(
                                    text = "重试",
                                    onClick = { reloadVersion++ },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.textButtonColorsPrimary()
                                )
                            }
                        }
                        localPlugins.isEmpty() -> item { EmptyText("没有可运行的本地插件") }
                        filteredPlugins.isEmpty() -> item { EmptyText("没有匹配的本地插件") }
                        else -> items(filteredPlugins.size, key = { filteredPlugins[it].id }) { index ->
                            val plugin = filteredPlugins[index]
                            if (index > 0) InsetDivider(start = 0.dp)
                            PluginMarketUploadRow(
                                plugin = plugin,
                                selected = plugin.id in selectedIds,
                                remoteName = remoteNames[plugin.id].orEmpty(),
                                releaseNotes = releaseNotes[plugin.id].orEmpty(),
                                extraFiles = extraFiles[plugin.id].orEmpty(),
                                status = statuses[plugin.id],
                                enabled = !uploading,
                                onSelectedChange = { selected ->
                                    selectedIds = if (selected) selectedIds + plugin.id else selectedIds - plugin.id
                                },
                                onRemoteNameChange = { name ->
                                    remoteNames = remoteNames + (plugin.id to name.take(100))
                                },
                                onReleaseNotesChange = { notes ->
                                    releaseNotes = releaseNotes + (plugin.id to notes.take(500))
                                },
                                onPickExtraFiles = {
                                    ScriptPluginAgentUi.findAgentActivity(context)?.let { activity ->
                                        PluginMarketExtraFilePickerBridge.launch(activity) { files ->
                                            val existing = extraFiles[plugin.id].orEmpty()
                                            val merged = (files + existing).distinctBy {
                                                it.name.lowercase(Locale.ROOT)
                                            }
                                            extraFiles = extraFiles + (plugin.id to merged)
                                        }
                                    } ?: Toast.makeText(
                                        context,
                                        "无法打开文件选择器",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                onClearExtraFiles = {
                                    extraFiles = extraFiles - plugin.id
                                }
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TextButton(
                        text = "关闭",
                        onClick = { onDismiss(uploadedAny) },
                        enabled = !uploading,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.textButtonColorsPrimary()
                    )
                    TextButton(
                        text = if (uploading) "正在上传" else "上传已选",
                        onClick = ::startUpload,
                        enabled = !uploading && selectedIds.isNotEmpty(),
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.textButtonColorsPrimary()
                    )
                }
            }
        }
    )
}

@Composable
private fun PluginMarketUploadRow(
    plugin: ScriptPluginRuntime.ScriptPlugin,
    selected: Boolean,
    remoteName: String,
    releaseNotes: String,
    extraFiles: List<PluginMarketFile>,
    status: PluginMarketUploadStatus?,
    enabled: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    onRemoteNameChange: (String) -> Unit,
    onReleaseNotesChange: (String) -> Unit,
    onPickExtraFiles: () -> Unit,
    onClearExtraFiles: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().clickable(enabled = enabled) { onSelectedChange(!selected) }
                .padding(horizontal = 4.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = plugin.name.ifBlank { plugin.id },
                    color = MiuixTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = buildString {
                        append(plugin.id)
                        append(" · ").append(plugin.author.ifBlank { "未知作者" })
                        append(" · ").append(plugin.version.ifBlank { "未知版本" })
                    },
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            SelectionMark(selected = selected, multiSelect = true)
        }
        if (selected) {
            Text(
                text = "在线名称",
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
            BasicTextField(
                value = remoteName,
                onValueChange = onRemoteNameChange,
                enabled = enabled,
                singleLine = true,
                textStyle = TextStyle(color = MiuixTheme.colorScheme.onSurface, fontSize = 14.sp),
                cursorBrush = SolidColor(MiuixTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth().padding(top = 5.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MiuixTheme.colorScheme.secondaryVariant)
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            )
            Text(
                text = "更新说明",
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp, top = 8.dp)
            )
            BasicTextField(
                value = releaseNotes,
                onValueChange = onReleaseNotesChange,
                enabled = enabled,
                minLines = 2,
                maxLines = 3,
                textStyle = TextStyle(color = MiuixTheme.colorScheme.onSurface, fontSize = 14.sp),
                cursorBrush = SolidColor(MiuixTheme.colorScheme.primary),
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.TopStart) {
                        if (releaseNotes.isBlank()) {
                            Text(
                                text = "填写本次版本的更新内容",
                                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                                fontSize = 14.sp
                            )
                        }
                        innerTextField()
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 5.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MiuixTheme.colorScheme.secondaryVariant)
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "附加文件${if (extraFiles.isEmpty()) "" else " (${extraFiles.size})"}",
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        fontSize = 12.sp
                    )
                    if (extraFiles.isNotEmpty()) {
                        Text(
                            text = extraFiles.joinToString("、") { it.name },
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            fontSize = 12.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
                TextButton(
                    text = "选择",
                    onClick = onPickExtraFiles,
                    enabled = enabled,
                    colors = ButtonDefaults.textButtonColorsPrimary()
                )
                if (extraFiles.isNotEmpty()) {
                    TextButton(
                        text = "清空",
                        onClick = onClearExtraFiles,
                        enabled = enabled,
                        colors = ButtonDefaults.textButtonColorsPrimary()
                    )
                }
            }
        }
        if (status != null) {
            val color = when (status.phase) {
                PluginMarketUploadPhase.SUCCESS -> MiuixTheme.colorScheme.primary
                PluginMarketUploadPhase.PENDING_REVIEW -> MiuixTheme.colorScheme.onSurfaceVariantSummary
                PluginMarketUploadPhase.FAILED -> Color(0xFFD93025)
                else -> MiuixTheme.colorScheme.onSurfaceVariantSummary
            }
            Text(
                text = status.message,
                color = color,
                fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth().padding(start = 4.dp, top = 6.dp)
            )
        }
    }
}

private fun pluginMarketErrorMessage(error: Throwable): String {
    if (error is PluginMarketException && error.errorCode == "UPLOADER_BLACKLISTED") {
        return "当前微信账号已被禁止上传在线插件"
    }
    return error.message?.trim().takeUnless { it.isNullOrBlank() } ?: error.javaClass.simpleName
}

}

private object PluginMarketHistoryUi {
    @Composable
    fun HistoryList(
        versions: List<PluginMarketHistoryVersion>,
        loading: Boolean,
        error: String,
        installingVersionId: String?,
        enabled: Boolean,
        onRetry: () -> Unit,
        onInstall: (PluginMarketHistoryVersion) -> Unit
    ) {
        Text(
            text = "历史版本",
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 14.dp)
        )
        when {
            loading -> EmptyText("正在加载历史版本...")
            error.isNotBlank() -> {
                EmptyText(error)
                TextButton(
                    text = "重新加载",
                    onClick = onRetry,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColorsPrimary()
                )
            }
            versions.isEmpty() -> EmptyText("暂无历史版本")
            else -> versions.forEachIndexed { index, version ->
                if (index > 0) InsetDivider(start = 0.dp)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = buildString {
                                append(version.versionName.ifBlank { "未命名版本" })
                                if (index == 0) append("（最新）")
                            },
                            color = MiuixTheme.colorScheme.onSurface,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${formatPluginMarketTime(version.createdAt)} · ${formatBytes(version.totalSize)}",
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "更新说明：${version.releaseNotes.ifBlank { "暂无更新说明" }}",
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 3.dp)
                        )
                    }
                    TextButton(
                        text = if (installingVersionId == version.versionId) "读取中" else "安装",
                        onClick = { onInstall(version) },
                        enabled = enabled && installingVersionId == null,
                        colors = ButtonDefaults.textButtonColorsPrimary()
                    )
                }
            }
        }
    }

    private fun formatBytes(size: Long): String = when {
        size >= 1024L * 1024L -> String.format(Locale.US, "%.1f MiB", size / (1024.0 * 1024.0))
        size >= 1024L -> String.format(Locale.US, "%.1f KiB", size / 1024.0)
        else -> "$size B"
    }
}

private val pluginMarketBeijingTimeFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
        .withZone(ZoneId.of("Asia/Shanghai"))

private fun formatPluginMarketTime(value: String): String {
    val source = value.trim()
    if (source.isEmpty()) return "时间未知"
    val instant = runCatching { Instant.parse(source) }
        .recoverCatching { OffsetDateTime.parse(source, DateTimeFormatter.ISO_DATE_TIME).toInstant() }
        .recoverCatching {
            LocalDateTime.parse(source, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                .toInstant(ZoneOffset.UTC)
        }
        .getOrNull()
        ?: return source
    return pluginMarketBeijingTimeFormatter.format(instant)
}

internal object ScriptPluginAgentUi {

private data class ScriptPluginAgentPendingWrite(
    val messageIndex: Int,
    val draft: ScriptPluginAgentDraft,
    val diff: String,
    val isNewPlugin: Boolean,
    val confirmsCreation: Boolean,
    val risky: Boolean
)

private data class ScriptPluginAgentPendingDelete(
    val messageIndex: Int,
    val pluginId: String,
    val pluginName: String
)

private data class ScriptPluginAgentPendingWorkspaceChange(
    val messageIndex: Int,
    val change: ScriptPluginAgentWorkspaceChange,
    val isNewPlugin: Boolean,
    val hasDeletedPaths: Boolean,
    val risky: Boolean,
    val applyError: String = ""
)

private class ScriptPluginAgentWorkspaceToolApproval(
    val confirmation: ScriptPluginAgentWorkspaceToolConfirmation
) {
    private val decision = AtomicReference<ScriptPluginAgentWorkspaceWriteDecision?>(null)
    private val latch = CountDownLatch(1)

    fun resolve(value: ScriptPluginAgentWorkspaceWriteDecision) {
        if (decision.compareAndSet(null, value)) latch.countDown()
    }

    fun await(cancellation: ScriptPluginAgentCancellation): ScriptPluginAgentWorkspaceWriteDecision {
        while (!latch.await(200L, TimeUnit.MILLISECONDS)) cancellation.throwIfCancelled()
        cancellation.throwIfCancelled()
        return decision.get() ?: ScriptPluginAgentWorkspaceWriteDecision.CANCEL
    }
}

private data class ScriptPluginAgentPendingMessage(
    val id: Long,
    val content: String,
    val attachments: List<ScriptPluginAgentAttachment>,
    val quotedMessage: ScriptPluginAgentQuotedMessage?
)

private data class ScriptPluginAgentRenderedMessage(
    val sourceIndex: Int,
    val message: ScriptPluginAgentChatMessage
)

private class ScriptPluginAgentSessionRuntimeState(session: ScriptPluginAgentSession) {
    val id = session.id
    val title = mutableStateOf(session.title)
    val createdAt = mutableStateOf(session.createdAt)
    val messages = mutableStateOf(session.messages)
    val draft = mutableStateOf(session.draft)
    val targetPluginId = mutableStateOf(session.targetPluginId)
    val workspaceSessionId = mutableStateOf(session.id)
    val conversationSummary = mutableStateOf(session.conversationSummary)
    val nativeToolHistory = mutableStateOf(session.nativeToolHistory)
    val protocolTranscript = mutableStateOf(session.protocolTranscript)
    val compactedMessageCount = mutableStateOf(session.compactedMessageCount)
    val pinned = mutableStateOf(session.pinned)
    val locked = mutableStateOf(session.locked)
    val sortOrder = mutableStateOf(session.sortOrder)
    val resumeState = mutableStateOf(session.resumeState)
    val messageInput = mutableStateOf("")
    val pendingAttachments = mutableStateOf<List<ScriptPluginAgentAttachment>>(emptyList())
    val pendingQuotedMessage = mutableStateOf<ScriptPluginAgentQuotedMessage?>(null)
    val pendingMessages = mutableStateOf<List<ScriptPluginAgentPendingMessage>>(emptyList())
    val pendingMessagesExpanded = mutableStateOf(true)
    val nextPendingMessageId = mutableStateOf(1L)
    val generating = mutableStateOf(false)
    val generationStartedAt = mutableStateOf(0L)
    val generationHasVisibleReply = mutableStateOf(false)
    val contextCompacting = mutableStateOf(false)
    val contextCompactionStartedAt = mutableStateOf(0L)
    val applyingFileOperation = mutableStateOf(false)
    val pendingWrite = mutableStateOf<ScriptPluginAgentPendingWrite?>(null)
    val pendingDelete = mutableStateOf<ScriptPluginAgentPendingDelete?>(null)
    val pendingWorkspaceChange = mutableStateOf<ScriptPluginAgentPendingWorkspaceChange?>(null)
    val pendingToolApproval = mutableStateOf<ScriptPluginAgentWorkspaceToolApproval?>(null)
    val showPendingWorkspaceDialog = mutableStateOf(false)
    val showPendingWriteDialog = mutableStateOf(false)
    val showPendingDeleteDialog = mutableStateOf(false)
    val showToolApprovalDialog = mutableStateOf(false)
    val activeGenerationId = mutableStateOf("")
    val activeAssistantIndex = mutableStateOf(-1)
    val activeCancellation = mutableStateOf<ScriptPluginAgentCancellation?>(null)
    private val lastSnapshotAt = AtomicLong(session.updatedAt)
    private val checkpointSeq = AtomicLong(session.checkpointSeq)

    fun snapshot(updatedAt: Long = nextSnapshotAt()): ScriptPluginAgentSession {
        val workspaceBelongsToSession = workspaceSessionId.value == id
        return ScriptPluginAgentSession(
            id = id,
            title = title.value,
            createdAt = createdAt.value,
            updatedAt = updatedAt,
            messages = messages.value,
            draft = draft.value.takeIf { workspaceBelongsToSession },
            targetPluginId = targetPluginId.value.takeIf { workspaceBelongsToSession }.orEmpty(),
            conversationSummary = conversationSummary.value,
            nativeToolHistory = nativeToolHistory.value,
            protocolTranscript = protocolTranscript.value,
            compactedMessageCount = compactedMessageCount.value.coerceIn(0, messages.value.size),
            pinned = pinned.value,
            locked = locked.value,
            sortOrder = sortOrder.value,
            resumeState = resumeState.value,
            checkpointSeq = checkpointSeq.incrementAndGet()
        )
    }

    private fun nextSnapshotAt(): Long {
        return lastSnapshotAt.updateAndGet { previous ->
            maxOf(System.currentTimeMillis(), previous + 1L)
        }
    }

    fun isBusy(): Boolean {
        return isWorking() ||
            pendingToolApproval.value != null || pendingWorkspaceChange.value != null ||
            pendingWrite.value != null || pendingDelete.value != null
    }

    fun isWorking(): Boolean {
        return generating.value || contextCompacting.value || applyingFileOperation.value
    }
}

private class ScriptPluginAgentSessionCheckpointSaver(
    context: Context,
    private val state: ScriptPluginAgentSessionRuntimeState
) {
    private val appContext = context.applicationContext ?: context
    private val handler = Handler(Looper.getMainLooper())
    private var scheduled = false
    private var lastQueuedAt = 0L
    private val saveRunnable = Runnable {
        scheduled = false
        persist()
    }

    fun schedule(force: Boolean = false) {
        val now = SystemClock.uptimeMillis()
        if (force || now - lastQueuedAt >= 600L) {
            handler.removeCallbacks(saveRunnable)
            scheduled = false
            persist()
            return
        }
        if (!scheduled) {
            scheduled = true
            handler.postDelayed(saveRunnable, (600L - (now - lastQueuedAt)).coerceAtLeast(1L))
        }
    }

    private fun persist() {
        if (!ScriptPluginAgentSessionStore.hasConversation(state.messages.value)) return
        lastQueuedAt = SystemClock.uptimeMillis()
        ScriptPluginAgentSessionStore.saveAsync(appContext, state.snapshot())
    }
}

private object ScriptPluginAgentRuntime {
    private val sessions = java.util.concurrent.ConcurrentHashMap<String, ScriptPluginAgentSessionRuntimeState>()

    @Volatile
    private var resumeSessionId: String? = null

    fun createSession(): ScriptPluginAgentSessionRuntimeState {
        return stateFor(ScriptPluginAgentSessionStore.newSession())
    }

    fun stateFor(session: ScriptPluginAgentSession): ScriptPluginAgentSessionRuntimeState {
        return sessions.computeIfAbsent(session.id) { ScriptPluginAgentSessionRuntimeState(session) }
    }

    fun consumeResumeSession(context: Context): ScriptPluginAgentSessionRuntimeState? {
        val id = resumeSessionId
        resumeSessionId = null
        if (id != null) {
            return sessions[id] ?: ScriptPluginAgentSessionStore.load(context, id)?.let(::stateFor)
        }
        val interrupted = ScriptPluginAgentSessionStore.list(context).firstOrNull { session ->
            val checkpoint = session.resumeState?.takeIf { it.autoOpen } ?: return@firstOrNull false
            session.messages.any { message ->
                message.turnId == checkpoint.turnId &&
                    (message.status == "interrupted" || message.status == "error" ||
                        message.toolEvents.any { it.status == "interrupted" })
            }
        }
        return interrupted?.let(::stateFor)
    }

    fun resumeOnNextEntry(sessionId: String) {
        resumeSessionId = sessionId
    }

    fun snapshots(): List<ScriptPluginAgentSession> {
        return sessions.values
            .map { it.snapshot() }
            .filter { ScriptPluginAgentSessionStore.hasConversation(it.messages) }
    }

    fun isBusy(sessionId: String): Boolean {
        return sessions[sessionId]?.isBusy() == true
    }

    fun workingSessionIds(): Set<String> {
        return sessions.values
            .filter { it.isWorking() }
            .mapTo(LinkedHashSet()) { it.id }
    }

    fun updateMetadata(session: ScriptPluginAgentSession) {
        sessions[session.id]?.let { state ->
            state.title.value = session.title
            state.pinned.value = session.pinned
            state.locked.value = session.locked
            state.sortOrder.value = session.sortOrder
        }
    }

    fun remove(sessionId: String) {
        sessions.remove(sessionId)
        if (resumeSessionId == sessionId) resumeSessionId = null
    }
}

@Composable
fun ScriptPluginAgentWorkspacePage(
    context: Context,
    onBack: () -> Unit,
    onExitHandlerChanged: ((() -> Unit)?) -> Unit
) {
    val initialSessionState = remember(context) {
        ScriptPluginAgentRuntime.consumeResumeSession(context) ?: ScriptPluginAgentRuntime.createSession()
    }
    var activeSessionState by remember { mutableStateOf(initialSessionState) }
    val renderedSessionState = activeSessionState
    val activeSessionId = renderedSessionState.id
    var sessionTitle by renderedSessionState.title
    var sessionMessages by renderedSessionState.messages
    var sessionDraft by renderedSessionState.draft
    var targetPluginId by renderedSessionState.targetPluginId
    var workspaceSessionId by renderedSessionState.workspaceSessionId
    var conversationSummary by renderedSessionState.conversationSummary
    var nativeToolHistory by renderedSessionState.nativeToolHistory
    var protocolTranscript by renderedSessionState.protocolTranscript
    var compactedMessageCount by renderedSessionState.compactedMessageCount
    var sessionPinned by renderedSessionState.pinned
    var sessionLocked by renderedSessionState.locked
    var sessionSortOrder by renderedSessionState.sortOrder
    var resumeState by renderedSessionState.resumeState
    var messageInput by renderedSessionState.messageInput
    var pendingAttachments by renderedSessionState.pendingAttachments
    var pendingQuotedMessage by renderedSessionState.pendingQuotedMessage
    var pendingMessages by renderedSessionState.pendingMessages
    var pendingMessagesExpanded by renderedSessionState.pendingMessagesExpanded
    var nextPendingMessageId by renderedSessionState.nextPendingMessageId
    val initialProfile = remember(context) { ScriptPluginAgentSettings.loadActiveProfile(context) }
    val savedConfig = initialProfile.config
    var activeProfileId by remember { mutableStateOf(initialProfile.id) }
    var activeProfileName by remember { mutableStateOf(initialProfile.name) }
    var apiBase by remember { mutableStateOf(savedConfig.apiBaseUrl) }
    var endpointMode by remember { mutableStateOf(savedConfig.endpointMode) }
    var apiKey by remember { mutableStateOf(savedConfig.apiKey) }
    var model by remember { mutableStateOf(savedConfig.model) }
    var reasoningEffort by remember { mutableStateOf(savedConfig.reasoningEffort) }
    var mcpServers by remember { mutableStateOf(savedConfig.mcpServers) }
    var autoCompactEnabled by remember { mutableStateOf(savedConfig.autoCompactEnabled) }
    var webSearchEnabled by remember { mutableStateOf(savedConfig.webSearchEnabled) }
    var workspaceWriteApprovalMode by remember { mutableStateOf(savedConfig.workspaceWriteApprovalMode) }
    var promptCacheMode by remember { mutableStateOf(savedConfig.promptCacheMode) }
    var compactTokenThreshold by remember { mutableStateOf(savedConfig.compactTokenThreshold.toString()) }
    var codexProfiles by remember(context) { mutableStateOf(CodexSettings.profiles(context)) }
    var codexProfile by remember(context) { mutableStateOf(CodexSettings.active(context)) }
    var showCodexModels by remember { mutableStateOf(false) }
    var settingsTab by remember { mutableStateOf(0) }
    var testingCodexConnection by remember { mutableStateOf(false) }
    var showHistory by remember { mutableStateOf(false) }
    var showConfig by remember { mutableStateOf(false) }
    var showModelPicker by remember { mutableStateOf(false) }
    var showQuickProfilePicker by remember { mutableStateOf(false) }
    var showQuickOptions by remember { mutableStateOf(false) }
    var showSkills by remember { mutableStateOf(false) }
    var openSkillsAfterQuickOptions by remember { mutableStateOf(false) }
    var skillBackHandler by remember { mutableStateOf<(() -> Unit)?>(null) }
    var generating by renderedSessionState.generating
    var generationStartedAt by renderedSessionState.generationStartedAt
    var generationHasVisibleReply by renderedSessionState.generationHasVisibleReply
    var contextCompacting by renderedSessionState.contextCompacting
    var contextCompactionStartedAt by renderedSessionState.contextCompactionStartedAt
    var applyingFileOperation by renderedSessionState.applyingFileOperation
    var pendingWrite by renderedSessionState.pendingWrite
    var pendingDelete by renderedSessionState.pendingDelete
    var pendingWorkspaceChange by renderedSessionState.pendingWorkspaceChange
    var pendingToolApproval by renderedSessionState.pendingToolApproval
    var showPendingWorkspaceDialog by renderedSessionState.showPendingWorkspaceDialog
    var showPendingWriteDialog by renderedSessionState.showPendingWriteDialog
    var showPendingDeleteDialog by renderedSessionState.showPendingDeleteDialog
    var showToolApprovalDialog by renderedSessionState.showToolApprovalDialog
    var testingConnection by remember { mutableStateOf(false) }
    var activeGenerationId by renderedSessionState.activeGenerationId
    var activeAssistantIndex by renderedSessionState.activeAssistantIndex
    var activeCancellation by renderedSessionState.activeCancellation
    var historyVersion by remember { mutableStateOf(0) }
    var profileVersion by remember { mutableStateOf(0) }
    val sessions = remember(historyVersion, showHistory) {
        if (showHistory) ScriptPluginAgentSessionStore.list(context) else emptyList()
    }
    val profiles = remember(profileVersion) { ScriptPluginAgentSettings.loadProfiles(context) }
    val checkpointSaver = remember(renderedSessionState) {
        ScriptPluginAgentSessionCheckpointSaver(context, renderedSessionState)
    }
    val exitPrepared = remember { AtomicBoolean(false) }

    fun hasPendingConfirmation(): Boolean {
        return pendingToolApproval != null ||
            pendingWorkspaceChange != null ||
            pendingWrite != null ||
            pendingDelete != null
    }

    fun showPendingConfirmation(): Boolean {
        when {
            pendingToolApproval != null -> showToolApprovalDialog = true
            pendingWorkspaceChange != null -> showPendingWorkspaceDialog = true
            pendingWrite != null -> showPendingWriteDialog = true
            pendingDelete != null -> showPendingDeleteDialog = true
            else -> return false
        }
        return true
    }

    fun saveCurrentSession() {
        if (!ScriptPluginAgentSessionStore.hasConversation(sessionMessages)) {
            val emptySessionId = activeSessionId
            ScriptPluginAgentRuntime.remove(emptySessionId)
            Thread({
                runCatching {
                    ScriptPluginAgentSessionStore.discardEmptySession(context, emptySessionId)
                }.onFailure { error ->
                    h.Hchat.utils.HLog.e(
                        "[Hchat:ScriptAgent] 清理空会话失败: $emptySessionId",
                        error
                    )
                }
            }, "Hchat-Agent-Empty-Session-Delete").start()
            return
        }
        ScriptPluginAgentSessionStore.revive(activeSessionId)
        ScriptPluginAgentSessionStore.saveAsync(context, renderedSessionState.snapshot())
        historyVersion++
    }

    fun openSession(session: ScriptPluginAgentSession, saveCurrent: Boolean = true) {
        if (saveCurrent) {
            if (!generating) {
                resumeState = resumeState?.copy(autoOpen = false, updatedAt = System.currentTimeMillis())
            }
            saveCurrentSession()
        }
        activeSessionState = ScriptPluginAgentRuntime.stateFor(session)
        showConfig = false
        showModelPicker = false
        showQuickProfilePicker = false
        showQuickOptions = false
        showSkills = false
        showHistory = false
    }

    fun newSession(saveCurrent: Boolean = true) {
        if (saveCurrent) {
            if (!generating) {
                resumeState = resumeState?.copy(autoOpen = false, updatedAt = System.currentTimeMillis())
            }
            saveCurrentSession()
        }
        activeSessionState = ScriptPluginAgentRuntime.createSession()
        showConfig = false
        showModelPicker = false
        showQuickProfilePicker = false
        showQuickOptions = false
        showSkills = false
        showHistory = false
    }

    fun currentConfig(): ScriptPluginAgentConfig {
        return ScriptPluginAgentConfig(
            apiBaseUrl = ScriptPluginAgentSettings.normalizedApiAddress(apiBase, endpointMode),
            apiPath = "",
            apiKey = apiKey.trim(),
            model = model.trim(),
            mcpServers = mcpServers,
            autoCompactEnabled = autoCompactEnabled,
            compactTokenThreshold = compactTokenThreshold.toIntOrNull()
                ?.coerceIn(2_000, 1_000_000)
                ?: ScriptPluginAgentSettings.DEFAULT_COMPACT_TOKEN_THRESHOLD,
            webSearchEnabled = webSearchEnabled,
            workspaceWriteApprovalMode = workspaceWriteApprovalMode,
            promptCacheMode = promptCacheMode,
            endpointMode = endpointMode,
            reasoningEffort = ScriptPluginAgentReasoning.effectiveEffort(endpointMode, model, reasoningEffort)
        )
    }

    fun endpointError(config: ScriptPluginAgentConfig): String {
        if (ScriptPluginAgentSettings.isValidRequestUrl(config)) return ""
        return if (config.endpointMode == ScriptPluginAgentSettings.ENDPOINT_MODE_CUSTOM_URL) {
            "请填写完整的 HTTP(S) 请求链接"
        } else {
            "API 地址无效"
        }
    }

    fun applyProfile(profile: ScriptPluginAgentProfile) {
        activeProfileId = profile.id
        activeProfileName = profile.name
        apiBase = profile.config.apiBaseUrl
        endpointMode = profile.config.endpointMode
        apiKey = profile.config.apiKey
        model = profile.config.model
        reasoningEffort = profile.config.reasoningEffort
        mcpServers = profile.config.mcpServers
        autoCompactEnabled = profile.config.autoCompactEnabled
        webSearchEnabled = profile.config.webSearchEnabled
        workspaceWriteApprovalMode = profile.config.workspaceWriteApprovalMode
        promptCacheMode = profile.config.promptCacheMode
        compactTokenThreshold = profile.config.compactTokenThreshold.toString()
    }

    fun selectAttachments() {
        if (applyingFileOperation) return
        val activity = context as? Activity
        if (activity == null) {
            Toast.makeText(context, "当前页面无法打开文件选择器", Toast.LENGTH_SHORT).show()
            return
        }
        ScriptPluginAgentAttachmentPickerBridge.launch(
            activity,
            ScriptPluginAgentSessionStore.attachmentDir(context, activeSessionId)
        ) { selected ->
            val retained = (pendingAttachments + selected)
                .distinctBy { it.sourceUri.ifBlank { it.path } }
                .take(12)
            val retainedPaths = retained.mapTo(HashSet()) { it.path }
            ScriptPluginAgentSessionStore.cleanupAttachments(
                context,
                selected.filterNot { it.path in retainedPaths }
            )
            pendingAttachments = retained
        }
    }

    fun cancelGeneration() {
        val cancellation = activeCancellation ?: return
        val cancelledTurnId = activeGenerationId
        pendingToolApproval?.resolve(ScriptPluginAgentWorkspaceWriteDecision.CANCEL)
        pendingToolApproval = null
        showToolApprovalDialog = false
        cancellation.cancel()
        resumeState = resumeState?.copy(autoOpen = false, updatedAt = System.currentTimeMillis())
        val indices = if (activeAssistantIndex in sessionMessages.indices) {
            setOf(activeAssistantIndex)
        } else {
            sessionMessages.indices.filter { sessionMessages[it].turnId == activeGenerationId }.toSet()
        }
        if (indices.isNotEmpty()) {
            val interruptedAt = System.currentTimeMillis()
            sessionMessages = sessionMessages.toMutableList().also { messages ->
                indices.forEach { index ->
                    val current = messages[index]
                    messages[index] = current.copy(
                        status = "interrupted",
                        completedAt = interruptedAt,
                        progress = mergeScriptPluginAgentProgress(current.progress, "已中断"),
                        toolEvents = current.toolEvents.map { event ->
                            if (event.status == "running" || event.status == "queued") {
                                event.copy(
                                    status = "interrupted",
                                    progress = "已中断",
                                    finishedAt = interruptedAt
                                )
                            } else {
                                event
                            }
                        }
                    )
                }
            }
            nativeToolHistory = ScriptPluginAgentToolResultStore.rebuildNativeToolHistory(
                context,
                ScriptPluginAgentContext.modelMessagesForTurn(
                    sessionMessages.drop(compactedMessageCount.coerceIn(0, sessionMessages.size)),
                    cancelledTurnId
                )
            )
        }
        checkpointSaver.schedule(force = true)
        activeCancellation = null
        activeGenerationId = ""
        activeAssistantIndex = -1
        generating = false
        contextCompacting = false
        contextCompactionStartedAt = 0L
        generationStartedAt = 0L
        generationHasVisibleReply = false
    }

    fun recordFileOperation(messageIndex: Int, progress: String) {
        val current = sessionMessages.getOrNull(messageIndex) ?: return
        sessionMessages = sessionMessages.toMutableList().also {
            it[messageIndex] = current.copy(
                progress = mergeScriptPluginAgentProgress(current.progress, progress),
                status = "complete"
            )
        }
        saveCurrentSession()
    }

    fun appendWorkspaceStatusMessage(
        messageIndex: Int,
        content: String,
        progress: String = "",
        draftSnapshot: ScriptPluginAgentDraft? = null,
        clearsDraft: Boolean = false
    ) {
        val source = sessionMessages.getOrNull(messageIndex) ?: return
        val now = System.currentTimeMillis()
        sessionMessages = sessionMessages + ScriptPluginAgentChatMessage(
            role = "assistant",
            content = content,
            turnId = source.turnId,
            parentMessageId = source.id,
            phase = "workspace_status",
            progress = progress,
            status = "complete",
            draftSnapshot = draftSnapshot,
            clearsDraft = clearsDraft,
            createdAt = now,
            completedAt = now
        )
        saveCurrentSession()
    }

    fun applyWorkspaceChange(messageIndex: Int, change: ScriptPluginAgentWorkspaceChange) {
        if (applyingFileOperation) return
        applyingFileOperation = true
        recordFileOperation(
            messageIndex,
            if (change.deletePlugin) "正在删除插件: ${change.pluginId}" else "正在提交插件工作区变更"
        )
        Thread({
            val result = ScriptPluginAgentWorkspaceTools.apply(context, change)
            Handler(Looper.getMainLooper()).post {
                applyingFileOperation = false
                result.onSuccess {
                    resumeState = null
                    val nextDraft = change.draft
                    val clearsCurrentDraft = change.deletePlugin &&
                        (sessionDraft?.pluginId.equals(change.pluginId, ignoreCase = true) ||
                            targetPluginId.equals(change.pluginId, ignoreCase = true))
                    if (clearsCurrentDraft) {
                        sessionDraft = null
                        targetPluginId = ""
                    } else if (nextDraft != null) {
                        sessionDraft = nextDraft
                        targetPluginId = nextDraft.pluginId
                    }
                    appendWorkspaceStatusMessage(
                        messageIndex = messageIndex,
                        content = when {
                            change.deletePlugin -> "插件已删除。"
                            change.existed -> "插件修改已提交到真实插件目录。"
                            else -> "插件已创建并写入真实插件目录。"
                        },
                        progress = when {
                            change.deletePlugin -> "已删除插件: ${change.pluginId}"
                            change.existed -> "已更新插件: ${change.pluginId}"
                            else -> "已创建插件: ${change.pluginId}"
                        },
                        draftSnapshot = sessionDraft,
                        clearsDraft = clearsCurrentDraft
                    )
                    Toast.makeText(
                        context,
                        when {
                            change.deletePlugin -> "插件已删除"
                            change.existed -> "插件已更新，当前为禁用状态"
                            else -> "插件已创建，当前为禁用状态"
                        },
                        Toast.LENGTH_LONG
                    ).show()
                }.onFailure { error ->
                    val reason = error.message ?: "未知错误"
                    pendingWorkspaceChange = ScriptPluginAgentPendingWorkspaceChange(
                        messageIndex = messageIndex,
                        change = change,
                        isNewPlugin = !change.existed,
                        hasDeletedPaths = change.deletePlugin || change.deletedPaths.isNotEmpty(),
                        risky = change.warnings.any { it.risky },
                        applyError = reason
                    )
                    showPendingWorkspaceDialog = true
                    appendWorkspaceStatusMessage(
                        messageIndex = messageIndex,
                        content = "插件修改尚未提交：$reason",
                        progress = "提交失败，暂存变更已保留，可直接重试"
                    )
                    Toast.makeText(context, "$reason\n暂存修改已保留，可直接重试", Toast.LENGTH_LONG).show()
                }
            }
        }, "Hchat-Script-Agent-Workspace-Apply").start()
    }

    fun prepareWorkspaceChange(
        messageIndex: Int,
        change: ScriptPluginAgentWorkspaceChange,
        requireConfirmation: Boolean
    ) {
        appendWorkspaceStatusMessage(
            messageIndex = messageIndex,
            content = if (change.deletePlugin) {
                "插件删除操作已暂存，尚未提交。"
            } else {
                "插件修改已暂存，尚未写入真实插件目录。"
            }
        )
        if (!requireConfirmation) {
            applyWorkspaceChange(messageIndex, change)
            return
        }
        val isNewPlugin = !change.existed
        val hasDeletedPaths = change.deletePlugin || change.deletedPaths.isNotEmpty()
        val risky = change.warnings.any { it.risky }
        pendingWorkspaceChange = ScriptPluginAgentPendingWorkspaceChange(
            messageIndex = messageIndex,
            change = change,
            isNewPlugin = isNewPlugin,
            hasDeletedPaths = hasDeletedPaths,
            risky = risky
        )
        showPendingWorkspaceDialog = true
        recordFileOperation(
            messageIndex,
            when {
                change.deletePlugin -> "等待确认删除插件"
                isNewPlugin && risky -> "等待确认创建插件和高风险代码"
                isNewPlugin -> "等待确认创建插件"
                hasDeletedPaths && risky -> "等待确认删除路径和高风险代码"
                hasDeletedPaths -> "等待确认删除插件路径"
                risky -> "等待确认高风险修改"
                else -> "等待确认插件修改"
            }
        )
    }

    fun writeAgentDraft(messageIndex: Int, draft: ScriptPluginAgentDraft, isNewPlugin: Boolean) {
        if (applyingFileOperation) return
        applyingFileOperation = true
        recordFileOperation(messageIndex, if (isNewPlugin) "正在创建插件" else "正在写入插件修改")
        Thread({
            val result = ScriptPluginAgentWriter.save(context, draft, overwrite = !isNewPlugin)
            Handler(Looper.getMainLooper()).post {
                applyingFileOperation = false
                result.onSuccess {
                    resumeState = null
                    sessionDraft = draft
                    targetPluginId = draft.pluginId
                    val current = sessionMessages.getOrNull(messageIndex)
                    if (current != null) {
                        sessionMessages = sessionMessages.toMutableList().also { messages ->
                            messages[messageIndex] = current.copy(
                                progress = mergeScriptPluginAgentProgress(
                                    current.progress,
                                    if (isNewPlugin) "已创建插件: ${draft.pluginId}" else "已更新插件: ${draft.pluginId}"
                                ),
                                status = "complete",
                                draftSnapshot = draft,
                                clearsDraft = false
                            )
                        }
                    }
                    saveCurrentSession()
                    Toast.makeText(
                        context,
                        if (isNewPlugin) "插件已创建，当前为禁用状态" else "插件已更新，当前为禁用状态",
                        Toast.LENGTH_LONG
                    ).show()
                }.onFailure { error ->
                    recordFileOperation(messageIndex, "写入失败: ${error.message ?: "未知错误"}")
                    Toast.makeText(context, error.message ?: "写入插件失败", Toast.LENGTH_LONG).show()
                }
            }
        }, "Hchat-Script-Agent-Write").start()
    }

    fun prepareAgentDraft(
        messageIndex: Int,
        draft: ScriptPluginAgentDraft,
        modifyingExisting: Boolean,
        requireConfirmation: Boolean
    ) {
        val validation = ScriptPluginAgentValidator.validate(draft)
        if (!validation.canSave) {
            val errors = validation.errors.joinToString("；") { it.message }
            recordFileOperation(messageIndex, "静态检查未通过，未写入: $errors")
            Toast.makeText(context, errors, Toast.LENGTH_LONG).show()
            return
        }
        val isNewPlugin = !File(ScriptPluginRuntime.scriptDir(context), draft.pluginId).exists()
        val confirmsCreation = isNewPlugin || !modifyingExisting
        val risky = validation.warnings.any { it.risky }
        if (!requireConfirmation) {
            writeAgentDraft(messageIndex, draft, isNewPlugin)
            return
        }
        pendingWrite = ScriptPluginAgentPendingWrite(
            messageIndex = messageIndex,
            draft = draft,
            diff = sessionMessages.getOrNull(messageIndex)?.diff.orEmpty(),
            isNewPlugin = isNewPlugin,
            confirmsCreation = confirmsCreation,
            risky = risky
        )
        showPendingWriteDialog = true
        recordFileOperation(
            messageIndex,
            when {
                isNewPlugin && risky -> "等待确认创建插件和高风险代码"
                confirmsCreation && risky -> "等待确认替换同名插件和高风险代码"
                isNewPlugin -> "等待确认创建插件"
                confirmsCreation -> "等待确认替换同名插件"
                risky -> "等待确认高风险代码"
                else -> "等待确认插件修改"
            }
        )
    }

    fun deleteAgentPlugin(messageIndex: Int, pluginId: String) {
        if (applyingFileOperation) return
        applyingFileOperation = true
        recordFileOperation(messageIndex, "正在删除插件: $pluginId")
        Thread({
            val result = ScriptPluginAgentWriter.delete(context, pluginId)
            Handler(Looper.getMainLooper()).post {
                applyingFileOperation = false
                result.onSuccess {
                    resumeState = null
                    val clearsCurrentDraft = sessionDraft?.pluginId.equals(pluginId, ignoreCase = true) ||
                        targetPluginId.equals(pluginId, ignoreCase = true)
                    if (clearsCurrentDraft) {
                        sessionDraft = null
                        targetPluginId = ""
                    }
                    val current = sessionMessages.getOrNull(messageIndex)
                    if (current != null) {
                        sessionMessages = sessionMessages.toMutableList().also { messages ->
                            messages[messageIndex] = current.copy(
                                progress = mergeScriptPluginAgentProgress(current.progress, "已删除插件: $pluginId"),
                                status = "complete",
                                draftSnapshot = sessionDraft,
                                clearsDraft = clearsCurrentDraft
                            )
                        }
                    }
                    saveCurrentSession()
                    Toast.makeText(context, "插件已删除", Toast.LENGTH_LONG).show()
                }.onFailure { error ->
                    recordFileOperation(messageIndex, "删除失败: ${error.message ?: "未知错误"}")
                    Toast.makeText(context, error.message ?: "删除插件失败", Toast.LENGTH_LONG).show()
                }
            }
        }, "Hchat-Script-Agent-Delete").start()
    }

    fun preparePluginDelete(messageIndex: Int, requestedId: String, requireConfirmation: Boolean) {
        val plugin = ScriptPluginRuntime.listPlugins(context).firstOrNull {
            it.id.equals(requestedId.trim(), ignoreCase = true)
        }
        if (plugin == null) {
            recordFileOperation(messageIndex, "未找到插件目录，未执行删除")
            Toast.makeText(context, "未找到要删除的插件", Toast.LENGTH_LONG).show()
            return
        }
        if (!requireConfirmation) {
            deleteAgentPlugin(messageIndex, plugin.id)
            return
        }
        pendingDelete = ScriptPluginAgentPendingDelete(
            messageIndex = messageIndex,
            pluginId = plugin.id,
            pluginName = plugin.displayName ?: plugin.name
        )
        showPendingDeleteDialog = true
        recordFileOperation(messageIndex, "等待确认删除插件: ${plugin.id}")
    }

    fun discardResumeWorkspace(state: ScriptPluginAgentResumeState? = resumeState) {
        val checkpoint = state?.workspaceCheckpoint ?: return
        Thread({
            runCatching { ScriptPluginAgentWorkspaceTools.discard(context, checkpoint) }
                .onFailure { error ->
                    h.Hchat.utils.HLog.e("[Hchat:ScriptAgent] 清理恢复工作区失败", error)
                }
        }, "Hchat-Script-Agent-Workspace-Discard").start()
    }

    fun sendMessage(
        content: String = messageInput,
        attachments: List<ScriptPluginAgentAttachment> = pendingAttachments,
        quotedMessage: ScriptPluginAgentQuotedMessage? = pendingQuotedMessage,
        consumeComposer: Boolean = true,
        resumeFrom: ScriptPluginAgentResumeState? = null
    ): Boolean {
        if (showPendingConfirmation()) return false
        if (generating || contextCompacting || applyingFileOperation) return false
        val message = content.trim()
        if (message.isBlank() && attachments.isEmpty()) {
            Toast.makeText(context, "请输入消息", Toast.LENGTH_SHORT).show()
            return false
        }
        val config = currentConfig()
        if (config.apiBaseUrl.isBlank() || config.model.isBlank()) {
            showConfig = true
            Toast.makeText(context, "请先完成 Agent 配置", Toast.LENGTH_SHORT).show()
            return false
        }
        endpointError(config).takeIf { it.isNotBlank() }?.let { error ->
            showConfig = true
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            return false
        }
        if (config.mcpServers.any { it.enabled && it.endpoint.isBlank() }) {
            showConfig = true
            Toast.makeText(context, "请填写已启用 MCP 的 Endpoint", Toast.LENGTH_SHORT).show()
            return false
        }
        ScriptPluginAgentSettings.save(context, config)
        val startedAt = System.currentTimeMillis()
        val resumeSource = resumeFrom?.let { checkpoint ->
            sessionMessages.lastOrNull { it.role == "user" && it.id == checkpoint.sourceUserMessageId }
                ?: sessionMessages.lastOrNull { it.role == "user" && it.turnId == checkpoint.turnId }
        }
        if (resumeFrom != null && resumeSource == null) {
            Toast.makeText(context, "原任务记录已不存在，无法继续", Toast.LENGTH_LONG).show()
            return false
        }
        val runId = resumeFrom?.turnId?.takeIf { it.isNotBlank() }
            ?: java.util.UUID.randomUUID().toString()
        val userMessageId = resumeSource?.id ?: java.util.UUID.randomUUID().toString()
        val nextMessages = if (resumeSource != null) {
            sessionMessages
        } else {
            sessionMessages + ScriptPluginAgentChatMessage(
                role = "user",
                content = message.ifBlank { "请分析附件内容" },
                id = userMessageId,
                turnId = runId,
                phase = "user",
                attachments = attachments,
                quotedMessage = quotedMessage,
                createdAt = startedAt,
                completedAt = startedAt
            )
        }
        if (sessionMessages.isEmpty()) sessionTitle = ScriptPluginAgentSessionStore.titleFrom(nextMessages)
        val assistantIndex = nextMessages.size
        sessionMessages = nextMessages + ScriptPluginAgentChatMessage(
            role = "assistant",
            content = "",
            turnId = runId,
            parentMessageId = userMessageId,
            phase = "assistant",
            progress = "正在连接模型",
            status = "streaming",
            createdAt = startedAt
        )
        if (resumeFrom == null) discardResumeWorkspace()
        resumeState = resumeFrom?.copy(
            turnId = runId,
            sourceUserMessageId = userMessageId,
            autoOpen = true,
            updatedAt = startedAt
        ) ?: ScriptPluginAgentResumeState(
            turnId = runId,
            sourceUserMessageId = userMessageId,
            startedAt = startedAt,
            updatedAt = startedAt
        )
        if (consumeComposer) {
            messageInput = ""
            pendingAttachments = emptyList()
            pendingQuotedMessage = null
        }
        saveCurrentSession()
        val previousSummary = conversationSummary
        val previousProtocolTranscript = protocolTranscript
        val previousCompactedCount = compactedMessageCount.coerceIn(0, nextMessages.size)
        val workspaceBelongsToSession = workspaceSessionId == activeSessionId
        val draftSnapshot = sessionDraft.takeIf { workspaceBelongsToSession }
        val targetSnapshot = targetPluginId.takeIf { workspaceBelongsToSession }.orEmpty()
        val cancellation = ScriptPluginAgentCancellation()
        val liveAssistantIndex = AtomicInteger(assistantIndex)
        activeGenerationId = runId
        activeAssistantIndex = assistantIndex
        activeCancellation = cancellation
        generationStartedAt = startedAt
        generationHasVisibleReply = false
        generating = true
        Thread({
            var requestSummary = previousSummary
            var requestCompactedCount = previousCompactedCount
            var requestProtocolTranscript = previousProtocolTranscript
            var compactProgress = ""
            var compactApplied = false
            var compactBoundary = previousCompactedCount
            fun modelMessagesFrom(startIndex: Int): List<ScriptPluginAgentChatMessage> {
                val modeled = ScriptPluginAgentContext.modelMessagesForTurn(
                    nextMessages.drop(startIndex),
                    runId
                ).filterNot { message ->
                    resumeSource != null && message.role == "assistant" && message.turnId == runId &&
                        (message.status == "error" || message.status == "interrupted")
                }
                val source = resumeSource ?: return modeled
                return if (modeled.any { it.id == source.id }) modeled else listOf(source) + modeled
            }
            if (cancellation.isCancelled) return@Thread
            val requestMessages = modelMessagesFrom(requestCompactedCount)
            var requestNativeToolHistory = ScriptPluginAgentToolResultStore.rebuildNativeToolHistory(
                context,
                requestMessages
            )
            val request = ScriptPluginAgentRequest(
                existing = null,
                messages = requestMessages,
                currentDraft = draftSnapshot,
                targetPluginId = targetSnapshot,
                conversationSummary = requestSummary,
                compactedMessageCount = requestCompactedCount,
                nativeToolHistory = requestNativeToolHistory,
                protocolTranscript = requestProtocolTranscript,
                lockedTaskGoal = resumeFrom?.taskGoal.orEmpty(),
                agentWorkContext = resumeFrom?.workContext.orEmpty().let { previous ->
                    if (resumeFrom == null) previous else listOf(
                        previous,
                        if (resumeFrom.workspaceCheckpoint != null) {
                            "上一轮任务意外中断。客户端将校验并恢复已保存的插件暂存工作区；继续同一目标和执行位置，不得重新执行已成功的工具调用或写入。"
                        } else {
                            "上一轮任务意外中断。继续同一目标并复用已完成的工具结果，不得自动重放已成功的 MCP 或未知副作用操作。"
                        }
                    ).filter { it.isNotBlank() }.joinToString("\n").takeLast(16_000)
                },
                workspaceCheckpoint = resumeFrom?.workspaceCheckpoint,
                sessionId = activeSessionId,
                turnId = runId,
                allowedLocalPaths = ScriptPluginAgentLocalFiles.extractMentionedPaths(
                    nextMessages.asSequence()
                        .filter { it.role == "user" }
                        .joinToString("\n") { it.content }
                ).map { it.path }
            )
            val result = ScriptPluginAgentSessionStore.withMaterializedAttachments(
                context,
                request.messages.flatMap { it.attachments }
            ) {
                ScriptPluginAgentClient.generate(
                    context,
                    config,
                    request,
                    cancellation,
                    { confirmation ->
                        val approval = ScriptPluginAgentWorkspaceToolApproval(confirmation)
                        Handler(Looper.getMainLooper()).post {
                            if (activeGenerationId == runId) {
                                pendingToolApproval = approval
                                showToolApprovalDialog = true
                            } else {
                                approval.resolve(ScriptPluginAgentWorkspaceWriteDecision.CANCEL)
                            }
                        }
                        try {
                            approval.await(cancellation)
                        } finally {
                            Handler(Looper.getMainLooper()).post {
                                if (pendingToolApproval === approval) {
                                    pendingToolApproval = null
                                    showToolApprovalDialog = false
                                }
                            }
                        }
                    }
                ) { update ->
                    Handler(Looper.getMainLooper()).post {
                        if (activeGenerationId != runId) return@post
                        when (update.phase) {
                        "compaction_start" -> {
                            // All preceding tool/assistant events are already enqueued before this boundary.
                            compactBoundary = ScriptPluginAgentContext.compactionBoundary(sessionMessages)
                            contextCompacting = true
                            contextCompactionStartedAt = System.currentTimeMillis()
                        }

                        "compaction_end" -> {
                            contextCompacting = false
                            contextCompactionStartedAt = 0L
                            compactProgress = update.progress
                        }

                        "context_compacted" -> {
                            contextCompacting = false
                            contextCompactionStartedAt = 0L
                            compactApplied = true
                            compactProgress = update.progress
                            requestSummary = update.checkpointConversationSummary ?: requestSummary
                            requestProtocolTranscript = update.checkpointProtocolTranscript ?: requestProtocolTranscript
                            requestNativeToolHistory = update.checkpointNativeToolHistory ?: requestNativeToolHistory
                            requestCompactedCount = compactBoundary.coerceIn(0, sessionMessages.size)
                            conversationSummary = requestSummary
                            protocolTranscript = requestProtocolTranscript
                            nativeToolHistory = requestNativeToolHistory
                            compactedMessageCount = requestCompactedCount
                            checkpointSaver.schedule(force = true)
                        }

                        "checkpoint" -> {
                            update.resumeState?.let { resumeState = it }
                            update.checkpointNativeToolHistory?.let {
                                nativeToolHistory = it
                                requestNativeToolHistory = it
                            }
                            update.checkpointProtocolTranscript?.let {
                                protocolTranscript = it
                                requestProtocolTranscript = it
                            }
                            update.checkpointConversationSummary?.let {
                                conversationSummary = it
                                requestSummary = it
                            }
                            update.checkpointCompactedMessageCount?.let {
                                compactedMessageCount = it.coerceIn(0, sessionMessages.size)
                                requestCompactedCount = compactedMessageCount
                            }
                            checkpointSaver.schedule(force = true)
                        }

                        "protocol_checkpoint" -> {
                            update.checkpointProtocolTranscript?.let {
                                protocolTranscript = it
                                requestProtocolTranscript = it
                            }
                            checkpointSaver.schedule(force = true)
                        }

                        "assistant_start" -> {
                            val streamId = update.streamId
                            var index = if (streamId.isNotBlank()) {
                                sessionMessages.indexOfLast { message ->
                                    message.role == "assistant" && message.streamId == streamId
                                }
                            } else {
                                liveAssistantIndex.get()
                            }
                            if (index !in sessionMessages.indices) {
                                val placeholderIndex = liveAssistantIndex.get()
                                val placeholder = sessionMessages.getOrNull(placeholderIndex)
                                val canAdoptPlaceholder = placeholderIndex == sessionMessages.lastIndex &&
                                    placeholder?.role == "assistant" &&
                                    placeholder.status == "streaming" &&
                                    placeholder.streamId.isBlank() &&
                                    placeholder.content.isBlank() &&
                                    placeholder.reasoning.isBlank() &&
                                    placeholder.toolEvents.isEmpty()
                                if (canAdoptPlaceholder) {
                                    index = placeholderIndex
                                } else {
                                    val previousIndex = liveAssistantIndex.get()
                                    val previous = sessionMessages.getOrNull(previousIndex)
                                    if (previous != null && previous.role == "assistant" && previous.status == "streaming") {
                                        sessionMessages = sessionMessages.toMutableList().also { messages ->
                                            messages[previousIndex] = previous.copy(
                                                status = "complete",
                                                completedAt = System.currentTimeMillis()
                                            )
                                        }
                                    }
                                }
                            }
                            if (index !in sessionMessages.indices) {
                                sessionMessages = sessionMessages.toMutableList().also { messages ->
                                    messages += ScriptPluginAgentChatMessage(
                                        role = "assistant",
                                        content = "",
                                        turnId = update.turnId,
                                        parentMessageId = update.parentMessageId,
                                        phase = "assistant",
                                        status = "streaming",
                                        streamId = streamId
                                    )
                                }
                                index = sessionMessages.lastIndex
                            }
                            liveAssistantIndex.set(index)
                            activeAssistantIndex = index
                            val assistant = sessionMessages[index]
                            val next = assistant.copy(
                                id = streamId.ifBlank { assistant.id },
                                turnId = update.turnId.ifBlank { assistant.turnId },
                                parentMessageId = update.parentMessageId.ifBlank { assistant.parentMessageId },
                                phase = "assistant",
                                status = "streaming",
                                streamId = streamId.ifBlank { assistant.streamId }
                            )
                            if (next != assistant) sessionMessages = sessionMessages.toMutableList().also {
                                it[index] = next
                            }
                            checkpointSaver.schedule()
                        }

                        "assistant_reset" -> {
                            val index = if (update.streamId.isNotBlank()) {
                                sessionMessages.indexOfLast { message ->
                                    message.role == "assistant" && message.streamId == update.streamId
                                }
                            } else {
                                liveAssistantIndex.get()
                            }
                            val current = sessionMessages.getOrNull(index)
                            if (current != null && current.role == "assistant") {
                                sessionMessages = sessionMessages.toMutableList().also { messages ->
                                    messages.removeAt(index)
                                }
                                liveAssistantIndex.set(-1)
                                activeAssistantIndex = -1
                                checkpointSaver.schedule()
                            }
                        }

                        "working" -> {
                            // Working is a transport state. Keep it out of the assistant message body.
                        }

                        "tool_start", "tool_update" -> {
                            val event = update.toolEvents?.firstOrNull() ?: return@post
                            if (update.phase == "tool_start") {
                                val index = if (update.streamId.isNotBlank()) {
                                    sessionMessages.indexOfLast { message ->
                                        message.role == "assistant" && message.streamId == update.streamId
                                    }
                                } else {
                                    liveAssistantIndex.get()
                                }
                                val current = sessionMessages.getOrNull(index)
                                if (current != null && current.role == "assistant") {
                                    sessionMessages = sessionMessages.toMutableList().also { messages ->
                                        if (index == messages.lastIndex &&
                                            (current.content.isBlank() ||
                                                isScriptPluginAgentControlPreview(current.content)) &&
                                            current.diff.isBlank()
                                        ) {
                                            messages[index] = current.copy(
                                                content = "",
                                                status = "complete",
                                                phase = "assistant_tool_call",
                                                completedAt = System.currentTimeMillis()
                                            )
                                        } else {
                                            messages[index] = current.copy(
                                                status = "complete",
                                                completedAt = System.currentTimeMillis()
                                            )
                                        }
                                    }
                                }
                                liveAssistantIndex.set(-1)
                            }
                            val existingToolIndex = sessionMessages.indexOfLast { message ->
                                message.role == "tool" &&
                                    (message.toolEvents.any { it.id == update.toolEventId } ||
                                        (event.turnId.isNotBlank() &&
                                            message.turnId == event.turnId &&
                                            message.parentMessageId == event.parentAssistantMessageId))
                            }
                            val toolIndex = if (existingToolIndex >= 0) {
                                sessionMessages = sessionMessages.toMutableList().also { messages ->
                                    val current = messages[existingToolIndex]
                                    val nextEvents = current.toolEvents.toMutableList().apply {
                                        val eventIndex = indexOfFirst { it.id == event.id }
                                        if (eventIndex >= 0) set(eventIndex, event) else add(event)
                                    }
                                    val hasPending = nextEvents.any {
                                        it.status == "running" || it.status == "queued"
                                    }
                                    messages[existingToolIndex] = current.copy(
                                        turnId = event.turnId.ifBlank { current.turnId },
                                        parentMessageId = event.parentAssistantMessageId,
                                        phase = "tool",
                                        toolEvents = nextEvents,
                                        status = if (hasPending) {
                                            "streaming"
                                        } else {
                                            "complete"
                                        },
                                        completedAt = if (hasPending) 0L else nextEvents.maxOfOrNull {
                                            it.finishedAt
                                        } ?: event.finishedAt
                                    )
                                }
                                existingToolIndex
                            } else {
                                sessionMessages = sessionMessages + ScriptPluginAgentChatMessage(
                                    role = "tool",
                                    content = "",
                                    id = ScriptPluginAgentEventIds.toolGroup(
                                        event.turnId,
                                        event.parentAssistantMessageId
                                    ),
                                    turnId = event.turnId,
                                    parentMessageId = event.parentAssistantMessageId,
                                    phase = "tool",
                                    toolEvents = listOf(event),
                                    status = if (event.status == "running" || event.status == "queued") {
                                        "streaming"
                                    } else {
                                        "complete"
                                    },
                                    completedAt = event.finishedAt,
                                    streamId = update.streamId
                                )
                                sessionMessages.lastIndex
                            }
                            activeAssistantIndex = toolIndex
                            checkpointSaver.schedule(
                                force = event.status != "running" && event.status != "queued"
                            )
                        }

                        else -> {
                            if (update.reply.isNotBlank()) generationHasVisibleReply = true
                            val index = if (update.streamId.isNotBlank()) {
                                sessionMessages.indexOfLast { message ->
                                    message.role == "assistant" && message.streamId == update.streamId
                                }
                            } else {
                                liveAssistantIndex.get()
                            }
                            if (index !in sessionMessages.indices) return@post
                            val current = sessionMessages[index]
                            val next = current.copy(
                                content = if (update.replyRevision) {
                                    update.reply
                                } else {
                                    mergeScriptPluginAgentReply(current.content, update.reply)
                                },
                                reasoning = if (update.reasoningRevision) {
                                    update.reasoning
                                } else {
                                    ScriptPluginAgentTextMerge.mergeReply(current.reasoning, update.reasoning)
                                },
                                status = "streaming"
                            )
                            if (next != current) sessionMessages = sessionMessages.toMutableList().also {
                                it[index] = next
                            }
                            checkpointSaver.schedule()
                        }
                        }
                    }
                }
            }
            Handler(Looper.getMainLooper()).post {
                if (activeGenerationId != runId) return@post
                generating = false
                contextCompacting = false
                contextCompactionStartedAt = 0L
                generationStartedAt = 0L
                generationHasVisibleReply = false
                activeCancellation = null
                activeGenerationId = ""
                activeAssistantIndex = -1
                pendingToolApproval = null
                showToolApprovalDialog = false
                conversationSummary = requestSummary
                compactedMessageCount = requestCompactedCount
                nativeToolHistory = requestNativeToolHistory
                protocolTranscript = requestProtocolTranscript
                result.onSuccess { turn ->
                    if (turn.protocolTranscript.isNotBlank()) {
                        protocolTranscript = turn.protocolTranscript
                    }
                    if (turn.nativeToolHistory.isNotBlank()) {
                        nativeToolHistory = turn.nativeToolHistory
                    } else {
                        nativeToolHistory = ScriptPluginAgentToolResultStore.mergeNativeToolHistory(
                            context,
                            requestNativeToolHistory,
                            ScriptPluginAgentContext.modelMessagesForTurn(
                                sessionMessages.drop(requestCompactedCount),
                                runId
                            )
                        )
                    }
                    var finalAssistantIndex = liveAssistantIndex.get()
                    val finalMessage = sessionMessages.getOrNull(finalAssistantIndex)
                    if (finalAssistantIndex !in sessionMessages.indices ||
                        finalMessage?.role != "assistant" ||
                        finalAssistantIndex != sessionMessages.lastIndex
                    ) {
                        sessionMessages = sessionMessages + ScriptPluginAgentChatMessage(
                            role = "assistant",
                            content = "",
                            turnId = runId,
                            parentMessageId = userMessageId,
                            phase = "assistant",
                            status = "streaming"
                        )
                        finalAssistantIndex = sessionMessages.lastIndex
                        liveAssistantIndex.set(finalAssistantIndex)
                    }
                    val previousDraft = sessionDraft
                    val nextDraft = turn.draft?.let { value ->
                        ScriptPluginAgentValidator.normalize(
                            if (turn.targetPluginId.isNotBlank()) value.copy(pluginId = turn.targetPluginId) else value
                        )
                    }
                    val actualDiff = nextDraft?.let { ScriptPluginAgentDiff.between(previousDraft, it) }.orEmpty()
                    val currentMessage = sessionMessages.getOrNull(finalAssistantIndex)
                    val displayedToolEventIds = sessionMessages.asSequence()
                        .flatMap { message -> message.toolEvents.asSequence() }
                        .map { event -> event.id }
                        .toSet()
                    val missingToolEvents = turn.toolEvents.filterNot { event ->
                        event.id in displayedToolEventIds
                    }
                    val completedMessage = (currentMessage ?: ScriptPluginAgentChatMessage("assistant", ""))
                        .copy(
                        content = mergeScriptPluginAgentReply(currentMessage?.content.orEmpty(), turn.reply),
                        progress = listOf(
                            compactProgress.takeIf { compactApplied }.orEmpty(),
                            turn.progress
                        ).fold(currentMessage?.progress.orEmpty()) {
                            current, next -> mergeScriptPluginAgentProgress(current, next)
                        },
                        diff = turn.diff.ifBlank { actualDiff },
                        toolEvents = (currentMessage?.toolEvents.orEmpty() + missingToolEvents)
                            .distinctBy { event -> event.id },
                        status = "complete",
                        completedAt = System.currentTimeMillis(),
                        phase = "assistant",
                        draftSnapshot = null,
                        clearsDraft = false
                    )
                    sessionMessages = sessionMessages.toMutableList().also {
                        if (finalAssistantIndex in it.indices) it[finalAssistantIndex] = completedMessage
                        else it += completedMessage
                    }
                    val awaitsFileOperation =
                        (turn.status.equals("workspace_ready", ignoreCase = true) && turn.workspaceChange != null) ||
                            turn.status.equals("delete", ignoreCase = true) ||
                            (turn.status.equals("ready", ignoreCase = true) && nextDraft != null)
                    if (!awaitsFileOperation) resumeState = null
                    if (turn.title.isNotBlank() && sessionMessages.count { it.role == "user" } <= 1) {
                        sessionTitle = turn.title.take(32)
                    }
                    when {
                        turn.status.equals("workspace_ready", ignoreCase = true) && turn.workspaceChange != null -> {
                            prepareWorkspaceChange(
                                finalAssistantIndex,
                                turn.workspaceChange,
                                workspaceWriteApprovalMode == ScriptPluginAgentSettings.WRITE_APPROVAL_ASK
                            )
                        }
                        turn.status.equals("delete", ignoreCase = true) -> {
                            preparePluginDelete(
                                finalAssistantIndex,
                                turn.targetPluginId,
                                workspaceWriteApprovalMode == ScriptPluginAgentSettings.WRITE_APPROVAL_ASK
                            )
                        }
                        turn.status.equals("ready", ignoreCase = true) && nextDraft != null -> {
                            val modifyingExisting = turn.targetPluginId.isNotBlank() ||
                                sessionDraft?.pluginId.equals(nextDraft.pluginId, ignoreCase = true)
                            prepareAgentDraft(
                                finalAssistantIndex,
                                nextDraft,
                                modifyingExisting,
                                workspaceWriteApprovalMode == ScriptPluginAgentSettings.WRITE_APPROVAL_ASK
                            )
                        }
                        else -> {
                            if (turn.targetPluginId.isNotBlank()) targetPluginId = turn.targetPluginId
                            saveCurrentSession()
                        }
                    }
                }.onFailure { error ->
                    var failureIndex = liveAssistantIndex.get()
                    val failureMessage = sessionMessages.getOrNull(failureIndex)
                    if (failureIndex !in sessionMessages.indices ||
                        failureMessage?.role != "assistant" ||
                        failureIndex != sessionMessages.lastIndex
                    ) {
                        sessionMessages = sessionMessages + ScriptPluginAgentChatMessage(
                            role = "assistant",
                            content = "",
                            turnId = runId,
                            parentMessageId = userMessageId,
                            phase = "assistant",
                            status = "error"
                        )
                        failureIndex = sessionMessages.lastIndex
                    }
                    val currentMessage = sessionMessages.getOrNull(failureIndex)
                    if (currentMessage != null) {
                        sessionMessages = sessionMessages.toMutableList().also { messages ->
                            messages[failureIndex] = currentMessage.copy(
                                status = if (cancellation.isCancellation(error)) "interrupted" else "error",
                                completedAt = System.currentTimeMillis(),
                                progress = mergeScriptPluginAgentProgress(
                                    currentMessage.progress,
                                    if (cancellation.isCancellation(error)) "已中断" else "请求失败"
                                )
                            )
                        }
                    }
                    nativeToolHistory = ScriptPluginAgentToolResultStore.mergeNativeToolHistory(
                        context,
                        requestNativeToolHistory,
                        ScriptPluginAgentContext.modelMessagesForTurn(
                            sessionMessages.drop(requestCompactedCount),
                            runId
                        )
                    )
                    saveCurrentSession()
                    if (!cancellation.isCancellation(error)) {
                        Toast.makeText(context, error.message ?: "请求失败", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }, "Hchat-Script-Agent-Chat").start()
        return true
    }

    fun queueMessage() {
        if (!generating || applyingFileOperation) return
        val message = messageInput.trim()
        if (message.isBlank() && pendingAttachments.isEmpty()) return
        pendingMessages = pendingMessages + ScriptPluginAgentPendingMessage(
            id = nextPendingMessageId++,
            content = message,
            attachments = pendingAttachments,
            quotedMessage = pendingQuotedMessage
        )
        pendingMessagesExpanded = true
        messageInput = ""
        pendingAttachments = emptyList()
        pendingQuotedMessage = null
        Toast.makeText(context, "已加入待发送队列", Toast.LENGTH_SHORT).show()
    }

    fun editPendingMessage(id: Long) {
        val pending = pendingMessages.firstOrNull { it.id == id } ?: return
        pendingMessages = pendingMessages.filterNot { it.id == id }
        messageInput = pending.content
        pendingAttachments = pending.attachments
        pendingQuotedMessage = pending.quotedMessage
    }

    fun sendPendingMessageNow(id: Long) {
        val pending = pendingMessages.firstOrNull { it.id == id } ?: return
        pendingMessages = listOf(pending) + pendingMessages.filterNot { it.id == id }
        if (generating) cancelGeneration()
    }

    fun compactContext() {
        if (generating || contextCompacting || applyingFileOperation) return
        if (showPendingConfirmation()) return
        val start = compactedMessageCount.coerceIn(0, sessionMessages.size)
        val messagesToCompact = sessionMessages.drop(start)
        if (messagesToCompact.isEmpty()) {
            Toast.makeText(context, "没有需要压缩的新上下文", Toast.LENGTH_SHORT).show()
            return
        }
        val config = currentConfig()
        val summarySnapshot = conversationSummary
        val protocolTranscriptSnapshot = protocolTranscript
        val draftSnapshot = sessionDraft
        val targetSnapshot = targetPluginId
        val compactedSize = sessionMessages.size
        val runId = "compact:${java.util.UUID.randomUUID()}"
        val cancellation = ScriptPluginAgentCancellation()
        contextCompacting = true
        contextCompactionStartedAt = System.currentTimeMillis()
        activeGenerationId = runId
        activeAssistantIndex = -1
        activeCancellation = cancellation
        Toast.makeText(context, "正在压缩上下文", Toast.LENGTH_SHORT).show()
        Thread({
            val nativeHistorySnapshot = ScriptPluginAgentToolResultStore.rebuildNativeToolHistory(
                context,
                messagesToCompact
            )
            val estimatedTokensBefore = ScriptPluginAgentContext.estimateTokens(
                summarySnapshot,
                messagesToCompact,
                draftSnapshot,
                nativeHistorySnapshot,
                protocolTranscriptSnapshot
            )
            val result = ScriptPluginAgentClient.compact(
                config = config,
                previousSummary = summarySnapshot,
                messages = messagesToCompact,
                currentDraft = draftSnapshot,
                targetPluginId = targetSnapshot,
                cancellation = cancellation,
                protocolTranscript = protocolTranscriptSnapshot
            )
            Handler(Looper.getMainLooper()).post {
                if (activeGenerationId != runId) return@post
                contextCompacting = false
                contextCompactionStartedAt = 0L
                activeGenerationId = ""
                activeAssistantIndex = -1
                activeCancellation = null
                result.onSuccess { summary ->
                    val estimatedTokensAfter = ScriptPluginAgentContext.estimateTokens(
                        summary,
                        emptyList(),
                        draftSnapshot,
                        ""
                    )
                    if (estimatedTokensAfter < estimatedTokensBefore) {
                        conversationSummary = summary
                        nativeToolHistory = ""
                        protocolTranscript = ""
                        compactedMessageCount = compactedSize.coerceAtMost(sessionMessages.size)
                        saveCurrentSession()
                        Toast.makeText(
                            context,
                            "上下文已压缩：估算 $estimatedTokensBefore → $estimatedTokensAfter Token",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            context,
                            "压缩结果未减少上下文，已保留原上下文",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }.onFailure {
                    Toast.makeText(context, it.message ?: "上下文压缩失败", Toast.LENGTH_LONG).show()
                }
            }
        }, "Hchat-Script-Agent-Compact").start()
    }

    fun saveConfig() {
        val threshold = compactTokenThreshold.toIntOrNull()
        if (threshold == null || threshold !in 2_000..1_000_000) {
            Toast.makeText(context, "自动压缩阈值需为 2000 到 1000000", Toast.LENGTH_SHORT).show()
            return
        }
        val config = currentConfig()
        if (config.apiBaseUrl.isBlank() || config.model.isBlank()) {
            Toast.makeText(context, "API 地址和模型不能为空", Toast.LENGTH_SHORT).show()
            return
        }
        endpointError(config).takeIf { it.isNotBlank() }?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            return
        }
        if (config.mcpServers.any { it.enabled && it.endpoint.isBlank() }) {
            Toast.makeText(context, "已启用 MCP 的 Endpoint 不能为空", Toast.LENGTH_SHORT).show()
            return
        }
        ScriptPluginAgentSettings.save(context, config)
        apiBase = config.apiBaseUrl
        model = config.model
        reasoningEffort = config.reasoningEffort
        val activeProfile = ScriptPluginAgentSettings.loadActiveProfile(context)
        activeProfileId = activeProfile.id
        activeProfileName = activeProfile.name
        profileVersion++
        showConfig = false
        Toast.makeText(context, "Agent 配置已保存", Toast.LENGTH_SHORT).show()
    }

    fun refreshCodexProfiles() {
        codexProfiles = CodexSettings.profiles(context)
        codexProfile = CodexSettings.active(context)
    }

    fun saveCodexConfig() {
        if (codexProfile.baseUrl.isBlank() || codexProfile.model.isBlank()) {
            Toast.makeText(context, "codex 的 API 地址和模型不能为空", Toast.LENGTH_SHORT).show()
            return
        }
        CodexSettings.save(context, codexProfile)
        refreshCodexProfiles()
        Toast.makeText(context, "codex 配置已保存", Toast.LENGTH_SHORT).show()
    }

    fun selectCodexProfile(id: String) {
        CodexSettings.setActive(context, id)
        refreshCodexProfiles()
    }

    fun createCodexProfile(name: String) {
        CodexSettings.createProfile(context, name)
        refreshCodexProfiles()
    }

    fun renameCodexProfile(name: String) {
        CodexSettings.renameProfile(context, codexProfile.id, name)
        refreshCodexProfiles()
    }

    fun deleteCodexProfile() {
        CodexSettings.deleteProfile(context, codexProfile.id)
        refreshCodexProfiles()
    }

    fun testCodexConnection() {
        if (testingCodexConnection) return
        val profile = codexProfile
        if (profile.baseUrl.isBlank() || profile.apiKey.isBlank()) {
            Toast.makeText(context, "请先填写 API 地址和模型密钥", Toast.LENGTH_SHORT).show()
            return
        }
        testingCodexConnection = true
        Toast.makeText(context, "正在测试连接", Toast.LENGTH_SHORT).show()
        Thread({
            val result = CodexSettings.testConnection(profile.baseUrl, profile.apiKey)
            Handler(Looper.getMainLooper()).post {
                testingCodexConnection = false
                result.onSuccess {
                    Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                }.onFailure {
                    Toast.makeText(context, CodexSettings.friendlyError(it), Toast.LENGTH_LONG).show()
                }
            }
        }, "Hchat-Codex-Test").start()
    }

    fun selectProfile(profile: ScriptPluginAgentProfile) {
        if (profile.id == activeProfileId) return
        val appliesToNextRequest = generating
        ScriptPluginAgentSettings.save(context, currentConfig())
        ScriptPluginAgentSettings.setActiveProfile(context, profile.id)
        applyProfile(ScriptPluginAgentSettings.loadActiveProfile(context))
        profileVersion++
        Toast.makeText(
            context,
            if (appliesToNextRequest) "已切换配置，将用于下一次请求" else "已切换配置",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun setWebSearchEnabled(enabled: Boolean) {
        val nextConfig = currentConfig().copy(webSearchEnabled = enabled)
        webSearchEnabled = enabled
        ScriptPluginAgentSettings.save(context, nextConfig)
        profileVersion++
    }

    fun setMcpServerEnabled(id: String, enabled: Boolean) {
        val nextServers = mcpServers.map { server ->
            if (server.id == id) server.copy(enabled = enabled) else server
        }
        mcpServers = nextServers
        ScriptPluginAgentSettings.save(context, currentConfig().copy(mcpServers = nextServers))
        profileVersion++
    }

    fun setWorkspaceWriteApprovalMode(mode: String) {
        workspaceWriteApprovalMode = mode
        ScriptPluginAgentSettings.save(
            context,
            currentConfig().copy(workspaceWriteApprovalMode = mode)
        )
        profileVersion++
    }

    fun setPromptCacheMode(mode: String) {
        promptCacheMode = mode
        ScriptPluginAgentSettings.save(
            context,
            currentConfig().copy(promptCacheMode = mode)
        )
        profileVersion++
    }

    fun setReasoningEffort(effort: String) {
        val selected = ScriptPluginAgentReasoning.effectiveEffort(endpointMode, model, effort)
        ScriptPluginAgentSettings.save(context, currentConfig().copy(reasoningEffort = selected))
        reasoningEffort = selected
        profileVersion++
        Toast.makeText(context, "推理强度已保存，将用于下一次请求", Toast.LENGTH_SHORT).show()
    }

    fun createProfile(name: String) {
        runCatching {
            ScriptPluginAgentSettings.save(context, currentConfig())
            val created = ScriptPluginAgentSettings.createProfile(context, name)
            applyProfile(created)
            profileVersion++
        }.onFailure {
            Toast.makeText(context, it.message ?: "新建配置失败", Toast.LENGTH_SHORT).show()
        }
    }

    fun renameProfile(name: String) {
        runCatching {
            val renamed = ScriptPluginAgentSettings.renameProfile(context, activeProfileId, name)
            activeProfileName = renamed.name
            profileVersion++
        }.onFailure {
            Toast.makeText(context, it.message ?: "重命名失败", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteActiveProfile() {
        runCatching {
            ScriptPluginAgentSettings.save(context, currentConfig())
            val next = ScriptPluginAgentSettings.deleteProfile(context, activeProfileId)
            applyProfile(next)
            profileVersion++
        }.onFailure {
            Toast.makeText(context, it.message ?: "删除配置失败", Toast.LENGTH_SHORT).show()
        }
    }

    fun testAgentConnection() {
        if (testingConnection) return
        val config = currentConfig()
        if (config.apiBaseUrl.isBlank() || config.model.isBlank()) {
            Toast.makeText(context, "请先填写 API 地址和模型", Toast.LENGTH_SHORT).show()
            return
        }
        endpointError(config).takeIf { it.isNotBlank() }?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            return
        }
        testingConnection = true
        Toast.makeText(context, "正在测试连接", Toast.LENGTH_SHORT).show()
        Thread({
            val result = ScriptPluginAgentClient.testConnection(config)
            Handler(Looper.getMainLooper()).post {
                testingConnection = false
                result.onSuccess {
                    Toast.makeText(context, "连接成功", Toast.LENGTH_SHORT).show()
                }.onFailure {
                    Toast.makeText(context, it.message ?: "连接失败", Toast.LENGTH_LONG).show()
                }
            }
        }, "Hchat-Script-Agent-Test").start()
    }

    fun draftForMessages(messages: List<ScriptPluginAgentChatMessage>): ScriptPluginAgentDraft? {
        return messages.asReversed()
            .firstOrNull { it.draftSnapshot != null || it.clearsDraft }
            ?.draftSnapshot
    }

    fun rebuildNativeHistory(messages: List<ScriptPluginAgentChatMessage>): String {
        return ScriptPluginAgentToolResultStore.rebuildNativeToolHistory(context, messages)
    }

    fun deleteToolResults(messages: List<ScriptPluginAgentChatMessage>) {
        messages.asSequence()
            .flatMap { it.toolEvents.asSequence() }
            .map { it.resultHandle }
            .filter { it.isNotBlank() }
            .distinct()
            .forEach { ScriptPluginAgentToolResultStore.deleteHandle(context, it) }
    }

    fun prepareMessageHistoryChange(
        retainedMessages: List<ScriptPluginAgentChatMessage>,
        removedMessages: List<ScriptPluginAgentChatMessage>,
        actionName: String,
        onReady: (String) -> Boolean
    ) {
        val mutationState = renderedSessionState
        val sourceMessages = mutationState.messages.value
        mutationState.applyingFileOperation.value = true
        Thread({
            val historyResult = runCatching { rebuildNativeHistory(retainedMessages) }
            Handler(Looper.getMainLooper()).post {
                if (mutationState.messages.value !== sourceMessages) {
                    mutationState.applyingFileOperation.value = false
                    Toast.makeText(context, "会话内容已变化，请重试", Toast.LENGTH_SHORT).show()
                    return@post
                }
                historyResult.fold(
                    onSuccess = { restoredHistory ->
                        mutationState.applyingFileOperation.value = false
                        val applied = runCatching { onReady(restoredHistory) }
                            .onFailure { error ->
                                h.Hchat.utils.HLog.e(
                                    "[Hchat:ScriptAgent] ${actionName}失败",
                                    error
                                )
                                Toast.makeText(context, "${actionName}失败，请重试", Toast.LENGTH_SHORT).show()
                            }
                            .getOrDefault(false)
                        if (applied) {
                            val snapshot = mutationState.snapshot()
                            val sessionId = mutationState.id
                            Thread({
                                val saveResult = runCatching {
                                    if (ScriptPluginAgentSessionStore.hasConversation(snapshot.messages)) {
                                        ScriptPluginAgentSessionStore.revive(sessionId)
                                        ScriptPluginAgentSessionStore.save(context, snapshot)
                                    } else {
                                        ScriptPluginAgentSessionStore.delete(context, sessionId)
                                    }
                                }
                                saveResult.onSuccess {
                                    runCatching {
                                        if (removedMessages.isNotEmpty()) deleteToolResults(removedMessages)
                                    }.onFailure { error ->
                                        h.Hchat.utils.HLog.e(
                                            "[Hchat:ScriptAgent] ${actionName}清理工具结果失败",
                                            error
                                        )
                                    }
                                }.onFailure { error ->
                                    h.Hchat.utils.HLog.e(
                                        "[Hchat:ScriptAgent] ${actionName}保存会话失败",
                                        error
                                    )
                                    Handler(Looper.getMainLooper()).post {
                                        Toast.makeText(
                                            context,
                                            "${actionName}已完成，但会话保存失败",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            }, "Hchat-Agent-History-Save").start()
                        }
                    },
                    onFailure = { error ->
                        mutationState.applyingFileOperation.value = false
                        h.Hchat.utils.HLog.e("[Hchat:ScriptAgent] ${actionName}重建历史失败", error)
                        Toast.makeText(context, "${actionName}失败，请重试", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }, "Hchat-Agent-History-Change").start()
    }

    fun removeMessageTree(index: Int): Pair<List<ScriptPluginAgentChatMessage>, List<ScriptPluginAgentChatMessage>> {
        val root = sessionMessages[index]
        val removedIds = linkedSetOf(root.id)
        var changed: Boolean
        do {
            changed = false
            sessionMessages.forEach { message ->
                if (message.id !in removedIds && message.parentMessageId in removedIds) {
                    removedIds += message.id
                    changed = true
                }
            }
        } while (changed)
        return sessionMessages.partition { it.id !in removedIds }
    }

    fun createMessageBranch(index: Int) {
        if (generating || contextCompacting || applyingFileOperation || hasPendingConfirmation() ||
            index !in sessionMessages.indices
        ) return
        val sourceMessages = sessionMessages.take(index + 1)
        val sourceSessionId = activeSessionId
        val sourceTitle = sessionTitle
        val sourceTargetPluginId = targetPluginId
        val branchId = java.util.UUID.randomUUID().toString().replace("-", "")
        applyingFileOperation = true
        Thread({
            val result = runCatching {
                val branchMessages = ScriptPluginAgentToolResultStore.copySessionResults(
                    context,
                    sourceSessionId,
                    branchId,
                    sourceMessages
                )
                val branchDraft = draftForMessages(branchMessages)
                val now = System.currentTimeMillis()
                ScriptPluginAgentSession(
                    id = branchId,
                    title = (sourceTitle.ifBlank { "新对话" } + " 分支").take(32),
                    createdAt = now,
                    updatedAt = now,
                    messages = branchMessages,
                    draft = branchDraft,
                    targetPluginId = branchDraft?.pluginId?.ifBlank { sourceTargetPluginId }
                        ?: sourceTargetPluginId,
                    conversationSummary = "",
                    nativeToolHistory = rebuildNativeHistory(branchMessages),
                    protocolTranscript = "",
                    compactedMessageCount = 0
                ).also { branch -> ScriptPluginAgentSessionStore.save(context, branch) }
            }
            Handler(Looper.getMainLooper()).post {
                applyingFileOperation = false
                result.onSuccess { branch ->
                    openSession(branch)
                    Toast.makeText(context, "已从此处创建分支", Toast.LENGTH_SHORT).show()
                    historyVersion++
                }.onFailure { error ->
                    h.Hchat.utils.HLog.e("[Hchat:ScriptAgent] 创建会话分支失败", error)
                    Toast.makeText(context, error.message ?: "创建分支失败", Toast.LENGTH_LONG).show()
                }
            }
        }, "Hchat-Agent-Create-Branch").start()
    }

    fun editAndResendMessage(index: Int) {
        if (generating || contextCompacting || applyingFileOperation || hasPendingConfirmation() ||
            index !in sessionMessages.indices
        ) return
        val selected = sessionMessages[index]
        if (selected.role != "user") return
        val sourceMessages = sessionMessages
        val retainedMessages = sourceMessages.take(index)
        val removedMessages = sourceMessages.drop(index)
        val restoredDraft = draftForMessages(retainedMessages)
        val previousResumeState = resumeState
        prepareMessageHistoryChange(
            retainedMessages = retainedMessages,
            removedMessages = removedMessages,
            actionName = "编辑重发"
        ) { restoredHistory ->
            sessionMessages = retainedMessages
            sessionDraft = restoredDraft
            conversationSummary = ""
            nativeToolHistory = restoredHistory
            protocolTranscript = ""
            discardResumeWorkspace(previousResumeState)
            resumeState = null
            compactedMessageCount = 0
            messageInput = selected.content
            pendingAttachments = selected.attachments
            pendingQuotedMessage = selected.quotedMessage
            if (retainedMessages.isEmpty()) historyVersion++
            Toast.makeText(context, "已覆盖后续记录并填入原消息", Toast.LENGTH_SHORT).show()
            true
        }
    }

    fun quoteAgentMessage(index: Int) {
        val selected = sessionMessages.getOrNull(index) ?: return
        val quotedContent = selected.content.trim()
        if (quotedContent.isBlank()) {
            Toast.makeText(context, "当前消息没有可引用内容", Toast.LENGTH_SHORT).show()
            return
        }
        pendingQuotedMessage = ScriptPluginAgentQuotedMessage(
            role = selected.role,
            content = quotedContent.take(24_000),
            createdAt = selected.createdAt
        )
    }

    fun regenerateAssistantMessage(index: Int) {
        if (generating || contextCompacting || applyingFileOperation || hasPendingConfirmation() ||
            index !in sessionMessages.indices
        ) return
        val selected = sessionMessages[index]
        val sourceIndex = (index - 1 downTo 0).firstOrNull { sessionMessages[it].role == "user" } ?: return
        val source = sessionMessages[sourceIndex]
        if (selected.role != "assistant") return
        val config = currentConfig()
        if (config.apiBaseUrl.isBlank() || config.model.isBlank()) {
            showConfig = true
            Toast.makeText(context, "请先完成 Agent 配置", Toast.LENGTH_SHORT).show()
            return
        }
        endpointError(config).takeIf { it.isNotBlank() }?.let { error ->
            showConfig = true
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            return
        }
        if (config.mcpServers.any { it.enabled && it.endpoint.isBlank() }) {
            showConfig = true
            Toast.makeText(context, "请填写已启用 MCP 的 Endpoint", Toast.LENGTH_SHORT).show()
            return
        }
        val sourceMessages = sessionMessages
        val retainedMessages = sourceMessages.take(sourceIndex)
        val removedMessages = sourceMessages.drop(sourceIndex)
        val restoredDraft = draftForMessages(retainedMessages)
        val previousResumeState = resumeState
        prepareMessageHistoryChange(
            retainedMessages = retainedMessages,
            removedMessages = removedMessages,
            actionName = "重新生成"
        ) { restoredHistory ->
            sessionMessages = retainedMessages
            sessionDraft = restoredDraft
            targetPluginId = restoredDraft?.pluginId.orEmpty()
            conversationSummary = ""
            nativeToolHistory = restoredHistory
            protocolTranscript = ""
            discardResumeWorkspace(previousResumeState)
            resumeState = null
            compactedMessageCount = 0
            val started = sendMessage(
                content = source.content,
                attachments = source.attachments,
                quotedMessage = source.quotedMessage,
                consumeComposer = false
            )
            if (started) Toast.makeText(context, "正在重新生成", Toast.LENGTH_SHORT).show()
            started
        }
    }

    fun continueInterruptedTask(index: Int) {
        if (generating || contextCompacting || applyingFileOperation || index !in sessionMessages.indices) return
        val selected = sessionMessages[index]
        val turnInterrupted = selected.status == "interrupted" || selected.status == "error" ||
            sessionMessages.any { message ->
                message.turnId == selected.turnId && (
                    message.status == "interrupted" || message.status == "error" ||
                        message.toolEvents.any { it.status == "interrupted" }
                )
            }
        if (!turnInterrupted) return
        val sourceIndex = (index downTo 0).firstOrNull { candidate ->
            val message = sessionMessages[candidate]
            message.role == "user" && (
                selected.turnId.isBlank() || message.turnId == selected.turnId
            )
        } ?: return
        val source = sessionMessages[sourceIndex]
        val savedCheckpoint = resumeState?.takeIf { it.turnId == selected.turnId }
        val hasLaterTurnMessages = sessionMessages.drop(index + 1).any {
            it.turnId == selected.turnId
        }
        if (savedCheckpoint == null && hasLaterTurnMessages) {
            Toast.makeText(context, "该中断记录已经继续处理", Toast.LENGTH_SHORT).show()
            return
        }
        val checkpoint = savedCheckpoint
            ?: ScriptPluginAgentResumeState(
                turnId = selected.turnId,
                sourceUserMessageId = source.id,
                startedAt = source.createdAt,
                updatedAt = System.currentTimeMillis()
            )
        val started = sendMessage(
            content = source.content,
            attachments = emptyList(),
            quotedMessage = null,
            consumeComposer = false,
            resumeFrom = checkpoint
        )
        if (started) Toast.makeText(context, "正在从已保存进度继续", Toast.LENGTH_SHORT).show()
    }

    fun rollbackToMessage(messageId: String) {
        val index = sessionMessages.indexOfFirst { it.id == messageId }
        if (generating || contextCompacting || applyingFileOperation || hasPendingConfirmation() ||
            index < 0
        ) return
        val sourceMessages = sessionMessages
        val retainedMessages = sourceMessages.take(index + 1)
        val removedMessages = sourceMessages.drop(index + 1)
        val restoredDraft = draftForMessages(retainedMessages)
        val rollbackResumeState = resumeState
        prepareMessageHistoryChange(
            retainedMessages = retainedMessages,
            removedMessages = removedMessages,
            actionName = "回滚会话"
        ) { restoredHistory ->
            sessionMessages = retainedMessages
            sessionDraft = restoredDraft
            conversationSummary = ""
            nativeToolHistory = restoredHistory
            protocolTranscript = ""
            discardResumeWorkspace(rollbackResumeState)
            resumeState = null
            compactedMessageCount = 0
            Toast.makeText(context, "已回滚到所选消息", Toast.LENGTH_SHORT).show()
            true
        }
    }

    fun deleteAgentMessage(index: Int) {
        if (generating || contextCompacting || applyingFileOperation || hasPendingConfirmation() ||
            index !in sessionMessages.indices
        ) return
        val hadLaterMessages = index < sessionMessages.lastIndex
        val (remainingMessages, removedMessages) = removeMessageTree(index)
        val restoredDraft = draftForMessages(remainingMessages)
        val nextDraft = if (restoredDraft != null || !hadLaterMessages) restoredDraft else sessionDraft
        val previousResumeState = resumeState
        prepareMessageHistoryChange(
            retainedMessages = remainingMessages,
            removedMessages = removedMessages,
            actionName = "删除消息"
        ) { restoredHistory ->
            sessionMessages = remainingMessages
            sessionDraft = nextDraft
            conversationSummary = ""
            nativeToolHistory = restoredHistory
            protocolTranscript = ""
            discardResumeWorkspace(previousResumeState)
            resumeState = null
            compactedMessageCount = 0
            if (remainingMessages.isEmpty()) {
                historyVersion++
            }
            Toast.makeText(context, "消息已删除", Toast.LENGTH_SHORT).show()
            true
        }
    }

    fun currentSessionSnapshot(updatedAt: Long = System.currentTimeMillis()): ScriptPluginAgentSession {
        return renderedSessionState.snapshot(updatedAt)
    }

    fun persistSession(session: ScriptPluginAgentSession) {
        ScriptPluginAgentSessionStore.save(context, session)
        ScriptPluginAgentRuntime.updateMetadata(session)
        if (session.id == activeSessionId) {
            sessionTitle = session.title
            sessionPinned = session.pinned
            sessionLocked = session.locked
            sessionSortOrder = session.sortOrder
        }
        historyVersion++
    }

    fun renameSession(session: ScriptPluginAgentSession, title: String) {
        val cleanTitle = title.replace(Regex("\\s+"), " ").trim().take(32)
        if (cleanTitle.isBlank()) return
        val source = if (session.id == activeSessionId) {
            currentSessionSnapshot(session.updatedAt)
        } else {
            session
        }
        persistSession(source.copy(title = cleanTitle))
    }

    fun setSessionPinned(session: ScriptPluginAgentSession, pinned: Boolean) {
        val source = if (session.id == activeSessionId) {
            currentSessionSnapshot(session.updatedAt)
        } else {
            session
        }
        val nextOrder = if (pinned) {
            (ScriptPluginAgentSessionStore.list(context).maxOfOrNull { it.sortOrder } ?: source.sortOrder) + 1L
        } else {
            source.sortOrder
        }
        persistSession(source.copy(pinned = pinned, sortOrder = nextOrder))
    }

    fun setSessionLocked(session: ScriptPluginAgentSession, locked: Boolean) {
        val source = if (session.id == activeSessionId) {
            currentSessionSnapshot(session.updatedAt)
        } else {
            session
        }
        persistSession(source.copy(locked = locked))
    }

    fun persistSessionOrder(ordered: List<ScriptPluginAgentSession>) {
        val topOrder = (ordered.maxOfOrNull { it.sortOrder } ?: System.currentTimeMillis()) + ordered.size + 1L
        val reordered = ordered.mapIndexed { index, item -> item.copy(sortOrder = topOrder - index) }
        reordered.firstOrNull { it.id == activeSessionId }?.let { sessionSortOrder = it.sortOrder }
        Thread({
            runCatching {
                reordered.forEach { ScriptPluginAgentSessionStore.save(context, it) }
            }.onSuccess {
                Handler(Looper.getMainLooper()).post { historyVersion++ }
            }.onFailure { error ->
                h.Hchat.utils.HLog.e("[Hchat:ScriptAgent] 保存历史会话排序失败", error)
            }
        }, "Hchat-Agent-Session-Order").start()
    }

    fun deleteSession(session: ScriptPluginAgentSession) {
        if (session.locked) {
            Toast.makeText(context, "请先解锁该对话", Toast.LENGTH_SHORT).show()
            return
        }
        if (ScriptPluginAgentRuntime.isBusy(session.id)) {
            Toast.makeText(context, "该对话正在运行或有待确认操作，请先处理后再删除", Toast.LENGTH_SHORT).show()
            return
        }
        discardResumeWorkspace(
            if (session.id == activeSessionId) resumeState else session.resumeState
        )
        ScriptPluginAgentSessionStore.delete(context, session.id)
        ScriptPluginAgentRuntime.remove(session.id)
        historyVersion++
        if (session.id == activeSessionId) {
            val next = ScriptPluginAgentSessionStore.list(context).firstOrNull()
            if (next == null) newSession(saveCurrent = false) else openSession(next, saveCurrent = false)
        }
    }

    fun prepareForExit() {
        if (!exitPrepared.compareAndSet(false, true)) return
        val hasConversation = ScriptPluginAgentSessionStore.hasConversation(sessionMessages)
        saveCurrentSession()
        if (hasConversation) ScriptPluginAgentRuntime.resumeOnNextEntry(activeSessionId)
    }

    fun exitPage() {
        prepareForExit()
        onBack()
    }

    val latestExitHandler = rememberUpdatedState(newValue = {
        if (showSkills) {
            skillBackHandler?.invoke() ?: run { showSkills = false }
        } else {
            exitPage()
        }
    })
    val latestPrepareForExit = rememberUpdatedState(newValue = { prepareForExit() })
    DisposableEffect(onExitHandlerChanged) {
        val handler = { latestExitHandler.value.invoke() }
        onExitHandlerChanged(handler)
        onDispose {
            onExitHandlerChanged(null)
            latestPrepareForExit.value.invoke()
        }
    }

    LaunchedEffect(
        generating,
        applyingFileOperation,
        pendingWrite,
        pendingDelete,
        pendingWorkspaceChange,
        showConfig,
        showModelPicker,
        showQuickProfilePicker,
        showQuickOptions,
        showSkills,
        pendingMessages
    ) {
        if (
            !generating &&
            !applyingFileOperation &&
            pendingWrite == null &&
            pendingDelete == null &&
            pendingWorkspaceChange == null &&
            !showConfig &&
            !showModelPicker &&
            !showQuickProfilePicker &&
            !showQuickOptions &&
            !showSkills &&
            pendingMessages.isNotEmpty()
        ) {
            delay(250L)
            val next = pendingMessages.firstOrNull() ?: return@LaunchedEffect
            if (
                sendMessage(
                    content = next.content,
                    attachments = next.attachments,
                    quotedMessage = next.quotedMessage,
                    consumeComposer = false
                )
            ) {
                pendingMessages = pendingMessages.filterNot { it.id == next.id }
            }
        }
    }

    if (showToolApprovalDialog) {
        pendingToolApproval?.let { approval ->
            var alwaysAllow by remember(approval.confirmation.eventId) { mutableStateOf(false) }
            WindowDialog(
                show = true,
                title = approval.confirmation.toolName,
                onDismissRequest = { showToolApprovalDialog = false },
                content = {
                    Column(modifier = Modifier.fillMaxWidth().heightIn(max = 520.dp)) {
                        Column(
                            modifier = Modifier.fillMaxWidth().weight(1f, fill = false)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = "Agent 正在修改插件 ${approval.confirmation.pluginId}，确认后继续执行。",
                                color = MiuixTheme.colorScheme.onSurface,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "代码差异",
                                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                            ScriptPluginAgentDiffCodeBlock(
                                approval.confirmation.diff.ifBlank { "无代码变化" }
                            )
                        }
                        ScriptPluginAgentAlwaysAllowRow(
                            checked = alwaysAllow,
                            summary = "确认后，本轮后续写入和最终提交不再询问",
                            onCheckedChange = { alwaysAllow = it }
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            TextButton(
                                text = "取消修改",
                                onClick = {
                                    showToolApprovalDialog = false
                                    pendingToolApproval = null
                                    approval.resolve(ScriptPluginAgentWorkspaceWriteDecision.CANCEL)
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.textButtonColorsPrimary()
                            )
                            TextButton(
                                text = "确认并继续",
                                onClick = {
                                    if (alwaysAllow) {
                                        setWorkspaceWriteApprovalMode(
                                            ScriptPluginAgentSettings.WRITE_APPROVAL_ALWAYS_ALLOW
                                        )
                                    }
                                    showToolApprovalDialog = false
                                    pendingToolApproval = null
                                    approval.resolve(
                                        if (alwaysAllow) {
                                            ScriptPluginAgentWorkspaceWriteDecision.ALWAYS_ALLOW
                                        } else {
                                            ScriptPluginAgentWorkspaceWriteDecision.APPROVE_ONCE
                                        }
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.textButtonColorsPrimary()
                            )
                        }
                    }
                }
            )
        }
    }
    if (showPendingWorkspaceDialog) pendingWorkspaceChange?.let { pending ->
        var alwaysAllow by remember(pending.change.stagingPath) { mutableStateOf(false) }
        fun dismissWorkspaceChange() {
            showPendingWorkspaceDialog = false
            pendingWorkspaceChange = null
            resumeState = null
            Thread({
                ScriptPluginAgentWorkspaceTools.discard(context, pending.change)
            }, "Hchat-Script-Agent-Workspace-Discard").start()
            recordFileOperation(pending.messageIndex, "已取消插件工作区变更")
        }
        WindowDialog(
            show = true,
            title = when {
                pending.change.deletePlugin -> "删除插件"
                pending.isNewPlugin -> "创建插件"
                pending.hasDeletedPaths -> "确认删除路径"
                pending.risky -> "确认高风险修改"
                else -> "确认修改"
            },
            onDismissRequest = { showPendingWorkspaceDialog = false },
            content = {
                Column(modifier = Modifier.fillMaxWidth().heightIn(max = 520.dp)) {
                    Column(
                        modifier = Modifier.fillMaxWidth().weight(1f, fill = false)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = buildString {
                                when {
                                    pending.change.deletePlugin -> append(
                                        "Agent 将删除插件“${pending.change.pluginName}”（${pending.change.pluginId}）。"
                                    )
                                    pending.isNewPlugin -> append(
                                        "Agent 将创建插件“${pending.change.pluginName}”（${pending.change.pluginId}）。"
                                    )
                                    else -> append("Agent 将更新插件 ${pending.change.pluginId}。")
                                }
                                if (!pending.change.deletePlugin) append("提交后插件保持禁用。")
                                if (pending.applyError.isNotBlank()) {
                                    append("\n\n上次提交失败：").append(pending.applyError)
                                    append("\n暂存修改仍在，可直接重试；若目标代码文件已被其它操作修改，请取消后让 Agent 重新读取。")
                                }
                                val summaries = buildList {
                                    if (pending.change.createdPaths.isNotEmpty()) {
                                        add("新增 ${pending.change.createdPaths.size} 项")
                                    }
                                    if (pending.change.modifiedPaths.isNotEmpty()) {
                                        add("修改 ${pending.change.modifiedPaths.size} 项")
                                    }
                                    if (pending.change.deletedPaths.isNotEmpty()) {
                                        add("删除 ${pending.change.deletedPaths.size} 项")
                                    }
                                }
                                if (summaries.isNotEmpty()) {
                                    append("\n\n").append(summaries.joinToString("，"))
                                }
                                val changedPaths = (
                                    pending.change.createdPaths +
                                        pending.change.modifiedPaths +
                                        pending.change.deletedPaths
                                    ).distinct().take(6)
                                if (changedPaths.isNotEmpty()) {
                                    append("\n").append(changedPaths.joinToString("\n") { "- $it" })
                                }
                                if (pending.risky) {
                                    append("\n\n静态检查发现高风险代码：")
                                    append(
                                        pending.change.warnings.filter { it.risky }
                                            .joinToString("；") { it.message }
                                    )
                                }
                            },
                            color = MiuixTheme.colorScheme.onSurface,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "代码差异",
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                        ScriptPluginAgentDiffCodeBlock(pending.change.diff.ifBlank { "无代码变化" })
                    }
                    ScriptPluginAgentAlwaysAllowRow(
                        checked = alwaysAllow,
                        summary = "确认后，后续插件修改不再询问",
                        onCheckedChange = { alwaysAllow = it }
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TextButton(
                            text = "取消",
                            onClick = { dismissWorkspaceChange() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColorsPrimary()
                        )
                        TextButton(
                            text = when {
                                pending.change.deletePlugin -> "确认删除"
                                pending.isNewPlugin -> "确认创建"
                                pending.applyError.isNotBlank() -> "重试提交"
                                else -> "确认提交"
                            },
                            onClick = {
                                if (alwaysAllow) {
                                    setWorkspaceWriteApprovalMode(
                                        ScriptPluginAgentSettings.WRITE_APPROVAL_ALWAYS_ALLOW
                                    )
                                }
                                showPendingWorkspaceDialog = false
                                pendingWorkspaceChange = null
                                applyWorkspaceChange(pending.messageIndex, pending.change)
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColorsPrimary()
                        )
                    }
                }
            }
        )
    }
    if (showPendingWriteDialog) pendingWrite?.let { pending ->
        var alwaysAllow by remember(pending.messageIndex, pending.draft.pluginId) { mutableStateOf(false) }
        fun dismissPendingWrite() {
            showPendingWriteDialog = false
            pendingWrite = null
            resumeState = null
            recordFileOperation(
                pending.messageIndex,
                when {
                    pending.isNewPlugin -> "已取消创建插件"
                    pending.confirmsCreation -> "已取消替换同名插件"
                    pending.risky -> "已取消高风险修改"
                    else -> "已取消插件修改"
                }
            )
        }
        WindowDialog(
            show = true,
            title = when {
                pending.isNewPlugin -> "创建插件"
                pending.confirmsCreation -> "替换同名插件"
                pending.risky -> "确认高风险修改"
                else -> "确认修改"
            },
            onDismissRequest = { showPendingWriteDialog = false },
            content = {
                Column(modifier = Modifier.fillMaxWidth().heightIn(max = 520.dp)) {
                    Column(
                        modifier = Modifier.fillMaxWidth().weight(1f, fill = false)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = buildString {
                                if (pending.isNewPlugin) {
                                    append("Agent 将创建插件“${pending.draft.pluginName}”（${pending.draft.pluginId}）。创建后默认禁用。")
                                } else if (pending.confirmsCreation) {
                                    append("目录 ${pending.draft.pluginId} 已存在。继续会把它作为同名插件更新，并保持禁用。")
                                } else {
                                    append("Agent 将直接修改插件 ${pending.draft.pluginId}，写入后插件会保持禁用。")
                                }
                                if (pending.risky) {
                                    append("\n\n静态检查发现网络、反射、Hook、文件删除或进程执行等高风险代码，请确认代码来源和用途。")
                                }
                            },
                            color = MiuixTheme.colorScheme.onSurface,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "代码差异",
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                        ScriptPluginAgentDiffCodeBlock(pending.diff.ifBlank { "无代码变化" })
                    }
                    ScriptPluginAgentAlwaysAllowRow(
                        checked = alwaysAllow,
                        summary = "确认后，后续插件修改不再询问",
                        onCheckedChange = { alwaysAllow = it }
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TextButton(
                            text = "取消",
                            onClick = { dismissPendingWrite() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColorsPrimary()
                        )
                        TextButton(
                            text = when {
                                pending.isNewPlugin -> "确认创建"
                                pending.confirmsCreation -> "确认替换"
                                else -> "确认修改"
                            },
                            onClick = {
                                if (alwaysAllow) {
                                    setWorkspaceWriteApprovalMode(
                                        ScriptPluginAgentSettings.WRITE_APPROVAL_ALWAYS_ALLOW
                                    )
                                }
                                showPendingWriteDialog = false
                                pendingWrite = null
                                writeAgentDraft(pending.messageIndex, pending.draft, pending.isNewPlugin)
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColorsPrimary()
                        )
                    }
                }
            }
        )
    }
    if (showPendingDeleteDialog) pendingDelete?.let { pending ->
        var alwaysAllow by remember(pending.messageIndex, pending.pluginId) { mutableStateOf(false) }
        fun dismissPendingDelete() {
            showPendingDeleteDialog = false
            pendingDelete = null
            resumeState = null
            recordFileOperation(pending.messageIndex, "已取消删除插件")
        }
        WindowDialog(
            show = true,
            title = "删除插件",
            onDismissRequest = { showPendingDeleteDialog = false },
            content = {
                Column {
                    Text(
                        text = "确定删除插件“${pending.pluginName}”（${pending.pluginId}）吗？插件目录内的全部文件都会被删除，此操作无法撤销。",
                        color = MiuixTheme.colorScheme.onSurface,
                        fontSize = 13.sp
                    )
                    ScriptPluginAgentAlwaysAllowRow(
                        checked = alwaysAllow,
                        summary = "确认后，后续插件修改不再询问",
                        onCheckedChange = { alwaysAllow = it }
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TextButton(
                            text = "取消",
                            onClick = { dismissPendingDelete() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColorsPrimary()
                        )
                        TextButton(
                            text = "确认删除",
                            onClick = {
                                if (alwaysAllow) {
                                    setWorkspaceWriteApprovalMode(
                                        ScriptPluginAgentSettings.WRITE_APPROVAL_ALWAYS_ALLOW
                                    )
                                }
                                showPendingDeleteDialog = false
                                pendingDelete = null
                                deleteAgentPlugin(pending.messageIndex, pending.pluginId)
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColorsPrimary()
                        )
                    }
                }
            }
        )
    }
    if (showQuickProfilePicker) {
        ScriptPluginAgentQuickProfileDialog(
            profiles = profiles,
            activeProfileId = activeProfileId,
            onSelected = {
                selectProfile(it)
                showQuickProfilePicker = false
            },
            onDismiss = { showQuickProfilePicker = false }
        )
    }
    if (showQuickOptions) {
        ScriptPluginAgentQuickOptionsDialog(
            webSearchEnabled = webSearchEnabled,
            workspaceWriteApprovalMode = workspaceWriteApprovalMode,
            promptCacheMode = promptCacheMode,
            endpointMode = endpointMode,
            model = model,
            reasoningEffort = reasoningEffort,
            mcpServers = mcpServers,
            onReasoningEffortChanged = { setReasoningEffort(it) },
            onWebSearchChanged = { setWebSearchEnabled(it) },
            onWorkspaceWriteApprovalChanged = { setWorkspaceWriteApprovalMode(it) },
            onPromptCacheModeChanged = { setPromptCacheMode(it) },
            onMcpChanged = { id, enabled -> setMcpServerEnabled(id, enabled) },
            onOpenSkills = {
                showQuickOptions = false
                openSkillsAfterQuickOptions = true
            },
            onDismiss = { showQuickOptions = false }
        )
    }
    LaunchedEffect(showQuickOptions, openSkillsAfterQuickOptions) {
        if (!showQuickOptions && openSkillsAfterQuickOptions) {
            // Wait until WindowDialog has left composition before opening the next page.
            val activity = findAgentActivity(context)
            val decor = activity?.window?.decorView
            val openPage = Runnable {
                openSkillsAfterQuickOptions = false
                if (activity != null && !activity.isFinishing && !activity.isDestroyed) {
                    showSkills = true
                }
            }
            if (decor != null) {
                decor.postOnAnimation(openPage)
                try {
                    awaitCancellation()
                } finally {
                    decor.removeCallbacks(openPage)
                }
            } else {
                openSkillsAfterQuickOptions = false
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            showModelPicker -> ScriptPluginAgentModelPickerPage(
                config = currentConfig(),
                currentModel = model,
                onSelected = { selectedModel, selectedEffort ->
                    val appliesToNextRequest = generating
                    ScriptPluginAgentSettings.save(
                        context,
                        currentConfig().copy(model = selectedModel, reasoningEffort = selectedEffort)
                    )
                    model = selectedModel
                    reasoningEffort = selectedEffort
                    profileVersion++
                    showModelPicker = false
                    Toast.makeText(
                        context,
                        if (appliesToNextRequest) "已应用模型设置，将用于下一次请求" else "已应用模型设置",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                onBack = { showModelPicker = false }
            )
            showCodexModels -> CodexModelPickerPage(
                baseUrl = codexProfile.baseUrl,
                apiKey = codexProfile.apiKey,
                currentModel = codexProfile.model,
                currentEffort = codexProfile.effort,
                onSetModel = { selectedModel, selectedEffort ->
                    codexProfile = codexProfile.copy(model = selectedModel, effort = selectedEffort)
                    showCodexModels = false
                },
                onBack = { showCodexModels = false }
            )
            showConfig -> ScriptPluginAgentConfigPage(
                profiles = profiles,
                activeProfileId = activeProfileId,
                activeProfileName = activeProfileName,
                testingConnection = testingConnection,
                apiBase = apiBase,
                endpointMode = endpointMode,
                apiKey = apiKey,
                model = model,
                reasoningEffort = reasoningEffort,
                mcpServers = mcpServers,
                autoCompactEnabled = autoCompactEnabled,
                compactTokenThreshold = compactTokenThreshold,
                promptCacheMode = promptCacheMode,
                onApiBaseChange = { apiBase = it },
                onEndpointModeChange = { mode ->
                    endpointMode = mode
                    ScriptPluginAgentSettings.presetRequestUrl(mode)
                        .takeIf { it.isNotBlank() }
                        ?.let { apiBase = it }
                },
                onApiKeyChange = { apiKey = it },
                onModelChange = { model = it },
                onReasoningEffortChange = { reasoningEffort = it },
                onMcpServersChange = { mcpServers = it },
                onAutoCompactEnabledChange = { autoCompactEnabled = it },
                onCompactTokenThresholdChange = { compactTokenThreshold = it.filter(Char::isDigit) },
                onPromptCacheModeChange = { promptCacheMode = it },
                codexProfile = codexProfile,
                codexProfiles = codexProfiles,
                onCodexProfileChange = { codexProfile = it },
                onCodexProfileSelected = { selectCodexProfile(it) },
                onCodexCreateProfile = { createCodexProfile(it) },
                onCodexRenameProfile = { renameCodexProfile(it) },
                onCodexDeleteProfile = { deleteCodexProfile() },
                onCodexOpenModels = { showCodexModels = true },
                onCodexTestConnection = { testCodexConnection() },
                testingCodexConnection = testingCodexConnection,
                settingsTab = settingsTab,
                onSettingsTabSelected = { settingsTab = it },
                onCodexSave = { saveCodexConfig() },
                onProfileSelected = { selectProfile(it) },
                onCreateProfile = { createProfile(it) },
                onRenameProfile = { renameProfile(it) },
                onDeleteProfile = { deleteActiveProfile() },
                onTestConnection = { testAgentConnection() },
                onOpenModels = { showModelPicker = true },
                onOpenSkills = { showSkills = true },
                onCompact = { compactContext() },
                onSave = { saveConfig() },
                onBack = { showConfig = false }
            )
            else -> ScriptPluginAgentChatPage(
                title = sessionTitle,
                messages = sessionMessages,
                messageInput = messageInput,
                pendingAttachments = pendingAttachments,
                pendingQuotedMessage = pendingQuotedMessage,
                pendingMessages = pendingMessages,
                pendingMessagesExpanded = pendingMessagesExpanded,
                activeProfileName = activeProfileName,
                activeModel = model,
                enabled = !generating && !contextCompacting && !applyingFileOperation &&
                    !hasPendingConfirmation(),
                inputEnabled = !contextCompacting && !applyingFileOperation &&
                    !hasPendingConfirmation(),
                generating = generating,
                contextCompacting = contextCompacting,
                workingStartedAt = if (contextCompacting) contextCompactionStartedAt else generationStartedAt,
                workingLabel = when {
                    contextCompacting && generating -> "正在自动压缩上下文"
                    contextCompacting -> "正在压缩上下文"
                    else -> ""
                },
                showWorking = generating && !generationHasVisibleReply,
                pendingConfirmationLabel = when {
                    pendingToolApproval != null -> "${pendingToolApproval?.confirmation?.toolName}待确认"
                    pendingWorkspaceChange != null -> "最终插件修改待确认"
                    pendingWrite != null -> "最终插件写入待确认"
                    pendingDelete != null -> "删除插件待确认"
                    else -> null
                },
                onMessageInputChange = { messageInput = it },
                onSend = { sendMessage() },
                onQueue = { queueMessage() },
                onCancel = { cancelGeneration() },
                onAttach = { selectAttachments() },
                onRemoveAttachment = { path ->
                    val removed = pendingAttachments.filter { it.path == path }
                    pendingAttachments = pendingAttachments.filterNot { it.path == path }
                    ScriptPluginAgentSessionStore.cleanupAttachments(context, removed)
                },
                onClearQuotedMessage = { pendingQuotedMessage = null },
                onPendingMessagesExpandedChange = { pendingMessagesExpanded = it },
                onEditPendingMessage = { editPendingMessage(it) },
                onDeletePendingMessage = { id ->
                    pendingMessages.firstOrNull { it.id == id }?.let { pending ->
                        ScriptPluginAgentSessionStore.cleanupAttachments(context, pending.attachments)
                    }
                    pendingMessages = pendingMessages.filterNot { it.id == id }
                },
                onSendPendingMessageNow = { sendPendingMessageNow(it) },
                onOpenProfilePicker = { showQuickProfilePicker = true },
                onOpenModelPicker = { showModelPicker = true },
                onOpenQuickOptions = { showQuickOptions = true },
                onOpenPendingConfirmation = { showPendingConfirmation() },
                onCompact = { compactContext() },
                onOpenHistory = { showHistory = true },
                onOpenConfig = {
                    if (!generating && !contextCompacting && !applyingFileOperation &&
                        !showPendingConfirmation()
                    ) {
                        showConfig = true
                    }
                },
                onEditAndResend = { editAndResendMessage(it) },
                onQuoteMessage = { quoteAgentMessage(it) },
                onContinueMessage = { continueInterruptedTask(it) },
                onRegenerateMessage = { regenerateAssistantMessage(it) },
                onRollback = { rollbackToMessage(it) },
                onDeleteMessage = { deleteAgentMessage(it) },
                onCreateBranch = { createMessageBranch(it) },
                onBack = { exitPage() }
            )
        }
        AnimatedVisibility(
            visible = showSkills,
            enter = slideInHorizontally(tween(240)) { it } + fadeIn(tween(160)),
            exit = slideOutHorizontally(tween(220)) { it } + fadeOut(tween(140))
        ) {
            ScriptPluginAgentSkillsPage(
                context = context,
                onBack = { showSkills = false },
                onBackHandlerChanged = { skillBackHandler = it },
                onUse = { name ->
                    messageInput = "\$$name " + messageInput
                    showSkills = false
                    showConfig = false
                }
            )
        }
        if (showHistory) {
            val workingSessionIds = ScriptPluginAgentRuntime.workingSessionIds()
            val visibleSessions = if (ScriptPluginAgentSessionStore.hasConversation(sessionMessages)) {
                val currentSession = currentSessionSnapshot()
                (sessions.filterNot { it.id == activeSessionId } + currentSession).distinctBy { it.id }
            } else {
                sessions.filterNot { it.id == activeSessionId }
            }
            val runtimeSessions = ScriptPluginAgentRuntime.snapshots()
            ScriptPluginAgentHistoryDrawer(
                sessions = ScriptPluginAgentSessionStore.sorted(
                    (visibleSessions + runtimeSessions).distinctBy { it.id }
                ),
                activeId = activeSessionId,
                workingSessionIds = workingSessionIds,
                enabled = true,
                onNewSession = { newSession() },
                onSelect = { openSession(it) },
                onRename = { session, title -> renameSession(session, title) },
                onCommitOrder = { persistSessionOrder(it) },
                onSetPinned = { session, pinned -> setSessionPinned(session, pinned) },
                onSetLocked = { session, locked -> setSessionLocked(session, locked) },
                onDelete = { deleteSession(it) },
                onClose = { showHistory = false }
            )
        }
    }
}

private fun promptCacheModeChoices(): List<PopupChoice<String>> = listOf(
    PopupChoice("自动", ScriptPluginAgentSettings.PROMPT_CACHE_AUTO),
    PopupChoice("强制", ScriptPluginAgentSettings.PROMPT_CACHE_FORCE),
    PopupChoice("关闭", ScriptPluginAgentSettings.PROMPT_CACHE_OFF)
)

private fun promptCacheModeSummary(mode: String, endpointMode: String): String {
    if (endpointMode == ScriptPluginAgentSettings.ENDPOINT_MODE_GEMINI) {
        return "Gemini 不使用 OpenAI 或 Anthropic 的显式缓存字段"
    }
    return when (mode) {
        ScriptPluginAgentSettings.PROMPT_CACHE_FORCE ->
            "向兼容接口发送稳定缓存标识，不支持时自动回退"
        ScriptPluginAgentSettings.PROMPT_CACHE_OFF ->
            "不发送显式缓存字段"
        else -> "官方接口显式缓存，其他接口使用服务端默认策略"
    }
}

@Composable
private fun ScriptPluginAgentConfigPage(
    profiles: List<ScriptPluginAgentProfile>,
    activeProfileId: String,
    activeProfileName: String,
    testingConnection: Boolean,
    apiBase: String,
    endpointMode: String,
    apiKey: String,
    model: String,
    reasoningEffort: String,
    mcpServers: List<ScriptPluginAgentMcpServer>,
    autoCompactEnabled: Boolean,
    compactTokenThreshold: String,
    promptCacheMode: String,
    onApiBaseChange: (String) -> Unit,
    onEndpointModeChange: (String) -> Unit,
    onApiKeyChange: (String) -> Unit,
    onModelChange: (String) -> Unit,
    onReasoningEffortChange: (String) -> Unit,
    onMcpServersChange: (List<ScriptPluginAgentMcpServer>) -> Unit,
    onAutoCompactEnabledChange: (Boolean) -> Unit,
    onCompactTokenThresholdChange: (String) -> Unit,
    onPromptCacheModeChange: (String) -> Unit,
    onProfileSelected: (ScriptPluginAgentProfile) -> Unit,
    onCreateProfile: (String) -> Unit,
    onRenameProfile: (String) -> Unit,
    onDeleteProfile: () -> Unit,
    onTestConnection: () -> Unit,
    onOpenModels: () -> Unit,
    onOpenSkills: () -> Unit,
    onCompact: () -> Unit,
    codexProfile: CodexSettings.Profile,
    codexProfiles: List<CodexSettings.Profile>,
    onCodexProfileChange: (CodexSettings.Profile) -> Unit,
    onCodexProfileSelected: (String) -> Unit,
    onCodexCreateProfile: (String) -> Unit,
    onCodexRenameProfile: (String) -> Unit,
    onCodexDeleteProfile: () -> Unit,
    onCodexOpenModels: () -> Unit,
    onCodexTestConnection: () -> Unit,
    testingCodexConnection: Boolean,
    settingsTab: Int,
    onSettingsTabSelected: (Int) -> Unit,
    onCodexSave: () -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    var profileDialog by remember { mutableStateOf("") }
    var profileNameDraft by remember(activeProfileName, profileDialog) {
        mutableStateOf(if (profileDialog == "rename") activeProfileName else "")
    }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var codexProfileDialog by remember { mutableStateOf("") }
    var codexProfileNameDraft by remember(codexProfile.name, codexProfileDialog) {
        mutableStateOf(if (codexProfileDialog == "rename") codexProfile.name else "")
    }
    var codexDeleteConfirm by remember { mutableStateOf(false) }
    val resolvedEndpoint = remember(apiBase, endpointMode, model) {
        ScriptPluginAgentSettings.requestUrl(apiBase, endpointMode, model)
    }

    if (profileDialog.isNotBlank()) {
        WindowDialog(
            show = true,
            title = if (profileDialog == "rename") "重命名配置" else "新建配置",
            onDismissRequest = { profileDialog = "" },
            content = {
                Column {
                    InputRow(
                        title = "配置名称",
                        summary = "用于区分不同服务和模型",
                        value = profileNameDraft,
                        onValueChange = { profileNameDraft = it.take(32) }
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TextButton(
                            text = "取消",
                            onClick = { profileDialog = "" },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColorsPrimary()
                        )
                        TextButton(
                            text = "确认",
                            onClick = {
                                if (profileNameDraft.isNotBlank()) {
                                    if (profileDialog == "rename") {
                                        onRenameProfile(profileNameDraft)
                                    } else {
                                        onCreateProfile(profileNameDraft)
                                    }
                                    profileDialog = ""
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColorsPrimary()
                        )
                    }
                }
            }
        )
    }
    if (showDeleteConfirm) {
        WindowDialog(
            show = true,
            title = "删除配置",
            onDismissRequest = { showDeleteConfirm = false },
            content = {
                Column {
                    Text(
                        text = "确定删除“$activeProfileName”吗？",
                        color = MiuixTheme.colorScheme.onSurface,
                        fontSize = 14.sp
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TextButton(
                            text = "取消",
                            onClick = { showDeleteConfirm = false },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColorsPrimary()
                        )
                        TextButton(
                            text = "删除",
                            onClick = {
                                showDeleteConfirm = false
                                onDeleteProfile()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColorsPrimary()
                        )
                    }
                }
            }
        )
    }
    if (codexProfileDialog.isNotBlank()) {
        WindowDialog(
            show = true,
            title = if (codexProfileDialog == "rename") "重命名 codex 配置" else "新建 codex 配置",
            onDismissRequest = { codexProfileDialog = "" },
            content = {
                Column {
                    InputRow(
                        title = "配置名称",
                        summary = "用于区分不同服务和模型",
                        value = codexProfileNameDraft,
                        onValueChange = { codexProfileNameDraft = it.take(32) }
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TextButton(
                            text = "取消",
                            onClick = { codexProfileDialog = "" },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColorsPrimary()
                        )
                        TextButton(
                            text = "确认",
                            onClick = {
                                if (codexProfileNameDraft.isNotBlank()) {
                                    if (codexProfileDialog == "rename") {
                                        onCodexRenameProfile(codexProfileNameDraft)
                                    } else {
                                        onCodexCreateProfile(codexProfileNameDraft)
                                    }
                                    codexProfileDialog = ""
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColorsPrimary()
                        )
                    }
                }
            }
        )
    }
    if (codexDeleteConfirm) {
        WindowDialog(
            show = true,
            title = "删除 codex 配置",
            onDismissRequest = { codexDeleteConfirm = false },
            content = {
                Column {
                    Text(
                        text = "确定删除“${codexProfile.name}”吗？",
                        color = MiuixTheme.colorScheme.onSurface,
                        fontSize = 14.sp
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TextButton(
                            text = "取消",
                            onClick = { codexDeleteConfirm = false },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColorsPrimary()
                        )
                        TextButton(
                            text = "删除",
                            onClick = {
                                codexDeleteConfirm = false
                                onCodexDeleteProfile()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColorsPrimary()
                        )
                    }
                }
            }
        )
    }

    val listState = rememberLazyListState()
    val scrollBehavior = MiuixScrollBehavior()
    PageScaffold(
        title = "Agent 配置",
        largeTitle = "Agent 配置",
        scrollBehavior = scrollBehavior,
        bottomBar = {
            BottomActionBar(
                primaryText = if (settingsTab == 1) "保存 codex 配置" else "保存配置",
                onPrimaryClick = if (settingsTab == 1) onCodexSave else onSave,
                secondaryText = "返回",
                onSecondaryClick = onBack
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
            state = listState,
            contentPadding = PaddingValues(
                top = padding.calculateTopPadding() + 8.dp,
                bottom = padding.calculateBottomPadding() + 84.dp
            )
        ) {
            item {
                TabRow(
                    tabs = listOf("Agent 设置", "codex 设置"),
                    selectedTabIndex = settingsTab,
                    onTabSelected = { onSettingsTabSelected(it) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
            if (settingsTab == 1) {
                codexTabItems(
                    profile = codexProfile,
                    profiles = codexProfiles,
                    onProfileChange = onCodexProfileChange,
                    onProfileSelected = onCodexProfileSelected,
                    onCreateProfile = { codexProfileDialog = "create" },
                    onRenameProfile = { codexProfileDialog = "rename" },
                    onDeleteProfile = { codexDeleteConfirm = true },
                    onTestConnection = onCodexTestConnection,
                    testingConnection = testingCodexConnection,
                    onOpenModels = onCodexOpenModels
                )
            } else {
                item { SmallTitle(text = "模型配置") }
                item {
                    SettingsCard {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 10.dp, end = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "选择模型配置",
                                color = MiuixTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(
                                text = "+ 新建",
                                onClick = {
                                    profileNameDraft = ""
                                    profileDialog = "create"
                                },
                                minWidth = 0.dp,
                                minHeight = 34.dp,
                                cornerRadius = 10.dp,
                                insideMargin = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                colors = ButtonDefaults.textButtonColorsPrimary()
                            )
                        }
                        PopupChoiceRow(
                            title = "当前配置",
                            summary = activeProfileName,
                            options = profiles.map { PopupChoice(it.name, it.id) },
                            currentValue = activeProfileId,
                            onValueChanged = { id -> profiles.firstOrNull { it.id == id }?.let(onProfileSelected) }
                        )
                        InsetDivider()
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TextButton(
                                text = "重命名",
                                onClick = {
                                    profileNameDraft = activeProfileName
                                    profileDialog = "rename"
                                },
                                modifier = Modifier.weight(1f),
                                minWidth = 0.dp,
                                minHeight = 34.dp,
                                cornerRadius = 10.dp,
                                insideMargin = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                colors = ButtonDefaults.textButtonColorsPrimary()
                            )
                            TextButton(
                                text = "删除",
                                onClick = { showDeleteConfirm = true },
                                enabled = profiles.size > 1,
                                modifier = Modifier.weight(1f),
                                minWidth = 0.dp,
                                minHeight = 34.dp,
                                cornerRadius = 10.dp,
                                insideMargin = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                colors = ButtonDefaults.textButtonColorsPrimary()
                            )
                            TextButton(
                                text = if (testingConnection) "测试中" else "测试连接",
                                onClick = onTestConnection,
                                enabled = !testingConnection,
                                modifier = Modifier.weight(1f),
                                minWidth = 0.dp,
                                minHeight = 34.dp,
                                cornerRadius = 10.dp,
                                insideMargin = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                colors = ButtonDefaults.textButtonColorsPrimary()
                            )
                        }
                    }
                }
                item { SmallTitle(modifier = Modifier.padding(top = 10.dp), text = "API 设置") }
                item {
                    SettingsCard {
                        PopupChoiceRow(
                            title = "接口类型",
                            summary = ScriptPluginAgentSettings.endpointModeLabel(endpointMode),
                            options = listOf(
                                PopupChoice("OpenAI 兼容", ScriptPluginAgentSettings.ENDPOINT_MODE_OPENAI_COMPATIBLE),
                                PopupChoice("OpenAI", ScriptPluginAgentSettings.ENDPOINT_MODE_OPENAI),
                                PopupChoice("DeepSeek", ScriptPluginAgentSettings.ENDPOINT_MODE_DEEPSEEK),
                                PopupChoice("OpenRouter", ScriptPluginAgentSettings.ENDPOINT_MODE_OPENROUTER),
                                PopupChoice("硅基流动", ScriptPluginAgentSettings.ENDPOINT_MODE_SILICONFLOW),
                                PopupChoice("Gemini", ScriptPluginAgentSettings.ENDPOINT_MODE_GEMINI),
                                PopupChoice("Anthropic", ScriptPluginAgentSettings.ENDPOINT_MODE_ANTHROPIC),
                                PopupChoice("自定义请求链接", ScriptPluginAgentSettings.ENDPOINT_MODE_CUSTOM_URL)
                            ),
                            currentValue = endpointMode,
                            onValueChanged = onEndpointModeChange
                        )
                        InsetDivider()
                        InputRow(
                            if (endpointMode == ScriptPluginAgentSettings.ENDPOINT_MODE_CUSTOM_URL) {
                                "请求链接"
                            } else {
                                "API 地址"
                            },
                            if (endpointMode == ScriptPluginAgentSettings.ENDPOINT_MODE_GEMINI) {
                                "自动补全 Gemini 模型与 generateContent 请求路径"
                            } else if (endpointMode == ScriptPluginAgentSettings.ENDPOINT_MODE_ANTHROPIC) {
                                "自动补全 /v1/messages"
                            } else if (endpointMode == ScriptPluginAgentSettings.ENDPOINT_MODE_CUSTOM_URL) {
                                "手动填写完整 URL，不会自动补全或改写"
                            } else {
                                "可填写域名、/v1 地址或完整 /chat/completions 地址"
                            },
                            apiBase,
                            onValueChange = onApiBaseChange
                        )
                        if (resolvedEndpoint.isNotBlank()) {
                            InsetDivider()
                            ScriptPluginAgentEndpointPreview(resolvedEndpoint)
                        }
                        InsetDivider()
                        InputRow("API Key", "留空表示接口不需要密钥", apiKey, onValueChange = onApiKeyChange)
                        InsetDivider()
                        InputRow("模型 ID", "可直接填写，无需先获取模型列表", model, onValueChange = onModelChange)
                        InsetDivider()
                        ScriptPluginAgentReasoningRow(endpointMode, model, reasoningEffort, onReasoningEffortChange)
                        InsetDivider()
                        ActionRow("选择模型", "手动输入模型 ID 或从接口获取列表") { onOpenModels() }
                        InsetDivider()
                        PopupChoiceRow(
                            title = "提示缓存",
                            summary = promptCacheModeSummary(promptCacheMode, endpointMode),
                            options = promptCacheModeChoices(),
                            currentValue = promptCacheMode,
                            onValueChanged = onPromptCacheModeChange
                        )
                    }
                }
                item { SmallTitle(modifier = Modifier.padding(top = 10.dp), text = "上下文") }
                item {
                    SettingsCard {
                        SwitchRow(
                            checked = autoCompactEnabled,
                            title = "自动压缩上下文",
                            summary = "达到设定阈值后压缩较早对话",
                            onCheckedChange = onAutoCompactEnabledChange
                        )
                        if (autoCompactEnabled) {
                            InsetDivider()
                            InputRow(
                                "压缩阈值",
                                "Token 估算值，范围 2000 到 1000000",
                                compactTokenThreshold,
                                onValueChange = onCompactTokenThresholdChange
                            )
                        }
                        InsetDivider()
                        ActionRow("立即压缩当前会话", "保留本地历史和当前代码草稿") { onCompact() }
                    }
                }
                item { SmallTitle(modifier = Modifier.padding(top = 10.dp), text = "技能") }
                item {
                    SettingsCard {
                        ActionRow("Skill 管理", "编写或导入技能，让 Agent 按任务读取专用指南") { onOpenSkills() }
                    }
                }
                item { SmallTitle(modifier = Modifier.padding(top = 10.dp), text = "MCP 工具") }
                item {
                    SettingsCard {
                        ActionRow("添加 MCP", "可同时启用多个远程 MCP 服务器") {
                            val nextIndex = mcpServers.size + 1
                            onMcpServersChange(
                                mcpServers + ScriptPluginAgentMcpServer(
                                    id = java.util.UUID.randomUUID().toString().replace("-", ""),
                                    name = "MCP $nextIndex"
                                )
                            )
                        }
                    }
                }
                mcpServers.forEachIndexed { index, server ->
                    item(key = "agent-mcp-${server.id}") {
                        SettingsCard(modifier = Modifier.padding(top = 8.dp)) {
                            InputRow(
                                "名称",
                                "用于区分工具来源",
                                server.name,
                                onValueChange = { value ->
                                    onMcpServersChange(
                                        mcpServers.map { if (it.id == server.id) it.copy(name = value.take(32)) else it }
                                    )
                                }
                            )
                            InsetDivider()
                            SwitchRow(
                                checked = server.enabled,
                                title = "启用 ${server.name.ifBlank { "MCP ${index + 1}" }}",
                                summary = if (server.enabled) "此服务器的工具可供 Agent 调用" else "此服务器不会连接或提供工具",
                                onCheckedChange = { enabled ->
                                    onMcpServersChange(
                                        mcpServers.map { if (it.id == server.id) it.copy(enabled = enabled) else it }
                                    )
                                }
                            )
                            if (server.enabled) {
                                InsetDivider()
                                InputRow(
                                    "MCP Endpoint",
                                    "例如 https://example.com/mcp",
                                    server.endpoint,
                                    onValueChange = { value ->
                                        onMcpServersChange(
                                            mcpServers.map { if (it.id == server.id) it.copy(endpoint = value) else it }
                                        )
                                    }
                                )
                                InsetDivider()
                                InputRow(
                                    "Authorization",
                                    "可选，例如 Bearer token",
                                    server.authorization,
                                    onValueChange = { value ->
                                        onMcpServersChange(
                                            mcpServers.map {
                                                if (it.id == server.id) it.copy(authorization = value) else it
                                            }
                                        )
                                    }
                                )
                            }
                            InsetDivider()
                            ActionRow("删除 MCP", "移除此服务器配置") {
                                onMcpServersChange(mcpServers.filterNot { it.id == server.id })
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun LazyListScope.codexTabItems(
    profile: CodexSettings.Profile,
    profiles: List<CodexSettings.Profile>,
    onProfileChange: (CodexSettings.Profile) -> Unit,
    onProfileSelected: (String) -> Unit,
    onCreateProfile: () -> Unit,
    onRenameProfile: () -> Unit,
    onDeleteProfile: () -> Unit,
    onTestConnection: () -> Unit,
    testingConnection: Boolean,
    onOpenModels: () -> Unit
) {
    item { SmallTitle(text = "模型配置") }
    item {
        SettingsCard {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 10.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "选择模型配置",
                    color = MiuixTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                TextButton(
                    text = "+ 新建",
                    onClick = onCreateProfile,
                    minWidth = 0.dp,
                    minHeight = 34.dp,
                    cornerRadius = 10.dp,
                    insideMargin = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    colors = ButtonDefaults.textButtonColorsPrimary()
                )
            }
            PopupChoiceRow(
                title = "当前配置",
                summary = profile.name,
                options = profiles.map { PopupChoice(it.name, it.id) },
                currentValue = profile.id,
                onValueChanged = { id ->
                    profiles.firstOrNull { it.id == id }?.let { onProfileSelected(it.id) }
                }
            )
            InsetDivider()
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(
                    text = "重命名",
                    onClick = onRenameProfile,
                    modifier = Modifier.weight(1f),
                    minWidth = 0.dp,
                    minHeight = 34.dp,
                    cornerRadius = 10.dp,
                    insideMargin = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    colors = ButtonDefaults.textButtonColorsPrimary()
                )
                TextButton(
                    text = "删除",
                    onClick = onDeleteProfile,
                    enabled = profiles.size > 1,
                    modifier = Modifier.weight(1f),
                    minWidth = 0.dp,
                    minHeight = 34.dp,
                    cornerRadius = 10.dp,
                    insideMargin = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    colors = ButtonDefaults.textButtonColorsPrimary()
                )
                TextButton(
                    text = if (testingConnection) "测试中" else "测试连接",
                    onClick = onTestConnection,
                    enabled = !testingConnection,
                    modifier = Modifier.weight(1f),
                    minWidth = 0.dp,
                    minHeight = 34.dp,
                    cornerRadius = 10.dp,
                    insideMargin = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    colors = ButtonDefaults.textButtonColorsPrimary()
                )
            }
        }
    }
    item { SmallTitle(modifier = Modifier.padding(top = 10.dp), text = "codex 配置") }
    item {
        SettingsCard {
            InputRow(
                "API 地址",
                "可填写域名、/v1 地址或完整接口地址",
                profile.baseUrl,
                onValueChange = { onProfileChange(profile.copy(baseUrl = it)) }
            )
            val resolvedCodexEndpoint = remember(profile.baseUrl) { CodexSettings.resolvedBaseUrl(profile.baseUrl) }
            if (resolvedCodexEndpoint.isNotBlank()) {
                InsetDivider()
                ScriptPluginAgentEndpointPreview(resolvedCodexEndpoint)
            }
            InsetDivider()
            InputRow(
                "API Key",
                "模型密钥",
                profile.apiKey,
                onValueChange = { onProfileChange(profile.copy(apiKey = it)) }
            )
            InsetDivider()
            InputRow(
                "模型",
                "填接口里的模型名，也可以点下面「选择模型」拉取",
                profile.model,
                onValueChange = { onProfileChange(profile.copy(model = it)) }
            )
            InsetDivider()
            ActionRow("选择模型", "手动输入模型名，或从接口拉取模型列表") { onOpenModels() }
            InsetDivider()
            PopupChoiceRow(
                title = "思考程度",
                summary = ScriptPluginAgentReasoning.label(profile.effort),
                options = CodexSettings.EFFORT_OPTIONS.map { PopupChoice(it.second, it.first) },
                currentValue = profile.effort,
                onValueChanged = { onProfileChange(profile.copy(effort = it)) }
            )
        }
    }
    item { SmallTitle(modifier = Modifier.padding(top = 10.dp), text = "MCP 服务") }
    item {
        SettingsCard {
            ActionRow("添加 MCP", "远程 MCP 服务器，保存后写入 codex 配置") {
                val next = profile.mcpServers.size + 1
                onProfileChange(
                    profile.copy(mcpServers = profile.mcpServers + CodexSettings.McpServer(name = "MCP $next"))
                )
            }
        }
    }
    profile.mcpServers.forEachIndexed { index, server ->
        item(key = "codex-mcp-${server.id}") {
            SettingsCard(modifier = Modifier.padding(top = 8.dp)) {
                InputRow(
                    "名称",
                    "只能是字母数字，写入 codex 配置时作为服务器名",
                    server.name,
                    onValueChange = { value ->
                        onProfileChange(
                            profile.copy(
                                mcpServers = profile.mcpServers.map {
                                    if (it.id == server.id) it.copy(name = value.take(32)) else it
                                }
                            )
                        )
                    }
                )
                InsetDivider()
                SwitchRow(
                    checked = server.enabled,
                    title = "启用 ${server.name.ifBlank { "MCP ${index + 1}" }}",
                    summary = if (server.enabled) "保存后写入 codex 配置" else "不会写入 codex 配置",
                    onCheckedChange = { enabled ->
                        onProfileChange(
                            profile.copy(
                                mcpServers = profile.mcpServers.map {
                                    if (it.id == server.id) it.copy(enabled = enabled) else it
                                }
                            )
                        )
                    }
                )
                if (server.enabled) {
                    InsetDivider()
                    InputRow(
                        "MCP Endpoint",
                        "例如 https://example.com/mcp",
                        server.url,
                        onValueChange = { value ->
                            onProfileChange(
                                profile.copy(
                                    mcpServers = profile.mcpServers.map {
                                        if (it.id == server.id) it.copy(url = value) else it
                                    }
                                )
                            )
                        }
                    )
                    InsetDivider()
                    InputRow(
                        "访问令牌",
                        "可选，按环境变量注入，不写进配置文件",
                        server.token,
                        onValueChange = { value ->
                            onProfileChange(
                                profile.copy(
                                    mcpServers = profile.mcpServers.map {
                                        if (it.id == server.id) it.copy(token = value) else it
                                    }
                                )
                            )
                        }
                    )
                }
                InsetDivider()
                ActionRow("删除 MCP", "移除此服务器配置") {
                    onProfileChange(profile.copy(mcpServers = profile.mcpServers.filterNot { it.id == server.id }))
                }
            }
        }
    }
    item { SmallTitle(modifier = Modifier.padding(top = 10.dp), text = "Ubuntu 终端环境") }
    item { ScriptPluginAgentTerminalCard() }
}

@Composable
private fun ScriptPluginAgentTerminalCard() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var status by remember {
        mutableStateOf(h.Hchat.hooks.items.script.agent.ProotEnvironment.refreshStatus(context))
    }
    var diskText by remember { mutableStateOf("") }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showTerminal by remember { mutableStateOf(false) }
    // 终端显示字体（未下载的先按需下载，下载后切换即时生效）
    var fontId by remember {
        mutableStateOf(h.Hchat.hooks.items.script.agent.TerminalFonts.selectedId(context))
    }
    // 内置 codex 更新（服务器清单驱动，不依赖官方 chatgpt.com 通道）
    var codexVersion by remember { mutableStateOf("") }
    var codexRemote by remember { mutableStateOf<h.Hchat.hooks.items.script.agent.CodexUpdater.Release?>(null) }
    var codexBusy by remember { mutableStateOf(false) }
    var codexProgress by remember { mutableStateOf("") }
    var codexMessage by remember { mutableStateOf("") }
    var showCodexConfirm by remember { mutableStateOf(false) }
    // codex 网页版：在手机浏览器里用内置 codex
    var webInstalled by remember { mutableStateOf(false) }
    var webRunning by remember { mutableStateOf(false) }
    var webBusy by remember { mutableStateOf(false) }
    var webProgress by remember { mutableStateOf("") }
    var webMessage by remember { mutableStateOf("") }
    var webAutoKeep by remember { mutableStateOf(false) }
    var webRestarts by remember { mutableStateOf(0) }

    fun refreshDisk() {
        scope.launch(Dispatchers.IO) {
            val bytes = h.Hchat.hooks.items.script.agent.ProotEnvironment.diskUsage(context)
            withContext(Dispatchers.Main) {
                diskText = when {
                    bytes <= 0 -> ""
                    bytes >= 1073741824L -> String.format(Locale.US, "占用 %.2f GB", bytes / 1073741824.0)
                    else -> String.format(Locale.US, "占用 %.0f MB", bytes / 1048576.0)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        status = h.Hchat.hooks.items.script.agent.ProotEnvironment.refreshStatus(context)
        refreshDisk()
    }

    fun refreshCodex() {
        scope.launch(Dispatchers.IO) {
            val inst = h.Hchat.hooks.items.script.agent.CodexUpdater.installed(context)
            val remote = h.Hchat.hooks.items.script.agent.CodexUpdater.check(context).getOrNull()
            val need = remote != null && h.Hchat.hooks.items.script.agent.CodexUpdater.needsUpdate(context, remote)
            withContext(Dispatchers.Main) {
                codexVersion = inst?.version.orEmpty()
                if (need) codexRemote = remote
                if (h.Hchat.hooks.items.script.agent.CodexUpdater.isBusy()) {
                    codexMessage = "后台下载中 ${h.Hchat.hooks.items.script.agent.CodexUpdater.progress()}%"
                }
            }
        }
    }

    fun checkCodexUpdate() {
        if (codexBusy) return
        scope.launch(Dispatchers.IO) {
            val remote = h.Hchat.hooks.items.script.agent.CodexUpdater.check(context).getOrElse { error ->
                val inst = h.Hchat.hooks.items.script.agent.CodexUpdater.installed(context)
                withContext(Dispatchers.Main) {
                    codexVersion = inst?.version.orEmpty()
                    codexMessage = "检查失败：${error.message.orEmpty()}"
                }
                return@launch
            }
            val inst = h.Hchat.hooks.items.script.agent.CodexUpdater.installed(context)
            val need = h.Hchat.hooks.items.script.agent.CodexUpdater.needsUpdate(context, remote)
            withContext(Dispatchers.Main) {
                codexVersion = inst?.version.orEmpty()
                codexRemote = if (need) remote else null
                if (need) {
                    codexMessage = ""
                    showCodexConfirm = true
                } else {
                    codexMessage = "已是最新版本 ${inst?.version.orEmpty()}"
                }
            }
        }
    }

    fun runCodexUpdate(release: h.Hchat.hooks.items.script.agent.CodexUpdater.Release) {
        if (codexBusy) return
        showCodexConfirm = false
        codexBusy = true
        codexProgress = "准备下载"
        codexMessage = ""
        scope.launch(Dispatchers.IO) {
            val updater = h.Hchat.hooks.items.script.agent.CodexUpdater
            if (!updater.isBusy() && !updater.start(context, release)) {
                withContext(Dispatchers.Main) {
                    codexBusy = false
                    codexProgress = ""
                    codexMessage = "已有下载任务在进行"
                }
                return@launch
            }
            // 下载在模块后台线程，这里只刷新进度；断了下次从断点续传
            while (updater.isBusy()) {
                val pct = updater.progress()
                val stage = updater.stage()
                withContext(Dispatchers.Main) { codexProgress = "$stage（$pct%）" }
                delay(500)
            }
            val inst = updater.installed(context)
            val failed = updater.lastError()
            withContext(Dispatchers.Main) {
                codexBusy = false
                codexProgress = ""
                codexVersion = inst?.version.orEmpty()
                codexRemote = null
                codexMessage = if (failed.isBlank()) "已更新到 ${inst?.version.orEmpty()}"
                else "更新失败：$failed"
            }
        }
    }

    fun refreshWeb() {
        scope.launch(Dispatchers.IO) {
            val updater = h.Hchat.hooks.items.script.agent.CodexWebUpdater
            val server = h.Hchat.hooks.items.script.agent.CodexWebServer
            val guard = h.Hchat.hooks.items.script.agent.CodexWebGuard
            val installed = updater.isInstalled(context)
            val running = server.isRunning()
            val busy = updater.isBusy()
            val pct = updater.progress()
            val autoKeep = guard.isEnabled(context)
            val restarts = guard.restartCount()
            withContext(Dispatchers.Main) {
                webInstalled = installed
                webRunning = running
                webAutoKeep = autoKeep
                webRestarts = restarts
                if (busy) webMessage = "后台下载中 $pct%"
            }
        }
    }

    // 一次点击走完：没装就先下载运行环境 → 启动服务 → 打开浏览器
    fun openCodexWeb() {
        if (webBusy) return
        webBusy = true
        webMessage = ""
        scope.launch(Dispatchers.IO) {
            val updater = h.Hchat.hooks.items.script.agent.CodexWebUpdater
            val server = h.Hchat.hooks.items.script.agent.CodexWebServer
            val installed = updater.isInstalled(context)
            val release = updater.check(context).getOrNull()
            val needPrepare = !installed ||
                (release != null && updater.needsUpdate(context, release))
            if (needPrepare) {
                if (updater.isBusy()) {
                    // 上次没下完（下载在模块线程，关页面不中断），接着等它
                } else if (release == null) {
                    withContext(Dispatchers.Main) {
                        webBusy = false
                        webMessage = "获取运行环境清单失败，请检查插件仓库地址"
                    }
                    return@launch
                } else if (!updater.start(context, release)) {
                    withContext(Dispatchers.Main) {
                        webBusy = false
                        webMessage = "已有下载任务在进行"
                    }
                    return@launch
                }
                withContext(Dispatchers.Main) { webProgress = "准备下载" }
                // 只刷新进度，下载本身在后台线程；中途断了下次从断点续传
                while (updater.isBusy()) {
                    val pct = updater.progress()
                    val stage = updater.stage()
                    withContext(Dispatchers.Main) { webProgress = "$stage（$pct%）" }
                    delay(500)
                }
                if (!updater.isInstalled(context)) {
                    withContext(Dispatchers.Main) {
                        webBusy = false
                        webProgress = ""
                        webInstalled = false
                        webMessage = "安装失败：${updater.lastError().ifBlank { "下载未完成" }}"
                    }
                    return@launch
                }
            }
            withContext(Dispatchers.Main) {
                webInstalled = true
                webProgress = "正在启动服务…"
            }
            val started = server.start(context)
            if (started.isSuccess) {
                // 手动启动即视为要保活：不给用户留“下次还得自己点”的坑
                h.Hchat.hooks.items.script.agent.CodexWebGuard.enable(context)
            }
            withContext(Dispatchers.Main) {
                webBusy = false
                webProgress = ""
                webRunning = server.isRunning()
                if (started.isSuccess) {
                    webAutoKeep = true
                    webMessage = "已启动，正在打开浏览器"
                    server.openInBrowser(context)
                } else {
                    webMessage = "启动失败：${started.exceptionOrNull()?.message.orEmpty()}"
                }
            }
        }
    }

    fun stopCodexWeb() {
        if (webBusy) return
        scope.launch(Dispatchers.IO) {
            val server = h.Hchat.hooks.items.script.agent.CodexWebServer
            // 手动停止同时关掉保活，否则会被自动拉起
            h.Hchat.hooks.items.script.agent.CodexWebGuard.disable(context)
            server.stop(context)
            val running = server.isRunning()
            withContext(Dispatchers.Main) {
                webRunning = running
                webAutoKeep = false
                webMessage = "已停止服务"
            }
        }
    }

    // 进页面时读一次内置 codex 版本，并顺带查服务器有无新版（失败静默，不打扰用户）
    LaunchedEffect(Unit) { refreshCodex() }
    LaunchedEffect(Unit) { refreshWeb() }

    val installing = status.state ==
        h.Hchat.hooks.items.script.agent.ProotEnvironment.State.INSTALLING
    val ready = status.state ==
        h.Hchat.hooks.items.script.agent.ProotEnvironment.State.READY

    fun startInstall(force: Boolean) {
        if (installing) return
        scope.launch(Dispatchers.IO) {
            h.Hchat.hooks.items.script.agent.ProotEnvironment.install(context, force = force) { pct, msg ->
                scope.launch(Dispatchers.Main) {
                    status = h.Hchat.hooks.items.script.agent.ProotEnvironment.currentStatus()
                }
            }
            val done = h.Hchat.hooks.items.script.agent.ProotEnvironment.refreshStatus(context)
            withContext(Dispatchers.Main) {
                status = done
                refreshDisk()
            }
        }
    }

    if (showDeleteConfirm) {
        WindowDialog(
            show = true,
            title = "删除终端环境",
            onDismissRequest = { showDeleteConfirm = false },
            content = {
                Column {
                    Text(
                        text = "将删除已下载的 Ubuntu 环境并释放磁盘空间。下次使用需重新下载。",
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        fontSize = 13.sp
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TextButton(
                            text = "取消",
                            onClick = { showDeleteConfirm = false },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColorsPrimary()
                        )
                        TextButton(
                            text = "删除",
                            onClick = {
                                showDeleteConfirm = false
                                scope.launch(Dispatchers.IO) {
                                    h.Hchat.hooks.items.script.agent.TermuxSessions.closeAll(context)
                                    h.Hchat.hooks.items.script.agent.ProotEnvironment.uninstall(context)
                                    val s = h.Hchat.hooks.items.script.agent.ProotEnvironment.refreshStatus(context)
                                    withContext(Dispatchers.Main) {
                                        status = s
                                        refreshDisk()
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColors()
                        )
                    }
                }
            }
        )
    }

    SettingsCard {
        val stateSummary = when (status.state) {
            h.Hchat.hooks.items.script.agent.ProotEnvironment.State.READY ->
                "已就绪${if (diskText.isNotBlank()) "，$diskText" else ""}"
            h.Hchat.hooks.items.script.agent.ProotEnvironment.State.INSTALLING ->
                if (status.progress in 0..100) "安装中 ${status.progress}% · ${status.message}"
                else "安装中 · ${status.message}"
            h.Hchat.hooks.items.script.agent.ProotEnvironment.State.ERROR ->
                status.message.ifBlank { "安装失败" }
            h.Hchat.hooks.items.script.agent.ProotEnvironment.State.DEGRADED ->
                "降级模式，部分功能不可用"
            else -> "未安装，run_command 需要此环境（约 290MB 下载，已内置 codex 与 DexClub）"
        }
        InfoRow(
            label = "环境状态",
            value = stateSummary
        )
        InsetDivider()
        if (!ready) {
            ActionRow(
                if (installing) "正在安装…" else "安装 Ubuntu 终端",
                "首次需联网下载，完成后 Agent 可用 run_command"
            ) { if (!installing) startInstall(false) }
        } else {
            ActionRow("打开终端", "在 Ubuntu 环境里直接敲命令") { showTerminal = true }
            InsetDivider()
            ActionRow(
                title = if (webRunning) "codex 网页版 · 运行中" else "codex 网页版",
                summary = when {
                    webBusy -> webProgress.ifBlank { "准备中…" }
                    webMessage.isNotBlank() -> webMessage
                    !webInstalled -> "点按下载运行环境（约 214MB），之后在手机浏览器里用 codex"
                    webRunning -> "在手机浏览器里使用，点按直接打开"
                    else -> "点按启动并在浏览器里打开"
                }
            ) { openCodexWeb() }
            if (webInstalled && webRunning) {
                InsetDivider()
                ActionRow("停止网页版服务", "关闭后台运行的服务，释放内存") { stopCodexWeb() }
            }
            if (webInstalled) {
                InsetDivider()
                SwitchRow(
                    checked = webAutoKeep,
                    title = "后台保活",
                    summary = when {
                        !webAutoKeep -> "关闭后服务掉线需要手动重新启动"
                        webRunning && webRestarts > 0 -> "掉线自动重启，本次已恢复 $webRestarts 次"
                        webRunning -> "服务掉线自动重启，微信重启后自动恢复"
                        else -> "已开启，正在拉起服务…"
                    },
                    onCheckedChange = { on ->
                        webAutoKeep = on
                        scope.launch(Dispatchers.IO) {
                            val guard = h.Hchat.hooks.items.script.agent.CodexWebGuard
                            if (on) guard.enable(context) else guard.disable(context)
                            delay(600)
                            withContext(Dispatchers.Main) { refreshWeb() }
                        }
                    }
                )
            }
            InsetDivider()
            PopupChoiceRow(
                title = "终端字体",
                summary = "终端显示字体，未下载的需联网获取（每个约 200KB）",
                options = h.Hchat.hooks.items.script.agent.TerminalFonts.ALL.map {
                    PopupChoice(it.label, it.id)
                },
                currentValue = fontId,
                onValueChanged = { id ->
                    val fonts = h.Hchat.hooks.items.script.agent.TerminalFonts
                    val font = fonts.byId(id)
                    if (font.fileName.isEmpty() || fonts.isDownloaded(context, font)) {
                        fontId = id
                        fonts.select(context, id)
                    } else {
                        val activity = context as? Activity
                            ?: WeChatApis.currentActivity()?.currentActivity() as? Activity
                        if (activity != null) {
                            VoiceForwardMiuixDialog.showConfirm(
                                activity = activity,
                                title = "下载字体",
                                message = "「${font.label}」尚未下载（约 ${font.sizeKb}KB），需要联网下载后使用，是否下载？",
                                onResult = { ok ->
                                    if (ok) {
                                        fonts.download(context, font,
                                            onSuccess = {
                                                fontId = id
                                                fonts.select(context, id)
                                            },
                                            onError = { msg ->
                                                Toast.makeText(context, "字体下载失败：$msg", Toast.LENGTH_SHORT).show()
                                            })
                                    }
                                },
                                onDismiss = {}
                            )
                        }
                    }
                }
            )
            InsetDivider()
            ActionRow(
                title = when {
                    codexBusy -> "正在更新 codex…"
                    codexRemote != null -> "更新 codex 到 ${codexRemote?.version}"
                    codexVersion.isBlank() -> "内置 codex"
                    else -> "内置 codex ${codexVersion}"
                },
                summary = when {
                    codexBusy -> codexProgress.ifBlank { "准备中…" }
                    codexMessage.isNotBlank() -> codexMessage
                    codexVersion.isBlank() -> "点按检查服务器上的新版 codex"
                    else -> "点按检查更新"
                }
            ) {
                if (codexRemote != null) showCodexConfirm = true else checkCodexUpdate()
            }
            InsetDivider()
            ActionRow("重新安装", "重新下载并解压 Ubuntu 环境") {
                if (!installing) {
                    // 换 rootfs 前先关掉所有常驻会话（否则跑着的 shell 挂的是旧挂载，且旧 dexclub 会指向已删路径）
                    h.Hchat.hooks.items.script.agent.TermuxSessions.closeAll(context)
                    startInstall(true)
                }
            }
            InsetDivider()
            ActionRow("删除终端环境", "释放磁盘空间") { showDeleteConfirm = true }
        }
    }

    if (showCodexConfirm) {
        codexRemote?.let { release ->
            WindowDialog(
                show = true,
                title = "更新内置 codex ${release.version}",
                onDismissRequest = { showCodexConfirm = false },
                content = {
                    Column {
                        Text(
                            text = release.notes.ifBlank { "有新的 codex 版本可用。" },
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "包大小 ${formatFileSize(release.size)}，下载解压期间请勿退出微信。",
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            TextButton(
                                text = "取消",
                                onClick = { showCodexConfirm = false },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.textButtonColorsPrimary()
                            )
                            TextButton(
                                text = "更新",
                                onClick = { runCodexUpdate(release) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.textButtonColors()
                            )
                        }
                    }
                }
            )
        }
    }

    if (showTerminal) {
        ScriptPluginAgentTerminalDialog(onDismiss = { showTerminal = false })
    }
}

@Composable
private fun ScriptPluginAgentTerminalDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    var ready by remember { mutableStateOf(h.Hchat.hooks.items.script.agent.ProotEnvironment.isReady(context)) }
    var upgrading by remember { mutableStateOf(false) }
    var upgradeText by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        val env = h.Hchat.hooks.items.script.agent.ProotEnvironment
        if (!env.needsUpgrade(context)) return@LaunchedEffect
        upgrading = true
        upgradeText = "正在准备终端环境…"
        h.Hchat.hooks.items.script.agent.TermuxSessions.closeAll(context)
        withContext(Dispatchers.IO) {
            env.install(context) { pct, msg -> upgradeText = "$msg $pct%" }
        }
        upgrading = false
        ready = env.isReady(context)
    }
    var pageRef by remember {
        mutableStateOf<h.Hchat.hooks.items.script.agent.TermuxTerminalPage?>(null)
    }
    val density = LocalDensity.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
        ),
    ) {
        val dialogView = LocalView.current
        SideEffect {
            var parent: android.view.ViewParent? = dialogView.parent
            while (parent != null) {
                val window = (parent as? DialogWindowProvider)?.window
                if (window != null) {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
                    window.setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING or
                            WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED
                    )
                    window.setDimAmount(0f)
                    window.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(0xFF000000.toInt()))
                    break
                }
                parent = parent.parent
            }
        }
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF000000))) {
            if (upgrading) {
                Text(
                    text = "$upgradeText\n正在更新终端环境（内置 codex 与 DexClub，约 290MB），完成后自动进入终端。",
                    color = Color(0xFF9AA0A6),
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(20.dp)
                )
            } else if (!ready) {
                Text(
                    text = "Ubuntu 终端环境未安装。\n请先在「Agent 配置 → Ubuntu 终端环境」里点安装，完成后再打开终端。",
                    color = Color(0xFF9AA0A6),
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(20.dp)
                )
            } else {
                AndroidView(
                    factory = { ctx ->
                        h.Hchat.hooks.items.script.agent.TermuxTerminalPage(ctx).also { page ->
                            pageRef = page
                            // ☰ →「关闭终端」：结束全部会话后由视图请求关掉对话框
                            page.setOnExitRequested { onDismiss() }
                            page.start()
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
                val barsTopPx = with(density) {
                    WindowInsets.statusBars.asPaddingValues().calculateTopPadding().roundToPx()
                }
                val imeBottom = WindowInsets.ime.asPaddingValues().calculateBottomPadding()
                val bottomPx = with(density) { imeBottom.roundToPx() }
                SideEffect {
                    pageRef?.applyInsetsFromCompose(barsTopPx, bottomPx, imeBottom > 0.dp)
                }
            }
            DisposableEffect(Unit) {
                onDispose { pageRef?.detach() }
            }
        }
    }
}

@Composable
private fun ScriptPluginAgentEndpointPreview(endpoint: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
        Text(
            text = "实际请求地址",
            color = MiuixTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = endpoint,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun ScriptPluginAgentReasoningRow(
    endpointMode: String,
    model: String,
    effort: String,
    onValueChanged: (String) -> Unit
) {
    val options = ScriptPluginAgentReasoning.options(endpointMode, model)
    PopupChoiceRow(
        title = "推理强度",
        summary = ScriptPluginAgentReasoning.description(endpointMode, model),
        options = options.map { (value, label) -> PopupChoice(label, value) },
        currentValue = ScriptPluginAgentReasoning.effectiveEffort(endpointMode, model, effort),
        onValueChanged = onValueChanged
    )
}

@Composable
private fun CodexModelPickerPage(
    baseUrl: String,
    apiKey: String,
    currentModel: String,
    currentEffort: String,
    onSetModel: (String, String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val scrollBehavior = MiuixScrollBehavior()
    var models by remember(baseUrl, apiKey) { mutableStateOf<List<String>>(emptyList()) }
    var pendingModel by remember(currentModel) { mutableStateOf(currentModel) }
    var pendingEffort by remember(currentEffort) { mutableStateOf(currentEffort) }
    var query by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var resultText by remember { mutableStateOf("") }
    val visibleModels = remember(models, query) {
        val keyword = query.trim()
        if (keyword.isBlank()) models else models.filter { it.contains(keyword, ignoreCase = true) }
    }

    fun loadModels() {
        if (loading) return
        loading = true
        resultText = "正在拉取模型"
        scope.launch {
            val outcome = withContext(Dispatchers.IO) { CodexSettings.fetchModels(baseUrl, apiKey) }
            loading = false
            outcome.onSuccess { fetched ->
                models = fetched
                resultText = if (fetched.isEmpty()) "未获取到模型，可手动填写" else "已获取 ${fetched.size} 个模型"
            }.onFailure {
                resultText = "拉取失败：" + (it.message ?: "未知错误")
            }
        }
    }

    PageScaffold(
        title = "选择模型",
        largeTitle = "选择模型",
        scrollBehavior = scrollBehavior,
        bottomBar = {
            BottomActionBar(
                primaryText = "使用该模型",
                onPrimaryClick = {
                    val selected = pendingModel.trim()
                    if (selected.isBlank()) {
                        Toast.makeText(context, "请输入或选择模型", Toast.LENGTH_SHORT).show()
                    } else {
                        onSetModel(selected, CodexSettings.normalizeEffort(pendingEffort))
                    }
                },
                secondaryText = "返回",
                onSecondaryClick = onBack
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
            state = listState,
            contentPadding = PaddingValues(
                top = padding.calculateTopPadding() + 8.dp,
                bottom = padding.calculateBottomPadding() + 84.dp
            )
        ) {
            item {
                SettingsCard {
                    InputRow(
                        "模型",
                        "可直接手动填写，不依赖拉取结果",
                        pendingModel,
                        onValueChange = { pendingModel = it }
                    )
                    InsetDivider()
                    PopupChoiceRow(
                        title = "思考程度",
                        summary = ScriptPluginAgentReasoning.label(pendingEffort),
                        options = CodexSettings.EFFORT_OPTIONS.map { PopupChoice(it.second, it.first) },
                        currentValue = pendingEffort,
                        onValueChanged = { pendingEffort = it }
                    )
                }
            }
            item { SmallTitle(modifier = Modifier.padding(top = 10.dp), text = "从列表选择（可选）") }
            item {
                SettingsCard {
                    ActionRow(
                        "拉取模型列表",
                        resultText.ifBlank { "从当前 API 地址获取模型列表" }
                    ) { loadModels() }
                    if (models.isNotEmpty()) {
                        InsetDivider()
                        InputRow("搜索", "输入模型名称", query, onValueChange = { query = it })
                    }
                }
            }
            visibleModels.take(300).forEach { modelName ->
                item(key = modelName) {
                    SettingsCard(modifier = Modifier.padding(top = 6.dp)) {
                        ActionRow(
                            title = modelName,
                            summary = when {
                                modelName == pendingModel.trim() -> "已选择"
                                modelName == currentModel -> "当前模型"
                                else -> ""
                            }
                        ) { pendingModel = modelName }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScriptPluginAgentModelPickerPage(
    config: ScriptPluginAgentConfig,
    currentModel: String,
    onSelected: (String, String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val scrollBehavior = MiuixScrollBehavior()
    var models by remember(config.apiBaseUrl, config.apiKey, config.endpointMode) {
        mutableStateOf<List<String>>(emptyList())
    }
    var pendingModel by remember(currentModel) { mutableStateOf(currentModel) }
    var pendingReasoningEffort by remember(config.reasoningEffort) {
        mutableStateOf(config.reasoningEffort)
    }
    var query by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var loadError by remember { mutableStateOf("") }
    val visibleModels = remember(models, query) {
        val keyword = query.trim()
        if (keyword.isBlank()) models else models.filter { it.contains(keyword, ignoreCase = true) }
    }

    fun loadModels() {
        if (loading) return
        loading = true
        loadError = ""
        scope.launch {
            val result = withContext(Dispatchers.IO) { ScriptPluginAgentClient.fetchModels(config) }
            loading = false
            result.onSuccess { fetched ->
                models = fetched
                if (fetched.isEmpty()) loadError = "未获取到模型"
            }.onFailure {
                loadError = it.message ?: "拉取模型失败"
            }
        }
    }

    PageScaffold(
        title = "模型选择",
        largeTitle = "模型选择",
        scrollBehavior = scrollBehavior,
        bottomBar = {
            BottomActionBar(
                primaryText = "应用模型设置",
                onPrimaryClick = {
                    val selectedModel = pendingModel.trim()
                    if (selectedModel.isBlank()) {
                        Toast.makeText(context, "请输入或选择模型 ID", Toast.LENGTH_SHORT).show()
                    } else {
                        onSelected(
                            selectedModel,
                            ScriptPluginAgentReasoning.effectiveEffort(
                                config.endpointMode, selectedModel, pendingReasoningEffort
                            )
                        )
                    }
                },
                secondaryText = "返回",
                onSecondaryClick = onBack
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
            state = listState,
            contentPadding = PaddingValues(
                top = padding.calculateTopPadding() + 8.dp,
                bottom = padding.calculateBottomPadding() + 84.dp
            )
        ) {
            item {
                SettingsCard {
                    InputRow(
                        "模型 ID",
                        "可手动输入；列表获取失败或未包含该模型时仍可使用",
                        pendingModel,
                        onValueChange = { pendingModel = it }
                    )
                    InsetDivider()
                    ScriptPluginAgentReasoningRow(
                        config.endpointMode,
                        pendingModel,
                        pendingReasoningEffort,
                        onValueChanged = { pendingReasoningEffort = it }
                    )
                }
            }
            item { SmallTitle(modifier = Modifier.padding(top = 10.dp), text = "从列表选择（可选）") }
            item {
                SettingsCard {
                    ActionRow(
                        "获取模型列表",
                        when {
                            loading -> "正在拉取模型"
                            loadError.isNotBlank() -> loadError
                            models.isEmpty() -> "按需从当前接口获取，不影响手动输入"
                            else -> "已获取 ${models.size} 个模型"
                        }
                    ) { loadModels() }
                    if (models.isNotEmpty()) {
                        InsetDivider()
                        InputRow("搜索", "输入模型名称", query) { query = it }
                    }
                }
            }
            visibleModels.take(300).forEach { modelName ->
                item(key = modelName) {
                    SettingsCard(modifier = Modifier.padding(top = 6.dp)) {
                        ActionRow(
                            title = modelName,
                            summary = when {
                                modelName == pendingModel.trim() -> "已选择"
                                modelName == currentModel -> "当前模型"
                                else -> ""
                            }
                        ) { pendingModel = modelName }
                    }
                }
            }
        }
    }
}

private fun scriptPluginAgentRenderedMessages(
    messages: List<ScriptPluginAgentChatMessage>
): List<ScriptPluginAgentRenderedMessage> {
    val firstToolIndexByTurn = HashMap<String, Int>()
    messages.forEachIndexed { index, message ->
        if (message.role == "tool" && message.turnId.isNotBlank()) {
            firstToolIndexByTurn.putIfAbsent(message.turnId, index)
        }
    }
    val eventsByTurn = messages.asSequence()
        .filter { it.role == "tool" && it.turnId.isNotBlank() }
        .groupBy { it.turnId }
        .mapValues { (_, groups) -> groups.flatMap { it.toolEvents }.distinctBy { it.id } }
    return buildList {
        messages.forEachIndexed { index, message ->
            if (message.role != "tool" || message.turnId.isBlank()) {
                add(ScriptPluginAgentRenderedMessage(index, message))
                return@forEachIndexed
            }
            if (firstToolIndexByTurn[message.turnId] != index) return@forEachIndexed
            val events = eventsByTurn[message.turnId].orEmpty()
            val hasPending = events.any { it.status == "queued" || it.status == "running" }
            val wasInterrupted = events.any { it.status == "interrupted" }
            add(
                ScriptPluginAgentRenderedMessage(
                    sourceIndex = index,
                    message = message.copy(
                        toolEvents = events,
                        status = when {
                            hasPending -> "streaming"
                            wasInterrupted -> "interrupted"
                            else -> "complete"
                        },
                        completedAt = events.maxOfOrNull { it.finishedAt } ?: message.completedAt
                    )
                )
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ScriptPluginAgentChatPage(
    title: String,
    messages: List<ScriptPluginAgentChatMessage>,
    messageInput: String,
    pendingAttachments: List<ScriptPluginAgentAttachment>,
    pendingQuotedMessage: ScriptPluginAgentQuotedMessage?,
    pendingMessages: List<ScriptPluginAgentPendingMessage>,
    pendingMessagesExpanded: Boolean,
    activeProfileName: String,
    activeModel: String,
    enabled: Boolean,
    inputEnabled: Boolean,
    generating: Boolean,
    contextCompacting: Boolean,
    workingStartedAt: Long,
    workingLabel: String,
    showWorking: Boolean,
    pendingConfirmationLabel: String?,
    onMessageInputChange: (String) -> Unit,
    onSend: () -> Unit,
    onQueue: () -> Unit,
    onCancel: () -> Unit,
    onAttach: () -> Unit,
    onRemoveAttachment: (String) -> Unit,
    onClearQuotedMessage: () -> Unit,
    onPendingMessagesExpandedChange: (Boolean) -> Unit,
    onEditPendingMessage: (Long) -> Unit,
    onDeletePendingMessage: (Long) -> Unit,
    onSendPendingMessageNow: (Long) -> Unit,
    onOpenProfilePicker: () -> Unit,
    onOpenModelPicker: () -> Unit,
    onOpenQuickOptions: () -> Unit,
    onOpenPendingConfirmation: () -> Unit,
    onCompact: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenConfig: () -> Unit,
    onEditAndResend: (Int) -> Unit,
    onQuoteMessage: (Int) -> Unit,
    onContinueMessage: (Int) -> Unit,
    onRegenerateMessage: (Int) -> Unit,
    onRollback: (String) -> Unit,
    onDeleteMessage: (Int) -> Unit,
    onCreateBranch: (Int) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val scrollBehavior = MiuixScrollBehavior()
    val imeVisible = WindowInsets.isImeVisible
    var actionMessageId by remember { mutableStateOf<String?>(null) }
    var infoMessageId by remember { mutableStateOf<String?>(null) }
    var showScrollToBottom by remember { mutableStateOf(false) }
    var autoFollowOutput by remember { mutableStateOf(true) }
    var previousRenderedMessageCount by remember { mutableStateOf(-1) }
    val actionMessage = actionMessageId?.let { id -> messages.firstOrNull { it.id == id } }
    val infoMessage = infoMessageId?.let { id -> messages.firstOrNull { it.id == id } }
    val renderedMessages = remember(messages) {
        scriptPluginAgentRenderedMessages(messages)
    }
    var showTerminal by remember { mutableStateOf(false) }
    if (showTerminal) {
        ScriptPluginAgentTerminalDialog(onDismiss = { showTerminal = false })
    }

    fun consumeActionMessageIndex(): Int? {
        val messageId = actionMessageId
        actionMessageId = null
        return messageId?.let { id -> messages.indexOfFirst { it.id == id }.takeIf { it >= 0 } }
    }

    DisposableEffect(context) {
        val window = findAgentActivity(context)?.window
        val originalSoftInputMode = window?.attributes?.softInputMode
        if (window != null && originalSoftInputMode != null) {
            val state = originalSoftInputMode and WindowManager.LayoutParams.SOFT_INPUT_MASK_STATE
            window.setSoftInputMode(state or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
        onDispose {
            if (window != null && originalSoftInputMode != null) {
                window.setSoftInputMode(originalSoftInputMode)
            }
        }
    }

    if (actionMessage != null) {
        ScriptPluginAgentMessageMenu(
            message = actionMessage,
            onDismiss = { actionMessageId = null },
            onCopy = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                clipboard?.setPrimaryClip(ClipData.newPlainText("插件 Agent 消息", actionMessage.content))
                Toast.makeText(context, "消息已复制", Toast.LENGTH_SHORT).show()
                actionMessageId = null
            },
            onReadAloud = {
                ScriptPluginAgentSpeech.speak(context, actionMessage.content)
                Toast.makeText(context, "开始朗读", Toast.LENGTH_SHORT).show()
                actionMessageId = null
            },
            onQuote = {
                val index = consumeActionMessageIndex()
                if (index != null) onQuoteMessage(index)
            },
            onRegenerate = if (actionMessage.role == "assistant" && actionMessage.status != "streaming") {
                {
                    val index = consumeActionMessageIndex()
                    if (index != null) onRegenerateMessage(index)
                }
            } else {
                null
            },
            onEditAndResend = if (actionMessage.role == "user") {
                {
                    val index = consumeActionMessageIndex()
                    if (index != null) onEditAndResend(index)
                }
            } else {
                null
            },
            onRollback = {
                val messageId = actionMessageId
                actionMessageId = null
                if (messageId != null) {
                    val renderedIndex = renderedMessages.indexOfFirst { it.message.id == messageId }
                    if (renderedIndex >= 0) listState.requestScrollToItem(renderedIndex)
                    onRollback(messageId)
                }
            },
            onDelete = {
                val index = consumeActionMessageIndex()
                if (index != null) onDeleteMessage(index)
            },
            onCreateBranch = {
                val index = consumeActionMessageIndex()
                if (index != null) onCreateBranch(index)
            },
            onInfo = {
                infoMessageId = actionMessageId
                actionMessageId = null
            }
        )
    }
    if (infoMessage != null) {
        ScriptPluginAgentMessageInfoDialog(
            message = infoMessage,
            onDismiss = { infoMessageId = null }
        )
    }
    LaunchedEffect(renderedMessages.size) {
        val previousCount = previousRenderedMessageCount
        previousRenderedMessageCount = renderedMessages.size
        if (messages.isNotEmpty()) {
            autoFollowOutput = true
            if (previousCount >= 0 && renderedMessages.size < previousCount) {
                listState.requestScrollToItem(renderedMessages.size)
            } else {
                delay(80L)
                listState.animateScrollToLatest(renderedMessages.size)
            }
        }
    }
    LaunchedEffect(
        messages.lastOrNull()?.content,
        messages.lastOrNull()?.reasoning,
        messages.lastOrNull()?.toolEvents?.size,
        messages.lastOrNull()?.progress
    ) {
        if (messages.isNotEmpty() && autoFollowOutput) {
            delay(40L)
            listState.scrollToLatest(renderedMessages.size)
        }
    }
    LaunchedEffect(listState, messages.isNotEmpty()) {
        snapshotFlow { listState.isScrollInProgress to listState.isNearBottom() }.collect { (scrolling, atBottom) ->
            if (atBottom) autoFollowOutput = true
            else if (scrolling) autoFollowOutput = false
            showScrollToBottom = !atBottom && messages.isNotEmpty()
        }
    }
    LaunchedEffect(imeVisible) {
        if (imeVisible && autoFollowOutput && messages.isNotEmpty()) {
            delay(180L)
            listState.scrollToLatest(renderedMessages.size)
        }
    }
    LaunchedEffect(contextCompacting) {
        if (contextCompacting && !generating && messages.isNotEmpty()) {
            autoFollowOutput = true
            delay(40L)
            listState.animateScrollToLatest(renderedMessages.size)
        }
    }
    PageScaffold(
        title = title.ifBlank { "插件 Agent" },
        largeTitle = "",
        scrollBehavior = scrollBehavior,
        onBack = onBack,
        topBarActions = {
            Image(
                imageVector = NavIcons.Terminal,
                contentDescription = "Ubuntu 终端",
                colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onSurface),
                modifier = Modifier.size(24.dp).responsiveTap(onClick = { showTerminal = true })
            )
            Image(
                imageVector = NavIcons.Compact,
                contentDescription = "压缩上下文",
                colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onSurface),
                modifier = Modifier.padding(start = 14.dp).size(24.dp).responsiveTap(onClick = onCompact)
            )
            Image(
                imageVector = NavIcons.History,
                contentDescription = "会话历史",
                colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onSurface),
                modifier = Modifier.padding(start = 14.dp).size(24.dp).responsiveTap(onClick = onOpenHistory)
            )
            Image(
                imageVector = NavIcons.Settings,
                contentDescription = "Agent 配置",
                colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onSurface),
                modifier = Modifier.padding(start = 14.dp).size(24.dp).responsiveTap(onClick = onOpenConfig)
            )
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().imePadding()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
                    state = listState,
                    verticalArrangement = Arrangement.Bottom,
                    contentPadding = PaddingValues(
                        top = padding.calculateTopPadding() + 8.dp,
                        bottom = 8.dp
                    )
                ) {
                    if (messages.isEmpty()) {
                        item {
                            Text(
                                text = "开始一个新会话",
                                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                                fontSize = 14.sp,
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 28.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        renderedMessages.forEach { rendered ->
                            val index = rendered.sourceIndex
                            val message = rendered.message
                            item(key = message.id) {
                                ScriptPluginAgentMessageBlock(
                                    message = message,
                                    showWorking = showWorking && index == messages.lastIndex,
                                    workingStartedAt = workingStartedAt,
                                    workingLabel = workingLabel,
                                    longPressEnabled = enabled && message.role != "tool",
                                    onLongPress = { actionMessageId = message.id },
                                    onContinue = if (
                                        !generating &&
                                        (message.status == "interrupted" || message.status == "error") &&
                                        messages.drop(index + 1).none { it.role == "user" }
                                    ) {
                                        { onContinueMessage(index) }
                                    } else null,
                                    onRetry = if (
                                        !generating && message.role == "assistant" &&
                                        (message.status == "interrupted" || message.status == "error")
                                    ) {
                                        { onRegenerateMessage(index) }
                                    } else null
                                )
                            }
                        }
                        if (contextCompacting && !generating) {
                            item(key = "agent-context-compaction") {
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 10.dp)
                                ) {
                                    ScriptPluginAgentWorkingIndicator(
                                        startedAt = workingStartedAt,
                                        label = workingLabel
                                    )
                                }
                            }
                        }
                        item(key = "agent-message-tail") {
                            Spacer(modifier = Modifier.height(1.dp))
                        }
                    }
                }
                if (showScrollToBottom && messages.isNotEmpty()) {
                    Image(
                        imageVector = NavIcons.MoveDown,
                        contentDescription = "回到底部",
                        colorFilter = ColorFilter.tint(Color.White),
                        modifier = Modifier.align(Alignment.BottomEnd)
                            .padding(end = 16.dp, bottom = 8.dp)
                            .size(36.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(MiuixTheme.colorScheme.primary)
                            .clickable {
                                showScrollToBottom = false
                                autoFollowOutput = true
                                scope.launch { listState.animateScrollToLatest(renderedMessages.size) }
                            }
                            .padding(7.dp)
                    )
                }
            }
            if (pendingConfirmationLabel != null) {
                ScriptPluginAgentPendingConfirmationBar(
                    label = pendingConfirmationLabel,
                    onClick = onOpenPendingConfirmation
                )
            }
            Box(
                modifier = Modifier.fillMaxWidth()
                    .padding(bottom = padding.calculateBottomPadding())
            ) {
                ScriptPluginAgentComposer(
                    value = messageInput,
                    attachments = pendingAttachments,
                    quotedMessage = pendingQuotedMessage,
                    pendingMessages = pendingMessages,
                    pendingMessagesExpanded = pendingMessagesExpanded,
                    activeProfileName = activeProfileName,
                    activeModel = activeModel,
                    enabled = inputEnabled,
                    generating = generating || contextCompacting,
                    onValueChange = onMessageInputChange,
                    onSend = onSend,
                    onQueue = onQueue,
                    onCancel = onCancel,
                    onAttach = onAttach,
                    onRemoveAttachment = onRemoveAttachment,
                    onClearQuotedMessage = onClearQuotedMessage,
                    onPendingMessagesExpandedChange = onPendingMessagesExpandedChange,
                    onEditPendingMessage = onEditPendingMessage,
                    onDeletePendingMessage = onDeletePendingMessage,
                    onSendPendingMessageNow = onSendPendingMessageNow,
                    onOpenProfilePicker = onOpenProfilePicker,
                    onOpenModelPicker = onOpenModelPicker,
                    onOpenQuickOptions = onOpenQuickOptions
                )
            }
        }
    }
}

@Composable
private fun ScriptPluginAgentAlwaysAllowRow(
    checked: Boolean,
    summary: String,
    onCheckedChange: (Boolean) -> Unit
) {
    SwitchRow(
        checked = checked,
        title = "始终允许",
        summary = summary,
        onCheckedChange = onCheckedChange
    )
}

@Composable
private fun ScriptPluginAgentPendingConfirmationBar(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .background(MiuixTheme.colorScheme.surfaceVariant)
            .responsiveTap(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            imageVector = NavIcons.Modules,
            contentDescription = null,
            colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.primary),
            modifier = Modifier.size(20.dp)
        )
        Column(modifier = Modifier.padding(start = 10.dp).weight(1f)) {
            Text(
                text = "待确认的插件修改",
                color = MiuixTheme.colorScheme.onSurface,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = label,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            text = "查看 Diff",
            color = MiuixTheme.colorScheme.primary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 10.dp)
        )
    }
}

private suspend fun LazyListState.animateScrollToLatest(index: Int) {
    animateScrollToItem(index)
}

private suspend fun LazyListState.scrollToLatest(index: Int) {
    scrollToItem(index)
}

private fun LazyListState.isNearBottom(thresholdPx: Int = 120): Boolean {
    val info = layoutInfo
    if (info.totalItemsCount == 0) return true
    val lastVisible = info.visibleItemsInfo.lastOrNull() ?: return true
    if (lastVisible.index < info.totalItemsCount - 1) return false
    return lastVisible.offset + lastVisible.size <= info.viewportEndOffset + thresholdPx
}

@Composable
private fun ScriptPluginAgentMessageBlock(
    message: ScriptPluginAgentChatMessage,
    showWorking: Boolean,
    workingStartedAt: Long,
    workingLabel: String,
    longPressEnabled: Boolean,
    onLongPress: () -> Unit,
    onContinue: (() -> Unit)?,
    onRetry: (() -> Unit)?
) {
    if (message.phase == "assistant_tool_call" && message.content.isBlank()) return
    val context = LocalContext.current
    val waitingForReply = message.role == "assistant" &&
        message.status == "streaming" &&
        message.content.isBlank()
    if (waitingForReply && !showWorking && message.reasoning.isBlank() && message.toolEvents.isEmpty()) return
    val longPressModifier = Modifier.agentMessageLongPress(
        key = message.id.hashCode().toLong(),
        enabled = longPressEnabled,
        onLongPress = onLongPress
    )
    if (message.role == "user") {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Box(
                modifier = Modifier
                    .then(longPressModifier)
                    .clip(RoundedCornerShape(18.dp))
                    .background(MiuixTheme.colorScheme.primary.copy(alpha = 0.14f))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Column {
                    message.quotedMessage?.let { quoted ->
                        ScriptPluginAgentQuotedMessagePreview(
                            quotedMessage = quoted,
                            maxLines = 3,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    Text(text = message.content, color = MiuixTheme.colorScheme.onSurface, fontSize = 14.sp)
                    if (message.attachments.isNotEmpty()) {
                        Text(
                            text = message.attachments.joinToString("\n") { "附件: ${it.name}" },
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxWidth().then(longPressModifier)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            if (waitingForReply) {
                if (showWorking) {
                    ScriptPluginAgentWorkingIndicator(
                        startedAt = workingStartedAt.takeIf { it > 0L } ?: message.createdAt,
                        label = workingLabel
                    )
                }
            } else if (message.role == "tool") {
                Text(
                    text = "工具调用",
                    color = MiuixTheme.colorScheme.primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            ScriptPluginAgentReasoningGroup(message)
            ScriptPluginAgentToolGroup(message)
            if (message.role != "tool" && message.content.isBlank() && !waitingForReply) {
                Text(
                    text = when (message.status) {
                        "interrupted" -> "已中断，尚未收到回复"
                        else -> "本轮没有返回可显示内容"
                    },
                    color = MiuixTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = if (message.toolEvents.isNotEmpty()) 8.dp else 4.dp)
                )
            } else if (message.role != "tool") {
                if (message.status == "streaming") {
                    Text(
                        text = message.content,
                        color = MiuixTheme.colorScheme.onSurface,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                } else {
                    MarkdownUi.Content(
                        context = context,
                        markdown = message.content,
                        contentPadding = PaddingValues(top = if (message.toolEvents.isNotEmpty()) 8.dp else 4.dp),
                        bodyFontSize = 14.sp,
                        onCopyCode = { code ->
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                            clipboard?.setPrimaryClip(ClipData.newPlainText("Agent 代码", code))
                            Toast.makeText(context, "代码已复制", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
            if (message.status == "interrupted" || message.status == "error") {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (message.status == "interrupted") "已中断" else "请求失败",
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        fontSize = 12.sp,
                        modifier = Modifier.weight(1f)
                    )
                    if (onContinue != null) {
                        TextButton(
                            text = "继续任务",
                            onClick = onContinue,
                            minWidth = 0.dp,
                            minHeight = 30.dp,
                            insideMargin = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            colors = ButtonDefaults.textButtonColorsPrimary()
                        )
                    }
                    if (onRetry != null) {
                        TextButton(
                            text = "重新开始",
                            onClick = onRetry,
                            minWidth = 0.dp,
                            minHeight = 30.dp,
                            insideMargin = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            colors = ButtonDefaults.textButtonColorsPrimary()
                        )
                    }
                }
            }
            if (message.role != "tool" && message.diff.isNotBlank()) {
                ScriptPluginAgentDiffGroup("message:${message.createdAt}", message.diff)
            }
        }
    }
}

private fun isScriptPluginAgentControlPreview(content: String): Boolean {
    return content.trim().startsWith("准备调用")
}

@Composable
private fun ScriptPluginAgentWorkingIndicator(startedAt: Long, label: String = "") {
    var elapsedSeconds by remember(startedAt) {
        mutableStateOf(((System.currentTimeMillis() - startedAt).coerceAtLeast(0L) / 1_000L))
    }
    LaunchedEffect(startedAt) {
        while (true) {
            elapsedSeconds = (System.currentTimeMillis() - startedAt).coerceAtLeast(0L) / 1_000L
            delay(1_000L)
        }
    }
    val elapsedText = if (elapsedSeconds < 60L) {
        "${elapsedSeconds}s"
    } else {
        "${elapsedSeconds / 60L}m ${elapsedSeconds % 60L}s"
    }
    Text(
        text = buildString {
            append("Working (").append(elapsedText).append(')')
            if (label.isNotBlank()) append(" · ").append(label)
        },
        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium
    )
}

fun findAgentActivity(context: Context): Activity? {
    var current: Context? = context
    while (current is ContextWrapper) {
        if (current is Activity) return current
        current = current.baseContext
    }
    return current as? Activity
}

@Composable
private fun ScriptPluginAgentReasoningGroup(message: ScriptPluginAgentChatMessage) {
    val context = LocalContext.current
    val reasoning = message.reasoning
    if (reasoning.isEmpty()) return
    var expanded by rememberSaveable(message.createdAt, "reasoning") {
        mutableStateOf(false)
    }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 90f else 0f,
        animationSpec = tween(220),
        label = "agentReasoningArrow"
    )
    Column(modifier = Modifier.fillMaxWidth().padding(top = 5.dp).animateContentSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(6.dp))
                .responsiveTap(onClick = {
                    expanded = !expanded
                })
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                imageVector = NavIcons.Expand,
                contentDescription = if (expanded) "收起思考过程" else "展开思考过程",
                colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onSurfaceVariantSummary),
                modifier = Modifier.size(18.dp).graphicsLayer { rotationZ = rotation }
            )
            Text(
                text = if (message.status == "streaming") "思考中" else "思考过程",
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 7.dp)
            )
        }
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(tween(160)),
            exit = fadeOut(tween(120))
        ) {
            MarkdownUi.Text(
                context = context,
                text = reasoning,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth().padding(start = 25.dp, bottom = 6.dp)
            )
        }
    }
}

@Composable
private fun ScriptPluginAgentQuotedMessagePreview(
    quotedMessage: ScriptPluginAgentQuotedMessage,
    maxLines: Int,
    modifier: Modifier = Modifier,
    onClear: (() -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(MiuixTheme.colorScheme.onSurface.copy(alpha = 0.05f))
            .padding(start = 8.dp, top = 6.dp, end = if (onClear == null) 8.dp else 4.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (quotedMessage.role == "assistant") "引用 Agent" else "引用用户",
                color = MiuixTheme.colorScheme.primary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = quotedMessage.content,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                fontSize = 11.sp,
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (onClear != null) {
            Image(
                imageVector = NavIcons.Close,
                contentDescription = "取消引用",
                colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onSurfaceVariantSummary),
                modifier = Modifier.padding(start = 6.dp).size(24.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable(onClick = onClear)
                    .padding(5.dp)
            )
        }
    }
}

@Composable
private fun ScriptPluginAgentToolGroup(message: ScriptPluginAgentChatMessage) {
    val context = LocalContext.current
    if (message.toolEvents.isEmpty()) return
    val streaming = message.status == "streaming"
    var expanded by rememberSaveable(message.createdAt) { mutableStateOf(streaming) }
    var userOverride by rememberSaveable(message.createdAt, "work-override") {
        mutableStateOf<Boolean?>(null)
    }
    var detailEvent by remember(message.createdAt) { mutableStateOf<ScriptPluginAgentToolEvent?>(null) }
    detailEvent?.let { event ->
        ScriptPluginAgentToolEventDialog(
            event = event,
            onCopy = { value ->
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                clipboard?.setPrimaryClip(ClipData.newPlainText(event.name, value))
                Toast.makeText(context, "已复制", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { detailEvent = null }
        )
    }
    LaunchedEffect(streaming) {
        if (userOverride == null) expanded = streaming
    }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 90f else 0f,
        animationSpec = tween(250),
        label = "agentWorkArrow"
    )
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 5.dp).animateContentSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(6.dp))
                .responsiveTap(onClick = {
                    expanded = !expanded
                    userOverride = expanded
                })
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                imageVector = NavIcons.Expand,
                contentDescription = if (expanded) "收起" else "展开",
                colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onSurfaceVariantSummary),
                modifier = Modifier.size(18.dp).graphicsLayer { rotationZ = rotation }
            )
            Text(
                text = "工具调用（${message.toolEvents.size}）",
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 7.dp)
            )
        }
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(tween(180)),
            exit = fadeOut(tween(140))
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(start = 24.dp, bottom = 6.dp)) {
                message.toolEvents.forEach { event ->
                    ScriptPluginAgentToolEventRow(event) { detailEvent = event }
                }
            }
        }
    }
}

@Composable
private fun ScriptPluginAgentToolEventRow(
    event: ScriptPluginAgentToolEvent,
    onClick: () -> Unit
) {
    val workspaceWrite = isScriptPluginAgentWorkspaceWriteEvent(event)
    val icon = when (event.kind) {
        "search" -> NavIcons.Search
        "plugin", "workspace" -> NavIcons.Modules
        else -> NavIcons.Compact
    }
    val statusText = when (event.status) {
        "queued" -> "排队"
        "running" -> "进行中"
        "success" -> "完成"
        "interrupted" -> "已中断"
        else -> "失败"
    }
    val durationMs = if (event.startedAt > 0L && event.finishedAt >= event.startedAt) {
        event.finishedAt - event.startedAt
    } else {
        0L
    }
    val durationText = if (durationMs >= 1_000L) {
        "${durationMs / 1_000L}.${(durationMs % 1_000L) / 100}s"
    } else if (durationMs > 0L) {
        "${durationMs}ms"
    } else {
        ""
    }
    val canOpen = if (workspaceWrite) {
        event.diff.isNotBlank()
    } else {
        event.arguments.isNotBlank() || event.diff.isNotBlank() || event.result.isNotBlank()
    }
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(6.dp))
            .then(if (canOpen) Modifier.responsiveTap(onClick = onClick) else Modifier)
            .padding(vertical = 5.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                imageVector = icon,
                contentDescription = null,
                colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.primary),
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = event.name,
                color = MiuixTheme.colorScheme.primary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start = 8.dp).weight(1f)
            )
            Text(
                text = listOf(statusText, durationText).filter { it.isNotBlank() }.joinToString(" "),
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        if (workspaceWrite) {
            if (event.progress.isNotBlank() && event.status == "running") {
                Text(
                    text = event.progress,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth().padding(start = 26.dp, top = 2.dp)
                )
            }
            return@Column
        }
        if (event.arguments.isNotBlank()) {
            Text(
                text = scriptPluginAgentToolPreview(event.arguments),
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth().padding(start = 26.dp, top = 2.dp)
            )
        }
        if (event.progress.isNotBlank() && event.status == "running") {
            Text(
                text = event.progress,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth().padding(start = 26.dp, top = 2.dp)
            )
        }
        if (event.diff.isNotBlank()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 26.dp, top = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    imageVector = NavIcons.Modules,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.primary),
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "查看 Diff",
                    color = MiuixTheme.colorScheme.primary,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
        }
        if (event.result.isNotBlank()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 26.dp, top = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    imageVector = NavIcons.Back,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onSurfaceVariantSummary),
                    modifier = Modifier.size(14.dp).graphicsLayer { rotationZ = 180f }
                )
                Text(
                    text = scriptPluginAgentToolPreview(event.result),
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 6.dp).weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ScriptPluginAgentToolEventDialog(
    event: ScriptPluginAgentToolEvent,
    onCopy: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var fullResult by remember(event.id) { mutableStateOf<String?>(null) }
    var loadingFullResult by remember(event.id) { mutableStateOf(false) }
    WindowDialog(
        show = true,
        title = event.name,
        onDismissRequest = onDismiss,
        content = {
            Column(
                modifier = Modifier.fillMaxWidth().heightIn(max = 460.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                val workspaceWrite = isScriptPluginAgentWorkspaceWriteEvent(event)
                if (workspaceWrite && event.diff.isNotBlank()) {
                    ScriptPluginAgentDiffCodeBlock(event.diff)
                }
                if (!workspaceWrite && event.arguments.isNotBlank()) {
                    Text(
                        text = "调用参数",
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                    )
                    MarkdownUi.CodeBlock(event.arguments)
                }
                if (!workspaceWrite && event.diff.isNotBlank()) {
                    Text(
                        text = "代码差异",
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                    )
                    ScriptPluginAgentDiffCodeBlock(event.diff)
                }
                if (!workspaceWrite && event.result.isNotBlank()) {
                    Text(
                        text = if (event.status == "success") "执行结果" else "执行信息",
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                    )
                    ScriptPluginAgentToolResultBlock(event, event.result)
                    TextButton(
                        text = "复制结果",
                        onClick = { onCopy(event.result) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColorsPrimary()
                    )
                }
                if (!workspaceWrite && event.resultHandle.isNotBlank()) {
                    Text(
                        text = "完整结果已保存（${event.resultLength} 字符）",
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                    )
                    TextButton(
                        text = if (loadingFullResult) "正在读取完整结果" else "加载完整结果",
                        enabled = !loadingFullResult,
                        onClick = {
                            loadingFullResult = true
                            scope.launch {
                                fullResult = withContext(Dispatchers.IO) {
                                    ScriptPluginAgentToolResultStore.readAll(context, event.resultHandle).getOrNull()
                                }
                                loadingFullResult = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColorsPrimary()
                    )
                    fullResult?.let { value ->
                        ScriptPluginAgentToolResultBlock(event, value)
                        TextButton(
                            text = "复制完整结果",
                            onClick = { onCopy(value) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.textButtonColorsPrimary()
                        )
                    }
                }
                TextButton(
                    text = "关闭",
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    colors = ButtonDefaults.textButtonColorsPrimary()
                )
            }
        }
    )
}

private fun isScriptPluginAgentWorkspaceWriteEvent(event: ScriptPluginAgentToolEvent): Boolean {
    if (event.name == "写入插件文件" || event.name == "修改插件文件") return true
    if (event.kind != "workspace") return false
    val protocolName = event.protocolName.lowercase(Locale.ROOT)
    return protocolName == "write_file" ||
        protocolName == "apply_patch" ||
        protocolName.endsWith(".write_file") ||
        protocolName.endsWith(".apply_patch") ||
        protocolName.endsWith("_write_file") ||
        protocolName.endsWith("_apply_patch")
}

@Composable
private fun ScriptPluginAgentDiffGroup(stateKey: String, diff: String) {
    var expanded by rememberSaveable(stateKey, "diff") { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 90f else 0f,
        animationSpec = tween(250),
        label = "agentDiffArrow"
    )
    Column(modifier = Modifier.fillMaxWidth().padding(top = 6.dp).animateContentSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(6.dp))
                .responsiveTap(onClick = { expanded = !expanded })
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                imageVector = NavIcons.Expand,
                contentDescription = if (expanded) "收起代码变更" else "展开代码变更",
                colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onSurfaceVariantSummary),
                modifier = Modifier.size(18.dp).graphicsLayer { rotationZ = rotation }
            )
            Text(
                text = "代码变更",
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 7.dp)
            )
        }
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(tween(180)),
            exit = fadeOut(tween(140))
        ) {
            ScriptPluginAgentDiffCodeBlock(diff)
        }
    }
}

@Composable
private fun ScriptPluginAgentToolResultBlock(event: ScriptPluginAgentToolEvent, value: String) {
    val isDiffTool = event.name == "查看代码差异" ||
        event.protocolName.contains("show_diff", ignoreCase = true)
    val diff = if (isDiffTool) scriptPluginAgentDiffFromToolResult(value) else null
    if (diff != null) {
        ScriptPluginAgentDiffCodeBlock(diff)
    } else {
        MarkdownUi.CodeBlock(value)
    }
}

private fun scriptPluginAgentDiffFromToolResult(value: String): String? {
    return runCatching {
        JSONObject(value).optString("diff", "").takeIf { it.isNotBlank() }
    }.getOrNull()
}

@Composable
private fun ScriptPluginAgentDiffCodeBlock(diff: String) {
    val darkTheme = isSystemInDarkTheme()
    val additionText = if (darkTheme) Color(0xFF7EE787) else Color(0xFF116329)
    val additionBackground = if (darkTheme) Color(0xFF123820) else Color(0xFFE6FFEC)
    val deletionText = if (darkTheme) Color(0xFFFFA198) else Color(0xFFCF222E)
    val deletionBackground = if (darkTheme) Color(0xFF3D1C20) else Color(0xFFFFEBE9)
    val hunkText = if (darkTheme) Color(0xFF79C0FF) else Color(0xFF0969DA)
    val hunkBackground = if (darkTheme) Color(0xFF162A42) else Color(0xFFDDF4FF)
    val neutralText = MiuixTheme.colorScheme.onSurface
    val headerText = MiuixTheme.colorScheme.onSurfaceVariantSummary

    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MiuixTheme.colorScheme.surfaceVariant)
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 6.dp)
    ) {
        diff.lines().forEach { line ->
            val isHeader = line.startsWith("diff --git ") ||
                line.startsWith("--- ") ||
                line.startsWith("+++ ") ||
                line.startsWith("index ") ||
                line.startsWith("new file mode ") ||
                line.startsWith("deleted file mode ") ||
                line.startsWith("similarity index ") ||
                line.startsWith("rename from ") ||
                line.startsWith("rename to ") ||
                line.startsWith("Binary files ") ||
                line.startsWith("Only in ")
            val (textColor, backgroundColor) = when {
                line.startsWith("@@") -> hunkText to hunkBackground
                isHeader -> headerText to Color.Transparent
                line.startsWith("+") -> additionText to additionBackground
                line.startsWith("-") -> deletionText to deletionBackground
                else -> neutralText to Color.Transparent
            }
            Text(
                text = line.ifEmpty { " " },
                color = textColor,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                softWrap = false,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
                    .background(backgroundColor)
                    .padding(horizontal = 10.dp, vertical = 1.dp)
            )
        }
    }
}

private fun scriptPluginAgentToolPreview(value: String): String {
    return value.replace(Regex("\\s+"), " ").trim()
}

@Composable
private fun ScriptPluginAgentMessageMenu(
    message: ScriptPluginAgentChatMessage,
    onDismiss: () -> Unit,
    onCopy: () -> Unit,
    onReadAloud: () -> Unit,
    onQuote: () -> Unit,
    onRegenerate: (() -> Unit)?,
    onEditAndResend: (() -> Unit)?,
    onRollback: () -> Unit,
    onDelete: () -> Unit,
    onCreateBranch: () -> Unit,
    onInfo: () -> Unit
) {
    WindowDialog(
        show = true,
        title = "消息操作",
        onDismissRequest = onDismiss,
        content = {
            Column {
                ScriptPluginAgentMessageActionRow(NavIcons.Copy, "复制消息", onCopy)
                InsetDivider()
                ScriptPluginAgentMessageActionRow(NavIcons.Volume, "朗读消息", onReadAloud)
                InsetDivider()
                ScriptPluginAgentMessageActionRow(NavIcons.Quote, "引用消息", onQuote)
                if (onRegenerate != null) {
                    InsetDivider()
                    ScriptPluginAgentMessageActionRow(NavIcons.Refresh, "重新生成", onRegenerate)
                }
                if (onEditAndResend != null) {
                    InsetDivider()
                    ScriptPluginAgentMessageActionRow(NavIcons.Edit, "编辑并重发", onEditAndResend)
                }
                InsetDivider()
                ScriptPluginAgentMessageActionRow(NavIcons.History, "回滚到此处", onRollback)
                InsetDivider()
                ScriptPluginAgentMessageActionRow(NavIcons.Delete, "删除", onDelete)
                InsetDivider()
                ScriptPluginAgentMessageActionRow(NavIcons.Branch, "创建分支", onCreateBranch)
                InsetDivider()
                ScriptPluginAgentMessageActionRow(NavIcons.Info, "信息", onInfo)
                if (message.content.isBlank()) {
                    Text(
                        text = "当前消息没有正文",
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }
    )
}

@Composable
private fun ScriptPluginAgentMessageActionRow(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(6.dp))
            .responsiveTap(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            imageVector = icon,
            contentDescription = null,
            colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onSurfaceVariantSummary),
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = text,
            color = MiuixTheme.colorScheme.onSurface,
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 14.dp)
        )
    }
}

@Composable
private fun ScriptPluginAgentMessageInfoDialog(
    message: ScriptPluginAgentChatMessage,
    onDismiss: () -> Unit
) {
    val status = when (message.status) {
        "streaming" -> "生成中"
        "interrupted" -> "已中断"
        "error" -> "请求失败"
        else -> "已完成"
    }
    WindowDialog(
        show = true,
        title = "消息信息",
        onDismissRequest = onDismiss,
        content = {
            Column {
                InfoRow(
                    "角色",
                    when (message.role) {
                        "user" -> "用户"
                        "tool" -> "工具"
                        else -> "Agent"
                    }
                )
                InsetDivider()
                InfoRow("时间", scheduledTaskTimeText(message.createdAt))
                InsetDivider()
                InfoRow("状态", status)
                InsetDivider()
                InfoRow("字符数", message.content.length.toString())
                InsetDivider()
                InfoRow(
                    "Token 估算",
                    ScriptPluginAgentContext.estimateTokens("", listOf(message), null).toString()
                )
                if (message.attachments.isNotEmpty()) {
                    InsetDivider()
                    InfoRow("附件", message.attachments.size.toString())
                }
                TextButton(
                    text = "关闭",
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    colors = ButtonDefaults.textButtonColorsPrimary()
                )
            }
        }
    )
}

private fun mergeScriptPluginAgentProgress(current: String, next: String): String {
    val cleanNext = next.trim()
    return cleanNext.ifBlank { current }
}

private fun mergeScriptPluginAgentReply(current: String, incoming: String): String {
    if (incoming.isBlank()) return current
    return ScriptPluginAgentTextMerge.mergeReply(current, incoming)
}

private fun Modifier.agentMessageLongPress(
    key: Long,
    enabled: Boolean,
    onLongPress: () -> Unit
): Modifier = pointerInput(key, enabled) {
    if (!enabled) return@pointerInput
    awaitEachGesture {
        val down = awaitFirstDown(
            requireUnconsumed = false,
            pass = PointerEventPass.Initial
        )
        val releasedOrMoved = withTimeoutOrNull(viewConfiguration.longPressTimeoutMillis) {
            while (true) {
                val event = awaitPointerEvent(PointerEventPass.Initial)
                val change = event.changes.firstOrNull { it.id == down.id }
                    ?: return@withTimeoutOrNull true
                if (!change.pressed) return@withTimeoutOrNull true
                if (
                    abs(change.position.x - down.position.x) > viewConfiguration.touchSlop ||
                    abs(change.position.y - down.position.y) > viewConfiguration.touchSlop
                ) {
                    return@withTimeoutOrNull true
                }
            }
            @Suppress("UNREACHABLE_CODE")
            false
        }
        if (releasedOrMoved == null) {
            onLongPress()
            while (true) {
                val event = awaitPointerEvent(PointerEventPass.Initial)
                val change = event.changes.firstOrNull { it.id == down.id }
                change?.consume()
                if (change == null || !change.pressed) break
            }
        }
    }
}

@Composable
private fun ScriptPluginAgentHistoryDrawer(
    sessions: List<ScriptPluginAgentSession>,
    activeId: String,
    workingSessionIds: Set<String>,
    enabled: Boolean,
    onNewSession: () -> Unit,
    onSelect: (ScriptPluginAgentSession) -> Unit,
    onRename: (ScriptPluginAgentSession, String) -> Unit,
    onCommitOrder: (List<ScriptPluginAgentSession>) -> Unit,
    onSetPinned: (ScriptPluginAgentSession, Boolean) -> Unit,
    onSetLocked: (ScriptPluginAgentSession, Boolean) -> Unit,
    onDelete: (ScriptPluginAgentSession) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val uiPrefs = remember(context) { HchatStorage.preferences(context, UI_PREFS_NAME) }
    var actionSessionId by remember { mutableStateOf<String?>(null) }
    var renameSessionId by remember { mutableStateOf<String?>(null) }
    var deleteSessionId by remember { mutableStateOf<String?>(null) }
    var draggedSessionId by remember { mutableStateOf<String?>(null) }
    var dragOrderChanged by remember { mutableStateOf(false) }
    var orderedSessions by remember { mutableStateOf(sessions) }
    var showGestureHint by remember {
        mutableStateOf(uiPrefs.getBoolean(KEY_AGENT_HISTORY_GESTURE_HINT, true))
    }
    var titleDraft by remember { mutableStateOf("") }
    val actionSession = actionSessionId?.let { id -> orderedSessions.firstOrNull { it.id == id } }
    val renameSession = renameSessionId?.let { id -> orderedSessions.firstOrNull { it.id == id } }
    val deleteSession = deleteSessionId?.let { id -> orderedSessions.firstOrNull { it.id == id } }

    LaunchedEffect(sessions) {
        if (draggedSessionId == null) orderedSessions = sessions
    }

    fun movedSessions(session: ScriptPluginAgentSession, delta: Int): List<ScriptPluginAgentSession>? {
        val next = orderedSessions.toMutableList()
        val from = next.indexOfFirst { it.id == session.id }
        val to = from + delta
        if (from !in next.indices || to !in next.indices || next[from].pinned != next[to].pinned) return null
        val moved = next.removeAt(from)
        next.add(to, moved)
        return next
    }

    if (renameSession != null) {
        WindowDialog(
            show = true,
            title = "编辑标题",
            onDismissRequest = { renameSessionId = null },
            content = {
                Column {
                    InputRow(
                        title = "对话标题",
                        summary = "最多 32 个字符",
                        value = titleDraft,
                        onValueChange = { titleDraft = it.take(32) }
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TextButton(
                            text = "取消",
                            onClick = { renameSessionId = null },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColorsPrimary()
                        )
                        TextButton(
                            text = "保存",
                            onClick = {
                                if (titleDraft.isNotBlank()) {
                                    onRename(renameSession, titleDraft)
                                    renameSessionId = null
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColorsPrimary()
                        )
                    }
                }
            }
        )
    }
    if (deleteSession != null) {
        WindowDialog(
            show = true,
            title = "删除对话",
            onDismissRequest = { deleteSessionId = null },
            content = {
                Column {
                    Text(
                        text = "确定删除“${deleteSession.title.ifBlank { "新对话" }}”吗？此操作无法撤销。",
                        color = MiuixTheme.colorScheme.onSurface,
                        fontSize = 14.sp
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TextButton(
                            text = "取消",
                            onClick = { deleteSessionId = null },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColorsPrimary()
                        )
                        TextButton(
                            text = "删除",
                            onClick = {
                                deleteSessionId = null
                                onDelete(deleteSession)
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColorsPrimary()
                        )
                    }
                }
            }
        )
    }
    if (actionSession != null) {
        val index = orderedSessions.indexOfFirst { it.id == actionSession.id }
        val canMoveUp = index > 0 && orderedSessions[index - 1].pinned == actionSession.pinned
        val canMoveDown = index in 0 until orderedSessions.lastIndex &&
            orderedSessions[index + 1].pinned == actionSession.pinned
        WindowDialog(
            show = true,
            title = "对话历史",
            onDismissRequest = { actionSessionId = null },
            content = {
                Column {
                    Text(
                        text = actionSession.title.ifBlank { "新对话" },
                        color = MiuixTheme.colorScheme.onSurface,
                        fontSize = 15.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp, bottom = 10.dp),
                        textAlign = TextAlign.Center
                    )
                    ScriptPluginAgentHistoryActionRow(NavIcons.Edit, "编辑标题") {
                        titleDraft = actionSession.title
                        renameSessionId = actionSession.id
                        actionSessionId = null
                    }
                    ScriptPluginAgentHistoryActionRow(NavIcons.MoveUp, "上移", enabled = canMoveUp) {
                        movedSessions(actionSession, -1)?.let { next ->
                            orderedSessions = next
                            onCommitOrder(next)
                        }
                        actionSessionId = null
                    }
                    ScriptPluginAgentHistoryActionRow(NavIcons.MoveDown, "下移", enabled = canMoveDown) {
                        movedSessions(actionSession, 1)?.let { next ->
                            orderedSessions = next
                            onCommitOrder(next)
                        }
                        actionSessionId = null
                    }
                    ScriptPluginAgentHistoryActionRow(
                        NavIcons.Pin,
                        if (actionSession.pinned) "取消置顶" else "置顶聊天"
                    ) {
                        onSetPinned(actionSession, !actionSession.pinned)
                        actionSessionId = null
                    }
                    ScriptPluginAgentHistoryActionRow(
                        if (actionSession.locked) NavIcons.Unlock else NavIcons.Lock,
                        if (actionSession.locked) "解锁聊天" else "锁定聊天"
                    ) {
                        onSetLocked(actionSession, !actionSession.locked)
                        actionSessionId = null
                    }
                    ScriptPluginAgentHistoryActionRow(
                        NavIcons.Delete,
                        if (actionSession.locked) "删除（已锁定）" else "删除",
                        destructive = true,
                        enabled = !actionSession.locked
                    ) {
                        deleteSessionId = actionSession.id
                        actionSessionId = null
                    }
                    TextButton(
                        text = "取消",
                        onClick = { actionSessionId = null },
                        modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                        colors = ButtonDefaults.textButtonColorsPrimary()
                    )
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.28f)).responsiveTap(onClick = onClose)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth(0.84f)
                .fillMaxHeight()
                .background(MiuixTheme.colorScheme.background)
                .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "对话历史",
                    color = MiuixTheme.colorScheme.onSurface,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                TextButton(
                    text = "关闭",
                    onClick = onClose,
                    colors = ButtonDefaults.textButtonColorsPrimary()
                )
            }
            TextButton(
                text = "+  新建对话",
                onClick = onNewSession,
                enabled = enabled,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 4.dp),
                colors = ButtonDefaults.textButtonColorsPrimary()
            )
            if (showGestureHint) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .responsiveTap(onClick = {
                            showGestureHint = false
                            uiPrefs.edit().putBoolean(KEY_AGENT_HISTORY_GESTURE_HINT, false).apply()
                        })
                        .padding(horizontal = 18.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        imageVector = NavIcons.Swipe,
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onSurfaceVariantSummary),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "左右滑动可编辑或删除（点击不再显示）",
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 9.dp)
                    )
                }
            }
            InsetDivider()
            LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(vertical = 8.dp)) {
                orderedSessions.forEach { session ->
                    item(key = session.id) {
                        ScriptPluginAgentHistoryItem(
                            modifier = if (draggedSessionId == session.id) {
                                Modifier
                            } else {
                                Modifier.animateItem(
                                    fadeInSpec = null,
                                    placementSpec = tween(durationMillis = 180),
                                    fadeOutSpec = null
                                )
                            },
                            session = session,
                            selected = session.id == activeId,
                            working = session.id in workingSessionIds,
                            enabled = enabled,
                            onSelect = { onSelect(session) },
                            onLongPress = { actionSessionId = session.id },
                            onSwipeRename = {
                                titleDraft = session.title
                                renameSessionId = session.id
                            },
                            onSwipeDelete = {
                                if (session.locked) {
                                    Toast.makeText(context, "请先解锁该对话", Toast.LENGTH_SHORT).show()
                                } else {
                                    deleteSessionId = session.id
                                }
                            },
                            onDragMove = { delta ->
                                movedSessions(session, delta)?.let { next ->
                                    orderedSessions = next
                                    dragOrderChanged = true
                                    true
                                } ?: false
                            },
                            onDragStateChange = { dragging ->
                                if (dragging) {
                                    draggedSessionId = session.id
                                    dragOrderChanged = false
                                } else if (draggedSessionId == session.id) {
                                    draggedSessionId = null
                                    if (dragOrderChanged) onCommitOrder(orderedSessions)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScriptPluginAgentWorkingSpinner(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "agentHistoryWorking")
    val spinnerColor = MiuixTheme.colorScheme.primary
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing)
        ),
        label = "agentHistoryWorkingRotation"
    )
    Canvas(
        modifier = modifier
            .size(18.dp)
            .graphicsLayer { rotationZ = rotation }
    ) {
        drawArc(
            color = spinnerColor,
            startAngle = -90f,
            sweepAngle = 285f,
            useCenter = false,
            style = Stroke(width = 2.dp.toPx())
        )
    }
}

@Composable
private fun ScriptPluginAgentHistoryItem(
    modifier: Modifier = Modifier,
    session: ScriptPluginAgentSession,
    selected: Boolean,
    working: Boolean,
    enabled: Boolean,
    onSelect: () -> Unit,
    onLongPress: () -> Unit,
    onSwipeRename: () -> Unit,
    onSwipeDelete: () -> Unit,
    onDragMove: (Int) -> Boolean,
    onDragStateChange: (Boolean) -> Unit
) {
    val density = LocalDensity.current
    val actionWidthPx = with(density) { 82.dp.toPx() }
    var offsetX by remember(session.id) { mutableStateOf(0f) }
    var itemHeightPx by remember(session.id) { mutableStateOf(0f) }
    var dragDistanceY by remember(session.id) { mutableStateOf(0f) }
    var dragging by remember(session.id) { mutableStateOf(false) }
    val currentOnDragMove by rememberUpdatedState(onDragMove)
    val currentOnDragStateChange by rememberUpdatedState(onDragStateChange)
    val visualDragY by animateFloatAsState(
        targetValue = dragDistanceY,
        animationSpec = tween(durationMillis = if (dragging) 0 else 170),
        label = "AgentHistoryDragOffset"
    )
    val dragScale by animateFloatAsState(
        targetValue = if (dragging) 1.015f else 1f,
        animationSpec = tween(durationMillis = 120),
        label = "AgentHistoryDragScale"
    )
    Box(
        modifier = modifier
            .zIndex(if (dragging) 1f else 0f)
            .graphicsLayer {
                translationY = visualDragY
                scaleX = dragScale
                scaleY = dragScale
                shadowElevation = if (dragging) with(density) { 8.dp.toPx() } else 0f
                shape = RoundedCornerShape(8.dp)
            }
            .fillMaxWidth().padding(horizontal = 12.dp, vertical = 3.dp)
            .clip(RoundedCornerShape(8.dp))
    ) {
        if (offsetX != 0f) {
            Box(
                modifier = Modifier.matchParentSize(),
                contentAlignment = if (offsetX > 0f) Alignment.CenterStart else Alignment.CenterEnd
            ) {
                Box(
                    modifier = Modifier.fillMaxHeight().sizeIn(minWidth = 82.dp)
                        .background(
                            if (offsetX > 0f) MiuixTheme.colorScheme.primary.copy(alpha = 0.88f)
                            else Color(0xFFD93025)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (offsetX > 0f) "编辑" else "删除",
                        color = Color.White,
                        fontSize = 13.sp
                    )
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth()
                .onSizeChanged { itemHeightPx = it.height.toFloat() }
                .graphicsLayer { translationX = offsetX }
                .background(MiuixTheme.colorScheme.background)
                .background(
                    if (selected) MiuixTheme.colorScheme.primary.copy(alpha = 0.13f)
                    else Color.Transparent
                )
                .pointerInput(session.id, enabled) {
                    if (!enabled) return@pointerInput
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        var totalX = 0f
                        var totalY = 0f
                        var horizontal = false
                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull { it.id == down.id } ?: break
                            totalX += change.position.x - change.previousPosition.x
                            totalY += change.position.y - change.previousPosition.y
                            if (!horizontal && abs(totalX) > viewConfiguration.touchSlop && abs(totalX) > abs(totalY) * 1.25f) {
                                horizontal = true
                            }
                            if (horizontal) {
                                offsetX = totalX.coerceIn(-actionWidthPx, actionWidthPx)
                                change.consume()
                            }
                            if (!change.pressed) break
                        }
                        val action = when {
                            offsetX >= actionWidthPx * 0.58f -> 1
                            offsetX <= -actionWidthPx * 0.58f -> -1
                            else -> 0
                        }
                        offsetX = 0f
                        if (action > 0) onSwipeRename() else if (action < 0) onSwipeDelete()
                    }
                }
                .pointerInput(session.id, enabled) {
                    detectTapGestures(
                        onTap = { if (enabled) onSelect() },
                        onLongPress = { position ->
                            if (enabled && position.x > with(density) { 38.dp.toPx() }) onLongPress()
                        }
                    )
                }
                .padding(horizontal = 14.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                imageVector = NavIcons.Drag,
                contentDescription = "长按拖动排序",
                colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onSurfaceVariantSummary),
                modifier = Modifier.size(22.dp)
                    .pointerInput(session.id, enabled, itemHeightPx) {
                        if (!enabled) return@pointerInput
                        detectDragGesturesAfterLongPress(
                            onDragStart = {
                                dragDistanceY = 0f
                                dragging = true
                                currentOnDragStateChange(true)
                            },
                            onDragEnd = {
                                dragging = false
                                dragDistanceY = 0f
                                currentOnDragStateChange(false)
                            },
                            onDragCancel = {
                                dragging = false
                                dragDistanceY = 0f
                                currentOnDragStateChange(false)
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                dragDistanceY += dragAmount.y
                                val step = (itemHeightPx.takeIf { it > 0f }
                                    ?: with(density) { 56.dp.toPx() }) + with(density) { 6.dp.toPx() }
                                while (dragDistanceY <= -step * 0.5f) {
                                    if (currentOnDragMove(-1)) {
                                        dragDistanceY += step
                                    } else {
                                        dragDistanceY = -step * 0.45f
                                        break
                                    }
                                }
                                while (dragDistanceY >= step * 0.5f) {
                                    if (currentOnDragMove(1)) {
                                        dragDistanceY -= step
                                    } else {
                                        dragDistanceY = step * 0.45f
                                        break
                                    }
                                }
                            }
                        )
                    }
            )
            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                Text(
                    text = session.title.ifBlank { "新对话" },
                    color = MiuixTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                val lastUser = session.messages.lastOrNull { it.role == "user" }?.content.orEmpty()
                if (lastUser.isNotBlank() && lastUser != session.title) {
                    Text(
                        text = lastUser,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 3.dp)
                    )
                }
            }
            if (session.pinned) {
                Image(
                    imageVector = NavIcons.Pin,
                    contentDescription = "已置顶",
                    colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.primary),
                    modifier = Modifier.padding(start = 6.dp).size(17.dp)
                )
            }
            if (session.locked) {
                Image(
                    imageVector = NavIcons.Lock,
                    contentDescription = "已锁定",
                    colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onSurfaceVariantSummary),
                    modifier = Modifier.padding(start = 6.dp).size(17.dp)
                )
            }
            if (working) {
                ScriptPluginAgentWorkingSpinner(Modifier.padding(start = 8.dp))
            }
        }
    }
}

@Composable
private fun ScriptPluginAgentHistoryActionRow(
    icon: ImageVector,
    text: String,
    destructive: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val contentColor = when {
        !enabled -> MiuixTheme.colorScheme.onSurfaceVariantSummary.copy(alpha = 0.45f)
        destructive -> Color(0xFFD93025)
        else -> MiuixTheme.colorScheme.onSurface
    }
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (destructive) Color(0xFFD93025).copy(alpha = 0.08f)
                else MiuixTheme.colorScheme.secondaryVariant
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            imageVector = icon,
            contentDescription = null,
            colorFilter = ColorFilter.tint(contentColor),
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = text,
            color = contentColor,
            fontSize = 15.sp,
            modifier = Modifier.padding(start = 18.dp)
        )
    }
}

@Composable
private fun ScriptPluginAgentQuickProfileDialog(
    profiles: List<ScriptPluginAgentProfile>,
    activeProfileId: String,
    onSelected: (ScriptPluginAgentProfile) -> Unit,
    onDismiss: () -> Unit
) {
    WindowDialog(
        show = true,
        title = "切换模型配置",
        onDismissRequest = onDismiss,
        content = {
            LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 360.dp)) {
                profiles.forEach { profile ->
                    item(key = profile.id) {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { onSelected(profile) }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = profile.name,
                                    color = MiuixTheme.colorScheme.onSurface,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = profile.config.model.ifBlank { "未设置模型" },
                                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            if (profile.id == activeProfileId) {
                                Text(
                                    text = "当前",
                                    color = MiuixTheme.colorScheme.primary,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(start = 12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun ScriptPluginAgentQuickOptionsDialog(
    webSearchEnabled: Boolean,
    workspaceWriteApprovalMode: String,
    promptCacheMode: String,
    endpointMode: String,
    model: String,
    reasoningEffort: String,
    onReasoningEffortChanged: (String) -> Unit,
    mcpServers: List<ScriptPluginAgentMcpServer>,
    onWebSearchChanged: (Boolean) -> Unit,
    onWorkspaceWriteApprovalChanged: (String) -> Unit,
    onPromptCacheModeChanged: (String) -> Unit,
    onMcpChanged: (String, Boolean) -> Unit,
    onOpenSkills: () -> Unit,
    onDismiss: () -> Unit
) {
    WindowDialog(
        show = true,
        title = "快捷选项",
        onDismissRequest = onDismiss,
        content = {
            Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                ScriptPluginAgentReasoningRow(
                    endpointMode = endpointMode,
                    model = model,
                    effort = reasoningEffort,
                    onValueChanged = onReasoningEffortChanged
                )
                InsetDivider()
                SwitchRow(
                    checked = webSearchEnabled,
                    title = "联网搜索",
                    summary = if (webSearchEnabled) "允许 Agent 按需查询公开资料" else "已关闭，Agent 不会发起搜索",
                    onCheckedChange = onWebSearchChanged
                )
                InsetDivider()
                PopupChoiceRow(
                    title = "插件文件修改确认",
                    summary = if (workspaceWriteApprovalMode == ScriptPluginAgentSettings.WRITE_APPROVAL_ASK) {
                        "每次询问工具写入和最终提交"
                    } else {
                        "始终允许并自动提交"
                    },
                    options = listOf(
                        PopupChoice("每次询问", ScriptPluginAgentSettings.WRITE_APPROVAL_ASK),
                        PopupChoice("始终允许", ScriptPluginAgentSettings.WRITE_APPROVAL_ALWAYS_ALLOW)
                    ),
                    currentValue = workspaceWriteApprovalMode,
                    onValueChanged = onWorkspaceWriteApprovalChanged
                )
                InsetDivider()
                PopupChoiceRow(
                    title = "提示缓存",
                    summary = promptCacheModeSummary(promptCacheMode, endpointMode),
                    options = promptCacheModeChoices(),
                    currentValue = promptCacheMode,
                    onValueChanged = onPromptCacheModeChanged
                )
                InsetDivider()
                ActionRow("Skill 管理", "启用技能、编辑指南或从文件导入") { onOpenSkills() }
                if (mcpServers.isNotEmpty()) {
                    InsetDivider()
                    Text(
                        text = "MCP 服务器",
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    mcpServers.forEachIndexed { index, server ->
                        if (index > 0) InsetDivider()
                        SwitchRow(
                            checked = server.enabled,
                            title = server.name.ifBlank { "MCP ${index + 1}" },
                            summary = server.endpoint.ifBlank { "未配置 Endpoint" },
                            onCheckedChange = { onMcpChanged(server.id, it) }
                        )
                    }
                } else {
                    Text(
                        text = "暂无 MCP 服务器，请先在 Agent 配置中添加。",
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    )
                }
            }
        }
    )
}

@Composable
private fun ScriptPluginAgentPendingMessagePanel(
    messages: List<ScriptPluginAgentPendingMessage>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onEdit: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    onSendNow: (Long) -> Unit
) {
    if (messages.isEmpty()) return
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(start = 10.dp, top = 8.dp, end = 10.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(MiuixTheme.colorScheme.secondaryVariant)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().clickable { onExpandedChange(!expanded) }
                .padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "待发送 ${messages.size}",
                color = MiuixTheme.colorScheme.onSurface,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            Image(
                imageVector = if (expanded) NavIcons.MoveUp else NavIcons.MoveDown,
                contentDescription = if (expanded) "收起待发送消息" else "展开待发送消息",
                colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onSurfaceVariantSummary),
                modifier = Modifier.size(18.dp)
            )
        }
        if (expanded) {
            LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 150.dp)) {
                messages.forEachIndexed { index, message ->
                    item(key = message.id) {
                        if (index > 0) InsetDivider()
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(start = 10.dp, end = 6.dp, top = 5.dp, bottom = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = buildString {
                                    if (message.quotedMessage != null) append("引用 · ")
                                    append(message.content.ifBlank { "${message.attachments.size} 个附件" })
                                },
                                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            ScriptPluginAgentPendingMessageAction(
                                icon = NavIcons.Edit,
                                description = "编辑待发送消息"
                            ) { onEdit(message.id) }
                            ScriptPluginAgentPendingMessageAction(
                                icon = NavIcons.Send,
                                description = "立即发送"
                            ) { onSendNow(message.id) }
                            ScriptPluginAgentPendingMessageAction(
                                icon = NavIcons.Delete,
                                description = "删除待发送消息"
                            ) { onDelete(message.id) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScriptPluginAgentPendingMessageAction(
    icon: ImageVector,
    description: String,
    onClick: () -> Unit
) {
    Image(
        imageVector = icon,
        contentDescription = description,
        colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onSurfaceVariantSummary),
        modifier = Modifier.padding(start = 4.dp).size(26.dp)
            .clip(RoundedCornerShape(4.dp))
            .clickable(onClick = onClick)
            .padding(5.dp)
    )
}

@Composable
private fun ScriptPluginAgentQuickChoice(
    label: String,
    value: String,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier.height(28.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(MiuixTheme.colorScheme.secondaryVariant)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label · $value",
            color = if (enabled) {
                MiuixTheme.colorScheme.onSurfaceVariantSummary
            } else {
                MiuixTheme.colorScheme.onSurfaceVariantSummary.copy(alpha = 0.5f)
            },
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Image(
            imageVector = NavIcons.MoveDown,
            contentDescription = null,
            colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onSurfaceVariantSummary),
            modifier = Modifier.padding(start = 3.dp).size(14.dp)
        )
    }
}

@Composable
private fun ScriptPluginAgentComposer(
    value: String,
    attachments: List<ScriptPluginAgentAttachment>,
    quotedMessage: ScriptPluginAgentQuotedMessage?,
    pendingMessages: List<ScriptPluginAgentPendingMessage>,
    pendingMessagesExpanded: Boolean,
    activeProfileName: String,
    activeModel: String,
    enabled: Boolean,
    generating: Boolean,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onQueue: () -> Unit,
    onCancel: () -> Unit,
    onAttach: () -> Unit,
    onRemoveAttachment: (String) -> Unit,
    onClearQuotedMessage: () -> Unit,
    onPendingMessagesExpandedChange: (Boolean) -> Unit,
    onEditPendingMessage: (Long) -> Unit,
    onDeletePendingMessage: (Long) -> Unit,
    onSendPendingMessageNow: (Long) -> Unit,
    onOpenProfilePicker: () -> Unit,
    onOpenModelPicker: () -> Unit,
    onOpenQuickOptions: () -> Unit
) {
    val canSend = value.isNotBlank() || attachments.isNotEmpty()
    val actionEnabled = enabled && canSend
    val actionBackground = MiuixTheme.colorScheme.primary
    val actionTint = Color.White
    Column(
        modifier = Modifier.fillMaxWidth()
            .background(MiuixTheme.colorScheme.background.copy(alpha = 0.96f))
    ) {
        quotedMessage?.let { quoted ->
            ScriptPluginAgentQuotedMessagePreview(
                quotedMessage = quoted,
                maxLines = 1,
                modifier = Modifier.padding(start = 10.dp, top = 8.dp, end = 10.dp),
                onClear = onClearQuotedMessage
            )
        }
        ScriptPluginAgentPendingMessagePanel(
            messages = pendingMessages,
            expanded = pendingMessagesExpanded,
            onExpandedChange = onPendingMessagesExpandedChange,
            onEdit = onEditPendingMessage,
            onDelete = onDeletePendingMessage,
            onSendNow = onSendPendingMessageNow
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 10.dp, top = 8.dp, end = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ScriptPluginAgentQuickChoice(
                label = "配置",
                value = activeProfileName.ifBlank { "默认配置" },
                enabled = enabled,
                modifier = Modifier.weight(1f),
                onClick = onOpenProfilePicker
            )
            ScriptPluginAgentQuickChoice(
                label = "模型",
                value = activeModel.ifBlank { "未设置" },
                enabled = enabled,
                modifier = Modifier.weight(1f),
                onClick = onOpenModelPicker
            )
            Image(
                imageVector = NavIcons.Settings,
                contentDescription = "快捷选项",
                colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onSurfaceVariantSummary),
                modifier = Modifier.size(28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(MiuixTheme.colorScheme.secondaryVariant)
                    .clickable(enabled = enabled, onClick = onOpenQuickOptions)
                    .padding(6.dp)
            )
        }
        if (attachments.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
                    .padding(start = 12.dp, top = 8.dp, end = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                attachments.forEach { attachment ->
                    Row(
                        modifier = Modifier.clip(RoundedCornerShape(6.dp))
                            .background(MiuixTheme.colorScheme.secondaryVariant)
                            .padding(start = 8.dp, top = 6.dp, bottom = 6.dp, end = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = attachment.name,
                            color = MiuixTheme.colorScheme.onSurface,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.sizeIn(maxWidth = 180.dp)
                        )
                        Image(
                            imageVector = NavIcons.Close,
                            contentDescription = "移除附件",
                            colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onSurfaceVariantSummary),
                            modifier = Modifier.padding(start = 4.dp).size(18.dp)
                                .responsiveTap(onClick = { onRemoveAttachment(attachment.path) })
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 10.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MiuixTheme.colorScheme.secondaryVariant)
                .padding(start = 8.dp, top = 9.dp, end = 12.dp, bottom = 9.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Image(
                imageVector = NavIcons.Attach,
                contentDescription = "添加附件",
                colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onSurfaceVariantSummary),
                modifier = Modifier.padding(end = 8.dp, bottom = 1.dp).size(24.dp)
                    .responsiveTap(onClick = onAttach)
            )
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                enabled = enabled,
                minLines = 1,
                maxLines = 4,
                textStyle = TextStyle(color = MiuixTheme.colorScheme.onSurface, fontSize = 14.sp),
                modifier = Modifier.weight(1f).align(Alignment.Top).heightIn(min = 34.dp),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxWidth().heightIn(min = 34.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (value.isBlank()) {
                            Text(
                                text = "输入消息",
                                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                                fontSize = 14.sp
                            )
                        }
                        innerTextField()
                    }
                }
            )
            if (generating) {
                Image(
                    imageVector = NavIcons.Stop,
                    contentDescription = "停止生成",
                    colorFilter = ColorFilter.tint(Color(0xFFD93025)),
                    modifier = Modifier.padding(start = 6.dp).size(30.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(Color.White)
                        .clickable(onClick = onCancel)
                        .padding(7.dp)
                )
            }
            if (!generating || canSend) {
                Image(
                    imageVector = if (generating) NavIcons.Add else NavIcons.Send,
                    contentDescription = if (generating) "加入待发送队列" else "发送",
                    colorFilter = ColorFilter.tint(actionTint),
                    modifier = Modifier.padding(start = 6.dp)
                        .size(34.dp)
                        .clip(RoundedCornerShape(17.dp))
                        .background(actionBackground)
                        .clickable(enabled = actionEnabled) {
                            if (generating) onQueue() else onSend()
                        }
                        .padding(7.dp)
                )
            }
        }
    }
}

}

@Composable
private fun ClickHintTag() {
    Text(
        text = "单击",
        color = MiuixTheme.colorScheme.primary,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        maxLines = 1,
        softWrap = false,
        overflow = TextOverflow.Clip,
        modifier = Modifier
            .defaultMinSize(minWidth = 40.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(MiuixTheme.colorScheme.primary.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    )
}

// ---- Hchat 支撑工具（NavIcons / 返回处理 / 按压反馈）----
private const val PRESS_RELEASE_DELAY_MS = 110L

private object NavIcons {
    val Back: ImageVector = navIcon(
        name = "Rounded.ArrowBack",
        path = "M20,11H7.83l5.59,-5.59L12,4l-8,8 8,8 1.41,-1.41L7.83,13H20v-2z"
    )
    val Attach: ImageVector = navIcon(
        name = "Rounded.AttachFile",
        path = "M16.5,6.5v11c0,2.21 -1.79,4 -4,4s-4,-1.79 -4,-4V5c0,-1.38 1.12,-2.5 2.5,-2.5s2.5,1.12 2.5,2.5v10.5c0,0.55 -0.45,1 -1,1s-1,-0.45 -1,-1V6.5H10v9c0,1.38 1.12,2.5 2.5,2.5s2.5,-1.12 2.5,-2.5V5c0,-2.21 -1.79,-4 -4,-4S7,2.79 7,5v12.5c0,3.04 2.46,5.5 5.5,5.5s5.5,-2.46 5.5,-5.5v-11h-1.5z"
    )
    val Add: ImageVector = navIcon(
        name = "Rounded.Add",
        path = "M19,13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"
    )
    val Compact: ImageVector = navIcon(
        name = "Rounded.Summarize",
        path = "M14,2H6c-1.1,0 -2,0.9 -2,2v16c0,1.1 0.9,2 2,2h12c1.1,0 2,-0.9 2,-2V8l-6,-6z M13,9V3.5L18.5,9H13z M8,13h8v2H8v-2z M8,17h8v2H8v-2z M8,9h3v2H8V9z"
    )
    val Close: ImageVector = navIcon(
        name = "Rounded.Close",
        path = "M18.3,5.71L12,12l6.3,6.29 -1.41,1.42L10.59,13.41 4.29,19.71 2.88,18.29 9.17,12 2.88,5.71 4.29,4.29 10.59,10.59 16.89,4.29z"
    )
    val History: ImageVector = navIcon(
        name = "Rounded.History",
        path = "M13,3c-4.97,0 -9,4.03 -9,9s4.03,9 9,9c4.63,0 8.44,-3.5 8.94,-8h-2.02c-0.49,3.39 -3.4,6 -6.92,6 -3.87,0 -7,-3.13 -7,-7s3.13,-7 7,-7c1.93,0 3.68,0.79 4.95,2.05L15,10h6V4l-2.63,2.63C16.73,4.42 14.95,3 13,3z M12,7v6l5,3 1,-1.64 -4,-2.36V7h-2z"
    )
    val Bell: ImageVector = navIcon(
        name = "Rounded.Notifications",
        path = "M12,22c1.1,0 2,-0.9 2,-2h-4c0,1.1 0.9,2 2,2z M18,16v-5c0,-3.07 -1.63,-5.64 -4.5,-6.32V4c0,-0.83 -0.67,-1.5 -1.5,-1.5S10.5,3.17 10.5,4v0.68C7.64,5.36 6,7.92 6,11v5l-2,2v1h16v-1l-2,-2z"
    )
    val Modules: ImageVector = navIcon(
        name = "Rounded.Extension",
        path = "M20.5,11H19V7c0,-1.1 -0.9,-2 -2,-2h-4V3.5C13,2.12 11.88,1 10.5,1S8,2.12 8,3.5V5H4c-1.1,0 -1.99,0.9 -1.99,2v3.8H3.5c1.49,0 2.7,1.21 2.7,2.7s-1.21,2.7 -2.7,2.7H2V20c0,1.1 0.9,2 2,2h3.8v-1.5c0,-1.49 1.21,-2.7 2.7,-2.7s2.7,1.21 2.7,2.7V22H17c1.1,0 2,-0.9 2,-2v-4h1.5c1.38,0 2.5,-1.12 2.5,-2.5S21.88,11 20.5,11z"
    )
    val Settings: ImageVector = navIcon(
        name = "Rounded.Settings",
        path = "M19.5,12c0,-0.23 -0.01,-0.45 -0.03,-0.68l1.86,-1.41c0.4,-0.3 0.51,-0.86 0.26,-1.3l-1.87,-3.23c-0.25,-0.44 -0.79,-0.62 -1.25,-0.42l-2.15,0.91c-0.37,-0.26 -0.76,-0.49 -1.17,-0.68l-0.29,-2.31C14.8,2.38 14.37,2 13.87,2h-3.73C9.63,2 9.2,2.38 9.14,2.88L8.85,5.19c-0.41,0.19 -0.8,0.42 -1.17,0.68L5.53,4.96c-0.46,-0.2 -1,-0.02 -1.25,0.42L2.41,8.62c-0.25,0.44 -0.14,0.99 0.26,1.3l1.86,1.41C4.51,11.55 4.5,11.77 4.5,12s0.01,0.45 0.03,0.68l-1.86,1.41c-0.4,0.3 -0.51,0.86 -0.26,1.3l1.87,3.23c0.25,0.44 0.79,0.62 1.25,0.42l2.15,-0.91c0.37,0.26 0.76,0.49 1.17,0.68l0.29,2.31c0.06,0.5 0.49,0.88 0.99,0.88h3.73c0.5,0 0.93,-0.38 0.99,-0.88l0.29,-2.31c0.41,-0.19 0.8,-0.42 1.17,-0.68l2.15,0.91c0.46,0.2 1,0.02 1.25,-0.42l1.87,-3.23c0.25,-0.44 0.14,-0.99 -0.26,-1.3l-1.86,-1.41C19.49,12.45 19.5,12.23 19.5,12z M12.04,15.5c-1.93,0 -3.5,-1.57 -3.5,-3.5s1.57,-3.5 3.5,-3.5s3.5,1.57 3.5,3.5S13.97,15.5 12.04,15.5z"
    )
    val More: ImageVector = navIcon(
        name = "Rounded.MoreVert",
        path = "M12,8c1.1,0 2,-0.9 2,-2s-0.9,-2 -2,-2 -2,0.9 -2,2 0.9,2 2,2z M12,10c-1.1,0 -2,0.9 -2,2s0.9,2 2,2 2,-0.9 2,-2 -0.9,-2 -2,-2z M12,16c-1.1,0 -2,0.9 -2,2s0.9,2 2,2 2,-0.9 2,-2 -0.9,-2 -2,-2z"
    )
    val Import: ImageVector = navIcon(
        name = "Rounded.FileDownload",
        path = "M19,9h-4V3H9v6H5l7,7 7,-7z M5,18v2h14v-2H5z"
    )
    val Export: ImageVector = navIcon(
        name = "Rounded.FileUpload",
        path = "M9,16h6v-6h4l-7,-7 -7,7h4v6z M5,18v2h14v-2H5z"
    )
    val Practical: ImageVector = navIcon(
        name = "Rounded.GridView",
        path = "M5,3h4c1.1,0 2,0.9 2,2v4c0,1.1 -0.9,2 -2,2H5c-1.1,0 -2,-0.9 -2,-2V5c0,-1.1 0.9,-2 2,-2z M15,3h4c1.1,0 2,0.9 2,2v4c0,1.1 -0.9,2 -2,2h-4c-1.1,0 -2,-0.9 -2,-2V5c0,-1.1 0.9,-2 2,-2z M5,13h4c1.1,0 2,0.9 2,2v4c0,1.1 -0.9,2 -2,2H5c-1.1,0 -2,-0.9 -2,-2v-4c0,-1.1 0.9,-2 2,-2z M15,13h4c1.1,0 2,0.9 2,2v4c0,1.1 -0.9,2 -2,2h-4c-1.1,0 -2,-0.9 -2,-2v-4c0,-1.1 0.9,-2 2,-2z"
    )
    val Entertainment: ImageVector = navIcon(
        name = "Rounded.PlayCircle",
        path = "M12,2C6.48,2 2,6.48 2,12s4.48,10 10,10s10,-4.48 10,-10S17.52,2 12,2z M10,15.5v-7c0,-0.4 0.45,-0.64 0.78,-0.42l5.25,3.5c0.3,0.2 0.3,0.64 0,0.84l-5.25,3.5C10.45,16.14 10,15.9 10,15.5z"
    )
    val Play: ImageVector = navIcon(
        name = "Rounded.PlayArrow",
        path = "M8,5v14l11,-7z"
    )
    val Pause: ImageVector = navIcon(
        name = "Rounded.Pause",
        path = "M6,19h4V5H6v14z M14,5v14h4V5h-4z"
    )
    val Search: ImageVector = navIcon(
        name = "Rounded.Search",
        path = "M9.5,3C5.91,3 3,5.91 3,9.5S5.91,16 9.5,16c1.61,0 3.09,-0.59 4.23,-1.57l4.42,4.42c0.29,0.29 0.77,0.29 1.06,0s0.29,-0.77 0,-1.06l-4.42,-4.42C15.91,12.09 16,10.82 16,9.5C16,5.91 13.09,3 9.5,3z M9.5,4.5c2.76,0 5,2.24 5,5s-2.24,5 -5,5s-5,-2.24 -5,-5s2.24,-5 5,-5z"
    )
    val Send: ImageVector = navIcon(
        name = "Rounded.Send",
        path = "M2.01,21L23,12 2.01,3 2,10l15,2 -15,2z"
    )
    val Stop: ImageVector = navIcon(
        name = "Rounded.Stop",
        path = "M6,6h12v12H6z"
    )
    val Refresh: ImageVector = navIcon(
        name = "Rounded.Refresh",
        path = "M17.65,6.35C16.2,4.9 14.21,4 12,4c-4.42,0 -7.99,3.58 -7.99,8s3.57,8 7.99,8c3.73,0 6.84,-2.55 7.73,-6h-2.08c-0.82,2.33 -3.04,4 -5.65,4 -3.31,0 -6,-2.69 -6,-6s2.69,-6 6,-6c1.66,0 3.14,0.69 4.22,1.78L13,11h7V4l-2.35,2.35z"
    )
    val Copy: ImageVector = navIcon(
        name = "Rounded.Copy",
        path = "M16,1H4c-1.1,0 -2,0.9 -2,2v14h2V4h12V1z M19,5H8c-1.1,0 -2,0.9 -2,2v14c0,1.1 0.9,2 2,2h11c1.1,0 2,-0.9 2,-2V7c0,-1.1 -0.9,-2 -2,-2z"
    )
    val Quote: ImageVector = navIcon(
        name = "Rounded.Reply",
        path = "M10,9V5l-7,7 7,7v-4.1c5,0 8.5,1.6 11,5.1 -1,-5 -4,-10 -11,-11z"
    )
    val Volume: ImageVector = navIcon(
        name = "Rounded.VolumeUp",
        path = "M3,9v6h4l5,5V4L7,9H3z M16.5,12c0,-1.77 -1.02,-3.29 -2.5,-4.03v8.05c1.48,-0.73 2.5,-2.25 2.5,-4.02z M14,3.23v2.06c2.89,0.86 5,3.54 5,6.71s-2.11,5.85 -5,6.71v2.06c4.01,-0.91 7,-4.49 7,-8.77s-2.99,-7.86 -7,-8.77z"
    )
    val Edit: ImageVector = navIcon(
        name = "Rounded.Edit",
        path = "M3,17.25V21h3.75L17.81,9.94l-3.75,-3.75L3,17.25z M20.71,7.04c0.39,-0.39 0.39,-1.02 0,-1.41l-2.34,-2.34c-0.39,-0.39 -1.02,-0.39 -1.41,0l-1.83,1.83 3.75,3.75 1.83,-1.83z"
    )
    val Delete: ImageVector = navIcon(
        name = "Rounded.Delete",
        path = "M6,19c0,1.1 0.9,2 2,2h8c1.1,0 2,-0.9 2,-2V7H6v12z M8,9h8v10H8V9z M15.5,4l-1,-1h-5l-1,1H5v2h14V4z"
    )
    val MoveUp: ImageVector = navIcon(
        name = "Rounded.KeyboardArrowUp",
        path = "M7.41,15.41L12,10.83l4.59,4.58L18,14l-6,-6 -6,6z"
    )
    val MoveDown: ImageVector = navIcon(
        name = "Rounded.KeyboardArrowDown",
        path = "M7.41,8.59L12,13.17l4.59,-4.58L18,10l-6,6 -6,-6z"
    )
    val Expand: ImageVector = navIcon(
        name = "Rounded.ChevronRight",
        path = "M9.29,6.71c-0.39,0.39 -0.39,1.02 0,1.41L13.17,12l-3.88,3.88c-0.39,0.39 -0.39,1.02 0,1.41 0.39,0.39 1.02,0.39 1.41,0l4.59,-4.59c0.39,-0.39 0.39,-1.02 0,-1.41L10.7,6.7c-0.38,-0.38 -1.02,-0.38 -1.41,0.01z"
    )
    val Pin: ImageVector = navIcon(
        name = "Rounded.PushPin",
        path = "M16,9V4l1,-1V2H7v1l1,1v5c0,1.66 -1.34,3 -3,3v2h6v7l1,1 1,-1v-7h6v-2c-1.66,0 -3,-1.34 -3,-3z"
    )
    val Lock: ImageVector = navIcon(
        name = "Rounded.Lock",
        path = "M18,8h-1V6c0,-2.76 -2.24,-5 -5,-5S7,3.24 7,6v2H6c-1.1,0 -2,0.9 -2,2v10c0,1.1 0.9,2 2,2h12c1.1,0 2,-0.9 2,-2V10c0,-1.1 -0.9,-2 -2,-2z M9,6c0,-1.66 1.34,-3 3,-3s3,1.34 3,3v2H9V6z M12,17c-1.1,0 -2,-0.9 -2,-2s0.9,-2 2,-2 2,0.9 2,2 -0.9,2 -2,2z"
    )
    val Unlock: ImageVector = navIcon(
        name = "Rounded.LockOpen",
        path = "M12,17c-1.1,0 -2,-0.9 -2,-2s0.9,-2 2,-2 2,0.9 2,2 -0.9,2 -2,2z M18,8h-8V6c0,-1.1 0.9,-2 2,-2 0.95,0 1.74,0.66 1.95,1.54l1.93,-0.52C15.43,3.28 13.86,2 12,2 9.79,2 8,3.79 8,6v2H6c-1.1,0 -2,0.9 -2,2v10c0,1.1 0.9,2 2,2h12c1.1,0 2,-0.9 2,-2V10c0,-1.1 -0.9,-2 -2,-2z"
    )
    val Drag: ImageVector = navIcon(
        name = "Rounded.DragHandle",
        path = "M4,10.5h16v-2H4v2z M4,15.5h16v-2H4v2z"
    )
    val Swipe: ImageVector = navIcon(
        name = "Rounded.SwapHoriz",
        path = "M6.99,11L3,15l3.99,4v-3H14v-2H6.99v-3z M21,9l-3.99,-4v3H10v2h7.01v3L21,9z"
    )
    val Branch: ImageVector = navIcon(
        name = "Rounded.AccountTree",
        path = "M17,11h3c0.55,0 1,-0.45 1,-1V4c0,-0.55 -0.45,-1 -1,-1h-6c-0.55,0 -1,0.45 -1,1v2H9.83C9.42,4.84 8.31,4 7,4 5.34,4 4,5.34 4,7s1.34,3 3,3c1.31,0 2.42,-0.84 2.83,-2H13v2c0,0.55 0.45,1 1,1h1v2H9.83C9.42,11.84 8.31,11 7,11c-1.66,0 -3,1.34 -3,3s1.34,3 3,3c1.31,0 2.42,-0.84 2.83,-2H15v5c0,0.55 0.45,1 1,1h4c0.55,0 1,-0.45 1,-1v-4c0,-0.55 -0.45,-1 -1,-1h-3v-4z"
    )
    val Info: ImageVector = navIcon(
        name = "Rounded.Info",
        path = "M11,17h2v-6h-2v6z M12,2C6.48,2 2,6.48 2,12s4.48,10 10,10 10,-4.48 10,-10S17.52,2 12,2z M12,20c-4.41,0 -8,-3.59 -8,-8s3.59,-8 8,-8 8,3.59 8,8 -3.59,8 -8,8z M11,9h2V7h-2v2z"
    )
    val Terminal: ImageVector = navIcon(
        name = "Rounded.Terminal",
        path = "M20,4H4C2.9,4 2,4.9 2,6v12c0,1.1 0.9,2 2,2h16c1.1,0 2,-0.9 2,-2V6C22,4.9 21.1,4 20,4z M20,18H4V6h16V18z M18,15h-6v-1.5h6V15z M7.5,15l-1.06,-1.06L8.38,12L6.44,10.06L7.5,9l3,3L7.5,15z"
    )

    private fun navIcon(name: String, path: String): ImageVector {
        return ImageVector.Builder(
            name = name,
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).addPath(
            pathData = addPathNodes(path),
            fill = SolidColor(Color.Black)
        ).build()
    }
}

private class SettingsBackHandlerRegistry {
    private val handlers = LinkedHashMap<Any, () -> Unit>()

    @Synchronized
    fun register(token: Any, handler: () -> Unit) {
        handlers.remove(token)
        handlers[token] = handler
    }

    @Synchronized
    fun unregister(token: Any) {
        handlers.remove(token)
    }

    fun handle(): Boolean {
        val handler = synchronized(this) { handlers.values.lastOrNull() } ?: return false
        handler()
        return true
    }

    @Synchronized
    fun clear() {
        handlers.clear()
    }
}

private val LocalSettingsBackHandlerRegistry = staticCompositionLocalOf<SettingsBackHandlerRegistry?> { null }

@Composable
private fun RegisterSettingsBackHandler(onBack: (() -> Unit)?) {
    val registry = LocalSettingsBackHandlerRegistry.current
    val currentOnBack by rememberUpdatedState(onBack)
    val token = remember { Any() }
    DisposableEffect(registry, token, onBack != null) {
        if (registry != null && onBack != null) {
            registry.register(token) { currentOnBack?.invoke() }
        }
        onDispose { registry?.unregister(token) }
    }
}

@Composable
private fun Modifier.responsiveTap(
    onClick: () -> Unit,
    onPressedChange: (Boolean) -> Unit = {}
): Modifier {
    val currentOnClick by rememberUpdatedState(onClick)
    val currentOnPressedChange by rememberUpdatedState(onPressedChange)
    return pointerInput(Unit) {
        awaitEachGesture {
            awaitFirstDown(requireUnconsumed = false)
            currentOnPressedChange(true)
            val up = waitForUpOrCancellation()
            currentOnPressedChange(false)
            if (up != null) {
                currentOnClick()
            }
        }
    }
}

@Composable
private fun rememberPressFeedbackColor(pressed: Boolean): Color {
    var feedbackVisible by remember { mutableStateOf(false) }
    LaunchedEffect(pressed) {
        if (pressed) {
            feedbackVisible = true
        } else {
            delay(PRESS_RELEASE_DELAY_MS)
            feedbackVisible = false
        }
    }
    val target = if (feedbackVisible) {
        MiuixTheme.colorScheme.onSurface.copy(alpha = 0.075f)
    } else {
        Color.Transparent
    }
    return animateColorAsState(
        targetValue = target,
        animationSpec = tween(durationMillis = if (pressed) 90 else 210),
        label = "PressFeedback"
    ).value
}

// ---- 缺失组件：PopupChoice / PopupChoiceRow（WCX 侧简化实现，签名与 Hchat 一致）----
internal data class PopupChoice<T>(val label: String, val value: T)

@Composable
internal fun PopupChoiceRow(
    title: String,
    summary: String,
    options: List<PopupChoice<String>>,
    currentValue: String,
    onValueChanged: (String) -> Unit,
    enabled: Boolean = true,
) {
    var expanded by remember { mutableStateOf(false) }
    val current = options.firstOrNull { it.value == currentValue }?.label ?: ""
    Column(modifier = Modifier.fillMaxWidth()) {
        BasicComponent(onClick = { if (enabled) expanded = true }) {
            Column {
                Text(
                    text = title,
                    fontSize = MiuixTheme.textStyles.headline1.fontSize,
                    color = BasicComponentDefaults.titleColor().color,
                )
                if (summary.isNotBlank() || current.isNotBlank()) {
                    Text(
                        text = if (summary.isNotBlank()) "$summary（$current）" else current,
                        fontSize = MiuixTheme.textStyles.body2.fontSize,
                        color = BasicComponentDefaults.summaryColor().color,
                    )
                }
            }
        }
        if (expanded) {
            Dialog(onDismissRequest = { expanded = false }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MiuixTheme.colorScheme.surface)
                        .padding(8.dp),
                ) {
                    options.forEach { opt ->
                        BasicComponent(onClick = { onValueChanged(opt.value); expanded = false }) {
                            Text(
                                text = opt.label,
                                fontSize = MiuixTheme.textStyles.body2.fontSize,
                                color = if (opt.value == currentValue) MiuixTheme.colorScheme.primary
                                else MiuixTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
            }
        }
    }
}

// ---- 文件选择桥接 + Markdown 渲染（Hchat 支撑对象）----
private object ScriptPluginAgentAttachmentPickerBridge {
    private val hookedClasses = HashSet<Class<*>>()
    private var pending: PendingResult? = null

    @Synchronized
    fun launch(
        activity: Activity,
        directory: File,
        onResult: (List<ScriptPluginAgentAttachment>) -> Unit
    ) {
        pending = PendingResult(activity, directory.apply { mkdirs() }, onResult)
        hookActivityResult(activity.javaClass)
        hookActivityResult(Activity::class.java)
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        }.preferSystemDocumentsUi(activity)
        try {
            activity.startActivityForResult(intent, SCRIPT_AGENT_ATTACHMENT_REQUEST_CODE)
        } catch (_: Throwable) {
            val fallback = Intent(Intent.ACTION_GET_CONTENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            activity.startActivityForResult(
                Intent.createChooser(fallback, "选择文件或图片"),
                SCRIPT_AGENT_ATTACHMENT_REQUEST_CODE
            )
        }
    }

    @Synchronized
    private fun hookActivityResult(clazz: Class<*>) {
        if (hookedClasses.contains(clazz)) return
        try {
            XposedBridge.hookAllMethods(clazz, "onActivityResult", object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val requestCode = param.args.getOrNull(0) as? Int ?: return
                    if (requestCode != SCRIPT_AGENT_ATTACHMENT_REQUEST_CODE) return
                    val pending = takePending() ?: return
                    val resultCode = param.args.getOrNull(1) as? Int ?: return
                    if (resultCode != Activity.RESULT_OK) return
                    val data = param.args.getOrNull(2) as? Intent ?: return
                    val activity = pending.activity
                    val directory = pending.directory
                    val uris = pickedUris(data)
                    val persistentUris = uris.filter { uri ->
                        takeReadPermission(activity, data, uri)
                    }.mapTo(HashSet()) { it.toString() }
                    Thread({
                        val attachments = uris.mapNotNull { uri ->
                            copyAttachment(
                                activity,
                                directory,
                                uri,
                                uri.toString() in persistentUris
                            )
                        }.distinctBy { it.sourceUri.ifBlank { it.path } }
                        if (attachments.isNotEmpty()) {
                            Handler(Looper.getMainLooper()).post { pending.callback(attachments) }
                        }
                    }, "Hchat-Agent-Attachment-Copy").start()
                }
            })
            hookedClasses.add(clazz)
        } catch (_: Throwable) {
        }
    }

    private fun pickedUris(data: Intent): List<Uri> {
        val result = ArrayList<Uri>()
        data.data?.let(result::add)
        val clip = data.clipData
        if (clip != null) {
            for (index in 0 until clip.itemCount) {
                runCatching { clip.getItemAt(index)?.uri }.getOrNull()?.let(result::add)
            }
        }
        return result.distinct()
    }

    private fun takeReadPermission(activity: Activity, data: Intent, uri: Uri): Boolean {
        if (uri.scheme != "content") return false
        val resolver = activity.contentResolver
        if (runCatching {
                resolver.persistedUriPermissions.any { it.uri == uri && it.isReadPermission }
            }.getOrDefault(false)
        ) {
            return true
        }
        val flags = data.flags and
            (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        if ((flags and Intent.FLAG_GRANT_READ_URI_PERMISSION) == 0 ||
            (flags and Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION) == 0
        ) {
            return false
        }
        return runCatching {
            resolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            resolver.persistedUriPermissions.any { it.uri == uri && it.isReadPermission }
        }.getOrDefault(false)
    }

    private fun copyAttachment(
        context: Context,
        directory: File,
        uri: Uri,
        persistentAccess: Boolean
    ): ScriptPluginAgentAttachment? {
        var target: File? = null
        return runCatching {
            val displayName = queryDisplayName(context, uri)
                ?: uri.lastPathSegment
                ?: "attachment"
            val safeName = displayName.replace(Regex("""[\\/:*?"<>|]"""), "_").ifBlank { "attachment" }
            val copyId = java.util.UUID.randomUUID().toString().substring(0, 8)
            val outputFile = File(directory, "${System.currentTimeMillis()}_${copyId}_$safeName")
            target = outputFile
            context.contentResolver.openInputStream(uri)?.use { input ->
                outputFile.outputStream().use { output ->
                    val buffer = ByteArray(32 * 1024)
                    while (true) {
                        val count = input.read(buffer)
                        if (count < 0) break
                        output.write(buffer, 0, count)
                    }
                }
            } ?: return null
            val attachment = ScriptPluginAgentAttachment(
                name = displayName,
                path = outputFile.absolutePath,
                mimeType = context.contentResolver.getType(uri).orEmpty().ifBlank { "application/octet-stream" },
                size = outputFile.length(),
                sourceUri = uri.toString().takeIf { persistentAccess }.orEmpty()
            )
            attachment.takeIf { outputFile.isFile && outputFile.length() > 0L }
                ?: run {
                    outputFile.delete()
                    null
                }
        }.getOrElse {
            target?.delete()
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context, it.message ?: "读取附件失败", Toast.LENGTH_SHORT).show()
            }
            null
        }
    }

    @Synchronized
    private fun takePending(): PendingResult? {
        val result = pending
        pending = null
        return result

private object PluginMarketExtraFilePickerBridge {
    private val hookedClasses = HashSet<Class<*>>()
    private var pending: PendingResult? = null

    @Synchronized
    fun launch(activity: Activity, onResult: (List<PluginMarketFile>) -> Unit) {
        pending = PendingResult(activity, onResult)
        hookActivityResult(activity.javaClass)
        hookActivityResult(Activity::class.java)
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        }.preferSystemDocumentsUi(activity)
        try {
            activity.startActivityForResult(intent, PLUGIN_MARKET_EXTRA_FILE_REQUEST_CODE)
        } catch (_: Throwable) {
            val fallback = Intent(Intent.ACTION_GET_CONTENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            activity.startActivityForResult(
                Intent.createChooser(fallback, "选择插件附加文件"),
                PLUGIN_MARKET_EXTRA_FILE_REQUEST_CODE
            )
        }
    }

    @Synchronized
    private fun hookActivityResult(clazz: Class<*>) {
        if (!hookedClasses.add(clazz)) return
        runCatching {
            XposedBridge.hookAllMethods(clazz, "onActivityResult", object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val requestCode = param.args.getOrNull(0) as? Int ?: return
                    if (requestCode != PLUGIN_MARKET_EXTRA_FILE_REQUEST_CODE) return
                    val result = takePending() ?: return
                    val resultCode = param.args.getOrNull(1) as? Int ?: return
                    if (resultCode != Activity.RESULT_OK) return
                    val data = param.args.getOrNull(2) as? Intent ?: return
                    val uris = pickedUris(data)
                    if (uris.isEmpty()) return
                    Thread({
                        val files = uris.mapNotNull { uri -> readFile(result.activity, uri) }
                            .distinctBy { it.name.lowercase(Locale.ROOT) }
                        if (files.isNotEmpty()) {
                            Handler(Looper.getMainLooper()).post { result.callback(files) }
                        }
                    }, "Hchat-Plugin-Market-Files").start()
                }
            })
        }.onFailure {
            hookedClasses.remove(clazz)
        }
    }

    private fun pickedUris(data: Intent): List<Uri> {
        val result = ArrayList<Uri>()
        data.data?.let(result::add)
        data.clipData?.let { clip ->
            for (index in 0 until clip.itemCount) {
                runCatching { clip.getItemAt(index)?.uri }.getOrNull()?.let(result::add)
            }
        }
        return result.distinct()
    }

    private fun readFile(activity: Activity, uri: Uri): PluginMarketFile? {
        return runCatching {
            val displayName = queryDisplayName(activity, uri)
                ?.substringAfterLast('/')
                ?.substringAfterLast('\\')
                ?.ifBlank { null }
                ?: uri.lastPathSegment?.substringAfterLast('/').orEmpty()
            require(displayName.isNotBlank()) { "无法读取所选文件名" }
            val bytes = java.io.ByteArrayOutputStream().use { output ->
                activity.contentResolver.openInputStream(uri)?.use { input ->
                    val buffer = ByteArray(32 * 1024)
                    var total = 0L
                    while (true) {
                        val count = input.read(buffer)
                        if (count < 0) break
                        total += count
                        require(total <= PluginMarketInstaller.MAX_EXTRA_FILE_BYTES) {
                            "$displayName 超过 ${PluginMarketInstaller.MAX_EXTRA_FILE_BYTES / 1024} KiB"
                        }
                        output.write(buffer, 0, count)
                    }
                } ?: error("无法打开所选文件")
                output.toByteArray()
            }
            PluginMarketInstaller.createExternalFile(displayName, bytes)
        }.getOrElse {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(activity, it.message ?: "读取附加文件失败", Toast.LENGTH_SHORT).show()
            }
            null
        }
    }

    @Synchronized
    private fun takePending(): PendingResult? {
        val result = pending
        pending = null
        return result
    }

    private data class PendingResult(
        val activity: Activity,
        val callback: (List<PluginMarketFile>) -> Unit
    )
}


private object MarkdownUi {

@Composable
fun Content(
    context: Context,
    markdown: String,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
    bodyFontSize: androidx.compose.ui.unit.TextUnit = 13.sp,
    onCopyCode: ((String) -> Unit)? = null
) {
    val lines = markdown.replace("\r\n", "\n").replace('\r', '\n').lines()
    var inCodeBlock = false
    var inlineState = MarkdownInlineState()
    val codeLines = ArrayList<String>()
    Column(modifier = Modifier.fillMaxWidth().padding(contentPadding)) {
        lines.forEach { rawLine ->
            val line = rawLine.trimEnd()
            if (line.trimStart().startsWith("```")) {
                if (inCodeBlock) {
                    CodeBlock(codeLines.joinToString("\n"), onCopyCode)
                    codeLines.clear()
                    inCodeBlock = false
                } else {
                    inCodeBlock = true
                    codeLines.clear()
                }
                return@forEach
            }
            if (inCodeBlock) {
                codeLines += rawLine
                return@forEach
            }
            val nextState = MarkdownLine(context, line, inlineState, bodyFontSize)
            inlineState = nextState
        }
        if (inCodeBlock && codeLines.isNotEmpty()) {
            CodeBlock(codeLines.joinToString("\n"), onCopyCode)
        }
    }
}
