# Changelog

All notable changes to the **AuraPlayer** project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [1.0.1] - 2026-10-22

### Added
- **Full Video Player Suite**:
  - **True Immersive Fullscreen**: Seamless sensor landscape rotation and edge-to-edge rendering with system status and navigation bars hidden via `WindowInsetsControllerCompat`.
  - **Multi-Touch Gesture Engine**:
    - Vertical swipe on the left screen half for real-time display brightness control with HUD pill feedback.
    - Vertical swipe on the right screen half for system media volume control with HUD pill feedback.
    - Horizontal dragging for high-precision timestamp scrubbing with live target time preview.
    - Double-tap on left/right edges for instant backward/forward skip with animated badges.
  - **Subtitles & Captions Selection**: Real-time query of Media3 text tracks (`C.TRACK_TYPE_TEXT`), dynamic subtitle track switching modal, and synchronized on-screen subtitle rendering.
  - **Interactive Synchronized Transcripts**: Auto-scrolling transcript drawer synchronized with video playback, active timestamp highlighting, and tap-to-seek navigation.
  - **Multi-Track Audio & Video Resolution Switching**: Alternate audio stream language selection (`C.TRACK_TYPE_AUDIO`) and video quality level selection for adaptive HLS/DASH streams.
- **Player & Gestures Settings**:
  - Toggle for player touch gesture controls (brightness, volume, scrubbing).
  - Configurable double-tap skip intervals (`5s`, `10s`, `15s`, `30s`).
  - Default video aspect ratio options (`Fit / Aspect`, `Zoom / Crop`, `Fill / Stretch`).
  - Adjustable subtitle font size slider (12sp – 24sp).
- **Automated Agent Delivery Skills**:
  - `.agents/skills/deliver-dev`: One-action deployment of the `devDebug` build to Firebase App Distribution with automated release branch (`release/v<version>-dev`), semantic versioning, and git tag creation.
  - `.agents/skills/deliver-pro`: Production release packaging and validation for `proRelease` signed artifacts.
- **Dual-Environment Architecture (`dev` & `pro`)**:
  - Independent package identities (`dev.shabeen.aura.player.dev` and `dev.shabeen.aura.player`).
  - Distinct app branding ("Aura Player (DEV)" vs "Aura Player").
  - Centralized Firebase integration (`google-services.json`), Firebase Crashlytics crash reporting, and Firebase App Distribution Gradle plugin.

### Changed
- **Whole-App UI Revamp & Simplification**:
  - Clean, modern top app bar with unified branding and quick search trigger, removing mock diagnostic modals.
  - Redesigned Home screen featuring a sleek "Resume Playing" card, 1-line stream URL player, and device media carousel without bloated banners.
  - Streamlined Library screen with modern media category tabs (`All Media` | `Videos` | `Audio`) and real-time filtering.
  - Simplified Private Vault with direct 4-digit PIN lock screen and uncluttered media cards.
  - Collision-free layout padding across all tabs coordinating with the floating `MiniPlayerDock` and an explicit dismiss/stop button.

### Fixed
- Fixed layout overlap where the floating mini-player dock obscured list items on scrollable tab screens.
- Fixed orientation locking and system bar visibility inconsistencies when entering and exiting fullscreen video playback.
- Resolved deprecations and experimental API opt-ins across Jetpack Compose Material 3 and Media3 modules.

---

## [1.0.0] - 2026-10-20

### Added
- **Core Media & Audio Engine**:
  - ExoPlayer / AndroidX Media3 streaming engine supporting local storage media, progressive MP4/MKV, and adaptive HLS streams (`.m3u8`).
  - 5-band Graphic Equalizer attached to the active Media3 audio session with custom presets (Rock, Pop, Jazz, Electronic, Bass Boost, Vocal, Flat).
  - Virtualizer / Spatial 3D binaural headphone audio enhancement.
  - Secure Media Vault protected by 4-digit PIN authentication.
  - Local MediaStore indexing for device audio and video files.
- Baseline Clean Architecture scaffolding, navigation shells, and dark minimalist theme.
