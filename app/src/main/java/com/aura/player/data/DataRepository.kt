package com.aura.player.data

import android.content.Context
import com.aura.player.R
import com.aura.player.data.model.DownloadItem
import com.aura.player.data.model.MediaItem
import com.aura.player.data.model.MediaTrackInfo
import com.aura.player.data.model.PlayerPreferences
import com.aura.player.data.model.PlayerState
import com.aura.player.data.model.StorageStats
import com.aura.player.data.model.TranscriptSegment
import com.aura.player.data.model.VaultStoredItem
import com.aura.player.data.source.LocalMediaDataSource
import com.aura.player.player.PlayerController
import com.aura.player.util.PermissionUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

interface DataRepository {
    val localAudios: StateFlow<List<MediaItem>>
    val localVideos: StateFlow<List<MediaItem>>
    val allMedia: StateFlow<List<MediaItem>>
    val downloadedMedia: List<MediaItem>
    val trendingChannels: List<MediaItem>
    val activeDownloads: StateFlow<List<DownloadItem>>
    val vaultItems: StateFlow<List<VaultStoredItem>>
    val storageStats: StateFlow<StorageStats>
    val playerState: StateFlow<PlayerState>
    val playerController: PlayerController
    val audioFxController: com.aura.player.audio.AudioFxController
    val hasStoragePermission: StateFlow<Boolean>

    suspend fun refreshLocalMedia()
    fun checkPermissionAndUpdate()
    fun playMedia(media: MediaItem)
    fun playStreamUrl(url: String, title: String? = null, isVideo: Boolean = true)
    fun togglePlayPause()
    fun stopPlayback()
    fun setPlaybackProgress(progress: Float)
    fun seekTo(positionMs: Long)
    fun skipForward(seconds: Long = 10)
    fun skipBackward(seconds: Long = 10)
    fun updateVolume(volume: Float)
    fun updateBrightness(brightness: Float)
    fun setPlaybackSpeed(speed: Float)
    fun toggleFullscreen(enabled: Boolean? = null)
    fun selectSubtitleTrack(track: MediaTrackInfo?)
    fun selectAudioTrack(track: MediaTrackInfo)
    fun selectVideoTrack(track: MediaTrackInfo?)
    fun updatePlayerPreferences(preferences: PlayerPreferences)
    fun loadTranscripts(segments: List<TranscriptSegment>)
    fun toggleDownloadPause(id: String)
    fun removeDownload(id: String)
}

class DefaultDataRepository(private val context: Context) : DataRepository {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val localDataSource = LocalMediaDataSource(context)
    override val playerController: PlayerController = PlayerController(context)
    override val audioFxController: com.aura.player.audio.AudioFxController = playerController.audioFxController

    private val _hasStoragePermission = MutableStateFlow(PermissionUtils.hasStoragePermission(context))
    override val hasStoragePermission: StateFlow<Boolean> = _hasStoragePermission.asStateFlow()

    private val _localAudios = MutableStateFlow<List<MediaItem>>(emptyList())
    override val localAudios: StateFlow<List<MediaItem>> = _localAudios.asStateFlow()

    private val _localVideos = MutableStateFlow<List<MediaItem>>(emptyList())
    override val localVideos: StateFlow<List<MediaItem>> = _localVideos.asStateFlow()

    override val allMedia: StateFlow<List<MediaItem>> = combine(_localAudios, _localVideos) { audios, videos ->
        (videos + audios)
    }.stateIn(scope, SharingStarted.Eagerly, emptyList())

    override val downloadedMedia: List<MediaItem>
        get() = allMedia.value

    override val trendingChannels: List<MediaItem> = listOf(
        MediaItem(
            id = "stream_hls_demo",
            title = "Big Buck Bunny (HLS Live/VOD)",
            subtitle = "Adaptive Mux Stream",
            category = "Video Stream",
            duration = "LIVE",
            qualityBadge = "1080p HLS",
            fileSize = "Adaptive Rate",
            uri = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8",
            imageResId = R.drawable.img_arcane,
            isLive = true,
            isVideo = true,
            extraTag = "MPEG-HLS"
        ),
        MediaItem(
            id = "stream_radio_groovesalad",
            title = "Groove Salad Ambient Lounge",
            subtitle = "SomaFM Independent Radio",
            category = "Radio",
            duration = "24/7 STREAM",
            qualityBadge = "128k MP3",
            fileSize = "Continuous Stream",
            uri = "https://ice1.somafm.com/groovesalad-128-mp3",
            imageResId = R.drawable.img_synthwave,
            isLive = true,
            isVideo = false,
            extraTag = "Lossless Stream"
        ),
        MediaItem(
            id = "stream_radio_nightride",
            title = "Chillsynth & Retrowave Radio",
            subtitle = "NightRide FM HQ",
            category = "Radio",
            duration = "24/7 STREAM",
            qualityBadge = "HQ AAC",
            fileSize = "Continuous Stream",
            uri = "https://stream.nightride.fm/nightride.m4a",
            imageResId = R.drawable.img_podcast,
            isLive = true,
            isVideo = false,
            extraTag = "Bit-Perfect"
        )
    )

    private val _storageStats = MutableStateFlow(StorageStats())
    override val storageStats: StateFlow<StorageStats> = _storageStats.asStateFlow()

    private val _activeDownloads = MutableStateFlow<List<DownloadItem>>(emptyList())
    override val activeDownloads: StateFlow<List<DownloadItem>> = _activeDownloads.asStateFlow()

    override val vaultItems: StateFlow<List<VaultStoredItem>> = combine(_localAudios, _localVideos) { audios, videos ->
        val items = mutableListOf<VaultStoredItem>()
        videos.forEach { video ->
            items.add(
                VaultStoredItem(
                    id = video.id,
                    title = video.title,
                    subtitle = video.subtitle,
                    qualityBadge = video.qualityBadge,
                    fileSize = video.fileSize,
                    format = "Video",
                    category = "Movies & Videos",
                    uri = video.uri,
                    isVideo = true,
                    duration = video.duration,
                    thumbnailUri = video.thumbnailUri
                )
            )
        }
        audios.forEach { audio ->
            items.add(
                VaultStoredItem(
                    id = audio.id,
                    title = audio.title,
                    subtitle = audio.subtitle,
                    qualityBadge = audio.qualityBadge,
                    fileSize = audio.fileSize,
                    format = "Audio",
                    category = "Audio & Music",
                    uri = audio.uri,
                    isVideo = false,
                    duration = audio.duration,
                    thumbnailUri = audio.thumbnailUri
                )
            )
        }
        items
    }.stateIn(scope, SharingStarted.Eagerly, emptyList())

    override val playerState: StateFlow<PlayerState> = playerController.playerState

    init {
        checkPermissionAndUpdate()
    }

    override fun checkPermissionAndUpdate() {
        val granted = PermissionUtils.hasStoragePermission(context)
        _hasStoragePermission.value = granted
        if (granted) {
            scope.launch {
                refreshLocalMedia()
            }
        }
    }

    override suspend fun refreshLocalMedia() {
        val audios = localDataSource.queryAudios()
        val videos = localDataSource.queryVideos()
        _localAudios.value = audios
        _localVideos.value = videos
        _storageStats.value = localDataSource.calculateStorageStats(audios, videos)
    }

    override fun playMedia(media: MediaItem) {
        playerController.playMedia(media)
    }

    override fun playStreamUrl(url: String, title: String?, isVideo: Boolean) {
        playerController.playStreamUrl(url, title, isVideo)
    }

    override fun togglePlayPause() {
        playerController.togglePlayPause()
    }

    override fun stopPlayback() {
        playerController.stopPlayback()
    }

    override fun setPlaybackProgress(progress: Float) {
        playerController.seekToProgress(progress)
    }

    override fun seekTo(positionMs: Long) {
        playerController.seekTo(positionMs)
    }

    override fun skipForward(seconds: Long) {
        playerController.skipForward(seconds)
    }

    override fun skipBackward(seconds: Long) {
        playerController.skipBackward(seconds)
    }

    override fun updateVolume(volume: Float) {
        playerController.updateVolume(volume)
    }

    override fun updateBrightness(brightness: Float) {
        playerController.updateBrightness(brightness)
    }

    override fun setPlaybackSpeed(speed: Float) {
        playerController.setPlaybackSpeed(speed)
    }

    override fun toggleFullscreen(enabled: Boolean?) {
        playerController.toggleFullscreen(enabled)
    }

    override fun selectSubtitleTrack(track: MediaTrackInfo?) {
        playerController.selectSubtitleTrack(track)
    }

    override fun selectAudioTrack(track: MediaTrackInfo) {
        playerController.selectAudioTrack(track)
    }

    override fun selectVideoTrack(track: MediaTrackInfo?) {
        playerController.selectVideoTrack(track)
    }

    override fun updatePlayerPreferences(preferences: PlayerPreferences) {
        playerController.updatePlayerPreferences(preferences)
    }

    override fun loadTranscripts(segments: List<TranscriptSegment>) {
        playerController.loadTranscripts(segments)
    }

    override fun toggleDownloadPause(id: String) {
        _activeDownloads.value = _activeDownloads.value.map {
            if (it.id == id) it.copy(isPaused = !it.isPaused) else it
        }
    }

    override fun removeDownload(id: String) {
        _activeDownloads.value = _activeDownloads.value.filter { it.id != id }
    }
}
