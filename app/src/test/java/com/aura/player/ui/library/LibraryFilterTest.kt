package com.aura.player.ui.library

import com.aura.player.data.model.MediaItem
import org.junit.Assert.assertEquals
import org.junit.Test

class LibraryFilterTest {

    private val sampleItems = listOf(
        MediaItem(
            id = "1",
            title = "Cyberpunk Ep 10",
            subtitle = "Night City",
            durationMs = 1500_000L,
            fileSizeBytes = 500_000_000L,
            isVideo = true
        ),
        MediaItem(
            id = "2",
            title = "Synthwave Chill Track",
            subtitle = "Retro Beats",
            durationMs = 240_000L,
            fileSizeBytes = 12_000_000L,
            isVideo = false
        ),
        MediaItem(
            id = "3",
            title = "Arcane Episode 1",
            subtitle = "Piltover",
            durationMs = 2400_000L,
            fileSizeBytes = 800_000_000L,
            isVideo = true
        )
    )

    @Test
    fun testCategoryFiltering() {
        val videos = sampleItems.filter { it.isVideo }
        val audios = sampleItems.filter { !it.isVideo }

        assertEquals(2, videos.size)
        assertEquals(1, audios.size)
        assertEquals("Synthwave Chill Track", audios[0].title)
    }

    @Test
    fun testKeywordSearch() {
        val query = "retro"
        val filtered = sampleItems.filter {
            it.title.contains(query, ignoreCase = true) || it.subtitle.contains(query, ignoreCase = true)
        }

        assertEquals(1, filtered.size)
        assertEquals("Synthwave Chill Track", filtered[0].title)
    }

    @Test
    fun testSorting() {
        val sortedByTitle = sampleItems.sortedBy { it.title.lowercase() }
        assertEquals("Arcane Episode 1", sortedByTitle[0].title)
        assertEquals("Cyberpunk Ep 10", sortedByTitle[1].title)
        assertEquals("Synthwave Chill Track", sortedByTitle[2].title)

        val sortedBySize = sampleItems.sortedByDescending { it.fileSizeBytes }
        assertEquals("Arcane Episode 1", sortedBySize[0].title)
        assertEquals("Cyberpunk Ep 10", sortedBySize[1].title)
        assertEquals("Synthwave Chill Track", sortedBySize[2].title)
    }
}
