package com.aura.player.ui.vault

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.aura.player.data.model.VaultStoredItem
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
import com.aura.player.theme.AuraSurfaceContainerLowest
import com.aura.player.theme.AuraSurfaceVariant
import com.aura.player.theme.AuraTertiary
import kotlinx.coroutines.launch

@Composable
fun VaultScreen(
    repository: DataRepository,
    onPlayItem: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val vaultItems by repository.vaultItems.collectAsState()
    val stats by repository.storageStats.collectAsState()

    var selectedFilter by remember { mutableStateOf("All") }
    val filterOptions = listOf("All", "Movies & Videos", "Audio & Music")
    val scrollState = rememberScrollState()

    val filteredItems = when (selectedFilter) {
        "Movies & Videos" -> vaultItems.filter { it.isVideo }
        "Audio & Music" -> vaultItems.filter { !it.isVideo }
        else -> vaultItems
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AuraSurface)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Top View Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(9.dp)
                        .clip(CircleShape)
                        .background(AuraTertiary)
                )
                Text(
                    text = "Media Library & Vault",
                    color = AuraOnSurface,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(
                onClick = {
                    scope.launch {
                        repository.refreshLocalMedia()
                    }
                },
                modifier = Modifier
                    .size(38.dp)
                    .background(AuraSurfaceContainer, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = AuraOnSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Storage Diagnostic Gauge Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(AuraSurfaceContainer)
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = "LOCAL DEVICE STORAGE",
                            color = AuraOnSurfaceVariant,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Text(
                                text = String.format("%.1f", stats.usedGb),
                                color = AuraOnSurface,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "/ ${String.format("%.0f", stats.totalGb)} GB",
                                color = AuraOnSurfaceVariant,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(bottom = 3.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .background(AuraSurfaceContainerHighest, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = "Device Storage",
                            tint = AuraTertiary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = stats.technology,
                            color = AuraOnSurface,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Storage Legend Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AuraSecondary))
                        Text(
                            text = "Videos (${stats.videoCount}) ${String.format("%.1f", stats.videoGb)} GB",
                            color = AuraOnSurface,
                            fontSize = 11.sp
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AuraPrimaryContainer))
                        Text(
                            text = "Audio (${stats.audioCount}) ${String.format("%.1f", stats.audioGb)} GB",
                            color = AuraOnSurface,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Library Filter Chips Strip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filterOptions.forEach { filter ->
                val isSelected = filter == selectedFilter
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) AuraPrimaryContainer else AuraSurfaceContainerHigh)
                        .clickable { selectedFilter = filter }
                        .padding(horizontal = 14.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = filter,
                        color = if (isSelected) AuraOnPrimaryContainer else AuraOnSurfaceVariant,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Vault Items List
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (filteredItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(AuraSurfaceContainer)
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storage,
                            contentDescription = null,
                            tint = AuraOnSurfaceVariant,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = "No files in $selectedFilter",
                            color = AuraOnSurfaceVariant,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                filteredItems.forEach { item ->
                    VaultItemRow(
                        item = item,
                        onClick = {
                            repository.playMedia(
                                MediaItem(
                                    id = item.id,
                                    title = item.title,
                                    subtitle = item.subtitle,
                                    category = item.category,
                                    qualityBadge = item.qualityBadge,
                                    fileSize = item.fileSize,
                                    uri = item.uri,
                                    thumbnailUri = if (item.isVideo) item.uri else null,
                                    isVideo = item.isVideo
                                )
                            )
                            onPlayItem()
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(84.dp))
    }
}

@Composable
fun VaultItemRow(
    item: VaultStoredItem,
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
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(AuraSurfaceContainerHighest),
            contentAlignment = Alignment.Center
        ) {
            if (item.isVideo && item.uri.isNotBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(item.uri)
                        .crossfade(true)
                        .build(),
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else if (item.imageResId != 0) {
                Image(
                    painter = painterResource(id = item.imageResId),
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    imageVector = if (item.isVideo) Icons.Default.Movie else Icons.Default.Headphones,
                    contentDescription = null,
                    tint = AuraPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                color = AuraOnSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${item.subtitle} • ${item.fileSize}",
                color = AuraOnSurfaceVariant,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

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
