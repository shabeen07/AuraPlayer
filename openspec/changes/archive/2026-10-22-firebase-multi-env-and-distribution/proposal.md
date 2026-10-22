# Proposal

## Why

Aura Player currently operates on a single default build configuration (`com.example.auraplayer`) without distinction between development testing and production release, and lacks crash reporting and distribution infrastructure. To enable automated QA delivery, runtime telemetry, and production separation, the application requires two distinct environments (`DEV` and `PRO`) hosted under a single dedicated Firebase project, integrated Crashlytics crash reporting, and automated Firebase App Distribution for internal testers.

## What Changes

- **Environment Flavors (DEV & PRO)**: Configure Gradle `flavorDimensions` with base package `dev.shabeen.aura.player`:
  - `dev`: `dev.shabeen.aura.player.dev` (named "Aura Player (DEV)")
  - `pro`: `dev.shabeen.aura.player` (named "Aura Player")
- **Single Firebase Project Integration**: Set up a new unified Firebase project holding both the `dev.shabeen.aura.player.dev` and `dev.shabeen.aura.player` Android applications.
- **Firebase Core & Crash Telemetry**: Integrate Google Services plugin, Firebase BoM, Firebase Analytics, and Firebase Crashlytics.
- **Firebase App Distribution Setup**: Configure the `firebase-appdistribution` Gradle plugin for the `dev` flavor, creating tester group `internal-testers` with `shabeenmhmd07@gmail.com` as the initial tester.
- **End-to-End App Stability Audit**: Verify zero crash vectors across ExoPlayer streaming, AudioFx Equalizer, MediaStore queries, Vault authentication, and Compose UI navigation.

## Capabilities

### New Capabilities
- `firebase-multi-env`: Multi-environment build flavor matrix (`dev` and `pro`) under base application ID `dev.shabeen.aura.player`, with environment-specific resources, naming, and `google-services.json` registration.
- `firebase-app-distribution`: Automated build artifact upload and distribution to internal testers (`internal-testers` group, `shabeenmhmd07@gmail.com`) via Firebase App Distribution.
- `crash-monitoring-and-reliability`: Centralized crash reporting via Firebase Crashlytics and runtime exception safeguards across media playback and UI states.

### Modified Capabilities
<!-- None -->

## Impact

- **Build Configuration**: `build.gradle.kts` (root and app level), `libs.versions.toml` updated with Google Services, Firebase BoM, Crashlytics, and App Distribution plugins.
- **Namespace & App ID**: `applicationId` updated to `dev.shabeen.aura.player` (with `.dev` suffix for DEV flavor).
- **Source Set Structure**: `app/src/dev/` and `app/src/pro/` directory structures for environment manifests, assets, and `google-services.json`.
- **Application Startup**: `MainActivity.kt` and runtime setup safely initialize Firebase for all build variants.
