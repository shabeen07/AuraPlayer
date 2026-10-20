# Spec Delta: settings-and-equalizer

## Purpose

Provides comprehensive audio customization via a 5-band graphic equalizer, audio effect presets, playback configuration, and cache management.

## ADDED Requirements

### Requirement: 5-Band Graphic Equalizer
The system MUST provide a 5-band equalizer connected to the active Media3 audio session, supporting frequency adjustments at 60 Hz, 230 Hz, 910 Hz, 3.6 kHz, and 14 kHz.

#### Scenario: Adjusting Equalizer Band
- **WHEN** user moves the 60 Hz equalizer slider in the Settings tab
- **THEN** audio effect adjusts low-frequency output and persists the new band level in audio preferences

### Requirement: Audio Presets and Spatial Virtualizer
The system SHALL provide audio presets (Flat, Bass Boost, Rock, Pop, Jazz, Electronic) and a Spatial Audio / Virtualizer toggle.

#### Scenario: Selecting Equalizer Preset
- **WHEN** user selects the "Bass Boost" preset chip
- **THEN** equalizer bands automatically reposition to pre-configured values and BassBoost is activated

### Requirement: Playback Configuration and Cache Clearing
The Settings screen SHALL allow users to toggle hardware decoder acceleration, background audio playback, and clear temporary image/video disk cache.

#### Scenario: Clearing Cache
- **WHEN** user clicks "Clear Disk Cache" button
- **THEN** system clears Coil cache directory and updates display to reflect freed megabytes
