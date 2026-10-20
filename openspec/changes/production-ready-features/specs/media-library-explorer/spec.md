# Spec Delta: media-library-explorer

## Purpose

Enables organized browsing, filtering, and sorting of local device audio, video, playlists, and folders within a dedicated Library tab.

## ADDED Requirements

### Requirement: Structured Library Categorization
The system MUST provide filtered media tabs for All Media, Videos, Audios, and Folders on the Library screen.

#### Scenario: Switching Library Tabs
- **WHEN** user selects the "Videos" category chip in the Library tab
- **THEN** system displays only local video media items with duration and resolution badges

### Requirement: Media Sorting and Search Filter
The Library screen SHALL support sorting media items by Title, Date Added, and File Size, with real-time text query filtering.

#### Scenario: Filtering Media Items by Keyword
- **WHEN** user enters a search query in the library filter bar
- **THEN** list dynamically filters items matching title or subtitle without full screen reload

### Requirement: Direct Playback Ingestion
Selecting any item from the Library SHALL immediately trigger playback in the Media3 engine and display the MiniPlayerDock.

#### Scenario: Item Tap from Library
- **WHEN** user taps a media card in the library list
- **THEN** Media3 engine begins playing the selected track and the MiniPlayerDock updates its title and state
