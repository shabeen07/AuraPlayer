# Spec Delta: secure-vault

## Purpose

Provides a secure offline private media vault protected by PIN and biometric authentication, storage diagnostics, and peer transfer capabilities.

## ADDED Requirements

### Requirement: PIN and Biometric Security Gate
The Vault screen MUST require PIN entry (or biometric authentication if available) before granting access to private stored media.

#### Scenario: Locked Vault Access
- **WHEN** user taps the "Vault" tab on the bottom navigation
- **THEN** system presents the biometric prompt or 4-digit PIN unlock screen, hiding vault contents until verified

#### Scenario: Successful Vault Unlock
- **WHEN** user enters the correct 4-digit PIN
- **THEN** vault unlocks and reveals private encrypted media files and storage management controls

### Requirement: Storage Diagnostic Gauge
The Vault screen SHALL display an accurate physical flash storage diagnostic card indicating utilized storage, free space, and device storage technology (e.g. UFS / eMMC).

#### Scenario: Inspecting Storage Allocation
- **WHEN** user views the unlocked Vault screen
- **THEN** system renders the multi-segment storage bar showing Video GB, Audio GB, and available capacity

### Requirement: Wi-Fi Direct Peer Transfer Sheet
The Vault screen SHALL provide a Wi-Fi Direct peer sync dialog to simulate or initiate direct offline file transfers between trusted devices.

#### Scenario: Opening Direct Sync Dialog
- **WHEN** user taps "Direct Sync" button
- **THEN** bottom sheet displays peer device discovery, transfer status, and Wi-Fi Direct pairing options
