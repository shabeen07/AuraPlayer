package com.aura.player.data.source

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.os.StatFs
import android.provider.MediaStore
import com.aura.player.data.model.MediaItem
import com.aura.player.data.model.StorageStats
import com.aura.player.data.model.formatDuration
import com.aura.player.data.model.formatFileSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalMediaDataSource(private val context: Context) {

    suspend fun queryAudios(): List<MediaItem> = withContext(Dispatchers.IO) {
        val audioList = mutableListOf<MediaItem>()
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.ALBUM_ID
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} DESC"

        try {
            context.contentResolver.query(uri, projection, selection, null, sortOrder)?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
                val albumIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idCol)
                    val title = cursor.getString(titleCol) ?: "Unknown Track"
                    val artist = cursor.getString(artistCol) ?: "Unknown Artist"
                    val durationMs = cursor.getLong(durationCol)
                    val sizeBytes = cursor.getLong(sizeCol)
                    val albumId = cursor.getLong(albumIdCol)

                    val contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id).toString()
                    val albumArtUri = ContentUris.withAppendedId(
                        Uri.parse("content://media/external/audio/albumart"),
                        albumId
                    ).toString()

                    audioList.add(
                        MediaItem(
                            id = "audio_$id",
                            title = title,
                            subtitle = artist,
                            category = "Audio",
                            duration = formatDuration(durationMs),
                            durationMs = durationMs,
                            qualityBadge = "Lossless Audio",
                            fileSize = formatFileSize(sizeBytes),
                            fileSizeBytes = sizeBytes,
                            uri = contentUri,
                            thumbnailUri = albumArtUri,
                            isVideo = false
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        audioList
    }

    suspend fun queryVideos(): List<MediaItem> = withContext(Dispatchers.IO) {
        val videoList = mutableListOf<MediaItem>()
        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.ARTIST,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.WIDTH,
            MediaStore.Video.Media.HEIGHT
        )
        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"

        try {
            context.contentResolver.query(uri, projection, null, null, sortOrder)?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)
                val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST)
                val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                val widthCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH)
                val heightCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idCol)
                    val title = cursor.getString(titleCol) ?: "Unknown Video"
                    val artist = cursor.getString(artistCol) ?: "Video"
                    val durationMs = cursor.getLong(durationCol)
                    val sizeBytes = cursor.getLong(sizeCol)
                    val width = cursor.getInt(widthCol)
                    val height = cursor.getInt(heightCol)

                    val contentUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id).toString()
                    val resolution = if (width > 0 && height > 0) {
                        when {
                            width >= 3840 || height >= 2160 -> "4K UHD"
                            width >= 1920 || height >= 1080 -> "1080p FHD"
                            width >= 1280 || height >= 720 -> "720p HD"
                            else -> "${width}x${height}"
                        }
                    } else "HD Video"

                    videoList.add(
                        MediaItem(
                            id = "video_$id",
                            title = title,
                            subtitle = if (artist.isNotBlank() && artist != "<unknown>") artist else resolution,
                            category = "Video",
                            duration = formatDuration(durationMs),
                            durationMs = durationMs,
                            qualityBadge = resolution,
                            fileSize = formatFileSize(sizeBytes),
                            fileSizeBytes = sizeBytes,
                            uri = contentUri,
                            thumbnailUri = contentUri, // Coil video decoder loads thumbnail directly from content URI
                            isVideo = true
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        videoList
    }

    fun calculateStorageStats(audios: List<MediaItem>, videos: List<MediaItem>): StorageStats {
        val totalAudioSize = audios.sumOf { it.fileSizeBytes }
        val totalVideoSize = videos.sumOf { it.fileSizeBytes }

        return try {
            val stat = StatFs(Environment.getDataDirectory().path)
            val blockSize = stat.blockSizeLong
            val totalBlocks = stat.blockCountLong
            val availableBlocks = stat.availableBlocksLong

            val totalBytes = totalBlocks * blockSize
            val freeBytes = availableBlocks * blockSize
            val usedBytes = totalBytes - freeBytes

            val totalGb = totalBytes / (1024f * 1024f * 1024f)
            val freeGb = freeBytes / (1024f * 1024f * 1024f)
            val usedGb = usedBytes / (1024f * 1024f * 1024f)
            val videoGb = totalVideoSize / (1024f * 1024f * 1024f)
            val audioGb = totalAudioSize / (1024f * 1024f * 1024f)

            StorageStats(
                audioCount = audios.size,
                videoCount = videos.size,
                totalAudioSizeBytes = totalAudioSize,
                totalVideoSizeBytes = totalVideoSize,
                totalStorageBytes = totalBytes,
                availableStorageBytes = freeBytes,
                usedGb = usedGb,
                totalGb = totalGb,
                videoGb = videoGb,
                audioGb = audioGb,
                cacheGb = 0f,
                freeGb = freeGb,
                technology = "Internal Storage"
            )
        } catch (e: Exception) {
            StorageStats(
                audioCount = audios.size,
                videoCount = videos.size,
                totalAudioSizeBytes = totalAudioSize,
                totalVideoSizeBytes = totalVideoSize
            )
        }
    }
}
