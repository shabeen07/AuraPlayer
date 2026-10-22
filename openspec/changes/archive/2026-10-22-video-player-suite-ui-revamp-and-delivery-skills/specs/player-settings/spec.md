# Spec Delta: player-settings

## Purpose

Provides dedicated user preferences to customize gesture controls, seek skip intervals, aspect ratio defaults, and subtitle styling.

## ADDED Requirements

### Requirement: Gesture Controls Preference
The system SHALL allow users to enable or disable touch gestures for brightness and volume control on the player screen.

#### Scenario: Disabling gestures
- **WHEN** the user disables player gesture swiping in Settings
- **THEN** dragging on the player surface will not alter device brightness or system media volume.

### Requirement: Skip Interval Configuration
The system SHALL provide configurable double-tap skip intervals of 5, 10, 15, and 30 seconds.

#### Scenario: Changing skip interval
- **WHEN** the user selects a 15-second skip interval in Settings
- **THEN** double-tapping during playback skips forward or backward by 15 seconds.

### Requirement: Default Aspect Ratio Preference
The system SHALL allow users to select their preferred default video resize mode (Fit, Zoom/Crop, Stretch).

#### Scenario: Applying default aspect ratio
- **WHEN** a new video starts playing
- **THEN** the video container applies the user's preferred default resize mode automatically.

### Requirement: Subtitle Customization
The system SHALL provide customization options for subtitle font size and styling.

#### Scenario: Adjusting subtitle text size
- **WHEN** the user selects a larger subtitle font size in Settings
- **THEN** rendered subtitles update to the specified scale factor.
