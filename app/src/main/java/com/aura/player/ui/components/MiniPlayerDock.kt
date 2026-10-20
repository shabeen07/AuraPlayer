package com.aura.player.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
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
import com.aura.player.theme.AuraSecondaryContainer
import com.aura.player.theme.AuraSurfaceContainer
import com.aura.player.theme.AuraSurfaceContainerHigh
import com.aura.player.theme.AuraSurfaceContainerLowest
import com.aura.player.theme.AuraSurfaceVariant
import com.aura.player.theme.AuraTertiary

@Composable
fun MiniPlayerDock(
    repository: DataRepository,
    modifier: Modifier = Modifier,
    onPlayerClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val playerState by repository.playerState.collectAsState()
    val currentMedia = playerState.currentMedia

    // If no media has been selected yet, hide dock
    if (currentMedia == null && playerState.title.isBlank()) return

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .shadow(12.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(AuraSurfaceContainerHigh.copy(alpha = 0.96f))
            .clickable { onPlayerClick() }
            .padding(top = 10.dp, bottom = 8.dp, start = 12.dp, end = 12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Artwork & Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
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
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = playerState.title.ifBlank { "Aura Media" },
                            color = AuraOnSurface,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = playerState.subtitle.ifBlank { if (playerState.isVideo) "Video Playback" else "Audio Playback" },
                            color = AuraOnSurfaceVariant,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = { repository.skipBackward(10) },
                        modifier = Modifier
                            .size(34.dp)
                            .background(AuraSurfaceContainer, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Replay10,
                            contentDescription = "Rewind 10s",
                            tint = AuraOnSurfaceVariant,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    // Tactile Play/Pause Pill
                    IconButton(
                        onClick = { repository.togglePlayPause() },
                        modifier = Modifier
                            .size(42.dp)
                            .background(AuraPrimaryContainer, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (playerState.isPlaying) "Pause" else "Play",
                            tint = AuraOnPrimaryContainer,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    IconButton(
                        onClick = { repository.skipForward(10) },
                        modifier = Modifier
                            .size(34.dp)
                            .background(AuraSurfaceContainer, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Forward10,
                            contentDescription = "Forward 10s",
                            tint = AuraOnSurfaceVariant,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Mini Scrub Progress Micro-Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(CircleShape)
                    .background(AuraSurfaceVariant.copy(alpha = 0.5f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(playerState.progress.coerceIn(0f, 1f))
                        .height(3.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(AuraSecondaryContainer, AuraPrimary)
                            )
                        )
                )
            }
        }
    }
}
