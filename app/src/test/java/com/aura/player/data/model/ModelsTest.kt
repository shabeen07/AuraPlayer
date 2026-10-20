package com.aura.player.data.model

import org.junit.Assert.assertEquals
import org.junit.Test

class ModelsTest {

    @Test
    fun testFormatDuration() {
        assertEquals("00:00", formatDuration(0L))
        assertEquals("00:00", formatDuration(-100L))
        assertEquals("00:05", formatDuration(5_000L))
        assertEquals("01:05", formatDuration(65_000L))
        assertEquals("10:30", formatDuration(630_000L))
        assertEquals("01:01:05", formatDuration(3_665_000L))
        assertEquals("02:46:30", formatDuration(9_990_000L))
    }

    @Test
    fun testFormatFileSize() {
        assertEquals("", formatFileSize(0L))
        assertEquals("", formatFileSize(-10L))
        assertEquals("1.0 KB", formatFileSize(1024L))
        assertEquals("1.0 MB", formatFileSize(1024L * 1024L))
        assertEquals("1.5 GB", formatFileSize((1.5 * 1024 * 1024 * 1024).toLong()))
    }

    @Test
    fun testPlayerStateCalculations() {
        val state = PlayerState(
            currentPositionMs = 30_000L,
            durationMs = 60_000L,
            bufferedPositionMs = 45_000L,
            currentTime = formatDuration(30_000L),
            totalTime = formatDuration(60_000L)
        )

        assertEquals(0.5f, state.computedProgress, 0.001f)
        assertEquals(0.75f, state.computedBufferedProgress, 0.001f)
        assertEquals("00:30", state.currentTime)
        assertEquals("01:00", state.totalTime)
    }

    @Test
    fun testPlayerStateZeroDuration() {
        val state = PlayerState(
            currentPositionMs = 0L,
            durationMs = 0L
        )

        assertEquals(0f, state.computedProgress, 0.001f)
        assertEquals(0f, state.computedBufferedProgress, 0.001f)
        assertEquals("00:00", state.currentTime)
        assertEquals("00:00", state.totalTime)
    }
}
