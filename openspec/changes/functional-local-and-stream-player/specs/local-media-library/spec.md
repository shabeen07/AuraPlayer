# Spec Delta

## Purpose

Provides discovery and retrieval of local device audio and video files from the Android MediaStore, handling runtime storage permissions and delivering structured metadata to the UI.

## ADDED Requirements

### Requirement: Storage Permission Handling
The application SHALL request appropriate storage permissions (`READ_MEDIA_AUDIO`, `READ_MEDIA_VIDEO` on Android 13+ / API 33+, and `READ_EXTERNAL_STORAGE` on API <33) before querying device storage. If permissions are denied, the system SHALL inform the user and provide a permission grant action.

#### Scenario: User grants media permission
- **WHEN** user launches the app or taps the grant permission button and approves access
- **THEN** system proceeds to scan and load local audio and video files from the device MediaStore

#### Scenario: User denies media permission
- **WHEN** user denies media storage permission
- **THEN** system displays an informative empty state explaining that storage access is required to view local media, with a button to open settings or re-request

### Requirement: Local Media Discovery via MediaStore
The system SHALL query the Android `MediaStore` content provider for both video (`MediaStore.Video.Media.EXTERNAL_CONTENT_URI`) and audio (`MediaStore.Audio.Media.EXTERNAL_CONTENT_URI`) files located on device internal and external storage. The query results SHALL include title, artist/author, display name, duration, file size, content URI, and thumbnail URI.

#### Scenario: Device contains local audio and video files
- **WHEN** storage permission is granted and media discovery runs
- **THEN** system populates the media list with real tracks and videos from the device with formatted durations and sizes

#### Scenario: No media files found on device
- **WHEN** storage permission is granted but device contains zero media files
- **THEN** system displays an empty state indicating no local media was found on the device

### Requirement: Media Category Filtering
The system SHALL provide category filtering enabling the user to switch between "All Media", "Videos", and "Audio".

#### Scenario: Filter by Videos
- **WHEN** user selects the "Videos" category filter
- **THEN** system displays only video items from the scanned media list

#### Scenario: Filter by Audio
- **WHEN** user selects the "Audio" category filter
- **THEN** system displays only audio tracks from the scanned media list

#### Scenario: Filter by All Media
- **WHEN** user selects the "All Media" category filter
- **THEN** system displays both audio and video items sorted by date modified or title
