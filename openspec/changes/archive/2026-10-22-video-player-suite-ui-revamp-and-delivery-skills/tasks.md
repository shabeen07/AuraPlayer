# Tasks: Video Player Suite, UI Simplification & Delivery Skills

## 1. Video Player Suite & Media3 Engine Enhancement

- [x] 1.1 Update `PlayerState` and `DataRepository` to track `isFullscreen`, `subtitleTracks`, `audioTracks`, `selectedSubtitleTrack`, `selectedAudioTrack`, `transcriptSegments`, and player preferences (verify state compiles).
- [x] 1.2 Implement ExoPlayer track querying and switching in `PlayerController.kt` for `C.TRACK_TYPE_TEXT` (subtitles) and `C.TRACK_TYPE_AUDIO` (alternate audio) (verify track switching methods in controller).
- [x] 1.3 Add interactive transcript data structures and playback synchronization in `PlayerController.kt` with timestamp seek (verify transcript sync unit tests).
- [x] 1.4 Refactor `PlayerScreen.kt` to support true immersive fullscreen mode with `ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE` and system bar hiding via `WindowInsetsControllerCompat` (verify fullscreen mode toggles without crashes).
- [x] 1.5 Implement multi-touch gesture engine on `PlayerScreen.kt` with brightness/volume HUD feedback, scrubbing seek, double-tap skip, and 3-second auto-hide overlay (verify gesture HUD feedback pills).
- [x] 1.6 Implement subtitle track selector modal and live subtitle rendering overlay on `PlayerScreen.kt` (verify subtitles render over video frame).
- [x] 1.7 Implement interactive synchronized transcript bottom drawer in `PlayerScreen.kt` with active segment highlighting and tap-to-seek (verify tap seeks to timestamp).

## 2. Whole-App UI Simplification & Redundancy Removal

- [x] 2.1 Refactor `AuraHeader.kt`: remove mock `CastPickerSheet` and `ProfileDiagnosticsSheet`; produce a clean, modern header with branding and search transition (verify clean header renders without mock dialogs).
- [x] 2.2 Refactor `HomeScreen.kt`: eliminate oversized hero banner, fake badges, and duplicate category chips; introduce a sleek "Continue Playing" compact card, clean 1-line stream player bar, and device media grid (verify streamlined Home layout).
- [x] 2.3 Refactor `LibraryScreen.kt`: clean modern category tabs (`Videos` | `Audio`), responsive search filter, and clean media list items without visual noise (verify search and tab filtering).
- [x] 2.4 Refactor `VaultScreen.kt`: remove faux P2P sync and hardware telemetry; implement clean 4-digit PIN lock screen followed by uncluttered secured media cards (verify PIN unlock and media list).
- [x] 2.5 Refactor `MainShellScreen.kt` and `MiniPlayerDock.kt`: add coordinated bottom content padding across all tab screens to eliminate dock overlap and ensure smooth dismiss and tap-to-expand (verify bottom content is completely visible).

## 3. Player Settings Management

- [x] 3.1 Implement Player & Gestures section in `SettingsScreen.kt` for gesture toggle, double-tap skip intervals (5s/10s/15s/30s), default aspect ratio, and subtitle styling (verify settings persist and take effect during playback).

## 4. Agent Delivery Skills

- [x] 4.1 Create `.agents/skills/deliver-dev/SKILL.md` to trigger `./gradlew appDistributionUploadDevDebug` deploying to Firebase App Distribution for `internal-testers` (`shabeenmhmd07@gmail.com`) (verify skill definition file is present and valid).
- [x] 4.2 Create `.agents/skills/deliver-pro/SKILL.md` to build and verify `./gradlew assembleProRelease` (verify skill definition file is present and valid).

## 5. Build Verification & Regression Testing

- [x] 5.1 Execute `./gradlew.bat test` to verify all unit tests pass with the new player and transcript logic.
- [x] 5.2 Execute `./gradlew.bat assembleDevDebug` to verify complete DEV build succeeds with clean lint and no compilation errors.
