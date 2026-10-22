# Spec Delta: clean-ui-architecture

## MODIFIED Requirements

### Requirement: Collision-Free Mini Player Dock
The system SHALL coordinate content insets across all scrollable tab screens to prevent the floating mini-player dock from obscuring content, and provide an intuitive, responsive mini-player interface with streamlined two-button transport controls, dedicated tap-to-expand routing, an edge-to-edge flush micro-progress bar, and media format indicators.

#### Scenario: Mini-player visible over list content
- **WHEN** the floating mini-player is visible at the bottom of the screen
- **THEN** the bottom content padding of scrollable tabs increases so all items can be scrolled completely above the dock.

#### Scenario: Tapping mini-player expands to full player
- **WHEN** the user taps on the artwork or metadata area of the mini-player dock
- **THEN** the system immediately navigates to the full PlayerScreen without gesture consumption interference or playback interruption.

#### Scenario: Mini-player transport actions
- **WHEN** the user interacts with the mini-player dock
- **THEN** the interface exposes a prominent circular Play/Pause toggle and a subtle Dismiss button without overcrowding the metadata area.
