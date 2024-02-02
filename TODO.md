# BackEnd

## Database models:

| Name               | Parameters                                                                                                                        |
|--------------------|-----------------------------------------------------------------------------------------------------------------------------------|
| User               | id\*, email\*, username\*, password\*, userType\*, partyRole, spotifyCredential [SpotifyCredential], party [Party]                |
| Party              | id\*, name\*, password\*, spotifyDeviceId, waitingForTrack, participants [Users], TracksInQueue [Tracks], PreviousTracks [Tracks] |
| SpotifyCredentials | id\*, token, refreshToken, state, owner [User]                                                                                    |
| TrackInQueue       | id\*, uri\*, score\*, addedBy [User]                                                                                              |
| PreviousTrack      | id\*, uri\*, score\*                                                                                                              |

## Endpoints:

Backend base uri:
localhost:8080/api/v1/

### User

| Done | Name     | Method | Path             | Description                                | Auth required? |
|------|----------|--------|------------------|--------------------------------------------|----------------|
| [X]  | Register | POST   | /user            | Validates the submitted data & saves to DB | no             |
| [X]  | Login    | POST   | /login           | Validates the submitted data & updates DB  | no             |
| [X]  | Logout   | POST   | /logout          | Logs out the user                          | yes            |
| [X]  | Profile  | GET    | /user/{username} | Returns the user infos                     | yes            |
| [X]  | Profile  | PUT    | /user/{username} | Updates the user infos                     | yes            |
| [X]  | Profile  | DELETE | /user/{username} | Deletes the profile                        | yes            |

### Party

Auth required for every endpoint

| Done | Name          | Method | Path                                | Description                                       |
|------|---------------|--------|-------------------------------------|---------------------------------------------------|
| [X]  | Join          | POST   | /party/{partyName}/join             | Validates the submitted data                      |
| [X]  | Create        | POST   | /party/create                       | Validates the submitted data                      |
| [X]  | Party         | GET    | /party/{partyName}                  | Returns the party infos                           |
| [X]  | Leave         | POST   | /party/{partyName}/leave            | Removes the user from the party                   |
| [X]  | Delete        | DELETE | /party/{partyName}                  | Deletes the party                                 |
| [X]  | Search        | GET    | /party/{partyName}/search           | Returns the search results                        |
| [X]  | WatchQueue    | GET    | /party/{partyName}/tracks           | Returns the tracks in queue                       |
| [X]  | GetPrevTracks | GET    | /party/{partyName}/tracks/previous  | Returns the tracks that already have played       |
| [X]  | AddTrack      | POST   | /party/{partyName}/tracks           | Adds a track to the queue                         |
| [X]  | RemoveTrack   | DELETE | /party/{partyName}/tracks/{trackId} | Removes a track form the queue                    |
| [X]  | SkipTrack     | POST   | /party/{partyName}/tracks/playNext  | Skips the current track                           |
| [X]  | setSpDeviceId | POST   | /party/{partyName}/spotifyDeviceId  | Sets the Spotify Web Playback's device at backend |

### SpotifyCredentials

Auth required for every endpoint

| Done | Name         | Method | Path                        | Description                                             |
|------|--------------|--------|-----------------------------|---------------------------------------------------------|
| [X]  | Login        | GET    | /platforms/spotify/login    | Retrieves the Spotify login link                        |
| [X]  | Callback     | GET    | /platforms/spotify/callback | Spotify login page redirects users here, processes data |
| [X]  | Logout       | POST   | /platforms/spotify/logout   | Disconnects the Spotify from the user                   |
| [X]  | GetToken     | GET    | /platforms/spotify/token    | Returns the user's Spotify token                        |
| [X]  | RefreshToken | PATCH  | /platforms/spotify/token    | Makes the backend refresh the user's Spotify token      |

* Create liveness endpoint
* Split user password update into its own endpoint
* Separate model, service, web layer into separate projects
    * Service repository methods should be private
* Add tests
    * Relation cascading tests
    * Constraints
    * Services
    * Controllers
* Add readme (introduction, configuring, running)
* Recommend songs when no tracks in queue
* Make documentation
* Introduce ModelMapper instead of creating them manually

* Add option to select playback device id on Spotify
* Add YouTube support
* When frontend comes alive
    * Enable CSRF protection
    * Check: when login fails -> response is 200 and no errors returned (does not log in)
  