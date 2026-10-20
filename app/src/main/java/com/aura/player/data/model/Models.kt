package com.aura.player.data.model

import java.util.Locale

enum class MediaType {
    AUDIO,
    VIDEO,
    ONLINE_STREAM
}

data class MediaItem(
    val id: String,
    val title: String,
    val subtitle: String = "",
    val category: String = "Media",
    val duration: String = "",
    val durationMs: Long = 0L,
    val qualityBadge: String = "",
    val fileSize: String = "",
    val fileSizeBytes: Long = 0L,
    val uri: String = "",
    val thumbnailUri: String? = null,
    val imageResId: Int = 0,
    val isLive: Boolean = false,
    val isVideo: Boolean = false,
    val extraTag: String = ""
)

data class DownloadItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val badge: String,
    val progress: Float,
    val speed: String,
    val fileSize: String,
    val imageResId: Int = 0,
    val isPaused: Boolean = false
)

data class VaultStoredItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val qualityBadge: String,
    val fileSize: String,
    val format: String,
    val imageResId: Int = 0,
    val category: String,
    val uri: String = "",
    val isVideo: Boolean = false
)

data class StorageStats(
    val audioCount: Int = 0,
    val videoCount: Int = 0,
    val totalAudioSizeBytes: Long = 0L,
    val totalVideoSizeBytes: Long = 0L,
    val totalStorageBytes: Long = 0L,
    val availableStorageBytes: Long = 0L,
    val usedGb: Float = 0f,
    val totalGb: Float = 128f,
    val videoGb: Float = 0f,
    val audioGb: Float = 0f,
    val cacheGb: Float = 0f,
    val freeGb: Float = 128f,
    val technology: String = "Internal Flash Storage"
)

data class PlayerState(
    val currentMedia: MediaItem? = null,
    val title: String = "No Media Selected",
    val subtitle: String = "",
    val specs: String = "Select a track or enter a stream URL",
    val audioTrack: String = "Default Track",
    val playbackSpeed: Float = 1.0f,
    val playbackSpeedLabel: String = "1.0x",
    val currentTime: String = "00:00",
    val totalTime: String = "00:00",
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val bufferedPositionMs: Long = 0L,
    val progress: Float = 0f,
    val bufferedProgress: Float = 0f,
    val chapterTitle: String = "Playback Engine",
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val isVideo: Boolean = false,
    val brightness: Float = 0.75f,
    val volume: Float = 0.85f,
    val isLocked: Boolean = false,
    val errorMessage: String? = null
) {
    val computedProgress: Float
        get() = if (durationMs > 0) (currentPositionMs.toFloat() / durationMs).coerceIn(0f, 1f) else progress

    val computedBufferedProgress: Float
        get() = if (durationMs > 0) (bufferedPositionMs.toFloat() / durationMs).coerceIn(0f, 1f) else bufferedProgress
}

fun formatDuration(durationMs: Long): String {
    if (durationMs <= 0) return "00:00"
    val totalSeconds = durationMs / 1000
    val seconds = totalSeconds % 60
    val minutes = (totalSeconds / 60) % 60
    val hours = totalSeconds / 3600
    return if (hours > 0) {
        String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }
}

fun formatFileSize(bytes: Long): String {
    if (bytes <= 0) return ""
    val kb = bytes / 1024.0
    val mb = kb / 1024.0
    val gb = mb / 1024.0
    return when {
        gb >= 1.0 -> String.format(Locale.US, "%.1f GB", gb)
        mb >= 1.0 -> String.format(Locale.US, "%.1f MB", mb)
        kb >= 1.0 -> String.format(Locale.US, "%.1f KB", kb)
        else -> "$bytes B"
    }
}
