# Spec Delta: video-player-suite

## Purpose

Provides a comprehensive, immersive playback suite including true fullscreen orientation, touch gesture controls, subtitles, synchronized transcripts, and stream track selection.

## ADDED Requirements

### Requirement: True Immersive Fullscreen Mode
The system SHALL support toggling true immersive fullscreen mode during video playback, rotating the display to landscape and hiding system status and navigation bars.

#### Scenario: User enters fullscreen mode
- **WHEN** the user taps the fullscreen toggle button while video is playing
- **THEN** the activity orientation changes to sensor landscape, system status and navigation bars are hidden, and the video container expands edge-to-edge.

#### Scenario: User exits fullscreen mode
- **WHEN** the user taps the collapse/back button while in fullscreen mode or presses the system back button
- **THEN** the activity orientation restores to portrait mode and system status and navigation bars are restored.

### Requirement: Gesture HUD Controls
The system SHALL provide intuitive on-screen touch gestures with translucent HUD feedback pills for adjusting brightness, volume, and playback scrubbing.

#### Scenario: Vertical drag on left half for brightness
- **WHEN** the user drags vertically on the left half of the player surface
- **THEN** the screen brightness level adjusts proportionally and a brightness HUD pill appears showing the updated percentage.

#### Scenario: Vertical drag on right half for volume
- **WHEN** the user drags vertically on the right half of the player surface
- **THEN** the media volume level adjusts proportionally and a volume HUD pill appears showing the updated percentage.

#### Scenario: Horizontal drag for scrubbing
- **WHEN** the user drags horizontally across the player surface
- **THEN** a scrub HUD pill displays the target timestamp and offset, and playback seeks to that timestamp upon release.

#### Scenario: Double tap to skip
- **WHEN** the user double-taps on the left or right side of the screen
- **THEN** the player skips backward or forward by the configured interval and displays a skip animation badge.

### Requirement: Subtitles and Closed Captions Selection
The system SHALL query available text tracks from the active media and allow users to select subtitles or disable them.

#### Scenario: Selecting a subtitle track
- **WHEN** the user opens the subtitle selector and chooses an available text track
- **THEN** ExoPlayer applies the selected track and renders the subtitles over the video frame in sync with playback.

#### Scenario: Disabling subtitles
- **WHEN** the user selects "Off" in the subtitle selector
- **THEN** subtitle rendering is deactivated without interrupting media playback.

### Requirement: Interactive Synchronized Transcripts
The system SHALL provide an interactive transcript view that highlights the currently spoken line and allows tap-to-seek navigation.

#### Scenario: Auto-scrolling active transcript
- **WHEN** media plays with synchronized transcript data available
- **THEN** the transcript view automatically highlights the active line corresponding to the current playback timestamp and auto-scrolls it into view.

#### Scenario: Tapping a transcript line to seek
- **WHEN** the user taps on any timestamp or line within the transcript view
- **THEN** playback immediately seeks to that line's start timestamp.

### Requirement: Audio and Video Quality Track Selection
The system SHALL expose available alternate audio languages and video stream resolutions for multi-track or adaptive streams.

#### Scenario: Changing audio track
- **WHEN** the user selects an alternate audio track from the tracks menu
- **THEN** playback switches to the selected audio track seamlessly without resetting playback position.
