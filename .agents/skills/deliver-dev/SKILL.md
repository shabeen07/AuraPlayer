---
name: deliver-dev
description: Deploy the Aura Player DEV build to Firebase App Distribution for internal testers, including automated semantic versioning, release branch creation, git tagging, and remote repository synchronization. Trigger whenever the user asks to "deliver dev", "deploy dev", "distribute dev", or "upload dev to firebase".
license: MIT
metadata:
  author: AuraPlayer
  version: "1.1"
---

# Deliver DEV: Firebase App Distribution & Release Workflow

Deploys the `devDebug` variant of AuraPlayer to Firebase App Distribution for the `internal-testers` tester group, while maintaining strict Git version control discipline by automatically managing versions, release branches, and annotated release tags.

## Configuration Details

- **Target Flavor**: `dev`
- **Build Type**: `debug`
- **Application ID**: `dev.shabeen.aura.player.dev`
- **Firebase App ID**: `1:124161078331:android:598e60b56b2dfeb07dd81e`
- **Tester Group**: `internal-testers`
- **Testers**: `shabeenmhmd07@gmail.com`
- **Version File**: `version.properties` (`VERSION_MAJOR`, `VERSION_MINOR`, `VERSION_PATCH`, `VERSION_BUILD`)
- **Release Branch Pattern**: `release/v<version>-dev` (e.g. `release/v1.0.1-dev`)
- **Release Tag Pattern**: `v<version>-dev` (e.g. `v1.0.1-dev`)

---

## Execution Steps

Follow this exact sequence whenever `/deliver-dev` is triggered:

### 1. Semantic Version Management & Changelog

1. Read `version.properties` to obtain current major, minor, patch, and build numbers:
   ```properties
   VERSION_MAJOR=1
   VERSION_MINOR=0
   VERSION_PATCH=1
   VERSION_BUILD=1
   ```
2. For every new DEV delivery containing specific changes or fixes:
   - Increment `VERSION_PATCH` (or `VERSION_MINOR` for feature milestones) in `version.properties`.
   - Resulting version name will be: `<major>.<minor>.<patch>-dev` (e.g., `1.0.1-dev`).
3. Update `CHANGELOG.md` with an entry for the version documenting added, changed, or fixed features.
4. Update `releaseNotes` in `app/build.gradle.kts` under `productFlavors.dev` to concisely summarize the build highlights for testers.

### 2. Create Release Branch

Create and switch to a dedicated delivery branch reflecting the version being delivered:

```bash
git checkout -b release/v<version>-dev
```
*(Example: `git checkout -b release/v1.0.1-dev`)*

### 3. Commit Version Changes

Stage and commit all version updates, release notes, and documentation:

```bash
git add version.properties app/build.gradle.kts CHANGELOG.md
git commit -m "chore(release): bump version to v<version>-dev for internal distribution"
```

### 4. Pre-deployment Verification & Build

Compile and assemble the `devDebug` flavor before distributing:

```bash
./gradlew.bat compileDevDebugKotlin
./gradlew.bat assembleDevDebug
```

Ensure compilation and APK assembly succeed cleanly with exit code 0.

### 5. Upload to Firebase App Distribution

Execute the automated distribution Gradle task:

```bash
./gradlew.bat appDistributionUploadDevDebug
```

> [!NOTE]
> If authentication is needed in CI/CD or new environments, ensure `FIREBASE_TOKEN` or `GOOGLE_APPLICATION_CREDENTIALS` is present in the environment, or run `firebase login`.

The APK artifact will be output to:
`app/build/outputs/apk/dev/debug/app-dev-debug.apk`

### 6. Create Annotated Git Tag

Tag the exact release commit with an annotated tag:

```bash
git tag -a v<version>-dev -m "AuraPlayer DEV delivery v<version>-dev to Firebase App Distribution"
```

### 7. Push Branch and Tags to Remote

Push the delivery branch and the release tag to `origin`:

```bash
git push origin release/v<version>-dev --tags
```

Synchronize the mainline branch (`master`):

```bash
git checkout master
git merge release/v<version>-dev
git push origin master
```

### 8. Provide Verification & Feedback

Report the distribution summary to the user:
- **Version Name**: e.g. `1.0.1-dev`
- **Version Code**: e.g. `10011`
- **Git Branch**: `release/v<version>-dev` (pushed to origin)
- **Git Tag**: `v<version>-dev` (pushed to origin)
- **Tester Group**: `internal-testers` (`shabeenmhmd07@gmail.com`)
- **Local Artifact**: `app/build/outputs/apk/dev/debug/app-dev-debug.apk`
- **Firebase Release URL**: Console and tester download links output by Gradle
