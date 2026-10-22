---
name: deliver-pro
description: Build, verify, and package the Aura Player PRO release artifact. Trigger whenever the user asks to "deliver pro", "build pro release", "assemble pro", or "package pro".
license: MIT
metadata:
  author: AuraPlayer
  version: "1.0"
---

# Deliver PRO: Release Assembly & Packaging

Builds and packages the production `proRelease` variant of AuraPlayer for release distribution.

## Configuration Details

- **Target Flavor**: `pro`
- **Build Type**: `release`
- **Application ID**: `dev.shabeen.aura.player`
- **App Name**: `Aura Player`

## Execution Steps

### 1. Pre-release Verification & Unit Tests

Run full unit tests across player controller, repository, and audio engine:

```bash
./gradlew.bat testProReleaseUnitTest
```

### 2. Assemble Production Release Artifact

Execute the production assembly task:

```bash
./gradlew.bat assembleProRelease
```

### 3. Verify Output Artifacts

Confirm that the output APK / Bundle was generated:
- Release APK: `app/build/outputs/apk/pro/release/app-pro-release-unsigned.apk` (or signed if keystore configured)
- Or Release Bundle (if building AAB): `./gradlew.bat bundleProRelease` -> `app/build/outputs/bundle/proRelease/app-pro-release.aab`

### 4. Provide Feedback

Report the artifact size, version code, version name, and filesystem path to the user.
