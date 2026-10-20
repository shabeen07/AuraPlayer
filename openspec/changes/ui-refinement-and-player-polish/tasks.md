# Tasks

## 1. UI Cleanup & Mockup Removal

- [x] 1.1 Remove simulated status bar row (`09:41`, fake wifi/battery icons) from `AuraHeader.kt` and verify layout aligns with Android status bar.
- [x] 1.2 Remove artificial bottom home pill handle from `AuraBottomNav.kt` and verify navigation bar respects system insets.
- [x] 1.3 Update `PlayerScreen.kt` to bind `isLocked` state to disable playback controls and show a locked visual badge.

## 2. Floating Mini-Player Dock Polish & Scroll Clearance

- [x] 2.1 Update `MiniPlayerDock.kt` to animate visibility with `slideInVertically` and `fadeIn`, add horizontal swipe gestures, and keep dock visible when media is active.
- [x] 2.2 Standardize bottom scroll padding to `110.dp` across `HomeScreen.kt`, `LibraryScreen.kt`, `VaultScreen.kt`, and `SettingsScreen.kt` to ensure content is fully scrollable above the floating dock.

## 3. Smooth Screen & Tab Transitions

- [x] 3.1 Implement `AnimatedContent` for tab transitions in `MainShellScreen.kt` with fluid crossfade and slide animations.
- [x] 3.2 Add smooth enter and exit transitions for Navigation backstack destinations between `SplashScreen`, `MainShellScreen`, and `PlayerScreen`.

## 4. Verification & Testing

- [x] 4.1 Run unit tests with `./gradlew.bat test` and verify all tests pass.
- [x] 4.2 Run `./gradlew.bat assembleDebug` and verify build succeeds.
