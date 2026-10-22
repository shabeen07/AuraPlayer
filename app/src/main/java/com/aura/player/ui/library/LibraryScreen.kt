package com.aura.player.ui.library

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import com.aura.player.theme.AuraSurface
import com.aura.player.theme.AuraSurfaceContainer
import com.aura.player.theme.AuraSurfaceContainerHigh
import com.aura.player.theme.AuraSurfaceContainerHighest
import com.aura.player.theme.AuraTertiary
import kotlinx.coroutines.launch

enum class LibrarySortOption(val label: String) {
    TITLE("A-Z"),
    SIZE("Size"),
    DURATION("Length")
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
    var selectedTabIdx by remember { mutableIntStateOf(0) } // 0: All, 1: Videos, 2: Audio
    var sortOption by remember { mutableStateOf(LibrarySortOption.TITLE) }

    val baseList = when (selectedTabIdx) {
        1 -> localVideos
        2 -> localAudios
        else -> allMedia
    }

    val filteredList = if (searchQuery.isBlank()) {
        baseList
    } else {
        baseList.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    it.subtitle.contains(searchQuery, ignoreCase = true)
        }
    }

    val sortedList = when (sortOption) {
        LibrarySortOption.TITLE -> filteredList.sortedBy { it.title.lowercase() }
        LibrarySortOption.SIZE -> filteredList.sortedByDescending { it.fileSizeBytes }
        LibrarySortOption.DURATION -> filteredList.sortedByDescending { it.durationMs }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AuraSurface)
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        // Modern Clean Category Tabs
        val tabs = listOf("All Media (${allMedia.size})", "Videos (${localVideos.size})", "Audio (${localAudios.size})")
        TabRow(
            selectedTabIndex = selectedTabIdx,
            containerColor = AuraSurface,
            contentColor = AuraPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIdx]),
                    color = AuraPrimary
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIdx == index,
                    onClick = { selectedTabIdx = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTabIdx == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Bar & Filter Options
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = "Search tracks, videos...",
                        color = AuraOnSurfaceVariant.copy(alpha = 0.6f),
                        fontSize = 13.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = AuraOnSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = AuraOnSurfaceVariant,
                                modifier = Modifier.size(16.dp)
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
                shape = RoundedCornerShape(14.dp)
            )

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
                    .size(44.dp)
                    .background(AuraSurfaceContainerHigh, RoundedCornerShape(12.dp))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Sort,
                    contentDescription = "Sort",
                    tint = AuraPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Refresh Button
            IconButton(
                onClick = {
                    scope.launch { repository.refreshLocalMedia() }
                },
                modifier = Modifier
                    .size(44.dp)
                    .background(AuraSurfaceContainerHigh, RoundedCornerShape(12.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = AuraOnSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Results Summary
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${sortedList.size} ITEMS • SORT: ${sortOption.label.uppercase()}",
                color = AuraTertiary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Media List
        if (sortedList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (searchQuery.isNotBlank()) "No media matching \"$searchQuery\"" else "No files in this category.",
                    color = AuraOnSurfaceVariant,
                    fontSize = 13.sp
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(sortedList, key = { it.id }) { item ->
                    CleanMediaListItem(
                        media = item,
                        context = context,
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
private fun CleanMediaListItem(
    media: MediaItem,
    context: android.content.Context,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AuraSurfaceContainer)
            .clickable { onClick() }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Thumbnail
        Box(
            modifier = Modifier
                .size(54.dp)
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

        // Details
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = media.title,
                color = AuraOnSurface,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = media.duration,
                    color = AuraOnSurfaceVariant,
                    fontSize = 11.sp
                )
                if (media.fileSize.isNotBlank()) {
                    Text(text = "•", color = AuraOnSurfaceVariant, fontSize = 10.sp)
                    Text(
                        text = media.fileSize,
                        color = AuraOnSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Play Action Pill
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
