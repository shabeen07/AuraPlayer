# Spec Delta: smooth-screen-transitions

## Purpose

Introduces smooth, cohesive motion transitions across all top-level backstack destinations and bottom navigation tabs.

## ADDED Requirements

### Requirement: Fluid Tab Switching Motion
The main shell container MUST animate transitions between tabs (Home, Library, Vault, Settings) using smooth fade and directional slide transitions rather than instantaneous cuts.

#### Scenario: Switching Between Main Tabs
- **WHEN** user taps a different tab in the bottom navigation bar
- **THEN** outgoing tab smoothly fades out while the incoming tab fades and slides in

### Requirement: Fullscreen Player Transition
Navigating between the main shell and the full player MUST animate with a vertical slide/scale transition simulating expanding out of and collapsing into the mini-player dock.

#### Scenario: Expanding Player from Dock
- **WHEN** user taps the mini-player dock to open the full player
- **THEN** player screen slides up smoothly from the bottom of the display

#### Scenario: Dismissing Full Player Back to Shell
- **WHEN** user taps back on the player screen
- **THEN** player screen slides downward back into the main shell
