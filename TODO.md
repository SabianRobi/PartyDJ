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
| [ ]  | WatchQueue    | GET    | /party/{partyName}/tracks           | Returns the tracks in queue                       |
| [ ]  | AddTrack      | POST   | /party/{partyName}/tracks           | Adds a track to the queue                         |
| [ ]  | RemoveTrack   | DELETE | /party/{partyName}/tracks/{trackId} | Removes a track form the queue                    |
| [ ]  | SkipTrack     | POST   | /party/{partyName}/tracks/skip      | Skips the current track                           |
| [ ]  | setSpDeviceId | POST   | /party/{partyName}/spotifyDeviceId  | Sets the Spotify Web Playback's device at backend |

### SpotifyCredentials

Auth required for every endpoint

| Done | Name         | Method | Path                        | Description                                             |
|------|--------------|--------|-----------------------------|---------------------------------------------------------|
| [X]  | Login        | GET    | /platforms/spotify/login    | Retrieves the Spotify login link                        |
| [X]  | Callback     | GET    | /platforms/spotify/callback | Spotify login page redirects users here, processes data |
| [X]  | Logout       | POST   | /platforms/spotify/logout   | Disconnects the Spotify from the user                   |
| [X]  | GetToken     | GET    | /platforms/spotify/token    | Returns the user's Spotify token                        |
| [X]  | RefreshToken | PATCH  | /platforms/spotify/token    | Makes the backend refresh the user's Spotify token      |

* Enable CSRF protection
* Update logged-in user's infos on user update
* Log out user when deletes profile

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
