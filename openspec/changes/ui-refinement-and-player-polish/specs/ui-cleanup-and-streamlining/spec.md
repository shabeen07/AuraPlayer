# Spec Delta: ui-cleanup-and-streamlining

## Purpose

Cleans up non-native UI artifacts, removes hardcoded prototype status/pill overlays, and ensures all interactive controls are strictly functional.

## ADDED Requirements

### Requirement: Clean Native Header Surface
The application header MUST NOT draw simulated status bar icons, fake clock timestamps, or mock battery percentages. It SHALL rely solely on Android's native status bar.

#### Scenario: Header Rendering
- **WHEN** user views any main screen with the AuraHeader visible
- **THEN** header renders only the application branding, status badge, subtitle, and action buttons without a hardcoded 09:41 status bar

### Requirement: Native Gesture Bar Compatibility
The bottom navigation bar SHALL NOT draw an artificial iOS-style home pill handle that collides with the system gesture navigation area.

#### Scenario: Bottom Navigation Appearance
- **WHEN** user views the bottom navigation bar
- **THEN** navigation tabs display cleanly with native edge-to-edge system insets and no artificial white/gray bar at the bottom

### Requirement: Functional Player Control Lock
When lock mode is enabled on the full player screen, all transport controls (play, pause, seek slider, skip) MUST be disabled from accidental touch interaction until the lock toggle is tapped again.

#### Scenario: Locked Player Touch Event
- **WHEN** user enables the lock toggle on PlayerScreen and touches the seek bar or skip buttons
- **THEN** player ignores touch input on playback controls and displays a lock indicator
