# Spec Delta: video-player-suite

## MODIFIED Requirements

### Requirement: True Immersive Fullscreen Mode
The system SHALL support toggling true immersive fullscreen mode during video playback, rotating the display to landscape, hiding system status and navigation bars, and preserving continuous uninterrupted media playback and decoder surface attachment across orientation transitions.

#### Scenario: User enters fullscreen mode
- **WHEN** the user taps the fullscreen toggle button while video is playing
- **THEN** the display orientation changes to sensor landscape, system bars are hidden, the video container expands edge-to-edge, and playback continues seamlessly without resetting position, buffering restart, or pausing.

#### Scenario: User exits fullscreen mode
- **WHEN** the user taps the collapse button while in fullscreen mode or presses the system back button
- **THEN** the display orientation restores to portrait mode, system bars are restored, and playback continues continuously without interruption.
