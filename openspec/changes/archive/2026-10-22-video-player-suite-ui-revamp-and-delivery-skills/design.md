# Design: Video Player Suite, UI Simplification & Delivery Skills

## Context

AuraPlayer uses Media3 ExoPlayer with Jetpack Compose. Currently, `PlayerScreen.kt` renders video inside a static 16:10 box without fullscreen support, gesture detection, text track rendering, or transcript synchronization. Screens across the app (`HomeScreen.kt`, `LibraryScreen.kt`, `VaultScreen.kt`, `AuraHeader.kt`) suffer from duplicate controls, mock sheets (`CastPickerSheet`, `ProfileDiagnosticsSheet`), and layout collision with `MiniPlayerDock.kt`.

## Goals / Non-Goals

**Goals:**
- Provide true immersive fullscreen video playback with landscape auto-rotation and system bar management.
- Implement responsive multi-touch gestures (brightness on left, volume on right, scrubbing seek, double-tap skip) with sleek HUD feedback pills.
- Support subtitle / closed-caption track selection and rendering via ExoPlayer track parameters.
- Build an interactive transcript drawer synchronized with playback timestamps, enabling tap-to-seek navigation.
- Strip out unwanted and redundant UI components, mock sheets, and cluttered badges to create a clean, modern aesthetic across all screens.
- Introduce customizable player settings in `SettingsScreen.kt`.
- Create callable agent skills (`deliver-dev` and `deliver-pro`) in `.agents/skills/`.

**Non-Goals:**
- Implementing actual Google Cast SDK casting protocol (mock sheet is being removed, not replaced with external SDK).
- Adding remote cloud sync to Vault (Vault remains purely local and secure).

## Decisions

### 1. Fullscreen System Bar & Orientation Management
- **Decision**: Manage orientation and system insets via Android `WindowInsetsControllerCompat` and `Activity.requestedOrientation` triggered by an `isFullscreen` state in `PlayerState`.
- **Rationale**: Provides native immersive Android behavior. When entering fullscreen, status and navigation bars hide (`BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE`) and orientation switches to `SCREEN_ORIENTATION_SENSOR_LANDSCAPE`. Exiting restores portrait orientation and shows system bars.
- **Alternatives Considered**: Keeping portrait-only fullscreen; rejected because user specifically requested full-screen landscape video.

### 2. Gesture Engine & HUD Overlay
- **Decision**: Use Compose `Modifier.pointerInput` with `detectVerticalDragGestures`, `detectHorizontalDragGestures`, and `detectTapGestures` on the player overlay:
  - Left half vertical drag: Window screen brightness adjustment (0.0f - 1.0f).
  - Right half vertical drag: Device media volume adjustment via Android `AudioManager`.
  - Horizontal drag: Playback scrub offset with timestamp preview.
  - Double tap: Left side skips back, right side skips forward.
  - Single tap: Toggles transport controls visibility with a 3-second auto-hide timer.
- **Rationale**: Follows standard modern player conventions (like VLC and MX Player) while keeping UI clutter off the video frame.

### 3. Subtitles & Track Selection via Media3
- **Decision**: Leverage ExoPlayer's `Tracks` and `TrackSelectionParameters`:
  - Query text tracks (`C.TRACK_TYPE_TEXT`) to display available languages or disabled ("Off").
  - Query audio tracks (`C.TRACK_TYPE_AUDIO`) for alternate audio stream selection.
- **Rationale**: Native Media3 track management avoids custom parser overhead and automatically supports embedded WebVTT, SRT, and CEA-608/708 captions.

### 4. Interactive Synchronized Transcripts
- **Decision**: Model transcripts as `List<TranscriptSegment>` where each segment has `id`, `startMs`, `endMs`, and `text`. Observe `PlayerState.currentPositionMs` to highlight the active line and auto-scroll the `LazyListState`. Tapping any segment triggers `PlayerController.seekTo(segment.startMs)`.
- **Rationale**: Provides an interactive lyrics/transcript experience with timestamp seeking for educational, podcasts, and video content.

### 5. UI Simplification & Layout Coordination
- **Decision**:
  - `AuraHeader`: Remove `CastPickerSheet` and `ProfileDiagnosticsSheet`. Keep clean brand logo, subtitle, and instant search button.
  - `HomeScreen`: Remove redundant 1,000-line clutter; show compact "Resume Playing" card, 1-line stream player bar, and clean device media cards.
  - `LibraryScreen`: Clean segment buttons (`Videos` | `Audio`), responsive search, and clean cards without redundant badges.
  - `VaultScreen`: Simple 4-digit PIN lock screen followed by clean secured media cards.
  - `MainShellScreen`: Pass `contentPadding` (or include bottom padding of 72.dp when `MiniPlayerDock` is visible) so lists can scroll fully above the mini-player without being obscured.

### 6. Player Settings Integration
- **Decision**: Add a dedicated "Player & Gestures" section to `SettingsScreen.kt` and `DataRepository` to manage:
  - Gestures enabled/disabled.
  - Skip interval (5s, 10s, 15s, 30s).
  - Default video resize mode (`RESIZE_MODE_FIT`, `RESIZE_MODE_ZOOM`, `RESIZE_MODE_FILL`).
  - Subtitle styling.

### 7. Agent Delivery Skills
- **Decision**: Define `.agents/skills/deliver-dev/SKILL.md` and `.agents/skills/deliver-pro/SKILL.md`:
  - `deliver-dev`: Executes `./gradlew appDistributionUploadDevDebug` deploying to Firebase App Distribution for `internal-testers` (`shabeenmhmd07@gmail.com`).
  - `deliver-pro`: Executes `./gradlew assembleProRelease` validating production build readiness.

## Risks / Trade-offs

- **[Risk] Orientation Changes on Configuration Changes**: Orientation switches can cause Compose activity recreation if not configured properly.
  - *Mitigation*: `MainActivity` already has `configChanges = "orientation|screenSize|screenLayout|keyboardHidden"`, ensuring smooth in-place composable re-layout without recreation.
- **[Risk] Volume Permission & Hardware Keys**: Modifying system volume directly may conflict with hardware volume keys.
  - *Mitigation*: Adjust `AudioManager.STREAM_MUSIC` volume in increments without UI flag flags (`FLAG_SHOW_UI`) so our custom HUD pill displays cleanly.
