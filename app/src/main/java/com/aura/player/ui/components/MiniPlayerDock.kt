package com.aura.player.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.aura.player.data.DataRepository
import com.aura.player.theme.AuraOnPrimaryContainer
import com.aura.player.theme.AuraOnSurface
import com.aura.player.theme.AuraOnSurfaceVariant
import com.aura.player.theme.AuraPrimary
import com.aura.player.theme.AuraPrimaryContainer
import com.aura.player.theme.AuraSecondary
import com.aura.player.theme.AuraSurfaceContainer
import com.aura.player.theme.AuraSurfaceContainerHigh
import com.aura.player.theme.AuraSurfaceContainerHighest
import com.aura.player.theme.AuraSurfaceContainerLowest
import com.aura.player.theme.AuraSurfaceVariant

@Composable
fun MiniPlayerDock(
    repository: DataRepository,
    modifier: Modifier = Modifier,
    onPlayerClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val playerState by repository.playerState.collectAsState()
    val currentMedia = playerState.currentMedia

    val isVisible = currentMedia != null || (playerState.title.isNotBlank() && playerState.title != "No Media Selected")

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut(),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .shadow(
                    elevation = 14.dp,
                    shape = RoundedCornerShape(18.dp),
                    ambientColor = Color.Black.copy(alpha = 0.5f),
                    spotColor = Color.Black.copy(alpha = 0.35f)
                )
                .clip(RoundedCornerShape(18.dp))
                .background(AuraSurfaceContainerHighest.copy(alpha = 0.95f))
                .border(1.dp, Color.White.copy(alpha = 0.09f), RoundedCornerShape(18.dp))
                .clickable { onPlayerClick() }
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Artwork / Video Thumbnail & Metadata
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        // 16:10 / 4:3 Thumbnail Box
                        Box(
                            modifier = Modifier
                                .size(width = 54.dp, height = 42.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(AuraSurfaceContainerLowest),
                            contentAlignment = Alignment.Center
                        ) {
                            if (currentMedia?.thumbnailUri != null) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(currentMedia.thumbnailUri)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = playerState.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else if (currentMedia?.imageResId != null && currentMedia.imageResId != 0) {
                                Image(
                                    painter = painterResource(id = currentMedia.imageResId),
                                    contentDescription = playerState.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Icon(
                                    imageVector = if (playerState.isVideo) Icons.Default.Movie else Icons.Default.Headphones,
                                    contentDescription = null,
                                    tint = AuraPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            // Format Badge Overlay for Videos
                            if (playerState.isVideo) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(2.dp)
                                        .background(Color.Black.copy(alpha = 0.75f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 3.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "VIDEO",
                                        color = Color.White,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Title, Subtitle, and Live Indicator
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = playerState.title.ifBlank { "Aura Media" },
                                color = AuraOnSurface,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                if (playerState.isVideo) {
                                    Text(
                                        text = playerState.subtitle.ifBlank { "Video Stream" },
                                        color = AuraOnSurfaceVariant,
                                        fontSize = 11.5.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                } else {
                                    // Audio live equalizer bars
                                    MiniEqualizerIndicator(
                                        isPlaying = playerState.isPlaying,
                                        modifier = Modifier.height(12.dp)
                                    )
                                    Text(
                                        text = playerState.subtitle.ifBlank { "Audio Playback" },
                                        color = AuraOnSurfaceVariant,
                                        fontSize = 11.5.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Focused 2-Button Transport Cluster
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Play / Pause Toggle
                        IconButton(
                            onClick = { repository.togglePlayPause() },
                            modifier = Modifier
                                .size(40.dp)
                                .background(AuraPrimaryContainer, CircleShape)
                        ) {
                            Icon(
                                imageVector = if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (playerState.isPlaying) "Pause" else "Play",
                                tint = AuraOnPrimaryContainer,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // Close / Dismiss Player
                        IconButton(
                            onClick = { repository.stopPlayback() },
                            modifier = Modifier
                                .size(30.dp)
                                .background(AuraSurfaceContainer.copy(alpha = 0.8f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Player",
                                tint = AuraOnSurfaceVariant,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }

                // Flush Edge-to-Edge Micro Progress Bar docked at bottom
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.5.dp)
                        .background(AuraSurfaceVariant.copy(alpha = 0.35f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(playerState.progress.coerceIn(0f, 1f))
                            .height(2.5.dp)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(AuraSecondary, AuraPrimary)
                                )
                            )
                    )
                }
            }
        }
    }
}

/**
 * Animated 3-bar equalizer indicator for audio playback in the mini player dock.
 */
@Composable
private fun MiniEqualizerIndicator(
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "eq_bars")
    val bar1Height by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(420, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar1"
    )
    val bar2Height by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 0.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(320, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar2"
    )
    val bar3Height by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(520, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar3"
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        val heights = if (isPlaying) listOf(bar1Height, bar2Height, bar3Height) else listOf(0.4f, 0.4f, 0.4f)
        heights.forEach { h ->
            Box(
                modifier = Modifier
                    .width(2.5.dp)
                    .height((h * 11).dp)
                    .clip(CircleShape)
                    .background(AuraPrimary)
            )
        }
    }
}
