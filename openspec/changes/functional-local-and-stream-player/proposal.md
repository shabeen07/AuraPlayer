# Proposal

## Why

AuraPlayer currently presents a high-fidelity visual prototype styled with hardcoded mockups, static image assets, and non-functional transport controls. The application lacks real media playback capabilities, local device filesystem querying, online streaming support, and proper permission flows, preventing users from playing their personal media or online streams.

## What Changes

- **Local Media Ingestion**: Request Android storage permissions (`READ_MEDIA_AUDIO`, `READ_MEDIA_VIDEO`, `READ_EXTERNAL_STORAGE`) and query device audio and video files via Android `MediaStore` with real metadata (title, artist, duration, size, content URI, thumbnail).
- **Online Audio & Video Streaming**: Implement support for streaming online URLs (HTTP, HTTPS, HLS `.m3u8`, direct MP4/MP3 links) with URL input validation and error feedback.
- **Media3 Playback Engine**: Integrate `androidx.media3:media3-exoplayer` and `media3-ui` to power the video player surface, audio playback, mini-player dock, and full-screen player with live progress, buffering, play/pause, and seeking.
- **Image/Thumbnail Loading**: Integrate `coil-compose` for dynamically loading video thumbnails and album artwork from content URIs rather than static drawables.
- **UI Simplification & Pattern Cleanup**: Strip out non-functional fake widgets (e.g. simulated DLNA scanner, fake UFS 3.1 encrypted storage metrics, fake codec spec banners, simulated spatial 3D toggles) while strictly preserving the app's dark futuristic design theme (`AuraSurface`, `AuraPrimary`, `AuraSecondary`, custom typography, and glassmorphic styling).
- **Clean Architecture & State Management**: Refactor business logic into Domain (entities, repository contracts, use cases), Data (MediaStore source, player controller, repository implementation), and Presentation (ViewModels, UI state).

## Capabilities

### New Capabilities
- `local-media-library`: Scans local device audio and video files using Android `MediaStore`, handles runtime permission requests, and exposes filtered lists (All Media, Videos, Audio) with real metadata and thumbnails.
- `media-playback-engine`: Powers audio and video playback for both local storage files and online streams using `androidx.media3` (ExoPlayer), synchronizing state (play/pause, seek, buffering, duration, elapsed time) with the UI and MiniPlayerDock.

### Modified Capabilities
(none)

## Impact

- **Dependencies**: Add `androidx.media3:media3-exoplayer`, `androidx.media3:media3-ui`, `androidx.media3:media3-session`, and `io.coil-kt:coil-compose` to `gradle/libs.versions.toml` and `app/build.gradle.kts`.
- **Manifest**: Add permissions (`INTERNET`, `ACCESS_NETWORK_STATE`, `READ_MEDIA_AUDIO`, `READ_MEDIA_VIDEO`, `READ_EXTERNAL_STORAGE`) to `app/src/main/AndroidManifest.xml`.
- **Codebase**:
  - Refactor `com.aura.player.data.DataRepository` and `com.aura.player.data.model.Models` to support real device media items, online stream sources, and dynamic playback state.
  - Update `HomeScreen.kt`, `PlayerScreen.kt`, `VaultScreen.kt`, and `MiniPlayerDock.kt` to bind to live ViewModel state and remove hardcoded mock components.
