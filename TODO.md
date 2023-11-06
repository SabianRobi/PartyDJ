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

### Home

| Done | Name | Method | Path | Description           |
|------|------|--------|------|-----------------------|
| [ ]  | Home | GET    | /    | Returns the home page |

### User

| Done | Name     | Method | Path              | Description                   |
|------|----------|--------|-------------------|-------------------------------|
| [ ]  | Register | GET    | /profile/register | Retruns the registration page |
| [ ]  | Register | POST   | /profile/register | Validates the submitted data  |
| [ ]  | Login    | GET    | /profile/login    | Retruns the login page        |
| [ ]  | Login    | POST   | /profile/login    | Validates the submitted data  |
| [ ]  | Logout   | POST   | /profile/logout   | Logs out the user             |
| [ ]  | Profile  | GET    | /profile          | Returns the profile page      |
| [ ]  | Profile  | PATCH  | /profile          | Updates the user's infos      |
| [ ]  | Profile  | DELETE | /profile          | Deletes the profile           |

### Party

| Done | Name          | Method | Path                   | Description                                       |
|------|---------------|--------|------------------------|---------------------------------------------------|
| [ ]  | Landing       | GET    | /party/landing         | Returns the create/join page                      |
| [ ]  | Join          | GET    | /party/join            | Returns the party login page                      |
| [ ]  | Join          | POST   | /party/join            | Validates the submitted data                      |
| [ ]  | Create        | GET    | /party/create          | Returns the party creation page                   |
| [ ]  | Create        | POST   | /party/create          | Validates the submitted data                      |
| [ ]  | Party         | GET    | /party                 | Returns the party page                            |
| [ ]  | Leave         | POST   | /party/leave           | Removes the user from the party                   |
| [ ]  | Delete        | POST   | /party/delete          | Deletes the party                                 |
| [ ]  | Search        | GET    | /party/serach          | Returns the search results                        |
| [ ]  | WatchQueue    | GET    | /party/tracks          | Return the tracks in queue                        |
| [ ]  | AddTrack      | POST   | /party/tracks          | Adds a track to the queue                         |
| [ ]  | RemoveTrack   | DELETE | /party/tracks          | Removes a track form the queue                    |
| [ ]  | SkipTrack     | POST   | /party/tracks/skip     | Skips the current track                           |
| [ ]  | setSpDeviceId | POST   | /party/spotifyDeviceId | Sets the Spotify Web Playback's device at backend |

### SpotifyCredentials

| Done | Name         | Method | Path                          | Description                                        |
|------|--------------|--------|-------------------------------|----------------------------------------------------|
| [ ]  | Connect      | POST   | /platforms/spotify/connect    | Redirects the user to the Spotify login page       |
| [ ]  | Connect      | _POST_ | /platforms/spotify/callback   | Spoitfy login page redirects users here            |
| [ ]  | Disconnect   | POST   | /platforms/spotify/disconnect | Disconnects the Spotify from the user              |
| [ ]  | GetToken     | GET    | /platforms/spotify/token      | Returns the user's Spotify token                   |
| [ ]  | RefreshToken | PATCH  | /platforms/spotify/token      | Makes the backend refresh the user's Spotify token |

[ ] Add cascade options for db relations.
[ ] Encrypt passwords

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
