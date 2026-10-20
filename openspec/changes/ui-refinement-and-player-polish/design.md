# Design: UI Refinement and Player Polish

## Context

AuraPlayer has established full playback, local scanning, streaming, splash, adaptive icon, library, vault, and equalizer screens. However, several mockup-era design elements remain that degrade the native Android feel (e.g. fake "09:41" status bar, artificial bottom pill bar), the mini-player overlaps the bottom of scrollable lists, and screen navigation lacks fluid transitions.

## Goals / Non-Goals

**Goals:**
- Eliminate all simulated web/iOS mockup elements (fake clock, status icons, home pill handle) so AuraPlayer respects Android's native system bars.
- Ensure the floating MiniPlayerDock is always visible when media is active, animates smoothly into view, and never blocks scrollable content across any tab.
- Add fluid Compose animations: `AnimatedContent` for tab changes and slide-up/slide-down transitions for the full player screen.
- Make the `isLocked` toggle in `PlayerScreen` functional by disabling playback controls during lock mode.

**Non-Goals:**
- Re-architecting Media3 playback controller or audio effect engine.
- Adding third-party motion libraries (we will use official Compose Foundation / Animation primitives).

## Decisions

### 1. Stripping Artificial Status Bar and Pill Bar
- **Decision**: Remove lines 80-123 from `AuraHeader.kt` (the mock "09:41" status row) and lines 92-106 from `AuraBottomNav.kt` (the artificial home handle).
- **Rationale**: Android system insets (`statusBarsPadding()`, `navigationBarsPadding()`) already present the user's real clock, notifications, battery, and gesture pill. Drawing redundant mock versions looks unprofessional and conflicts with system displays.

### 2. Floating Mini-Player Dock Placement & Scrollable Clearance
- **Decision**: Keep the floating dock elevated above the bottom navigation bar with `AnimatedVisibility` (fade + vertical slide). Add a standard `110.dp` bottom content padding to all scrollable lists and columns (`HomeScreen`, `LibraryScreen`, `VaultScreen`, `SettingsScreen`).
- **Rationale**: Guarantees users can scroll all the way to the end of any list without the mini-player covering the last card or button.

### 3. Motion Design with Compose Animation
- **Decision**: Use `AnimatedContent` with `fadeIn(tween(250)) + slideInHorizontally` / `fadeOut(tween(200))` when switching between tabs in `MainShellScreen`.
- **Rationale**: Smooth, intentional tab switching elevates the perceived quality of the app without adding lag or blocking input.

### 4. Player Control Lock Implementation
- **Decision**: Bind `isLocked` to the `enabled` parameter of `Slider`, skip buttons, and play/pause button on `PlayerScreen`.
- **Rationale**: Allows users to watch video or listen to audio with the device in their pocket or hand without accidental seeking or pausing.

## Risks / Trade-offs

- **[Bottom Padding Drift]** → If different tabs use varying bottom padding, some might leave too much whitespace while others get clipped.
  * *Mitigation*: Standardize bottom scroll padding to `110.dp` across all tab screens (`HomeScreen`, `LibraryScreen`, `VaultScreen`, `SettingsScreen`).
