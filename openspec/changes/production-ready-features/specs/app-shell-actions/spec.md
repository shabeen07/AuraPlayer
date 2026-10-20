# Spec Delta: app-shell-actions

## Purpose

Enables interactive header actions for quick global media search, cast playback destination picking, and engine diagnostics profile display.

## ADDED Requirements

### Requirement: Global Quick Search Modal
The system MUST provide a search overlay accessible from the top header search icon to search across all scanned media and streaming stations.

#### Scenario: Searching Media from Header
- **WHEN** user taps search icon in AuraHeader
- **THEN** search bottom sheet opens with search input, instant result filtering, and direct play action

### Requirement: Cast Destination Picker Dialog
The system SHALL provide a Cast dialog accessible from the top header cast icon to select audio/video output destinations or external players.

#### Scenario: Tapping Cast Icon
- **WHEN** user taps the Cast button in AuraHeader
- **THEN** dialog lists available local playback sinks and simulated DLNA / Google Cast targets

### Requirement: Profile and Engine Diagnostics Sheet
The system SHALL display an Aura Engine status dialog when the user avatar is clicked in the header.

#### Scenario: Tapping Profile Avatar
- **WHEN** user taps profile picture in AuraHeader
- **THEN** sheet displays current audio session ID, decoder mode, active pipeline buffer stats, and version
