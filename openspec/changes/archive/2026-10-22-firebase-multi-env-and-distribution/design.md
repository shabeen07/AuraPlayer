# Design

## Context

Aura Player currently operates on a single default build configuration (`com.example.auraplayer`). See `proposal.md` for the motivation. This design details how Gradle build flavors, single-project Firebase registration, Crashlytics telemetry, and Firebase App Distribution are structured and integrated into the Android project.

## Goals / Non-Goals

**Goals:**
- Migrate base application ID to `dev.shabeen.aura.player`.
- Establish Gradle `flavorDimensions += "environment"` with `dev` (`dev.shabeen.aura.player.dev`) and `pro` (`dev.shabeen.aura.player`).
- Configure a single Firebase project hosting both client apps with Google Services plugin, Firebase BoM, Analytics, and Crashlytics.
- Configure `com.google.firebase.appdistribution` targeting `dev` with tester group `internal-testers` and member `shabeenmhmd07@gmail.com`.
- Guarantee complete runtime stability with zero crashes across media decoders, Equalizer hardware effects, and navigation.

**Non-Goals:**
- Cloud synchronization or remote streaming of encrypted Vault contents (Vault stays purely on-device encrypted storage).
- Multi-project Firebase partitioning (user explicitly selected single Firebase project for both DEV and PRO).

## Decisions

### 1. Unified Firebase Project Architecture
- **Decision**: Host both `dev.shabeen.aura.player.dev` and `dev.shabeen.aura.player` in one Firebase project.
- **Rationale**: Keeps project management, billing, and tester infrastructure unified under a single Firebase console dashboard while maintaining distinct app IDs.
- **Alternative considered**: Two separate Firebase projects (`auraplayer-dev` and `auraplayer-pro`). Rejected per user direction to keep DEV and PRO inside a single project.

### 2. Gradle Flavor Dimension & Source Sets
- **Decision**: Configure `flavorDimensions += "environment"` in `app/build.gradle.kts`:
  ```kotlin
  flavorDimensions += "environment"
  productFlavors {
      create("dev") {
          dimension = "environment"
          applicationIdSuffix = ".dev"
          versionNameSuffix = "-dev"
          manifestPlaceholders["appName"] = "Aura Player (DEV)"
      }
      create("pro") {
          dimension = "environment"
          manifestPlaceholders["appName"] = "Aura Player"
      }
  }
  ```
- **Source Sets**:
  - `app/src/main/`: Core application logic and Compose UI.
  - `app/src/dev/`: DEV-specific resources (e.g. `google-services.json`, custom DEV badge or manifest).
  - `app/src/pro/`: PRO-specific resources (e.g. `google-services.json`).

### 3. Firebase App Distribution Plugin Integration
- **Decision**: Apply `com.google.firebase.appdistribution` plugin in `app/build.gradle.kts`:
  ```kotlin
  firebaseAppDistribution {
      artifactType = "APK"
      groups = "internal-testers"
      testers = "shabeenmhmd07@gmail.com"
      releaseNotes = "Aura Player DEV test build with latest UI refinements and AudioFx engine."
  }
  ```
- **Rationale**: Allows 1-command deployment (`./gradlew appDistributionUploadDevDebug`) using local Firebase CLI credentials or CI tokens.

### 4. Crashlytics Defensive Guardrails
- **Decision**: Initialize Firebase Crashlytics via Firebase BoM, and wrap external hardware APIs (Equalizer, BassBoost, Virtualizer, ContentResolver) in graceful fallbacks that report non-fatal issues to Crashlytics while keeping the UI responsive.
- **Rationale**: Prevents unexpected crashes on diverse OEM devices with custom audio HALs or file system permissions.

## Risks / Trade-offs

- **[Risk]** Missing or invalid `google-services.json` breaks build during Google Services processing.
  - **Mitigation**: Provide valid template/configured `google-services.json` for `dev.shabeen.aura.player.dev` and `dev.shabeen.aura.player` with clear registration instructions if credentials are updated.
- **[Risk]** Firebase App Distribution upload task fails if CLI is not authenticated during execution.
  - **Mitigation**: Pre-verify Firebase CLI login (`firebase login:list`), support `FIREBASE_TOKEN` and automated fallback guidance.
- **[Risk]** Application ID change from `com.example.auraplayer` to `dev.shabeen.aura.player` affects installed app updates on physical test devices.
  - **Mitigation**: Test devices will install DEV side-by-side with PRO without overwriting existing data.
