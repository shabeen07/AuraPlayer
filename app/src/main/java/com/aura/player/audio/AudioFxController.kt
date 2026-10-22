package com.aura.player.audio

import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.Virtualizer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class EqualizerState(
    val isEnabled: Boolean = true,
    val bandLevels: List<Int> = listOf(0, 0, 0, 0, 0), // millibels (-1500 to 1500 mB)
    val bandFrequencies: List<String> = listOf("60 Hz", "230 Hz", "910 Hz", "3.6 kHz", "14 kHz"),
    val currentPreset: String = "Normal",
    val bassBoostStrength: Int = 300, // 0 - 1000
    val isBassBoostEnabled: Boolean = true,
    val isSpatialAudioEnabled: Boolean = true,
    val virtualizerStrength: Int = 400 // 0 - 1000
)

class AudioFxController {

    private val _equalizerState = MutableStateFlow(EqualizerState())
    val equalizerState: StateFlow<EqualizerState> = _equalizerState.asStateFlow()

    private var currentAudioSessionId: Int = 0
    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null
    private var virtualizer: Virtualizer? = null

    val presets = mapOf(
        "Normal" to listOf(0, 0, 0, 0, 0),
        "Bass Boost" to listOf(600, 400, 100, -100, -200),
        "Cyberpunk" to listOf(500, 200, -200, 400, 600),
        "Rock" to listOf(400, 200, -100, 300, 500),
        "Vocal" to listOf(-200, 100, 500, 300, -100),
        "Electronic" to listOf(500, 300, 0, 200, 500)
    )

    fun attachAudioSession(audioSessionId: Int) {
        if (audioSessionId <= 0 || audioSessionId == currentAudioSessionId) return
        currentAudioSessionId = audioSessionId

        releaseEffects()

        try {
            equalizer = Equalizer(0, audioSessionId).apply {
                enabled = _equalizerState.value.isEnabled
                val numBands = numberOfBands.toInt().coerceAtMost(5)
                for (i in 0 until numBands) {
                    val level = _equalizerState.value.bandLevels.getOrElse(i) { 0 }.toShort()
                    setBandLevel(i.toShort(), level)
                }
            }
        } catch (e: Exception) {
            try {
                com.google.firebase.crashlytics.FirebaseCrashlytics.getInstance().recordException(e)
            } catch (_: Exception) {}
            Log.w("AudioFxController", "Equalizer init warning: ${e.message}")
        }

        try {
            bassBoost = BassBoost(0, audioSessionId).apply {
                enabled = _equalizerState.value.isBassBoostEnabled
                setStrength(_equalizerState.value.bassBoostStrength.toShort())
            }
        } catch (e: Exception) {
            Log.w("AudioFxController", "BassBoost init warning: ${e.message}")
        }

        try {
            virtualizer = Virtualizer(0, audioSessionId).apply {
                enabled = _equalizerState.value.isSpatialAudioEnabled
                setStrength(_equalizerState.value.virtualizerStrength.toShort())
            }
        } catch (e: Exception) {
            Log.w("AudioFxController", "Virtualizer init warning: ${e.message}")
        }
    }

    fun setBandLevel(bandIndex: Int, levelMb: Int) {
        val currentLevels = _equalizerState.value.bandLevels.toMutableList()
        if (bandIndex in currentLevels.indices) {
            currentLevels[bandIndex] = levelMb
            _equalizerState.value = _equalizerState.value.copy(
                bandLevels = currentLevels,
                currentPreset = "Custom"
            )

            try {
                equalizer?.setBandLevel(bandIndex.toShort(), levelMb.toShort())
            } catch (e: Exception) {
                Log.w("AudioFxController", "Failed to set band level: ${e.message}")
            }
        }
    }

    fun applyPreset(presetName: String) {
        val levels = presets[presetName] ?: return
        _equalizerState.value = _equalizerState.value.copy(
            bandLevels = levels,
            currentPreset = presetName
        )

        try {
            levels.forEachIndexed { index, level ->
                equalizer?.setBandLevel(index.toShort(), level.toShort())
            }
        } catch (e: Exception) {
            Log.w("AudioFxController", "Failed to apply preset: ${e.message}")
        }
    }

    fun setBassBoostStrength(strength: Int) {
        _equalizerState.value = _equalizerState.value.copy(bassBoostStrength = strength)
        try {
            bassBoost?.setStrength(strength.toShort())
        } catch (e: Exception) {
            Log.w("AudioFxController", "Failed to set bass boost: ${e.message}")
        }
    }

    fun toggleBassBoost(enabled: Boolean) {
        _equalizerState.value = _equalizerState.value.copy(isBassBoostEnabled = enabled)
        try {
            bassBoost?.enabled = enabled
        } catch (e: Exception) {
            Log.w("AudioFxController", "Failed to toggle bass boost: ${e.message}")
        }
    }

    fun toggleSpatialAudio(enabled: Boolean) {
        _equalizerState.value = _equalizerState.value.copy(isSpatialAudioEnabled = enabled)
        try {
            virtualizer?.enabled = enabled
        } catch (e: Exception) {
            Log.w("AudioFxController", "Failed to toggle spatial audio: ${e.message}")
        }
    }

    fun toggleEqualizer(enabled: Boolean) {
        _equalizerState.value = _equalizerState.value.copy(isEnabled = enabled)
        try {
            equalizer?.enabled = enabled
        } catch (e: Exception) {
            Log.w("AudioFxController", "Failed to toggle equalizer: ${e.message}")
        }
    }

    private fun releaseEffects() {
        try {
            equalizer?.release()
            bassBoost?.release()
            virtualizer?.release()
        } catch (e: Exception) {
            Log.w("AudioFxController", "Failed releasing effects: ${e.message}")
        } finally {
            equalizer = null
            bassBoost = null
            virtualizer = null
        }
    }
}
