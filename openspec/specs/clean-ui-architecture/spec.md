# clean-ui-architecture Specification

## Purpose

Enforces a clean, uncluttered, and stable user interface across all screens by eliminating redundant dialogs, fake gauges, and layout collisions.

## Requirements

### Requirement: Streamlined Top App Bar
The system SHALL display a clean, uncluttered top app bar across the application without non-functional or mock diagnostic and casting modals.

#### Scenario: Displaying primary header
- **WHEN** the user views any primary top-level tab (Home, Library, Vault, Settings)
- **THEN** the top app bar displays the AuraPlayer brand logo, active section title, and an instant search shortcut without pseudo-hardware diagnostic sheets.

### Requirement: Clean Home Screen Layout
The system SHALL present a focused, minimal home feed featuring an optional sleek resume-playing card, a one-line stream URL player, and a device media grid.

#### Scenario: Active playback resume card
- **WHEN** media is loaded or paused in the player controller
- **THEN** the Home screen displays a sleek, compact "Now Playing" card with media artwork, title, and quick transport controls.

#### Scenario: Online stream ingest
- **WHEN** the user pastes a stream URL into the streamlined stream player bar and submits
- **THEN** the stream begins playing immediately and transitions smoothly to the player.

### Requirement: Uncluttered Library Screen
The system SHALL present a clean library screen organized by media category tabs with real-time responsive filtering.

#### Scenario: Category tab filtering
- **WHEN** the user switches between "Videos" and "Audio" tabs
- **THEN** the media list updates immediately to show only the selected media type with title, duration, and file size.

### Requirement: Private Media Vault UI
The system SHALL protect secured media behind a clean 4-digit PIN lock screen and display an uncluttered private vault interface.

#### Scenario: Unlocking the vault
- **WHEN** the user enters the correct 4-digit PIN on the vault lock screen
- **THEN** the vault unlocks immediately and displays the secure media collection with an import action and lock button.

### Requirement: Collision-Free Mini Player Dock
The system SHALL coordinate content insets across all scrollable tab screens to prevent the floating mini-player dock from obscuring content.

#### Scenario: Mini-player visible over list content
- **WHEN** the floating mini-player is visible at the bottom of the screen
- **THEN** the bottom content padding of scrollable tabs increases so all items can be scrolled completely above the dock.
