package com.aura.player.ui.home

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.aura.player.data.DataRepository
import com.aura.player.data.model.MediaItem
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
import com.aura.player.theme.AuraTertiary
import com.aura.player.util.PermissionUtils

@Composable
fun HomeScreen(
    repository: DataRepository,
    onNavigateToPlayer: () -> Unit,
    onNavigateToVault: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val hasPermission by repository.hasStoragePermission.collectAsState()
    val allMedia by repository.allMedia.collectAsState()
    val localVideos by repository.localVideos.collectAsState()
    val localAudios by repository.localAudios.collectAsState()
    val playerState by repository.playerState.collectAsState()

    var streamUrlText by remember { mutableStateOf("") }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        repository.checkPermissionAndUpdate()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AuraSurface)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Storage Permission Card (if not granted)
        if (!hasPermission) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(AuraSurfaceContainerHigh)
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(AuraPrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = AuraOnPrimaryContainer,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Storage Permission",
                            color = AuraOnSurface,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Grant access to play your device videos & music.",
                            color = AuraOnSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = {
                            permissionLauncher.launch(PermissionUtils.getRequiredStoragePermissions())
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AuraPrimaryContainer),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Allow",
                            color = AuraOnPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Active Playback Hero Card (only if media is active)
        val activeMedia = playerState.currentMedia
        if (activeMedia != null || (playerState.title.isNotBlank() && playerState.title != "No Media Selected")) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(AuraSurfaceContainerHigh)
                    .clickable { onNavigateToPlayer() }
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Artwork Thumbnail
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(AuraSurfaceContainerLowest),
                        contentAlignment = Alignment.Center
                    ) {
                        if (activeMedia?.thumbnailUri != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(context).data(activeMedia.thumbnailUri).crossfade(true).build(),
                                contentDescription = playerState.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else if (activeMedia?.imageResId != null && activeMedia.imageResId != 0) {
                            Image(
                                painter = painterResource(id = activeMedia.imageResId),
                                contentDescription = playerState.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                imageVector = if (playerState.isVideo) Icons.Default.Movie else Icons.Default.Headphones,
                                contentDescription = null,
                                tint = AuraPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    // Title & Progress Info
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "RESUME PLAYING",
                            color = AuraPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = playerState.title,
                            color = AuraOnSurface,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${playerState.currentTime} / ${playerState.totalTime}",
                            color = AuraOnSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }

                    // Play/Pause Action Button
                    IconButton(
                        onClick = { repository.togglePlayPause() },
                        modifier = Modifier
                            .size(44.dp)
                            .background(AuraPrimaryContainer, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play/Pause",
                            tint = AuraOnPrimaryContainer,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // Sleek 1-Line Stream Player Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(AuraSurfaceContainer)
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Link,
                        contentDescription = null,
                        tint = AuraPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Play Network Stream",
                        color = AuraOnSurface,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = streamUrlText,
                        onValueChange = { streamUrlText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = {
                            Text(
                                text = "Enter HLS / MP4 / MP3 URL...",
                                color = AuraOnSurfaceVariant.copy(alpha = 0.6f),
                                fontSize = 12.sp
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    val text = clipboardManager.getText()?.text
                                    if (!text.isNullOrBlank()) {
                                        streamUrlText = text.trim()
                                    } else {
                                        Toast.makeText(context, "Clipboard empty", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentPaste,
                                    contentDescription = "Paste",
                                    tint = AuraOnSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = AuraOnSurface,
                            unfocusedTextColor = AuraOnSurface,
                            focusedBorderColor = AuraPrimary,
                            unfocusedBorderColor = AuraSurfaceContainerHighest,
                            focusedContainerColor = AuraSurfaceContainerHigh,
                            unfocusedContainerColor = AuraSurfaceContainerHigh
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(
                        onClick = {
                            if (streamUrlText.isNotBlank()) {
                                val isVideo = !streamUrlText.endsWith(".mp3", ignoreCase = true) &&
                                        !streamUrlText.endsWith(".m4a", ignoreCase = true) &&
                                        !streamUrlText.endsWith(".aac", ignoreCase = true)
                                repository.playStreamUrl(streamUrlText, isVideo = isVideo)
                                onNavigateToPlayer()
                            } else {
                                Toast.makeText(context, "Enter a valid stream URL", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AuraPrimaryContainer),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text(
                            text = "Play",
                            color = AuraOnPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Live Channels & Radio Section
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "FEATURED STREAMS & LIVE RADIO",
                color = AuraTertiary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(end = 4.dp)
            ) {
                items(repository.trendingChannels) { channel ->
                    Box(
                        modifier = Modifier
                            .width(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(AuraSurfaceContainerHigh)
                            .clickable {
                                repository.playMedia(channel)
                                onNavigateToPlayer()
                            }
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(90.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(AuraSurfaceContainerHighest),
                                contentAlignment = Alignment.Center
                            ) {
                                if (channel.imageResId != 0) {
                                    Image(
                                        painter = painterResource(id = channel.imageResId),
                                        contentDescription = channel.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Icon(
                                        imageVector = if (channel.isVideo) Icons.Default.Movie else Icons.Default.Radio,
                                        contentDescription = null,
                                        tint = AuraPrimary,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.5f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Play",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Text(
                                text = channel.title,
                                color = AuraOnSurface,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = channel.subtitle,
                                color = AuraOnSurfaceVariant,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // Recent Device Media Section
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DEVICE MEDIA (${allMedia.size})",
                    color = AuraTertiary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onNavigateToVault() }
                ) {
                    Text(
                        text = "Vault",
                        color = AuraPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Vault",
                        tint = AuraPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            if (allMedia.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(AuraSurfaceContainer)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (hasPermission) "No local audio or video files found." else "Storage permission required to show files.",
                        color = AuraOnSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(end = 4.dp)
                ) {
                    items(allMedia.take(15)) { media ->
                        Box(
                            modifier = Modifier
                                .width(150.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(AuraSurfaceContainer)
                                .clickable {
                                    repository.playMedia(media)
                                    onNavigateToPlayer()
                                }
                                .padding(10.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(75.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(AuraSurfaceContainerHighest),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (media.thumbnailUri != null) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(context).data(media.thumbnailUri).crossfade(true).build(),
                                            contentDescription = media.title,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else {
                                        Icon(
                                            imageVector = if (media.isVideo) Icons.Default.Movie else Icons.Default.Headphones,
                                            contentDescription = null,
                                            tint = AuraPrimary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = media.title,
                                    color = AuraOnSurface,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = media.duration,
                                    color = AuraOnSurfaceVariant,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
