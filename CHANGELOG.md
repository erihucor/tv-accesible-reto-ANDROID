# Changelog

## [Unreleased]
### Added
- Add a BLN YouTube channel by merging the first result for its dedicated search term into the channel catalog.
- Load the YouTube API key from the local `local.properties` file.
- Show diagnostic messages for YouTube search, catalog merge, and playback failures.

### Improved
- Separate generic YouTube API searching into `YouTubeSearchClient` and rename the BLN provider to `BlnYouTubeChannelProvider`.
- Show a refresh icon for three seconds after each automatic channel refresh.

## [1.3.0] - 2026-10-15
### Added
- Automatically refresh the channel list every minute while the app is active, keeping the current channel selected when it remains available.


## [1.2.3] - 2026-10-15
### Added
- Display an unavailable-channel image when a channel cannot be played.

## [1.2.2] - 2026-10-15
### Improved
- Prioritize loading the channel list from the online JSON source.
- Use the bundled local `channels.json` as a fallback when the online source is unavailable.
- Generate the bundled channel asset from the root `channels.json` file to keep a single editable source

## [1.2.1] - 2026-10-15
### Fixed
- Move the JSON to main branch
- Unique version of channels file

## [1.2.0] - 2026-10-14
stable release of TVAccesibleAndroid.

### Added
- Youtube Player with information display.
- Channel list loading from a local/online source.
- New UI with type of stream
- Channel Model Data STREAM/YOUTUBE | ChannelSource

## [1.1.0] - 2026-10-14

- Touch Zones controls to changes channels


## [1.0.0] - 2026-10-14

First stable release of TVAccesibleAndroid.

### Added
- IPTV video playback using M3U8 stream URLs.
- Online channel list loading from a JSON source.
- Channel selection and navigation controls with up/down button.
- Android TV launcher support.
- Auto Full-screen video playback.
- Channel information display.

### Improved
- User interface and channel presentation.
- Splash screen and application launcher icon.
- Playback behavior and channel switching.
- Error handling and user feedback.
