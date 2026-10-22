# Spec Delta: delivery-agent-skills

## Purpose

Enables automated, single-action delivery skills for deploying DEV builds to Firebase App Distribution and validating PRO release builds.

## ADDED Requirements

### Requirement: DEV Environment Delivery Skill
The system SHALL provide an agent skill (`deliver-dev`) that compiles the `devDebug` variant and uploads it directly to Firebase App Distribution for tester group `internal-testers`.

#### Scenario: Running deliver-dev skill
- **WHEN** the agent or user triggers the `deliver-dev` skill
- **THEN** `./gradlew appDistributionUploadDevDebug` executes, uploading the latest DEV build to Firebase App Distribution with release notes and notifying the tester group `internal-testers`.

### Requirement: PRO Environment Delivery Skill
The system SHALL provide an agent skill (`deliver-pro`) that compiles and verifies the optimized and signed `proRelease` variant.

#### Scenario: Running deliver-pro skill
- **WHEN** the agent or user triggers the `deliver-pro` skill
- **THEN** `./gradlew assembleProRelease` executes, verifying R8 obfuscation, resources, and producing release artifacts ready for production deployment.
