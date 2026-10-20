# Spec Delta: app-branding-and-splash

## Purpose

Provides a polished launch experience using the modern Android 12+ SplashScreen API, animated Compose branding, and cohesive cyberpunk adaptive launcher icons.

## ADDED Requirements

### Requirement: Android 12+ System Splash Screen
The application MUST configure an Android 12+ compatible starting theme using `androidx.core:core-splashscreen` that renders the dark obsidian brand background without blank white frames during cold start.

#### Scenario: App Cold Launch Starting Window
- **WHEN** user launches the app from launcher or recents on an Android device
- **THEN** system immediately displays the starting theme with obsidian background `#0F0E17` and brand icon before MainActivity draws

### Requirement: Animated Brand Compose Splash Transition
The application SHALL display an animated Compose splash screen showing the glowing AuraMedia logo and status indicator before transitioning seamlessly into the main catalog.

#### Scenario: Interactive Splash Screen Completion
- **WHEN** app finishes loading initial permissions and storage state
- **THEN** splash screen animates out and navigates into MainShellScreen

### Requirement: Neon Cyberpunk Adaptive Launcher Icon
The application SHALL provide vector adaptive launcher icons with obsidian background, cyan/violet waveform foreground, and monochrome icon support for Material You theming.

#### Scenario: Launcher Icon Rendering
- **WHEN** user views the app on Android launcher or applies Material You dynamic colors
- **THEN** launcher displays the branded neon geometric icon or the monochrome themed variant
