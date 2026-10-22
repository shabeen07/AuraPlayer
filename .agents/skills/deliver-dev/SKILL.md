---
name: deliver-dev
description: Deploy the Aura Player DEV build to Firebase App Distribution for the internal testers group (shabeenmhmd07@gmail.com). Trigger whenever the user asks to "deliver dev", "deploy dev", "distribute dev", or "upload dev to firebase".
license: MIT
metadata:
  author: AuraPlayer
  version: "1.0"
---

# Deliver DEV: Firebase App Distribution Deployment

Deploys the `devDebug` variant of AuraPlayer to Firebase App Distribution for the `internal-testers` tester group.

## Configuration Details

- **Target Flavor**: `dev`
- **Build Type**: `debug`
- **Application ID**: `dev.shabeen.aura.player.dev`
- **Firebase App ID**: `1:124161078331:android:598e60b56b2dfeb07dd81e`
- **Tester Group**: `internal-testers`
- **Testers**: `shabeenmhmd07@gmail.com`

## Execution Steps

### 1. Pre-deployment Verification

Verify that the DEV environment compiles without errors:

```bash
./gradlew.bat compileDevDebugKotlin
```

### 2. Upload to Firebase App Distribution

Execute the automated Gradle distribution task:

```bash
./gradlew.bat appDistributionUploadDevDebug
```

> [!NOTE]
> If authentication is requested, ensure either `FIREBASE_TOKEN` or `GOOGLE_APPLICATION_CREDENTIALS` is present in the environment, or run `firebase login` with the project owner account.

### 3. Verify Artifact Location

Upon successful build and upload, the generated APK will also be available locally at:
`app/build/outputs/apk/dev/debug/app-dev-debug.apk`

### 4. Provide Feedback

Report the distribution status to the user, including the build version, artifact name, and target tester group (`internal-testers`).
