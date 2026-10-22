# Tasks

## 1. Lifecycle and Orientation Resilience

- [x] 1.1 Add `android:configChanges="orientation|screenSize|screenLayout|smallestScreenSize|uiMode"` to `MainActivity` in `app/src/main/AndroidManifest.xml` and verify compilation succeeds via `./gradlew.bat compileDevDebugKotlin`
- [x] 1.2 Ensure `DataRepository` and `PlayerController` instances remain persistent across orientation changes in `Navigation.kt` and verify state continuity

## 2. Fullscreen Video Surface Unification

- [x] 2.1 Refactor `PlayerScreen.kt` to maintain a single continuous `PlayerSurfaceContent` composable across portrait and fullscreen states, eliminating dual `PlayerView` instantiation
- [x] 2.2 Verify that toggling fullscreen changes orientation and hides system bars without resetting `currentPositionMs`, pausing, or detaching the video decoder surface

## 3. Mini-Player Dock UI/UX Overhaul

- [x] 3.1 Streamline `MiniPlayerDock.kt` controls into a focused two-button cluster (prominent Play/Pause and subtle Dismiss), expanding available horizontal space for track title and subtitle
- [x] 3.2 Decouple tap-to-expand from horizontal drag detection so tapping anywhere on the dock card reliably triggers `onPlayerClick`
- [x] 3.3 Position an edge-to-edge flush micro-progress bar (2.5dp) seamlessly along the bottom edge of `MiniPlayerDock`
- [x] 3.4 Integrate media type indicators (16:10 video thumbnail with video badge vs. album art with animated 3-bar equalizer) and apply refined dark glassmorphic styling

## 4. Verification and DEV Delivery

- [x] 4.1 Execute full test suite and assembly checks via `./gradlew.bat testDevDebugUnitTest assembleDevDebug` to confirm build health
- [x] 4.2 Execute the `/deliver-dev` workflow to increment `version.properties` to `1.0.2-dev`, create delivery branch `release/v1.0.2-dev`, tag `v1.0.2-dev`, and upload artifact to Firebase App Distribution
