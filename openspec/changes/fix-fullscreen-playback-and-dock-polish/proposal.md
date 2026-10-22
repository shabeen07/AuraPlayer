# Proposal

## Why

Users experience playback stoppage when clicking the fullscreen toggle button in the video player because `MainActivity` lacks orientation configuration change handling, causing Android OS to recreate the Activity, discard the active `DefaultDataRepository`, and reset playback. Additionally, the Home mini-player dock suffers from cluttered controls (four crowded buttons leaving minimal room for metadata), touch conflicts where drag detection swallows card tap events, and harsh glowing borders. Resolving these issues provides seamless, continuous fullscreen video playback and a polished, responsive mini-dock player experience.

## What Changes

- **Activity Configuration Handling**: Configure `MainActivity` in `AndroidManifest.xml` with `android:configChanges="orientation|screenSize|screenLayout|smallestScreenSize|uiMode"` so landscape and portrait orientation changes occur smoothly without Activity recreation or playback interruption.
- **Continuous Single-Surface Player View**: Refactor `PlayerScreen.kt` to maintain a single persistent `PlayerSurfaceContent` instance across orientation and fullscreen toggles, avoiding video surface detachment (`clearVideoSurface`) and frame drops.
- **Retained Playback Controller**: Ensure `DataRepository` and `PlayerController` state persists across any system-level configuration changes.
- **Revamped Mini-Player Dock**:
  - Declutter transport actions into a clean two-button cluster: prominent circular Play/Pause and a subtle Dismiss button.
  - Decouple tap-to-expand from horizontal drag detection so clicking anywhere on the artwork or metadata reliably opens `PlayerScreen`.
  - Add edge-to-edge flush micro-progress indicator along the dock's bottom edge (2.5dp) with a smooth gradient.
  - Apply sleek glassmorphic container styling (`AuraSurfaceContainerHighest`, subtle border, soft drop shadow).
  - Add 16:10 video thumbnail aspect ratio with "VIDEO" badge for videos and animated 3-bar audio equalizer icon for audio media.

## Capabilities

### New Capabilities
*(None)*

### Modified Capabilities
- `video-player-suite`: Update True Immersive Fullscreen Mode to require non-disruptive fullscreen transitions without Activity destruction, surface unmounting, or playback interruption.
- `clean-ui-architecture`: Update Collision-Free Mini Player Dock to specify streamlined 2-button transport controls, dedicated tap-to-expand gesture routing, flush bottom progress indicator, and media format indicators.

## Impact

- **Manifest**: `app/src/main/AndroidManifest.xml` updated with `android:configChanges`.
- **UI Composables**:
  - `app/src/main/java/com/aura/player/ui/player/PlayerScreen.kt` updated to use a unified `PlayerSurfaceContent`.
  - `app/src/main/java/com/aura/player/ui/components/MiniPlayerDock.kt` redesigned for streamlined controls, gestures, and styling.
- **Navigation & Lifecycle**: `app/src/main/java/com/aura/player/Navigation.kt` verified for clean screen transitions and persistent state.
- **Release**: Followed by a dev build delivery via `/deliver-dev` (version `1.0.2-dev`).
