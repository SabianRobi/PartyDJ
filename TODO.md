# BackEnd

## Database models:

| Name               | Parameters                                                                            |
|--------------------|---------------------------------------------------------------------------------------|
| User               | email\*, username\*, password\*                                                       |
| Party              | creator\* [userId], name\*, password                                                  |
| SpotifyCredentials | owner\* [userId], token\*, refreshToken\*, state                                      |
| TrackInQueue       | addedBy\* [userId], party\* [partyId], platform\*, uri\*, score\*, currentlyPlaying\* |
| TrackAlreadyPlayed | addedBy\* [userId], party\* [partyId], platform\*, uri\*, endedAt                     |

## Endpoints:

Backend base uri:
localhost:8080/api/v1/

### Home

| Done | Name | Method | Path | Description           |
|------|------|--------|------|-----------------------|
| [ ]  | Home | GET    | /    | Returns the home page |

### User

| Done | Name     | Method | Path              | Description                                |
|------|----------|--------|-------------------|--------------------------------------------|
| [F]  | Register | GET    | F: /user/register | Shows the registration page                |
| [X]  | Register | POST   | /user             | Validates the submitted data & saves to DB |
| [F]  | Login    | GET    | F: /user/login    | Shows the login page                       |
| [ ]  | Login    | POST   | /user/login       | Validates the submitted data & updates DB  |
| [ ]  | Logout   | POST   | /user/logout      | Logs out the user                          |
| [X]  | Profile  | GET    | /user/{userId}    | Returns the user infos                     |
| [X]  | Profile  | PATCH  | /user/{userId}    | Updates the user infos                     |
| [X]  | Profile  | DELETE | /user/{userId}    | Deletes the profile                        |

### Party

| Done | Name          | Method | Path                                | Description                                       |
|------|---------------|--------|-------------------------------------|---------------------------------------------------|
| [F]  | Landing       | GET    | F: /party/landing                   | Returns the create/join page                      |
| [F]  | Join          | GET    | F: /party/join                      | Returns the party login page                      |
| [ ]  | Join          | POST   | /party/join                         | Validates the submitted data                      |
| [F]  | Create        | GET    | F: /party/create                    | Returns the party creation page                   |
| [X]  | Create        | POST   | /party/create                       | Validates the submitted data                      |
| [F]  | Party         | GET    | F: /party/{partyName}               | Returns the party page                            |
| [ ]  | Leave         | POST   | /party/{partyName}/leave            | Removes the user from the party                   |
| [ ]  | Delete        | DELETE | /party/{partyName}                  | Deletes the party                                 |
| [ ]  | Search        | GET    | /party/search                       | Returns the search results                        |
| [ ]  | WatchQueue    | GET    | /party/{partyName}/tracks           | Returns the tracks in queue                       |
| [ ]  | AddTrack      | POST   | /party/{partyName}/tracks           | Adds a track to the queue                         |
| [ ]  | RemoveTrack   | DELETE | /party/{partyName}/tracks/{trackId} | Removes a track form the queue                    |
| [ ]  | SkipTrack     | POST   | /party/{partyName}/tracks/skip      | Skips the current track                           |
| [ ]  | setSpDeviceId | POST   | /party/{partyName}/spotifyDeviceId  | Sets the Spotify Web Playback's device at backend |

### SpotifyCredentials

| Done | Name         | Method | Path                          | Description                                        |
|------|--------------|--------|-------------------------------|----------------------------------------------------|
| [ ]  | Connect      | POST   | /platforms/spotify/connect    | Redirects the user to the Spotify login page       |
| [ ]  | Connect      | _POST_ | /platforms/spotify/callback   | Spotify login page redirects users here            |
| [ ]  | Disconnect   | POST   | /platforms/spotify/disconnect | Disconnects the Spotify from the user              |
| [ ]  | GetToken     | GET    | /platforms/spotify/token      | Returns the user's Spotify token                   |
| [ ]  | RefreshToken | PATCH  | /platforms/spotify/token      | Makes the backend refresh the user's Spotify token |

[ ] Add cascade options for db relations. - test them
[ ] Encrypt passwords
[ ] User patch endpoint

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
