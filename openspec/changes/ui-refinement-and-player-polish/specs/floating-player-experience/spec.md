# Spec Delta: floating-player-experience

## Purpose

Provides a persistent, visible, and gesture-friendly floating mini-player dock that seamlessly accommodates scrollable screen content without occlusion.

## ADDED Requirements

### Requirement: Mini-Player Visibility and Persistence
The floating mini-player dock MUST remain visible above the bottom navigation bar across all main tabs whenever a media item is active, loaded, or playing.

#### Scenario: Active Media Display in Dock
- **WHEN** user selects a track or stream and navigates between Home, Library, Vault, or Settings
- **THEN** mini-player dock remains visible at the bottom of the screen showing current track title, artist, live progress, and play/pause controls

### Requirement: Scrollable Content Clearance
All scrollable tabs (HomeScreen, LibraryScreen, VaultScreen, SettingsScreen) MUST allocate sufficient dynamic bottom padding so the last list item can be fully scrolled into clear view above the floating player.

#### Scenario: Scrolling to Bottom of List
- **WHEN** user scrolls to the bottom of the media library or settings screen while the mini-player is visible
- **THEN** the bottom-most card or button is fully visible and clickable without being covered by the mini-player dock

### Requirement: Swipe Gestures and Expansion
The mini-player dock SHALL support tapping to smoothly transition to the full player, as well as a horizontal swipe gesture to skip tracks.

#### Scenario: Mini-Player Tap Expansion
- **WHEN** user taps anywhere on the mini-player dock
- **THEN** full PlayerScreen opens with a smooth expansion transition
