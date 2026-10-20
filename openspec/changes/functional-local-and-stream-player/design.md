# Design

## Context

AuraPlayer is currently an Android Jetpack Compose application designed with a futuristic dark theme (`AuraSurface`, `AuraPrimary`, `AuraSecondary`), but built entirely on static mock data inside `DefaultDataRepository.kt`. The UI renders fake Interstellar frames, simulated storage gauges, hardcoded audio waveforms, and non-functional buttons. 

To transform this into a real, functional media player, we need to integrate:
1. Android `MediaStore` content resolution for device audio and video.
2. `androidx.media3:media3-exoplayer` for reliable audio and video playback, including HLS and progressive streams.
3. Asynchronous thumbnail loading via `io.coil-kt:coil-compose`.
4. Clear architectural separation (Data layer with MediaStore and ExoPlayer controllers, Domain layer with entities and repository interfaces, Presentation layer with reactive ViewModels).

## Goals / Non-Goals

**Goals:**
- Query device internal and external storage for audio and video media files via `MediaStore`.
- Request and handle runtime permissions (`READ_MEDIA_AUDIO`, `READ_MEDIA_VIDEO`, `READ_EXTERNAL_STORAGE`) gracefully with clear UI prompts.
- Play local audio and video files as well as remote online streams (HTTP/HTTPS/HLS `.m3u8`/MP4/MP3) using `androidx.media3:media3-exoplayer`.
- Synchronize real playback state (playing/paused, position, duration, buffering, track info) between the playback engine, the bottom `MiniPlayerDock`, and the full `PlayerScreen`.
- Support online stream ingestion via URL input with a functional Paste button, URL validation, and error states for unreachable streams.
- Retain the exact neon cyberpunk/dark futuristic theme (`AuraPlayerTheme`), typography, and glassmorphic aesthetic while removing non-functional mock patterns (fake DLNA scanner, fake UFS storage gauges, fake codec overlays).

**Non-Goals:**
- Implementation of a custom DLNA/UPnP network server discovery engine.
- Biometric hardware-backed cryptographic vault encryption (the Vault/Storage section will serve as local storage and favorites library).
- Torrent/magnet downloading engine.

## Decisions

### Decision 1: Use AndroidX Media3 (ExoPlayer) for Unified Media Engine
- **Choice**: Add `androidx.media3:media3-exoplayer`, `media3-ui`, and `media3-session`.
- **Rationale**: Media3 is the modern Google-recommended standard for media playback in Android. It natively supports both audio and video, adaptive HLS/DASH streaming, standard container formats (MP4, MKV, MP3, FLAC, AAC), and fine-grained buffering/state listeners.
- **Alternatives Considered**:
  - Legacy `android.media.MediaPlayer`: Poor streaming protocol support, difficult HLS handling, lack of unified UI controller.
  - Custom WebRTC / third-party C++ libraries: Unnecessary complexity for local and standard online streaming.

### Decision 2: Query Media via MediaStore on Coroutines Dispatchers.IO
- **Choice**: Implement a dedicated `LocalMediaDataSource` that queries `MediaStore.Audio.Media.EXTERNAL_CONTENT_URI` and `MediaStore.Video.Media.EXTERNAL_CONTENT_URI` using Android's `ContentResolver`.
- **Rationale**: Respects Android Scoped Storage constraints (API 29+) while efficiently discovering all user media files with standard metadata (ID, title, artist, duration, size, date modified).
- **Alternatives Considered**:
  - Recursive java.io.File scanning: Blocked or heavily restricted on modern Android without `MANAGE_EXTERNAL_STORAGE`, and significantly slower.

### Decision 3: Use Coil Compose for Image & Video Thumbnail Rendering
- **Choice**: Add `io.coil-kt:coil-compose` and `coil-video` extension.
- **Rationale**: First-class Jetpack Compose image loader with automatic memory and disk caching, supporting content URIs, album art URIs, and video frame extraction.
- **Alternatives Considered**:
  - Glide / Picasso: Require legacy Android View interop or boilerplate Compose bridges.

### Decision 4: Architecture & Playback State Lifecycle
- **Choice**: Structure the playback engine in a singleton controller or scoped ViewModel that retains the `ExoPlayer` instance across Compose screen transitions (MainShellScreen to PlayerScreen).
- **Rationale**: Prevents audio/video disruption or re-buffering when the user navigates between screens or expands the MiniPlayerDock.
- **Alternatives Considered**:
  - Initializing ExoPlayer inside a composable lifecycle: Player gets destroyed on screen navigation.

### Decision 5: UI Streamlining & Theme Preservation
- **Choice**: Keep all existing theme tokens (`AuraSurface`, `AuraPrimaryContainer`, `AuraSecondary`, etc.), but replace mock UI components:
  - Replace fake "Direct Stream URL" text with a functional `OutlinedTextField`, a working "Paste" button, and a "Play Stream" action.
  - Replace static `img_interstellar` drawable in `PlayerScreen` with an `AndroidView` embedding `PlayerView` (for video) or a stylized animated audio waveform view (for audio).
  - Replace fake stats in `VaultScreen` with real local storage totals (number of audio tracks and videos found) and favorite/saved streams.

## Risks / Trade-offs

- **[Risk]**: User denies storage permissions on Android 13+.
  - **Mitigation**: Display a prominent, themed permission card on `HomeScreen` with an explanation and a "Grant Permission" button triggering `rememberLauncherForActivityResult`.
- **[Risk]**: Online stream URL is invalid, offline, or returns 404/SSL error.
  - **Mitigation**: Listen to `Player.Listener.onPlayerError`, update UI error state with friendly feedback, and prevent player crash.
- **[Risk]**: Video playback in background or orientation change can lose surface.
  - **Mitigation**: Bind `PlayerView` lifecycle appropriately and maintain player instance in application/viewmodel scope.
