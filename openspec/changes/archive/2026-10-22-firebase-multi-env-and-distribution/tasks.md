# Tasks

## 1. Gradle Environment Flavors & Package Migration

- [x] 1.1 Update base `applicationId` to `dev.shabeen.aura.player` in `app/build.gradle.kts` and configure `flavorDimensions += "environment"` with `dev` (suffix `.dev`, app title "Aura Player (DEV)") and `pro` (app title "Aura Player"). Verify by running `./gradlew.bat tasks` to confirm flavor tasks `assembleDevDebug` and `assembleProDebug` are registered.
- [x] 1.2 Scaffold source set directories `app/src/dev/` and `app/src/pro/` with manifest placeholders for `@string/app_name` and verify manifest merger succeeds for both flavors.

## 2. Firebase Project & Core SDK Integration

- [x] 2.1 Register Google Services (`com.google.gms.google-services`), Firebase Crashlytics (`com.google.firebase.crashlytics`), and Firebase App Distribution (`com.google.firebase.appdistribution`) plugins in `gradle/libs.versions.toml` and root `build.gradle.kts`. Verify build script evaluation passes.
- [x] 2.2 Add Firebase BoM (`firebase-bom`), Firebase Analytics (`firebase-analytics`), and Firebase Crashlytics (`firebase-crashlytics`) dependencies in `app/build.gradle.kts`. Verify dependency graph resolution without conflicts.
- [x] 2.3 Integrate unified `google-services.json` containing client registrations for both `dev.shabeen.aura.player.dev` and `dev.shabeen.aura.player`. Verify Google Services task generates `values.xml` without error.

## 3. Firebase App Distribution Setup

- [x] 3.1 Configure `firebaseAppDistribution` block in `app/build.gradle.kts` targeting the `dev` flavor with tester group `internal-testers`, tester `shabeenmhmd07@gmail.com`, and automated release notes. Verify Gradle distribution task `appDistributionUploadDevDebug` is available.
- [x] 3.2 Execute `./gradlew.bat assembleDevDebug` and verify the generated APK contains the package ID `dev.shabeen.aura.player.dev` and is ready for upload.

## 4. Stability, Crash Prevention & Final Verification

- [x] 4.1 Audit application components (ExoPlayer playback, AudioFx equalizer initializers, MediaStore query cursors, and navigation transitions) to ensure robust exception handling and Crashlytics logging.
- [x] 4.2 Run `./gradlew.bat test` and verify that all unit test suites pass across all variants.
- [x] 4.3 Run `./gradlew.bat assembleDevDebug` and `./gradlew.bat assembleProRelease` and verify clean, warning-free builds for both environments.
