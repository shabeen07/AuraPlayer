# Spec Delta: production-and-versioning

## Purpose

Establishes build configuration, ProGuard code shrinking rules, semantic versioning infrastructure, and Git version control for production releases.

## ADDED Requirements

### Requirement: Semantic Versioning Infrastructure
The application MUST configure explicit semantic versioning (`versionCode`, `versionName`) via `version.properties` and enable `buildConfig` in Gradle to expose version metadata to the runtime UI.

#### Scenario: Runtime Version Display
- **WHEN** user views the Settings or About dialog
- **THEN** system renders the exact version name (e.g. `1.0.0`) and build code derived from build configuration

### Requirement: ProGuard and R8 Rules
The project MUST include a comprehensive `proguard-rules.pro` file with keep rules for Media3, Coil, Kotlin Serialization, and Compose, allowing release builds to be minified without runtime reflection failures.

#### Scenario: Release Build Compilation
- **WHEN** release build task is run with ProGuard rules
- **THEN** compilation succeeds without missing class warnings or broken serialization descriptors

### Requirement: Git Version Control Setup
The repository MUST be initialized with Git, valid `.gitignore` rules, clean commit history, and a tagged initial production release `v1.0.0`.

#### Scenario: Repository Version Check
- **WHEN** developer runs git status or inspects tags
- **THEN** repository is clean, tracking all source files, and has tag `v1.0.0`
