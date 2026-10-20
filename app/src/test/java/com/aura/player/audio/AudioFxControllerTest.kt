package com.aura.player.audio

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AudioFxControllerTest {

    private lateinit var controller: AudioFxController

    @Before
    fun setUp() {
        controller = AudioFxController()
    }

    @Test
    fun testDefaultState() {
        val state = controller.equalizerState.value
        assertTrue(state.isEnabled)
        assertEquals("Normal", state.currentPreset)
        assertEquals(5, state.bandLevels.size)
        assertEquals(listOf(0, 0, 0, 0, 0), state.bandLevels)
        assertTrue(state.isBassBoostEnabled)
        assertTrue(state.isSpatialAudioEnabled)
    }

    @Test
    fun testApplyPreset() {
        controller.applyPreset("Bass Boost")
        var state = controller.equalizerState.value
        assertEquals("Bass Boost", state.currentPreset)
        assertEquals(listOf(600, 400, 100, -100, -200), state.bandLevels)

        controller.applyPreset("Cyberpunk")
        state = controller.equalizerState.value
        assertEquals("Cyberpunk", state.currentPreset)
        assertEquals(listOf(500, 200, -200, 400, 600), state.bandLevels)
    }

    @Test
    fun testSetBandLevelBecomesCustom() {
        controller.setBandLevel(0, 350)
        val state = controller.equalizerState.value
        assertEquals("Custom", state.currentPreset)
        assertEquals(350, state.bandLevels[0])
    }

    @Test
    fun testToggleBassBoost() {
        controller.toggleBassBoost(false)
        assertFalse(controller.equalizerState.value.isBassBoostEnabled)

        controller.setBassBoostStrength(800)
        assertEquals(800, controller.equalizerState.value.bassBoostStrength)
    }

    @Test
    fun testToggleEqualizer() {
        controller.toggleEqualizer(false)
        assertFalse(controller.equalizerState.value.isEnabled)

        controller.toggleEqualizer(true)
        assertTrue(controller.equalizerState.value.isEnabled)
    }

    @Test
    fun testToggleSpatialAudio() {
        controller.toggleSpatialAudio(false)
        assertFalse(controller.equalizerState.value.isSpatialAudioEnabled)

        controller.toggleSpatialAudio(true)
        assertTrue(controller.equalizerState.value.isSpatialAudioEnabled)
    }
}
