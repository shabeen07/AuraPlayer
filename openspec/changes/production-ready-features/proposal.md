# Proposal

## Why

AuraPlayer currently provides core media playback and local file scanning, but key user-facing screens and release infrastructure are missing:
1. Launch lacks a modern Android splash screen and branded adaptive launcher icon (it displays the default green Android sample robot).
2. The Bottom Navigation has placeholder routes: `SETTINGS` redirects to `HomeScreen`, and `LIBRARY` duplicates `VaultScreen`.
3. The Vault lacks PIN/biometric authentication and encrypted private media separation.
4. Audio customization (Equalizer, Bass Boost, Virtualizer) and playback preferences are not implemented.
5. Header actions (Search, Cast, Profile) are non-interactive stub callbacks.
6. The project lacks release hardening: ProGuard rules are missing, `buildConfig` is disabled, and Git version control is uninitialized.

## What Changes

- **Splash Screen & App Icon**:
  - Implement Android 12+ `androidx.core:core-splashscreen` theme.
  - Implement an animated cyberpunk AuraMedia Compose splash screen with glowing logo and particle pulse transition.
  - Create custom adaptive vector launcher icons (`ic_launcher_foreground.xml`, `ic_launcher_background.xml`) and monochrome icons matching Aura's violet/cyan aesthetic.
- **Dedicated Media Library Screen**:
  - Separate `AuraTab.LIBRARY` into an organized media explorer (All Media, Videos, Audios, Folders, Playlists, Recents) with sorting and search filtering.
- **Secure Encrypted Vault Screen**:
  - Implement a 4-digit PIN authentication lock screen with biometric unlock support.
  - Provide a private vault storage section for hiding sensitive media from public MediaStore queries.
  - Display real UFS/eMMC storage diagnostics and Wi-Fi Direct P2P transfer sheet.
- **Settings & 5-Band Equalizer Screen**:
  - Connect a 5-band Graphic Equalizer (60Hz, 230Hz, 910Hz, 3.6kHz, 14kHz) to ExoPlayer's `audioSessionId` with presets (Flat, Bass Boost, Rock, Pop, Jazz, Electronic, Vocal) and spatial virtualizer.
  - Add playback preference toggles (hardware acceleration, background playback, default streaming buffer).
  - Add Coil cache cleaner and live app version information.
- **Header Action Modals**:
  - Implement Quick Search modal sheet across local and online media.
  - Implement Cast / Remote Playback destination picker dialog.
  - Implement Profile / Audio Engine diagnostic bottom sheet.
- **Production Hardening & Versioning**:
  - Create `app/proguard-rules.pro` for Media3, Coil, Kotlin Serialization, and Compose.
  - Enable `buildConfig = true` in `app/build.gradle.kts`.
  - Establish `version.properties` with semantic versioning (`versionCode`, `versionName`).
  - Initialize Git repository, verify `.gitignore`, and create baseline commit and `v1.0.0` release tag.

## Capabilities

### New Capabilities
- `app-branding-and-splash`: Covers Android 12+ splash theme, animated splash screen composable, and cyberpunk adaptive vector launcher icons.
- `media-library-explorer`: Provides structured media catalog browsing across device storage with categorization and sorting.
- `secure-vault`: Provides a PIN and biometric secured private media locker with storage diagnostics.
- `settings-and-equalizer`: Implements a 5-band graphic equalizer linked to ExoPlayer, audio presets, playback toggles, and cache management.
- `app-shell-actions`: Implements search, cast destination picker, and audio engine status dialogs for the main header.
- `production-and-versioning`: Implements ProGuard rules, versioning configuration, and Git repository setup.

### Modified Capabilities
(none)

## Impact

- **Dependencies**: Add `androidx.core:core-splashscreen` and `androidx.biometric:biometric` to `gradle/libs.versions.toml` and `app/build.gradle.kts`.
- **Manifest**: Update launcher theme to splash theme, configure custom adaptive icons.
- **Navigation**: Introduce `SplashNavKey`, separate `LibraryScreen`, `VaultScreen`, and `SettingsScreen` in navigation backstack and shell tab state.
- **Audio Engine**: Link `PlayerController`'s ExoPlayer audioSessionId with `android.media.audiofx.Equalizer` and `Virtualizer`.
