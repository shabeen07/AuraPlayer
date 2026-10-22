# Design

## Context

See `proposal.md` for motivation. Currently, `MainActivity` does not declare `android:configChanges` in `AndroidManifest.xml`. When `PlayerScreen.kt` sets `activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE`, Android OS tears down `MainActivity` and reconstructs it. Because `DefaultDataRepository` was instantiated inside `@Composable fun MainNavigation()` with `remember { DefaultDataRepository(context) }`, recreation discards the repository and creates a new `PlayerController` with an empty ExoPlayer, stopping playback. Additionally, `PlayerScreen.kt` branches between two distinct `PlayerSurfaceContent` composables, tearing down the active `PlayerView` when toggling fullscreen. Lastly, `MiniPlayerDock.kt` uses `detectHorizontalDragGestures` directly on the root Box, consuming pointer taps and leaving insufficient room for track titles due to four crowded action buttons.

## Goals / Non-Goals

**Goals:**
- Enable continuous, zero-interruption video and audio playback across orientation changes and fullscreen toggles.
- Maintain a single, continuous `PlayerView` surface throughout the lifecycle of `PlayerScreen`.
- Deliver a responsive, decluttered mini-player dock with reliable tap-to-expand navigation, clean two-button transport cluster, flush bottom progress indicator, and modern glassmorphic visual treatment.

**Non-Goals:**
- Modifying underlying ExoPlayer codecs or media decoders.
- Changing app navigation graph structure beyond stabilizing repository retention.

## Decisions

### Decision 1: Activity Configuration Changes Declaration
- **Choice**: Add `android:configChanges="orientation|screenSize|screenLayout|smallestScreenSize|uiMode"` to `MainActivity` in `AndroidManifest.xml`.
- **Rationale**: This instructs Android OS to delegate orientation and size changes to the running Activity rather than destroying and recreating it. Compose handles size changes natively and reactively.
- **Alternatives Considered**: Using Android ViewModel / Hilt injection to survive recreation. While ViewModels preserve state, Activity destruction still causes brief surface teardown and audio glitches on older Android versions. Combining `configChanges` with persistent repository ensures zero disruption.

### Decision 2: Single Persistent PlayerSurfaceContent in PlayerScreen
- **Choice**: Unify `PlayerSurfaceContent` so only one instance exists in `PlayerScreen.kt`. In portrait mode, it is bounded by the 16:10 aspect ratio box; in fullscreen mode, it expands edge-to-edge with fullscreen controls overlaid.
- **Rationale**: Media3's `PlayerView` calls `setVideoSurface(null)` when detached from the window. Having two conditional instances (`if (isFullscreen) ... else ...`) detaches the surface during the toggle. A single persistent surface avoids any detachment or frame drops.
- **Alternatives Considered**: TextureView-based player view. TextureView adds rendering overhead and battery cost compared to SurfaceView, whereas fixing the single composition node solves the problem cleanly.

### Decision 3: Mini-Player Dock Touch and Layout Overhaul
- **Choice**:
  - Replace the 4-button cluster with a focused 2-button cluster: a prominent circular Play/Pause toggle (40dp) and a clean Close/Dismiss button (28dp).
  - Remove pointer drag gesture consumption from the root clickable area so clicks immediately trigger `onPlayerClick()`.
  - Position an edge-to-edge flush micro-progress line (2.5dp height) at the bottom edge of the dock.
  - Implement a 16:10 thumbnail container with "VIDEO" badge for video media and animated 3-bar audio equalizer icon for audio media.
- **Rationale**: Mobile dock players require maximum horizontal space for track titles and artist info. Decoupling gestures ensures tapping the dock never fails.

## Risks / Trade-offs

- **[Risk] System Bars Visibility in Fullscreen** → When Activity is not recreated, status and navigation bars might remain visible if insets are not explicitly controlled.
  - *Mitigation*: Continue using `WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE` and `insetsController.hide(WindowInsetsCompat.Type.systemBars())` inside `LaunchedEffect(isFullscreen)`.
- **[Risk] Mini-player Dismissal vs. Playback State** → Tapping the close button stops playback and clears current media.
  - *Mitigation*: Wire the close button to `repository.stopPlayback()` while ensuring its click area is isolated with an independent `IconButton` to prevent triggering dock expansion.
