package com.aura.player.ui.library

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VideoLibrary
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.aura.player.theme.AuraSurfaceContainerLow
import com.aura.player.theme.AuraTertiary
import kotlinx.coroutines.launch

enum class LibrarySortOption(val label: String) {
    TITLE("Title (A-Z)"),
    SIZE("Size (Max)"),
    DURATION("Duration")
}

@Composable
fun LibraryScreen(
    repository: DataRepository,
    onPlayItem: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val allMedia by repository.allMedia.collectAsState()
    val localVideos by repository.localVideos.collectAsState()
    val localAudios by repository.localAudios.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All Media") }
    var sortOption by remember { mutableStateOf(LibrarySortOption.TITLE) }

    val categories = listOf("All Media", "Videos", "Audio", "Folders")

    // Filter by category
    val baseList = when (selectedCategory) {
        "Videos" -> localVideos
        "Audio" -> localAudios
        else -> allMedia
    }

    // Filter by query
    val searchedList = if (searchQuery.isBlank()) {
        baseList
    } else {
        baseList.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    it.subtitle.contains(searchQuery, ignoreCase = true)
        }
    }

    // Sort
    val sortedList = when (sortOption) {
        LibrarySortOption.TITLE -> searchedList.sortedBy { it.title.lowercase() }
        LibrarySortOption.SIZE -> searchedList.sortedByDescending { it.fileSizeBytes }
        LibrarySortOption.DURATION -> searchedList.sortedByDescending { it.durationMs }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AuraSurface)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Top Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(AuraPrimary)
                )
                Text(
                    text = "Media Library",
                    color = AuraOnSurface,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Sort Toggle Button
                IconButton(
                    onClick = {
                        sortOption = when (sortOption) {
                            LibrarySortOption.TITLE -> LibrarySortOption.SIZE
                            LibrarySortOption.SIZE -> LibrarySortOption.DURATION
                            LibrarySortOption.DURATION -> LibrarySortOption.TITLE
                        }
                    },
                    modifier = Modifier
                        .size(38.dp)
                        .background(AuraSurfaceContainerHigh, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Sort,
                        contentDescription = "Sort",
                        tint = AuraSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Refresh Button
                IconButton(
                    onClick = {
                        scope.launch { repository.refreshLocalMedia() }
                    },
                    modifier = Modifier
                        .size(38.dp)
                        .background(AuraSurfaceContainerHigh, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = AuraOnSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Search Input Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = "Search tracks, artists, videos...",
                    color = AuraOnSurfaceVariant.copy(alpha = 0.6f),
                    fontSize = 13.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = AuraOnSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = AuraOnSurfaceVariant,
                            modifier = Modifier.size(18.dp)
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

        Spacer(modifier = Modifier.height(10.dp))

        // Category Filter Chips Strip
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
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (isSelected) AuraPrimaryContainer else AuraSurfaceContainerHigh)
                        .clickable { selectedCategory = category }
                        .padding(horizontal = 14.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val icon = when (category) {
                        "Videos" -> Icons.Default.Movie
                        "Audio" -> Icons.Default.Headphones
                        "Folders" -> Icons.Default.Folder
                        else -> Icons.Default.AutoAwesome
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = category,
                        tint = if (isSelected) AuraOnPrimaryContainer else AuraOnSurfaceVariant,
                        modifier = Modifier.size(15.dp)
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

        Spacer(modifier = Modifier.height(10.dp))

        // Stats Banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${sortedList.size} ITEMS • SORTED BY ${sortOption.label.uppercase()}",
                color = AuraOnSurfaceVariant,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Media List
        if (sortedList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(AuraSurfaceContainer)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VideoLibrary,
                        contentDescription = null,
                        tint = AuraOnSurfaceVariant,
                        modifier = Modifier.size(44.dp)
                    )
                    Text(
                        text = if (searchQuery.isNotBlank()) "No media matches \"$searchQuery\"" else "No $selectedCategory available",
                        color = AuraOnSurfaceVariant,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 88.dp)
            ) {
                items(sortedList, key = { it.id }) { item ->
                    LibraryMediaCard(
                        media = item,
                        onClick = {
                            repository.playMedia(item)
                            onPlayItem()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LibraryMediaCard(
    media: MediaItem,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(AuraSurfaceContainerLow)
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Thumbnail Box
        Box(
            modifier = Modifier
                .size(54.dp)
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
                    modifier = Modifier.size(24.dp)
                )
            }

            // Duration Pill Overlay
            if (media.duration.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(2.dp)
                        .background(AuraSurface.copy(alpha = 0.85f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = media.duration,
                        color = AuraTertiary,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Title and Subtitle Info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = media.title,
                color = AuraOnSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(top = 2.dp)
            ) {
                if (media.qualityBadge.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .background(AuraSurfaceContainerHighest, RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = media.qualityBadge,
                            color = AuraSecondary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    text = "${media.subtitle.ifBlank { media.category }} • ${media.fileSize}",
                    color = AuraOnSurfaceVariant,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Play Pill Button
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(AuraPrimaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play",
                tint = AuraOnPrimaryContainer,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
