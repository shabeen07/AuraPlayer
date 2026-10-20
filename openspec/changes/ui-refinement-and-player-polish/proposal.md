# Proposal

## Why

AuraPlayer has implemented its functional foundation (playback, local/online streams, splash, icon, library, vault, settings EQ), but the user experience retains artifacts from HTML/Figma mockups and lacks refined motion design:
1. **Unwanted Prototype Patterns**: The app header draws a fake, hardcoded "09:41" status bar with dummy Wi-Fi/Battery icons that clash with Android's system status bar. Similarly, the bottom navigation bar draws a redundant iOS-style home pill handle directly over Android's native gesture bar.
2. **Floating Player Occlusion**: The floating mini-player is sometimes obscured or overlaps content at the bottom of scrollable lists (`HomeScreen`, `LibraryScreen`, `VaultScreen`, `SettingsScreen`), and lacks swipe/drag interaction and clear visibility states.
3. **Abrupt Composable Transitions**: Tab switches in `MainShellScreen` and screen navigation between `SplashScreen`, `HomeScreen`, and `PlayerScreen` occur abruptly with zero motion transitions.

## What Changes

- **Remove Unwanted Design Clutter**:
  - Strip the hardcoded "09:41" mock status bar row from `AuraHeader.kt`, letting Android's system status bar manage device status with edge-to-edge transparency.
  - Remove the fake home pill handle from `AuraBottomNav.kt`.
  - Fix control locking behavior in `PlayerScreen.kt` so lock mode genuinely disables accidental touches on transport buttons and scrubber.
- **Enhanced Floating Mini-Player Experience**:
  - Ensure the floating MiniPlayerDock is always visible whenever media is loaded or active, using smooth entrance and exit animations (`slideInVertically` / `slideOutVertically` + `fadeIn` / `fadeOut`).
  - Add smooth swipe-to-dismiss and tap-to-expand gesture interactions.
  - Ensure all tab screens have adequate dynamic bottom padding (scrolling content clears the floating dock and bottom navigation bar cleanly).
- **Fluid Composable Transitions**:
  - Integrate `AnimatedContent` for tab switching in `MainShellScreen` (fade + subtle horizontal slide).
  - Implement smooth shared / animated transitions between `SplashScreen`, `MainShellScreen`, and `PlayerScreen` (slide up on enter, slide down on back).

## Capabilities

### New Capabilities
- `ui-cleanup-and-streamlining`: Strips simulated status bar elements and redundant mockup artifacts, ensuring a clean, native Android presentation.
- `floating-player-experience`: Makes the floating mini-player dock persistent, swipeable/draggable, visible, and gracefully integrated without occluding scrollable content.
- `smooth-screen-transitions`: Provides fluid motion transitions across top-level screens and main navigation tabs.

### Modified Capabilities
(none)

## Impact

- **UI / Presentation**:
  - Update `AuraHeader.kt` and `AuraBottomNav.kt` to remove prototype artifacts.
  - Update `MainShellScreen.kt` with `AnimatedContent` for tab switches and proper bottom content offsets.
  - Update `MiniPlayerDock.kt` with enter/exit animations and gesture handling.
  - Update `Navigation.kt` with animated transitions between backstack destinations.
  - Update `PlayerScreen.kt` to make lock mode actually protect controls.
