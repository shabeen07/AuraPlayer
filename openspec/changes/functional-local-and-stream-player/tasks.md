# Tasks

## 1. Dependencies and Permissions Setup

- [x] 1.1 Add AndroidX Media3 (`media3-exoplayer`, `media3-ui`, `media3-session`) and Coil (`coil-compose`, `coil-video`) dependencies to `gradle/libs.versions.toml` and `app/build.gradle.kts`, and verify Gradle build compiles successfully.
- [x] 1.2 Add `INTERNET`, `ACCESS_NETWORK_STATE`, `READ_MEDIA_AUDIO`, `READ_MEDIA_VIDEO`, and legacy `READ_EXTERNAL_STORAGE` permissions to `app/src/main/AndroidManifest.xml`.

## 2. Local Media Discovery & Data Layer

- [x] 2.1 Refactor media data models in `com.aura.player.data.model.Models` to support content URIs, real duration (ms), mime types, file sizes, and online stream sources.
- [x] 2.2 Implement `LocalMediaDataSource` to query `MediaStore.Audio.Media` and `MediaStore.Video.Media` with cursor projections for ID, title, artist, duration, size, and date added.
- [x] 2.3 Add runtime permission request handling in UI / repository to check and request `READ_MEDIA_AUDIO` / `READ_MEDIA_VIDEO` / `READ_EXTERNAL_STORAGE`.
- [x] 2.4 Update `DataRepository` to query and expose real device media lists (All Media, Videos, Audio) and storage metrics via Kotlin `StateFlow`.

## 3. Media3 ExoPlayer Engine Implementation

- [x] 3.1 Implement `PlayerController` managing an `ExoPlayer` instance, configuring audio attributes, error listeners, buffering states, and position polling.
- [x] 3.2 Support playback of both local device URIs and online streaming URLs (HLS `.m3u8`, direct MP4/MP3), updating `PlayerState` in real time.
- [x] 3.3 Implement playback control actions in `PlayerController` and `DataRepository`: `play`, `pause`, `seekTo`, `skipForward(10s)`, `skipBackward(10s)`, and `setPlaybackSpeed`.

## 4. UI Refinement & Feature Integration

- [x] 4.1 Update `HomeScreen.kt` to bind to real local media files, show storage permission prompt when ungranted, provide an empty state when no files exist, and display real media thumbnails using Coil.
- [x] 4.2 Make "Direct Stream URL" functional in `HomeScreen.kt` with a working text field, paste button, and stream button that triggers player loading.
- [x] 4.3 Replace fake mockups in `HomeScreen.kt` (simulated DLNA scan, fake hardware acceleration badges) while preserving the dark neon glassmorphic theme.
- [x] 4.4 Update `PlayerScreen.kt` to render video via `AndroidView(factory = { PlayerView(context) })` and audio via styled waveform/album art, bound to live ExoPlayer state.
- [x] 4.5 Connect `MiniPlayerDock.kt` to live playback state so play/pause, title, artist, and progress reflect current playback and persist across screen navigation.
- [x] 4.6 Update `VaultScreen.kt` to display real local library categories and saved streams, removing fake encryption and simulated storage meters.

## 5. Verification and Build Validation

- [x] 5.1 Write unit tests for `LocalMediaDataSource` mapping and `PlayerState` updates.
- [x] 5.2 Build the Android debug APK using `./gradlew assembleDebug` and verify there are no compilation errors or regressions.
