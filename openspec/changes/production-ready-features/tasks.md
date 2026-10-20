# Tasks

## 1. Build Infrastructure & Versioning

- [x] 1.1 Add `androidx.core:core-splashscreen` to `libs.versions.toml` and `app/build.gradle.kts`, configure `buildConfig = true`, and verify Gradle builds.
- [x] 1.2 Create `version.properties` with semantic versioning logic (`versionCode`, `versionName`), link it in `app/build.gradle.kts`, and verify build metadata.
- [x] 1.3 Create `app/proguard-rules.pro` with keep rules for Media3, Coil, Kotlin Serialization, and Compose, and verify `./gradlew.bat assembleDebug` succeeds.
- [x] 1.4 Initialize Git repository, configure `.gitignore`, create initial commit, and tag `v1.0.0`.

## 2. Branding, Splash Screen, & App Icon

- [x] 2.1 Create custom neon cyberpunk vector launcher icons (`ic_launcher_foreground.xml`, `ic_launcher_background.xml`, and monochrome icon in `mipmap-anydpi-v26`) and verify manifest icon references.
- [x] 2.2 Configure `Theme.AuraPlayer.Starting` splash screen theme in `themes.xml` and integrate `installSplashScreen()` in `MainActivity.kt`.
- [x] 2.3 Implement animated `SplashScreen.kt` composable with glowing AuraMedia logo, pulse animation, and navigation handoff to `MainShellScreen`.

## 3. Dedicated Media Library Screen

- [x] 3.1 Implement `LibraryScreen.kt` with category filters (All, Videos, Audio, Folders), search filtering, and sorting by title, size, and date.
- [x] 3.2 Update `AuraBottomNav` and `MainShellScreen` to route `AuraTab.LIBRARY` to `LibraryScreen` and verify tapping plays selected media.

## 4. Secure Encrypted Vault

- [x] 4.1 Implement `VaultLockScreen.kt` with 4-digit PIN pad overlay and biometric unlock simulation/verification.
- [x] 4.2 Update `VaultScreen.kt` to enforce the unlock gate, show storage diagnostic gauge, and provide private file management and Wi-Fi Direct sync dialog.

## 5. Audio Equalizer & Settings Screen

- [x] 5.1 Implement `AudioFxController.kt` connecting Android `Equalizer`, `BassBoost`, and `Virtualizer` to `PlayerController`'s `audioSessionId`.
- [x] 5.2 Implement `SettingsScreen.kt` featuring 5-band EQ sliders (60Hz to 14kHz), presets, playback toggles (HW acceleration, background play), cache clearer, and version display.
- [x] 5.3 Route `AuraTab.SETTINGS` to `SettingsScreen` in `MainShellScreen` and verify equalizer and settings persist across playback.

## 6. Header Action Modals & Verification

- [x] 6.1 Implement `SearchSheet.kt`, `CastPickerSheet.kt`, and `ProfileDiagnosticsSheet.kt` and connect them to `AuraHeader` action buttons.
- [x] 6.2 Add unit tests for `AudioFxController`, `StorageStats`, and `LibraryFilter`, run test suite, and verify assembleDebug build.
