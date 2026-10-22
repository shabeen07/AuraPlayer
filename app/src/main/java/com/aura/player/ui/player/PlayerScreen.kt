@file:OptIn(
    androidx.media3.common.util.UnstableApi::class,
    androidx.compose.material3.ExperimentalMaterial3Api::class
)

package com.aura.player.ui.player

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import kotlin.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.aura.player.data.DataRepository
import com.aura.player.data.model.MediaTrackInfo
import com.aura.player.data.model.TranscriptSegment
import com.aura.player.data.model.formatDuration
import com.aura.player.theme.AuraError
import com.aura.player.theme.AuraOnErrorContainer
import com.aura.player.theme.AuraOnPrimaryContainer
import com.aura.player.theme.AuraOnSurface
import com.aura.player.theme.AuraOnSurfaceVariant
import com.aura.player.theme.AuraPrimary
import com.aura.player.theme.AuraPrimaryContainer
import com.aura.player.theme.AuraSecondary
import com.aura.player.theme.AuraSurface
import com.aura.player.theme.AuraSurfaceContainer
import com.aura.player.theme.AuraSurfaceContainerHigh
import com.aura.player.theme.AuraSurfaceContainerHighest
import com.aura.player.theme.AuraSurfaceContainerLowest
import com.aura.player.theme.AuraSurfaceVariant
import com.aura.player.theme.AuraTertiary
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@OptIn(UnstableApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    repository: DataRepository,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val window = activity?.window
    val insetsController = remember(window) {
        window?.let { WindowCompat.getInsetsController(it, it.decorView) }
    }
    val audioManager = remember(context) {
        context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    }

    val playerState by repository.playerState.collectAsState()
    val isFullscreen = playerState.isFullscreen

    var showControls by remember { mutableStateOf(true) }
    var isDraggingSeek by remember { mutableStateOf(false) }
    var scrubProgress by remember { mutableFloatStateOf(0f) }
    var resizeMode by remember { mutableIntStateOf(AspectRatioFrameLayout.RESIZE_MODE_FIT) }

    // HUD gesture states
    var hudBrightness by remember { mutableStateOf<Int?>(null) }
    var hudVolume by remember { mutableStateOf<Int?>(null) }
    var hudSeekTargetMs by remember { mutableStateOf<Long?>(null) }
    var hudSeekDeltaSeconds by remember { mutableStateOf<Long?>(null) }
    var doubleTapSkipBadge by remember { mutableStateOf<String?>(null) }

    // Modals
    var showTrackSheet by remember { mutableStateOf(false) }
    var showTranscriptModal by remember { mutableStateOf(false) }
    var selectedDetailTab by remember { mutableIntStateOf(0) }

    // Persistent unified video surface that survives fullscreen/portrait layout transitions
    val videoSurface = remember(repository, resizeMode) {
        movableContentOf {
            PlayerSurfaceContent(
                repository = repository,
                resizeMode = resizeMode,
                context = context,
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    // Handle Fullscreen insets & orientation
    LaunchedEffect(isFullscreen) {
        if (activity != null && insetsController != null) {
            if (isFullscreen) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                insetsController.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                insetsController.hide(WindowInsetsCompat.Type.systemBars())
            } else {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                insetsController.show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    // Auto-hide controls timer
    LaunchedEffect(showControls, playerState.isPlaying) {
        if (showControls && playerState.isPlaying) {
            delay(3500)
            showControls = false
        }
    }

    // Dismiss HUD indicators after brief period
    LaunchedEffect(hudBrightness, hudVolume, hudSeekTargetMs, doubleTapSkipBadge) {
        if (hudBrightness != null || hudVolume != null || hudSeekTargetMs != null || doubleTapSkipBadge != null) {
            delay(1200)
            hudBrightness = null
            hudVolume = null
            hudSeekTargetMs = null
            hudSeekDeltaSeconds = null
            doubleTapSkipBadge = null
        }
    }

    // Intercept Back button when in Fullscreen
    BackHandler(enabled = isFullscreen) {
        repository.toggleFullscreen(false)
    }

    // Restore orientation when leaving screen
    DisposableEffect(Unit) {
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            insetsController?.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (isFullscreen) {
            // Fullscreen Landscape: video fills entire window
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                videoSurface()

                // Gesture detection surface
                PlayerGestureSurface(
                    enabled = !playerState.isLocked && playerState.preferences.gesturesEnabled,
                    onToggleControls = { showControls = !showControls },
                    onBrightnessChange = { delta ->
                        if (window != null) {
                            val current = window.attributes.screenBrightness.let { if (it < 0f) 0.5f else it }
                            val newB = (current - delta * 0.005f).coerceIn(0.01f, 1.0f)
                            val lp = window.attributes
                            lp.screenBrightness = newB
                            window.attributes = lp
                            hudBrightness = (newB * 100).roundToInt()
                        }
                    },
                    onVolumeChange = { delta ->
                        if (audioManager != null) {
                            val maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                            val curVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                            val step = if (delta < 0) 1 else -1
                            val newVol = (curVol + step).coerceIn(0, maxVol)
                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVol, 0)
                            hudVolume = ((newVol.toFloat() / maxVol) * 100).roundToInt()
                        }
                    },
                    onHorizontalScrub = { deltaX, screenWidth ->
                        val duration = playerState.durationMs
                        if (duration > 0) {
                            val scrubFraction = (deltaX / screenWidth) * 90
                            val targetMs = (playerState.currentPositionMs + (scrubFraction * 1000).toLong())
                                .coerceIn(0L, duration)
                            hudSeekTargetMs = targetMs
                            hudSeekDeltaSeconds = ((targetMs - playerState.currentPositionMs) / 1000)
                        }
                    },
                    onScrubFinished = {
                        hudSeekTargetMs?.let { target ->
                            repository.seekTo(target)
                        }
                    },
                    onDoubleTapLeft = {
                        val interval = playerState.preferences.skipIntervalSeconds
                        repository.skipBackward(interval)
                        doubleTapSkipBadge = "-${interval}s"
                    },
                    onDoubleTapRight = {
                        val interval = playerState.preferences.skipIntervalSeconds
                        repository.skipForward(interval)
                        doubleTapSkipBadge = "+${interval}s"
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Live Subtitles Display Overlay
                LiveSubtitlesOverlay(
                    subtitleText = playerState.currentSubtitleText,
                    textSizeSp = playerState.preferences.subtitleTextSizeSp,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = if (showControls) 88.dp else 24.dp)
                )

                // Gestures HUD Overlay (Brightness, Volume, Scrub, Skip)
                GesturesHudCenterOverlay(
                    brightness = hudBrightness,
                    volume = hudVolume,
                    seekTargetMs = hudSeekTargetMs,
                    seekDeltaSeconds = hudSeekDeltaSeconds,
                    skipBadge = doubleTapSkipBadge,
                    modifier = Modifier.align(Alignment.Center)
                )

                // Controls Overlay
                AnimatedVisibility(
                    visible = showControls,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.fillMaxSize()
                ) {
                    PlayerControlsOverlay(
                        repository = repository,
                        isFullscreen = true,
                        onToggleFullscreen = { repository.toggleFullscreen(false) },
                        onCycleResizeMode = {
                            resizeMode = when (resizeMode) {
                                AspectRatioFrameLayout.RESIZE_MODE_FIT -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                                AspectRatioFrameLayout.RESIZE_MODE_ZOOM -> AspectRatioFrameLayout.RESIZE_MODE_FILL
                                else -> AspectRatioFrameLayout.RESIZE_MODE_FIT
                            }
                        },
                        onNavigateBack = { repository.toggleFullscreen(false) },
                        onOpenTrackSheet = { showTrackSheet = true },
                        onOpenTranscripts = { showTranscriptModal = true },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        } else {
            // Portrait View: top video viewport + bottom controls and content tabs
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AuraSurface)
                    .verticalScroll(rememberScrollState())
            ) {
                // Top App Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .size(40.dp)
                                .background(AuraSurfaceContainer, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = AuraOnSurface,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Now Playing",
                                color = AuraOnSurface,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (playerState.isVideo) "Video Playback" else "Audio Engine",
                                color = AuraOnSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Track & Subtitle Menu Button
                        IconButton(
                            onClick = { showTrackSheet = true },
                            modifier = Modifier
                                .size(36.dp)
                                .background(AuraSurfaceContainer, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Subtitles,
                                contentDescription = "Tracks & Subtitles",
                                tint = AuraOnSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Playback Speed Pill
                        Text(
                            text = playerState.playbackSpeedLabel,
                            color = AuraOnPrimaryContainer,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(AuraPrimaryContainer)
                                .clickable {
                                    val nextSpeed = when (playerState.playbackSpeed) {
                                        1.0f -> 1.25f
                                        1.25f -> 1.5f
                                        1.5f -> 2.0f
                                        2.0f -> 0.75f
                                        else -> 1.0f
                                    }
                                    repository.setPlaybackSpeed(nextSpeed)
                                }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }

                // Video Surface Container with Gestures
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp)
                        .aspectRatio(16f / 10f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    videoSurface()

                    // Gestures on portrait viewport
                    PlayerGestureSurface(
                        enabled = !playerState.isLocked && playerState.preferences.gesturesEnabled,
                        onToggleControls = { showControls = !showControls },
                        onBrightnessChange = { delta ->
                            if (window != null) {
                                val current = window.attributes.screenBrightness.let { if (it < 0f) 0.5f else it }
                                val newB = (current - delta * 0.005f).coerceIn(0.01f, 1.0f)
                                val lp = window.attributes
                                lp.screenBrightness = newB
                                window.attributes = lp
                                hudBrightness = (newB * 100).roundToInt()
                            }
                        },
                        onVolumeChange = { delta ->
                            if (audioManager != null) {
                                val maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                                val curVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                                val step = if (delta < 0) 1 else -1
                                val newVol = (curVol + step).coerceIn(0, maxVol)
                                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVol, 0)
                                hudVolume = ((newVol.toFloat() / maxVol) * 100).roundToInt()
                            }
                        },
                        onHorizontalScrub = { deltaX, width ->
                            val duration = playerState.durationMs
                            if (duration > 0) {
                                val scrubFraction = (deltaX / width) * 60
                                val targetMs = (playerState.currentPositionMs + (scrubFraction * 1000).toLong())
                                    .coerceIn(0L, duration)
                                hudSeekTargetMs = targetMs
                                hudSeekDeltaSeconds = ((targetMs - playerState.currentPositionMs) / 1000)
                            }
                        },
                        onScrubFinished = {
                            hudSeekTargetMs?.let { target -> repository.seekTo(target) }
                        },
                        onDoubleTapLeft = {
                            val interval = playerState.preferences.skipIntervalSeconds
                            repository.skipBackward(interval)
                            doubleTapSkipBadge = "-${interval}s"
                        },
                        onDoubleTapRight = {
                            val interval = playerState.preferences.skipIntervalSeconds
                            repository.skipForward(interval)
                            doubleTapSkipBadge = "+${interval}s"
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Live Subtitles Display
                    LiveSubtitlesOverlay(
                        subtitleText = playerState.currentSubtitleText,
                        textSizeSp = playerState.preferences.subtitleTextSizeSp,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 12.dp)
                    )

                    // Center Gesture HUD Feedback
                    GesturesHudCenterOverlay(
                        brightness = hudBrightness,
                        volume = hudVolume,
                        seekTargetMs = hudSeekTargetMs,
                        seekDeltaSeconds = hudSeekDeltaSeconds,
                        skipBadge = doubleTapSkipBadge,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    // Floating Fullscreen Toggle Button in Video Corner
                    IconButton(
                        onClick = { repository.toggleFullscreen(true) },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .size(36.dp)
                            .background(Color.Black.copy(alpha = 0.65f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fullscreen,
                            contentDescription = "Fullscreen",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Transport Controls Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(AuraSurfaceContainerHigh)
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        // Title & Metadata
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = playerState.title.ifBlank { "No Media Selected" },
                                    color = AuraOnSurface,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = playerState.subtitle.ifBlank { playerState.specs },
                                    color = AuraSecondary,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            if (playerState.isVideo) {
                                IconButton(
                                    onClick = {
                                        resizeMode = when (resizeMode) {
                                            AspectRatioFrameLayout.RESIZE_MODE_FIT -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                                            AspectRatioFrameLayout.RESIZE_MODE_ZOOM -> AspectRatioFrameLayout.RESIZE_MODE_FILL
                                            else -> AspectRatioFrameLayout.RESIZE_MODE_FIT
                                        }
                                    },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(AuraSurfaceContainer, CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AspectRatio,
                                        contentDescription = "Aspect Ratio",
                                        tint = AuraOnSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        // Progress Slider & Timestamps
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            val currentSliderProgress = if (isDraggingSeek) scrubProgress else playerState.progress
                            Slider(
                                value = currentSliderProgress,
                                onValueChange = {
                                    isDraggingSeek = true
                                    scrubProgress = it
                                },
                                onValueChangeFinished = {
                                    isDraggingSeek = false
                                    repository.setPlaybackProgress(scrubProgress)
                                },
                                colors = SliderDefaults.colors(
                                    thumbColor = AuraPrimary,
                                    activeTrackColor = AuraPrimary,
                                    inactiveTrackColor = AuraSurfaceVariant
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = playerState.currentTime,
                                    color = AuraOnSurface,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = playerState.chapterTitle,
                                    color = AuraTertiary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = playerState.totalTime,
                                    color = AuraOnSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Transport Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { repository.skipBackward(playerState.preferences.skipIntervalSeconds) },
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(AuraSurfaceContainer, CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Replay10,
                                    contentDescription = "Skip Back",
                                    tint = AuraOnSurface,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(58.dp)
                                    .clip(CircleShape)
                                    .background(AuraPrimaryContainer)
                                    .clickable { repository.togglePlayPause() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play/Pause",
                                    tint = AuraOnPrimaryContainer,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            IconButton(
                                onClick = { repository.skipForward(playerState.preferences.skipIntervalSeconds) },
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(AuraSurfaceContainer, CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Forward10,
                                    contentDescription = "Skip Forward",
                                    tint = AuraOnSurface,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Detail Section: Tab Row (Media Specs / Synchronized Transcripts)
                TabRow(
                    selectedTabIndex = selectedDetailTab,
                    containerColor = AuraSurface,
                    contentColor = AuraPrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedDetailTab]),
                            color = AuraPrimary
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp)
                ) {
                    Tab(
                        selected = selectedDetailTab == 0,
                        onClick = { selectedDetailTab = 0 },
                        text = {
                            Text(
                                text = "Queue & Info",
                                fontWeight = if (selectedDetailTab == 0) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    )
                    Tab(
                        selected = selectedDetailTab == 1,
                        onClick = { selectedDetailTab = 1 },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = "Transcript",
                                    fontWeight = if (selectedDetailTab == 1) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Detail Tab Content
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(AuraSurfaceContainer)
                        .padding(14.dp)
                ) {
                    if (selectedDetailTab == 0) {
                        // Media Info & Stream Properties
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "PLAYBACK ENGINE SPECS",
                                color = AuraTertiary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Codec / Pipeline", color = AuraOnSurfaceVariant, fontSize = 12.sp)
                                Text(text = "Media3 ExoPlayer v1.5", color = AuraOnSurface, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Audio Stream", color = AuraOnSurfaceVariant, fontSize = 12.sp)
                                Text(text = playerState.audioTrack, color = AuraOnSurface, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Subtitles Track", color = AuraOnSurfaceVariant, fontSize = 12.sp)
                                Text(
                                    text = playerState.selectedSubtitleTrack?.label ?: "Disabled",
                                    color = AuraPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    } else {
                        // Interactive Live Transcript
                        SynchronizedTranscriptView(
                            segments = playerState.transcriptSegments,
                            activeIndex = playerState.activeTranscriptIndex,
                            onSeekToSegment = { segment ->
                                repository.seekTo(segment.startMs)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }

        // Subtitles & Track Selection Bottom Sheet
        if (showTrackSheet) {
            ModalBottomSheet(
                onDismissRequest = { showTrackSheet = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = AuraSurfaceContainerHigh
            ) {
                TrackSelectorSheetContent(
                    playerState = playerState,
                    onSelectSubtitle = { track ->
                        repository.selectSubtitleTrack(track)
                        showTrackSheet = false
                    },
                    onSelectAudio = { track ->
                        repository.selectAudioTrack(track)
                        showTrackSheet = false
                    },
                    onClose = { showTrackSheet = false }
                )
            }
        }

        // Fullscreen Transcripts Modal
        if (showTranscriptModal) {
            ModalBottomSheet(
                onDismissRequest = { showTranscriptModal = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = AuraSurfaceContainerHigh
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Interactive Synchronized Transcript",
                            color = AuraOnSurface,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { showTranscriptModal = false }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = AuraOnSurface)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    SynchronizedTranscriptView(
                        segments = playerState.transcriptSegments,
                        activeIndex = playerState.activeTranscriptIndex,
                        onSeekToSegment = { segment ->
                            repository.seekTo(segment.startMs)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp)
                    )
                }
            }
        }
    }
}

// Sub-composable: ExoPlayer / Media View
@Composable
private fun PlayerSurfaceContent(
    repository: DataRepository,
    resizeMode: Int,
    context: Context,
    modifier: Modifier = Modifier
) {
    val playerState by repository.playerState.collectAsState()

    if (playerState.isVideo) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = repository.playerController.exoPlayer
                    useController = false
                    this.resizeMode = resizeMode
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            update = { view ->
                if (view.player != repository.playerController.exoPlayer) {
                    view.player = repository.playerController.exoPlayer
                }
                if (view.resizeMode != resizeMode) {
                    view.resizeMode = resizeMode
                }
            },
            modifier = modifier
        )
    } else {
        // Audio Visualizer / Artwork Card
        Box(
            modifier = modifier.background(
                Brush.radialGradient(
                    colors = listOf(AuraSurfaceContainerHigh, AuraSurfaceContainerLowest)
                )
            ),
            contentAlignment = Alignment.Center
        ) {
            val thumbnail = playerState.currentMedia?.thumbnailUri
            if (thumbnail != null) {
                AsyncImage(
                    model = ImageRequest.Builder(context).data(thumbnail).crossfade(true).build(),
                    contentDescription = playerState.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.55f))
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(AuraPrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Headphones,
                        contentDescription = null,
                        tint = AuraOnPrimaryContainer,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Text(
                    text = "AURA HIGH-FIDELITY AUDIO",
                    color = AuraTertiary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }

    // Buffering indicator
    if (playerState.isBuffering) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = AuraPrimary, modifier = Modifier.size(38.dp), strokeWidth = 3.dp)
        }
    }

    // Error indicator
    if (playerState.errorMessage != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(imageVector = Icons.Default.Warning, contentDescription = "Error", tint = AuraError, modifier = Modifier.size(32.dp))
                Text(
                    text = playerState.errorMessage ?: "Playback Error",
                    color = AuraOnErrorContainer,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// Sub-composable: Touch Gestures Area
@Composable
private fun PlayerGestureSurface(
    enabled: Boolean,
    onToggleControls: () -> Unit,
    onBrightnessChange: (Float) -> Unit,
    onVolumeChange: (Float) -> Unit,
    onHorizontalScrub: (Float, Float) -> Unit,
    onScrubFinished: () -> Unit,
    onDoubleTapLeft: () -> Unit,
    onDoubleTapRight: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!enabled) {
        Box(
            modifier = modifier.clickable { onToggleControls() }
        )
        return
    }

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onToggleControls() },
                    onDoubleTap = { offset ->
                        if (offset.x < size.width / 2) {
                            onDoubleTapLeft()
                        } else {
                            onDoubleTapRight()
                        }
                    }
                )
            }
            .pointerInput(Unit) {
                var totalDragX = 0f
                var totalDragY = 0f
                var isHorizontal = false
                var isVertical = false

                detectDragGestures(
                    onDragStart = {
                        totalDragX = 0f
                        totalDragY = 0f
                        isHorizontal = false
                        isVertical = false
                    },
                    onDragEnd = {
                        if (isHorizontal) {
                            onScrubFinished()
                        }
                    },
                    onDragCancel = {
                        if (isHorizontal) {
                            onScrubFinished()
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        totalDragX += dragAmount.x
                        totalDragY += dragAmount.y

                        if (!isHorizontal && !isVertical) {
                            if (kotlin.math.abs(totalDragX) > kotlin.math.abs(totalDragY) && kotlin.math.abs(totalDragX) > 20f) {
                                isHorizontal = true
                            } else if (kotlin.math.abs(totalDragY) > 20f) {
                                isVertical = true
                            }
                        }

                        if (isHorizontal) {
                            onHorizontalScrub(totalDragX, size.width.toFloat())
                        } else if (isVertical) {
                            if (change.position.x < size.width / 2) {
                                onBrightnessChange(dragAmount.y)
                            } else {
                                onVolumeChange(dragAmount.y)
                            }
                        }
                    }
                )
            }
    )
}

// Sub-composable: Live Subtitles Pill
@Composable
private fun LiveSubtitlesOverlay(
    subtitleText: String,
    textSizeSp: Float,
    modifier: Modifier = Modifier
) {
    if (subtitleText.isBlank()) return

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black.copy(alpha = 0.75f))
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(
            text = subtitleText,
            color = Color.White,
            fontSize = textSizeSp.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}

// Sub-composable: HUD Indicator Feedback
@Composable
private fun GesturesHudCenterOverlay(
    brightness: Int?,
    volume: Int?,
    seekTargetMs: Long?,
    seekDeltaSeconds: Long?,
    skipBadge: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (brightness != null) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.Black.copy(alpha = 0.75f),
                modifier = Modifier.padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.BrightnessMedium, contentDescription = null, tint = AuraPrimary, modifier = Modifier.size(24.dp))
                    Text(text = "Brightness: $brightness%", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        } else if (volume != null) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.Black.copy(alpha = 0.75f),
                modifier = Modifier.padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = AuraPrimary, modifier = Modifier.size(24.dp))
                    Text(text = "Volume: $volume%", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        } else if (seekTargetMs != null) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.Black.copy(alpha = 0.8f),
                modifier = Modifier.padding(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val deltaStr = if ((seekDeltaSeconds ?: 0L) >= 0) "+${seekDeltaSeconds}s" else "${seekDeltaSeconds}s"
                    Text(text = formatDuration(seekTargetMs), color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(text = deltaStr, color = AuraPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        } else if (skipBadge != null) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.Black.copy(alpha = 0.75f),
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = skipBadge,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

// Sub-composable: Fullscreen Controls Overlay
@Composable
private fun PlayerControlsOverlay(
    repository: DataRepository,
    isFullscreen: Boolean,
    onToggleFullscreen: () -> Unit,
    onCycleResizeMode: () -> Unit,
    onNavigateBack: () -> Unit,
    onOpenTrackSheet: () -> Unit,
    onOpenTranscripts: () -> Unit,
    modifier: Modifier = Modifier
) {
    val playerState by repository.playerState.collectAsState()

    Box(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.45f))
            .padding(16.dp)
    ) {
        // Top Fullscreen Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                Column {
                    Text(
                        text = playerState.title.ifBlank { "Playing Media" },
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = playerState.subtitle.ifBlank { playerState.specs },
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 11.sp
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Transcripts Button
                IconButton(
                    onClick = onOpenTranscripts,
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.Description, contentDescription = "Transcripts", tint = Color.White, modifier = Modifier.size(18.dp))
                }

                // Subtitles Button
                IconButton(
                    onClick = onOpenTrackSheet,
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.Subtitles, contentDescription = "Subtitles & Tracks", tint = Color.White, modifier = Modifier.size(18.dp))
                }

                // Aspect Ratio Button
                IconButton(
                    onClick = onCycleResizeMode,
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.AspectRatio, contentDescription = "Resize Mode", tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
        }

        // Center Transport Buttons
        Row(
            modifier = Modifier.align(Alignment.Center),
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { repository.skipBackward(playerState.preferences.skipIntervalSeconds) },
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
            ) {
                Icon(imageVector = Icons.Default.Replay10, contentDescription = "Skip Back", tint = Color.White, modifier = Modifier.size(26.dp))
            }

            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(AuraPrimary)
                    .clickable { repository.togglePlayPause() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Play/Pause",
                    tint = Color.Black,
                    modifier = Modifier.size(38.dp)
                )
            }

            IconButton(
                onClick = { repository.skipForward(playerState.preferences.skipIntervalSeconds) },
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
            ) {
                Icon(imageVector = Icons.Default.Forward10, contentDescription = "Skip Forward", tint = Color.White, modifier = Modifier.size(26.dp))
            }
        }

        // Bottom Progress & Fullscreen Toggle Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            Slider(
                value = playerState.progress,
                onValueChange = { repository.setPlaybackProgress(it) },
                colors = SliderDefaults.colors(
                    thumbColor = AuraPrimary,
                    activeTrackColor = AuraPrimary,
                    inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${playerState.currentTime} / ${playerState.totalTime}",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = onToggleFullscreen,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                        contentDescription = "Toggle Fullscreen",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

// Sub-composable: Interactive Synchronized Transcript List
@Composable
private fun SynchronizedTranscriptView(
    segments: List<TranscriptSegment>,
    activeIndex: Int,
    onSeekToSegment: (TranscriptSegment) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LaunchedEffect(activeIndex) {
        if (activeIndex in segments.indices) {
            listState.animateScrollToItem(activeIndex)
        }
    }

    if (segments.isEmpty()) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No synchronized transcript available for this media.",
                color = AuraOnSurfaceVariant,
                fontSize = 12.sp
            )
        }
        return
    }

    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        itemsIndexed(segments) { index, segment ->
            val isActive = index == activeIndex
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isActive) AuraPrimaryContainer.copy(alpha = 0.35f) else Color.Transparent)
                    .clickable { onSeekToSegment(segment) }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = formatDuration(segment.startMs),
                    color = if (isActive) AuraPrimary else AuraOnSurfaceVariant,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = segment.text,
                    color = if (isActive) AuraOnSurface else AuraOnSurfaceVariant,
                    fontSize = 13.sp,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// Sub-composable: Subtitle & Audio Tracks Sheet
@Composable
private fun TrackSelectorSheetContent(
    playerState: com.aura.player.data.model.PlayerState,
    onSelectSubtitle: (MediaTrackInfo?) -> Unit,
    onSelectAudio: (MediaTrackInfo) -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Audio & Subtitle Tracks",
                color = AuraOnSurface,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onClose) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = AuraOnSurface)
            }
        }

        // Subtitles Section
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "SUBTITLES (CLOSED CAPTIONS)",
                color = AuraTertiary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            val isOffSelected = playerState.selectedSubtitleTrack == null
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isOffSelected) AuraPrimaryContainer else AuraSurfaceContainer)
                    .clickable { onSelectSubtitle(null) }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Off (Disabled)", color = if (isOffSelected) AuraOnPrimaryContainer else AuraOnSurface, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                if (isOffSelected) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Selected", tint = AuraOnPrimaryContainer, modifier = Modifier.size(18.dp))
                }
            }

            playerState.subtitleTracks.forEach { track ->
                val isSelected = playerState.selectedSubtitleTrack?.id == track.id
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) AuraPrimaryContainer else AuraSurfaceContainer)
                        .clickable { onSelectSubtitle(track) }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = track.label, color = if (isSelected) AuraOnPrimaryContainer else AuraOnSurface, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    if (isSelected) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Selected", tint = AuraOnPrimaryContainer, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        // Audio Tracks Section
        if (playerState.audioTracks.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "AUDIO STREAMS",
                    color = AuraTertiary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                playerState.audioTracks.forEach { track ->
                    val isSelected = playerState.selectedAudioTrack?.id == track.id
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) AuraPrimaryContainer else AuraSurfaceContainer)
                            .clickable { onSelectAudio(track) }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = track.label, color = if (isSelected) AuraOnPrimaryContainer else AuraOnSurface, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        if (isSelected) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = "Selected", tint = AuraOnPrimaryContainer, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}
