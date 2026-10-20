package com.aura.player.ui.home

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Podcasts
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
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
import com.aura.player.R
import com.aura.player.data.DataRepository
import com.aura.player.data.model.MediaItem
import com.aura.player.theme.AuraErrorContainer
import com.aura.player.theme.AuraOnErrorContainer
import com.aura.player.theme.AuraOnPrimaryContainer
import com.aura.player.theme.AuraOnSurface
import com.aura.player.theme.AuraOnSurfaceVariant
import com.aura.player.theme.AuraOutline
import com.aura.player.theme.AuraPrimary
import com.aura.player.theme.AuraPrimaryContainer
import com.aura.player.theme.AuraSecondary
import com.aura.player.theme.AuraSecondaryContainer
import com.aura.player.theme.AuraSurface
import com.aura.player.theme.AuraSurfaceContainer
import com.aura.player.theme.AuraSurfaceContainerHigh
import com.aura.player.theme.AuraSurfaceContainerHighest
import com.aura.player.theme.AuraSurfaceContainerLowest
import com.aura.player.theme.AuraTertiary
import com.aura.player.ui.components.SquigglyWaveform
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

    var selectedCategory by remember { mutableStateOf("All Media") }
    var streamUrlText by remember { mutableStateOf("") }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        repository.checkPermissionAndUpdate()
    }

    val categories = listOf("All Media", "Videos", "Audio", "Streams")
    val scrollState = rememberScrollState()

    val currentFilteredMedia = when (selectedCategory) {
        "Videos" -> localVideos
        "Audio" -> localAudios
        "Streams" -> repository.trendingChannels
        else -> allMedia
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AuraSurface)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Category Filter Pill Strip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->
                val isSelected = category == selectedCategory
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isSelected) AuraPrimaryContainer else AuraSurfaceContainerHigh
                        )
                        .clickable { selectedCategory = category }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val icon = when (category) {
                        "All Media" -> Icons.Default.AutoAwesome
                        "Videos" -> Icons.Default.Movie
                        "Audio" -> Icons.Default.Headphones
                        else -> Icons.AutoMirrored.Filled.QueueMusic
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = category,
                        tint = if (isSelected) AuraOnPrimaryContainer else AuraOnSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = category,
                        color = if (isSelected) AuraOnPrimaryContainer else AuraOnSurfaceVariant,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Storage Permission Card if not granted
        if (!hasPermission) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(AuraSurfaceContainerHigh)
                    .border(1.dp, AuraPrimary.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(AuraPrimaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Permission",
                                tint = AuraOnPrimaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Device Storage Access",
                                color = AuraOnSurface,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Grant permission to scan and play audio and video files stored on your device.",
                                color = AuraOnSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Button(
                        onClick = {
                            permissionLauncher.launch(PermissionUtils.getRequiredStoragePermissions())
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AuraPrimaryContainer),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Grant Storage Permission",
                            color = AuraOnPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Live Hero Banner: Dynamic Now Playing or Ready State
        val currentMedia = playerState.currentMedia
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(AuraSurfaceContainerHigh)
                .clickable { onNavigateToPlayer() }
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Top meta pill row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .background(AuraSurfaceContainer, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (playerState.isPlaying) AuraTertiary else AuraOutline)
                        )
                        Text(
                            text = if (playerState.isPlaying) "ENGINE ACTIVE" else "AURA ENGINE",
                            color = if (playerState.isPlaying) AuraTertiary else AuraOnSurfaceVariant,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        modifier = Modifier
                            .background(AuraSurfaceContainerHighest, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Quality",
                            tint = AuraSecondary,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = currentMedia?.qualityBadge?.ifBlank { "Media3 Engine" } ?: "Ready",
                            color = AuraSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Main Track Details & Thumbnail
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(AuraSurfaceContainerLowest),
                        contentAlignment = Alignment.Center
                    ) {
                        if (currentMedia?.thumbnailUri != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(currentMedia.thumbnailUri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = currentMedia.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else if (currentMedia?.imageResId != null && currentMedia.imageResId != 0) {
                            Image(
                                painter = painterResource(id = currentMedia.imageResId),
                                contentDescription = currentMedia.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                imageVector = if (playerState.isVideo) Icons.Default.Movie else Icons.Default.Headphones,
                                contentDescription = null,
                                tint = AuraPrimary,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = (currentMedia?.category ?: "AURA AUDIO / VIDEO").uppercase(),
                            color = AuraSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = currentMedia?.title ?: "Select a file or stream URL",
                            color = AuraOnSurface,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = currentMedia?.subtitle?.ifBlank { currentMedia.category } ?: "Local storage & live streaming enabled",
                            color = AuraOnSurfaceVariant,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Kinetic Waveform & Seek State
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    SquigglyWaveform(
                        progress = playerState.progress,
                        onProgressChange = { repository.setPlaybackProgress(it) }
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = playerState.currentTime, color = AuraOnSurfaceVariant, fontSize = 11.sp)
                        Text(
                            text = playerState.chapterTitle,
                            color = AuraTertiary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(text = playerState.totalTime, color = AuraOnSurfaceVariant, fontSize = 11.sp)
                    }
                }

                // Transport Action Strip
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { repository.skipBackward(10) },
                        modifier = Modifier
                            .size(38.dp)
                            .background(AuraSurfaceContainer, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "Previous 10s",
                            tint = AuraOnSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Button(
                        onClick = {
                            if (currentMedia != null) {
                                repository.togglePlayPause()
                            } else if (allMedia.isNotEmpty()) {
                                repository.playMedia(allMedia.first())
                            } else if (repository.trendingChannels.isNotEmpty()) {
                                repository.playMedia(repository.trendingChannels.first())
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AuraPrimaryContainer),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.height(46.dp)
                    ) {
                        Icon(
                            imageVector = if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play/Pause",
                            tint = AuraOnPrimaryContainer,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (playerState.isPlaying) "Pause" else "Play",
                            color = AuraOnPrimaryContainer,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = { repository.skipForward(10) },
                        modifier = Modifier
                            .size(38.dp)
                            .background(AuraSurfaceContainer, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Next 10s",
                            tint = AuraOnSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Functional Online Stream Ingest Section
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Hub,
                        contentDescription = "Stream Ingest",
                        tint = AuraPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "Online Stream Ingest",
                        color = AuraOnSurface,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(AuraSurfaceContainer)
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Link,
                                contentDescription = "Stream Link",
                                tint = AuraSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Stream Audio or Video URL (HLS / MP4 / MP3)",
                                color = AuraOnSurfaceVariant,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Stream Input Field
                    OutlinedTextField(
                        value = streamUrlText,
                        onValueChange = { streamUrlText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                text = "https://example.com/stream.m3u8",
                                color = AuraOnSurfaceVariant.copy(alpha = 0.6f),
                                fontSize = 13.sp
                            )
                        },
                        trailingIcon = {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 6.dp)) {
                                Button(
                                    onClick = {
                                        val text = clipboardManager.getText()?.text
                                        if (!text.isNullOrBlank()) {
                                            streamUrlText = text.trim()
                                        } else {
                                            Toast.makeText(context, "Clipboard is empty", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AuraSurfaceContainerHigh),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentPaste,
                                        contentDescription = "Paste",
                                        tint = AuraOnSurface,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Paste",
                                        color = AuraOnSurface,
                                        fontSize = 11.sp
                                    )
                                }
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
                        shape = RoundedCornerShape(16.dp)
                    )

                    // Stream Action Button
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
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = "Play Stream",
                            tint = AuraOnPrimaryContainer,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Play Online Stream",
                            color = AuraOnPrimaryContainer,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Local Device Media Section
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FolderSpecial,
                        contentDescription = "Device Storage",
                        tint = AuraSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "Device Media (${currentFilteredMedia.size})",
                        color = AuraOnSurface,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onNavigateToVault() }
                ) {
                    Text(
                        text = "View Library",
                        color = AuraPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "View Library",
                        tint = AuraPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            if (currentFilteredMedia.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(AuraSurfaceContainer)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FolderSpecial,
                            contentDescription = null,
                            tint = AuraOnSurfaceVariant,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = if (hasPermission) "No $selectedCategory files found" else "Permission required to view files",
                            color = AuraOnSurfaceVariant,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(end = 8.dp)
                ) {
                    items(currentFilteredMedia) { media ->
                        MediaCard(
                            media = media,
                            onClick = {
                                repository.playMedia(media)
                                onNavigateToPlayer()
                            }
                        )
                    }
                }
            }
        }

        // Online Radio & Live Streams
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "ONLINE RADIO & LIVE CHANNELS",
                color = AuraOnSurfaceVariant,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            repository.trendingChannels.forEach { channel ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(AuraSurfaceContainer)
                        .clickable {
                            repository.playMedia(channel)
                            onNavigateToPlayer()
                        }
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(14.dp))
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
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayCircle,
                                contentDescription = "Play",
                                tint = if (channel.category == "Radio") AuraPrimary else AuraSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (channel.duration == "LIVE") AuraErrorContainer else AuraSurfaceContainerHighest,
                                        RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = channel.duration,
                                    color = if (channel.duration == "LIVE") AuraOnErrorContainer else AuraSecondary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = channel.subtitle,
                                color = AuraOnSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                        Text(
                            text = channel.title,
                            color = AuraOnSurface,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${channel.extraTag} • ${channel.qualityBadge}",
                            color = AuraOnSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }

                    IconButton(
                        onClick = {
                            repository.playMedia(channel)
                            onNavigateToPlayer()
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .background(AuraSurfaceContainerHigh, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (channel.category == "Podcast") Icons.Default.Podcasts else Icons.Default.Radio,
                            contentDescription = "Channel Icon",
                            tint = AuraOnSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(110.dp))
    }
}

@Composable
fun MediaCard(
    media: MediaItem,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .width(230.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(AuraSurfaceContainer)
            .clickable { onClick() }
            .padding(10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(AuraSurfaceContainerHighest),
                contentAlignment = Alignment.Center
            ) {
                if (media.thumbnailUri != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(media.thumbnailUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = media.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (media.imageResId != 0) {
                    Image(
                        painter = painterResource(id = media.imageResId),
                        contentDescription = media.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = if (media.isVideo) Icons.Default.Movie else Icons.Default.Headphones,
                        contentDescription = null,
                        tint = AuraPrimary,
                        modifier = Modifier.size(48.dp)
                    )
                }

                // Top badge
                if (media.qualityBadge.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(6.dp)
                            .background(AuraSurfaceContainerLowest.copy(alpha = 0.85f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = media.qualityBadge,
                            color = AuraSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Bottom time badge
                if (media.duration.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(6.dp)
                            .background(AuraSurfaceContainerLowest.copy(alpha = 0.85f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = media.duration,
                            color = AuraOnSurface,
                            fontSize = 10.sp
                        )
                    }
                }

                // File size badge
                if (media.fileSize.isNotBlank()) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "File",
                            tint = AuraTertiary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = media.fileSize,
                            color = AuraTertiary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = media.title,
                        color = AuraOnSurface,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = media.subtitle.ifBlank { media.category },
                        color = AuraOnSurfaceVariant,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(AuraSurfaceContainerHigh),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = AuraOnSurface,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
