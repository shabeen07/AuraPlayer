package com.aura.player.ui.player

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.aura.player.data.DataRepository
import com.aura.player.theme.AuraError
import com.aura.player.theme.AuraErrorContainer
import com.aura.player.theme.AuraOnErrorContainer
import com.aura.player.theme.AuraOnPrimaryContainer
import com.aura.player.theme.AuraOnSurface
import com.aura.player.theme.AuraOnSurfaceVariant
import com.aura.player.theme.AuraOutline
import com.aura.player.theme.AuraOutlineVariant
import com.aura.player.theme.AuraPrimary
import com.aura.player.theme.AuraPrimaryContainer
import com.aura.player.theme.AuraSecondary
import com.aura.player.theme.AuraSecondaryContainer
import com.aura.player.theme.AuraSurface
import com.aura.player.theme.AuraSurfaceContainer
import com.aura.player.theme.AuraSurfaceContainerHigh
import com.aura.player.theme.AuraSurfaceContainerHighest
import com.aura.player.theme.AuraSurfaceContainerLow
import com.aura.player.theme.AuraSurfaceContainerLowest
import com.aura.player.theme.AuraSurfaceVariant
import com.aura.player.theme.AuraTertiary

@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(
    repository: DataRepository,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val playerState by repository.playerState.collectAsState()

    var isLocked by remember { mutableStateOf(false) }
    var isDraggingSeek by remember { mutableStateOf(false) }
    var scrubProgress by remember { mutableFloatStateOf(0f) }
    var resizeMode by remember { mutableStateOf(AspectRatioFrameLayout.RESIZE_MODE_FIT) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AuraSurface)
            .verticalScroll(rememberScrollState())
    ) {
        // Player Top Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp),
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
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column {
                    Text(
                        text = "Now Playing",
                        color = AuraOnSurface,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (playerState.isVideo) "Video Player" else "Audio Player",
                        color = AuraOnSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }

            // Quick Speed Switcher Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
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
                        .padding(horizontal = 9.dp, vertical = 5.dp)
                )
            }
        }

        // Playback View Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp)
                .aspectRatio(16f / 10f)
                .clip(RoundedCornerShape(22.dp))
                .background(AuraSurfaceContainerLowest),
            contentAlignment = Alignment.Center
        ) {
            if (playerState.isVideo) {
                // Real AndroidView wrapping Media3 PlayerView
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
                        view.player = repository.playerController.exoPlayer
                        view.resizeMode = resizeMode
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Audio Artwork & Waveform Surface
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    AuraSurfaceContainerHigh,
                                    AuraSurfaceContainerLowest
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (playerState.currentMedia?.thumbnailUri != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(playerState.currentMedia?.thumbnailUri)
                                .crossfade(true)
                                .build(),
                            contentDescription = playerState.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.5f))
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
                            text = "AURA AUDIO ENGINE",
                            color = AuraTertiary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            // Buffering Indicator
            if (playerState.isBuffering) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.65f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = AuraPrimary,
                        modifier = Modifier.size(28.dp),
                        strokeWidth = 3.dp
                    )
                }
            }

            // Error Overlay if any
            if (playerState.errorMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.8f))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Error",
                            tint = AuraError,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = playerState.errorMessage ?: "Playback Error",
                            color = AuraOnErrorContainer,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Transport Controls Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(AuraSurfaceContainerLow)
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Title and Subtitle Info
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

                // Seek Progress Bar & Timestamps
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
                            color = AuraOutlineVariant,
                            fontSize = 11.sp
                        )
                    }
                }

                // Playback Transport Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { isLocked = !isLocked },
                        modifier = Modifier
                            .size(38.dp)
                            .background(AuraSurfaceContainer, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                            contentDescription = "Lock",
                            tint = if (isLocked) AuraSecondary else AuraOnSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = { repository.skipBackward(10) },
                        modifier = Modifier
                            .size(42.dp)
                            .background(AuraSurfaceContainer, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Replay10,
                            contentDescription = "Rewind 10s",
                            tint = AuraOnSurface,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Main Elevated Play/Pause Button
                    Box(
                        modifier = Modifier
                            .size(62.dp)
                            .shadow(16.dp, CircleShape, ambientColor = AuraPrimaryContainer, spotColor = AuraPrimaryContainer)
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
                        onClick = { repository.skipForward(10) },
                        modifier = Modifier
                            .size(42.dp)
                            .background(AuraSurfaceContainer, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Forward10,
                            contentDescription = "Forward 10s",
                            tint = AuraOnSurface,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            val nextVol = if (playerState.volume > 0f) 0f else 1f
                            repository.updateVolume(nextVol)
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .background(AuraSurfaceContainer, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Mute/Unmute",
                            tint = if (playerState.volume > 0f) AuraPrimary else AuraOnSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Audio & Engine Details Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(AuraSurfaceContainerLow)
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(AuraTertiary)
                        )
                        Text(
                            text = "EXOPLAYER MEDIA3 ENGINE",
                            color = AuraOnSurfaceVariant,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = playerState.audioTrack,
                        color = AuraSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = "High-efficiency adaptive pipeline with native hardware acceleration, live buffering, and HLS/DASH/Direct streaming.",
                    color = AuraOnSurfaceVariant,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
