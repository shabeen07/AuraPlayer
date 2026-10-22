# Spec Delta

## Purpose

Enables multi-environment configuration supporting isolated DEV and PRO builds with dedicated application identifiers, display names, and unified Firebase project registration.

## ADDED Requirements

### Requirement: Multi-Environment Gradle Flavor Separation
The build system SHALL configure two distinct product flavors (`dev` and `pro`) within an `environment` dimension based on base application ID `dev.shabeen.aura.player`.

#### Scenario: DEV flavor build identification
- **WHEN** building the `dev` variant (`devDebug` or `devRelease`)
- **THEN** the application ID SHALL be `dev.shabeen.aura.player.dev`, the version name SHALL have suffix `-dev`, and the launcher title SHALL be "Aura Player (DEV)"

#### Scenario: PRO flavor build identification
- **WHEN** building the `pro` variant (`proDebug` or `proRelease`)
- **THEN** the application ID SHALL be `dev.shabeen.aura.player` and the launcher title SHALL be "Aura Player"

### Requirement: Unified Firebase Configuration Mapping
The build system SHALL bind Google Services configuration to both the `dev.shabeen.aura.player.dev` and `dev.shabeen.aura.player` package identities under a single Firebase project.

#### Scenario: Flavor-specific google-services resolution
- **WHEN** Gradle processes Google Services resources for either flavor
- **THEN** the configuration SHALL resolve the matching client definition from the unified Firebase configuration without compilation errors
