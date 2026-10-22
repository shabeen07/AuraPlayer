# Proposal: Video Player Suite, UI Simplification & Delivery Skills

## Why

AuraPlayer currently lacks modern video player essentials—such as true immersive fullscreen, multi-touch gesture HUD controls, closed captions/subtitles selection, interactive synchronized transcripts, and audio/video track pickers. Furthermore, the existing user interface suffers from visual clutter, repetitive mock sheets (e.g., non-functional cast and diagnostics modals), congested 1,000-line home layouts, redundant badges, and floating mini-player dock overlaps. Finally, developers lack convenient agent skills to deliver DEV builds directly to Firebase App Distribution and build optimized PRO release artifacts with single-action execution.

This change delivers a complete video player suite, strips out unwanted and redundant UI elements across the entire app for a clean, cohesive user experience, introduces granular player settings, and provides dedicated agent delivery skills.

## What Changes

- **Immersive Video Player**:
  - True fullscreen mode with auto landscape sensor rotation and system bar hiding via `WindowInsetsControllerCompat`.
  - Intuitive gesture HUD: left vertical drag for screen brightness, right vertical drag for media volume, horizontal swipe for scrubbing seek, and double-tap left/right for configurable ±10s skip.
  - Subtitles and closed-captions track querying, selection dialog, and live playback rendering.
  - Interactive synchronized transcripts sheet: live line-by-line auto-scroll matching playback timestamps and tap-to-seek navigation.
  - Multi-track selector for audio language tracks and HLS video resolution streams.
- **Whole-App UI Simplification & Redundancy Removal**:
  - Streamline `AuraHeader`: eliminate fake mock cast and diagnostics dialogs; provide a clean, modern header with branding and search transition.
  - Streamline `HomeScreen`: remove bloated hero banners, fake badges, and duplicate category chips; introduce a sleek "Continue Playing" compact card, clean 1-line stream player, and clean device media grid.
  - Streamline `LibraryScreen`: modern clean tabs (`Videos` | `Audio`), responsive search, and clean media list items without visual noise.
  - Streamline `VaultScreen`: remove fake Wi-Fi P2P sync and hardware telemetry; present an elegant 4-digit PIN lock followed by an uncluttered private media vault.
  - Fix `MiniPlayerDock` layout collisions by adding coordinated bottom padding across all tabbed screens.
- **Player Settings Management**:
  - Expand `SettingsScreen` with dedicated player preferences: gesture controls toggle, double-tap skip interval (5s/10s/15s/30s), default aspect ratio (Fit/Zoom/Stretch), and subtitle styling.
- **Dedicated Agent Delivery Skills**:
  - Add `.agents/skills/deliver-dev/SKILL.md` to trigger `./gradlew appDistributionUploadDevDebug` deploying to `internal-testers` (`shabeenmhmd07@gmail.com`).
  - Add `.agents/skills/deliver-pro/SKILL.md` to build and verify `./gradlew assembleProRelease`.

## Capabilities

### New Capabilities
- `video-player-suite`: Comprehensive media playback engine featuring true fullscreen, swipe gestures, subtitle selection, interactive live transcripts, and audio/video track selection.
- `clean-ui-architecture`: Clean, clutter-free, responsive UI across all primary application screens (Header, Home, Library, Vault, and MiniPlayer) removing all redundant and fake elements.
- `player-settings`: Configurable playback preferences in Settings (gesture sensitivity, seek intervals, aspect ratio, subtitle styling).
- `delivery-agent-skills`: Reusable agent skills for 1-click DEV distribution to Firebase and PRO release validation.

### Modified Capabilities
<!-- None: Existing capabilities (crash-monitoring-and-reliability, firebase-app-distribution, firebase-multi-env) remain untouched and valid. -->

## Impact

- **UI Composables**: Major refactor of `PlayerScreen.kt`, `HomeScreen.kt`, `LibraryScreen.kt`, `VaultScreen.kt`, `SettingsScreen.kt`, `AuraHeader.kt`, `MiniPlayerDock.kt`, and `MainShellScreen.kt`.
- **Media Engine**: Updates to `PlayerController.kt` and `PlayerState.kt` to support ExoPlayer tracks (`C.TRACK_TYPE_TEXT`, `C.TRACK_TYPE_AUDIO`, `C.TRACK_TYPE_VIDEO`), brightness/volume gestures, and transcript synchronization.
- **Data Models**: Addition of `TranscriptSegment` and player preference models.
- **Agent Environment**: Creation of `.agents/skills/deliver-dev/` and `.agents/skills/deliver-pro/`.
