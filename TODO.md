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

### Home

| Done | Name | Method | Path | Description           |
|------|------|--------|------|-----------------------|
| [F]  | Home | GET    | F: / | Returns the home page |

### User

| Done | Name     | Method | Path              | Description                                | Auth required? |
|------|----------|--------|-------------------|--------------------------------------------|----------------|
| [F]  | Register | GET    | F: /user/register | Shows the registration page                | no             |
| [X]  | Register | POST   | /user             | Validates the submitted data & saves to DB | no             |
| [F]  | Login    | GET    | F: /user/login    | Shows the login page                       | no             |
| [X]  | Login    | POST   | /login            | Validates the submitted data & updates DB  | no             |
| [X]  | Logout   | POST   | /logout           | Logs out the user                          | yes            |
| [X]  | Profile  | GET    | /user/{userId}    | Returns the user infos                     | yes            |
| [X]  | Profile  | PATCH  | /user/{userId}    | Updates the user infos                     | yes            |
| [X]  | Profile  | DELETE | /user/{userId}    | Deletes the profile                        | yes            |

### Party

Auth required for every endpoint

| Done | Name          | Method | Path                                | Description                                       |
|------|---------------|--------|-------------------------------------|---------------------------------------------------|
| [F]  | Landing       | GET    | F: /party/landing                   | Returns the create/join page                      |
| [F]  | Join          | GET    | F: /party/join                      | Returns the party login page                      |
| [X]  | Join          | POST   | /party/{partyName}/join             | Validates the submitted data                      |
| [F]  | Create        | GET    | F: /party/create                    | Returns the party creation page                   |
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

* Throw 401 Unauthorized instead of redirecting to login page
* Add tests
* Add CI
* [SHELVED] Fix UniqueUsernameValidator/Email: maybe try: entityManager.setFlushMode(FlushModeType.COMMIT);
* [SHELVED] UserController#67
* When frontend comes alive: remove PageController
* When frontend comes alive: Enable CSRF protection

# FrontEnd

## Pages

- [ ] Home
- [ ] Login
    - [ ] Input validation
    - [ ] Error handling (backend responses)
- [ ] Register
    - [ ] Input validation
    - [ ] Error handling (backend responses)
- [ ] Profile
    - [ ] List infos
    - [ ] Let the user change infos
        - [ ] Input validation
        - [ ] Error handling (backend responses)
- [ ] Party landing
- [ ] Join Party
    - [ ] Input validation
    - [ ] Error handling (backend responses)
- [ ] Create Party
    - [ ] Input validation
    - [ ] Error handling (backend responses)
- [ ] Party
    - [ ] Input validation (searchBar)
    - [ ] Error handling - to all requests that can be made to backend and during init of the Sp/YT players
