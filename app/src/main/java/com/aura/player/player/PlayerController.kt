package com.aura.player.player

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem as ExoMediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.aura.player.audio.AudioFxController
import com.aura.player.data.model.MediaItem
import com.aura.player.data.model.PlayerState
import com.aura.player.data.model.formatDuration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class PlayerController(
    context: Context,
    val audioFxController: AudioFxController = AudioFxController()
) {

    private val applicationContext = context.applicationContext
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var progressJob: Job? = null

    val exoPlayer: ExoPlayer = ExoPlayer.Builder(applicationContext)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .setUsage(C.USAGE_MEDIA)
                .build(),
            /* handleAudioFocus = */ true
        )
        .build()

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    init {
        exoPlayer.addListener(object : Player.Listener {
            override fun onAudioSessionIdChanged(audioSessionId: Int) {
                audioFxController.attachAudioSession(audioSessionId)
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        _playerState.value = _playerState.value.copy(
                            isBuffering = true,
                            errorMessage = null
                        )
                    }
                    Player.STATE_READY -> {
                        val duration = exoPlayer.duration.coerceAtLeast(0L)
                        _playerState.value = _playerState.value.copy(
                            isBuffering = false,
                            durationMs = duration,
                            totalTime = formatDuration(duration),
                            errorMessage = null
                        )
                    }
                    Player.STATE_ENDED -> {
                        _playerState.value = _playerState.value.copy(
                            isPlaying = false,
                            isBuffering = false
                        )
                    }
                    Player.STATE_IDLE -> {
                        _playerState.value = _playerState.value.copy(
                            isBuffering = false
                        )
                    }
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _playerState.value = _playerState.value.copy(isPlaying = isPlaying)
                if (isPlaying) {
                    startPositionPolling()
                } else {
                    stopPositionPolling()
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                val errorMsg = error.localizedMessage ?: "Playback error: ${error.errorCodeName}"
                _playerState.value = _playerState.value.copy(
                    isBuffering = false,
                    isPlaying = false,
                    errorMessage = errorMsg
                )
            }
        })
    }

    private fun startPositionPolling() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                val pos = exoPlayer.currentPosition.coerceAtLeast(0L)
                val dur = exoPlayer.duration.coerceAtLeast(0L)
                val buf = exoPlayer.bufferedPosition.coerceAtLeast(0L)

                val progress = if (dur > 0) (pos.toFloat() / dur).coerceIn(0f, 1f) else 0f
                val bufProgress = if (dur > 0) (buf.toFloat() / dur).coerceIn(0f, 1f) else 0f

                _playerState.value = _playerState.value.copy(
                    currentPositionMs = pos,
                    durationMs = dur,
                    bufferedPositionMs = buf,
                    progress = progress,
                    bufferedProgress = bufProgress,
                    currentTime = formatDuration(pos),
                    totalTime = formatDuration(dur)
                )
                delay(250)
            }
        }
    }

    private fun stopPositionPolling() {
        progressJob?.cancel()
        val pos = exoPlayer.currentPosition.coerceAtLeast(0L)
        val dur = exoPlayer.duration.coerceAtLeast(0L)
        val progress = if (dur > 0) (pos.toFloat() / dur).coerceIn(0f, 1f) else 0f

        _playerState.value = _playerState.value.copy(
            currentPositionMs = pos,
            durationMs = dur,
            progress = progress,
            currentTime = formatDuration(pos),
            totalTime = formatDuration(dur)
        )
    }

    fun playMedia(media: MediaItem) {
        if (media.uri.isBlank()) return

        val exoItem = ExoMediaItem.Builder()
            .setUri(media.uri)
            .setMediaId(media.id)
            .build()

        exoPlayer.setMediaItem(exoItem)
        exoPlayer.prepare()
        exoPlayer.play()

        _playerState.value = _playerState.value.copy(
            currentMedia = media,
            title = media.title,
            subtitle = media.subtitle.ifBlank { media.category },
            specs = "${media.qualityBadge.ifBlank { "HQ" }} • ${media.fileSize.ifBlank { if (media.isLive) "LIVE STREAM" else "Direct Play" }}",
            audioTrack = if (media.isVideo) "Auto • Stereo / Surround" else "Lossless Stereo",
            isVideo = media.isVideo,
            chapterTitle = if (media.isLive) "Live Stream" else if (media.isVideo) "Video Playback" else "Audio Playback",
            errorMessage = null,
            progress = 0f,
            currentPositionMs = 0L
        )
    }

    fun playStreamUrl(url: String, title: String? = null, isVideo: Boolean = true) {
        val trimmed = url.trim()
        if (trimmed.isBlank()) return

        val displayTitle = title ?: trimmed.substringAfterLast("/").substringBefore("?").ifBlank { "Online Stream" }
        val streamMedia = MediaItem(
            id = "stream_${System.currentTimeMillis()}",
            title = displayTitle,
            subtitle = trimmed,
            category = if (isVideo) "Video Stream" else "Audio Stream",
            qualityBadge = if (trimmed.contains(".m3u8", ignoreCase = true)) "HLS Adaptive" else "Direct Stream",
            fileSize = "Network Stream",
            uri = trimmed,
            thumbnailUri = null,
            isLive = true,
            isVideo = isVideo
        )

        playMedia(streamMedia)
    }

    fun togglePlayPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            if (exoPlayer.playbackState == Player.STATE_IDLE && _playerState.value.currentMedia != null) {
                _playerState.value.currentMedia?.let { playMedia(it) }
            } else {
                exoPlayer.play()
            }
        }
    }

    fun seekTo(positionMs: Long) {
        val target = positionMs.coerceIn(0L, exoPlayer.duration.coerceAtLeast(0L))
        exoPlayer.seekTo(target)
        _playerState.value = _playerState.value.copy(
            currentPositionMs = target,
            currentTime = formatDuration(target),
            progress = if (exoPlayer.duration > 0) (target.toFloat() / exoPlayer.duration).coerceIn(0f, 1f) else 0f
        )
    }

    fun seekToProgress(progress: Float) {
        val dur = exoPlayer.duration
        if (dur > 0) {
            val target = (dur * progress.coerceIn(0f, 1f)).toLong()
            seekTo(target)
        }
    }

    fun skipForward(seconds: Long = 10) {
        val dur = exoPlayer.duration
        val target = (exoPlayer.currentPosition + seconds * 1000).coerceAtMost(if (dur > 0) dur else Long.MAX_VALUE)
        exoPlayer.seekTo(target)
    }

    fun skipBackward(seconds: Long = 10) {
        val target = (exoPlayer.currentPosition - seconds * 1000).coerceAtLeast(0L)
        exoPlayer.seekTo(target)
    }

    fun setPlaybackSpeed(speed: Float) {
        val clamped = speed.coerceIn(0.25f, 3.0f)
        exoPlayer.setPlaybackSpeed(clamped)
        _playerState.value = _playerState.value.copy(
            playbackSpeed = clamped,
            playbackSpeedLabel = "${clamped}x"
        )
    }

    fun updateVolume(volume: Float) {
        val clamped = volume.coerceIn(0f, 1f)
        exoPlayer.volume = clamped
        _playerState.value = _playerState.value.copy(volume = clamped)
    }

    fun updateBrightness(brightness: Float) {
        _playerState.value = _playerState.value.copy(brightness = brightness.coerceIn(0f, 1f))
    }

    fun setLocked(locked: Boolean) {
        _playerState.value = _playerState.value.copy(isLocked = locked)
    }

    fun release() {
        stopPositionPolling()
        exoPlayer.release()
    }
}
