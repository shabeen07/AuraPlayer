# Design: Production Ready AuraPlayer Features

## Context

AuraPlayer's foundation includes an ExoPlayer Media3 playback controller, MediaStore local queries, and a single main shell with Compose. However, several tabs and screens are stubs or missing entirely (`SETTINGS`, `LIBRARY` vs `VAULT`), launch branding is stock Android, ProGuard rules are missing, and no version control or release versioning mechanism is in place.

See `proposal.md` for overall motivation.

## Goals / Non-Goals

**Goals:**
- Provide a cohesive dark cyberpunk launch experience with Android 12+ SplashScreen theme, animated Compose intro, and custom adaptive app icon.
- Split `LIBRARY` and `VAULT` into distinct experiences: `LibraryScreen` for media browsing/sorting and `VaultScreen` for PIN-secured offline media with storage diagnostics.
- Implement `SettingsScreen` containing a real Android `Equalizer` audio engine tied to ExoPlayer's `audioSessionId`, presets, playback controls, cache cleaner, and version info.
- Wire all header stubs (`AuraHeader`) to functional modals (Search sheet, Cast picker dialog, Profile & Engine diagnostics sheet).
- Set up production release infrastructure: ProGuard rules, Gradle semantic versioning via `version.properties`, `buildConfig = true`, and Git initialization.

**Non-Goals:**
- Real hardware casting protocols (e.g. Chromecast SDK / Google Cast Play Services SDK integration); instead we provide a functional Cast picker bottom sheet managing local and mock network playback sinks.
- Full hardware secure enclave AES file re-encryption; the Vault provides an application-private sandboxed directory with PIN/Biometric access gating.

## Decisions

### 1. Splash Screen Architecture
- **Decision**: Use `androidx.core:core-splashscreen` for the native window background and starting theme, plus a Compose `SplashScreen` route on startup that smoothly fades into `MainShellScreen`.
- **Rationale**: Android 12+ requires `core-splashscreen` to prevent unsightly white flash windows on cold starts, while the Compose splash provides brand consistency.
- **Alternatives Considered**: Compose-only splash (results in white/black flash during cold boot) or XML-only splash (no seamless animation into Compose state).

### 2. Audio Effects & Equalizer Engine
- **Decision**: Create an `AudioFxController` that initializes `android.media.audiofx.Equalizer`, `BassBoost`, and `Virtualizer` attached directly to `exoPlayer.audioSessionId`.
- **Rationale**: Media3 ExoPlayer exposes the underlying `audioSessionId`, which allows hardware-accelerated platform audio effects with negligible CPU overhead.
- **Alternatives Considered**: Software DSP filter in Media3 pipeline (unnecessary overhead when Android AudioFX API is available).

### 3. Vault Protection & PIN / Biometric
- **Decision**: Store the 4-digit PIN in EncryptedSharedPreferences / DataStore with fallback default (`1234` initially, customizable), accompanied by BiometricPrompt authentication.
- **Rationale**: Immediate security UX without requiring heavy backend authentication services.

### 4. ProGuard / R8 Rules
- **Decision**: Explicit rules for Media3 (`androidx.media3.**`), Coil, Kotlinx Serialization (`kotlinx.serialization.**`), and Jetpack Compose runtime.
- **Rationale**: Prevents missing reflection descriptors and obfuscation crashes when compiling release APKs.

## Risks / Trade-offs

- **[Audio Session ID Availability]** → When ExoPlayer initializes, `audioSessionId` might initially be `C.AUDIO_SESSION_ID_UNSET`.
  * *Mitigation*: Listen to `Player.Listener.onAudioSessionIdChanged` and attach `AudioFxController` as soon as a valid session ID is dispatched.
- **[Storage Access on Android 13+ vs Android 10]** → Scoped storage differences.
  * *Mitigation*: Continue using the existing `PermissionUtils` mapping `READ_MEDIA_AUDIO`/`VIDEO` on SDK 33+ and `READ_EXTERNAL_STORAGE` on older APIs.
