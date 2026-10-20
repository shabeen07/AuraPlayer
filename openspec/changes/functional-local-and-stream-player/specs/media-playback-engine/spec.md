# Spec Delta

## Purpose

Provides media playback capabilities for both local files and online network streams (audio and video) using ExoPlayer, including transport controls, position tracking, and UI synchronization.

## ADDED Requirements

### Requirement: Local Media Playback
The system SHALL play local audio and video files using AndroidX Media3 ExoPlayer when a user selects an item from the media list. Video items SHALL render on a SurfaceView/PlayerView container, and audio items SHALL render album art or an audio waveform visualizer.

#### Scenario: User selects a local video
- **WHEN** user taps a local video item
- **THEN** system starts video playback, renders video frames, updates transport controls, and reflects duration and position

#### Scenario: User selects a local audio track
- **WHEN** user taps a local audio track
- **THEN** system begins audio playback, displays track title and artist metadata, and activates audio progress tracking

### Requirement: Online Audio and Video Streaming
The system SHALL allow users to input or paste online stream URLs (including HTTP, HTTPS, HLS `.m3u8`, direct MP4, and direct MP3 links) and stream them over the network.

#### Scenario: User enters valid online stream URL
- **WHEN** user inputs a valid stream URL (e.g. HLS or MP4) and taps Play
- **THEN** system prepares and streams the online media stream, showing buffering progress and playing when ready

#### Scenario: Online stream URL is invalid or unreachable
- **WHEN** user attempts to play an invalid or unreachable network URL
- **THEN** system displays an error message indicating that the stream cannot be loaded, without crashing the player

### Requirement: Transport Controls and Seek Tracking
The system SHALL support standard playback controls: Play, Pause, Seek (forward/backward and progress bar scrubbing), 10-second skip, and Next/Previous navigation. Playback progress and buffered status SHALL update in real time.

#### Scenario: User toggles play/pause
- **WHEN** user taps the Play/Pause button during playback
- **THEN** playback toggles state and the UI updates the play/pause icon immediately

#### Scenario: User scrubs the seek bar
- **WHEN** user drags the progress scrubber to a target timestamp
- **THEN** ExoPlayer seeks to the corresponding timestamp and resumes playback from that position

### Requirement: Mini Player Dock Synchronization
The persistent mini player dock on the main screen SHALL display the currently playing media item, show active playback state, allow quick play/pause toggling, and navigate to the full player when tapped.

#### Scenario: Media is playing and user returns to Home screen
- **WHEN** media is active in background/dock and user views the main shell
- **THEN** mini player dock reflects the current track title, artist/category, and play/pause status

#### Scenario: User taps mini player dock
- **WHEN** user taps on the mini player dock
- **THEN** app navigates to the full-screen Player view for the active media item
