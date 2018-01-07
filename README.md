# DamicoGabboliniParroni
Software Engineering 2 project - Travlendar+

## Download apk from:
https://www.dropbox.com/s/qoldgag188dm6ra/app-armeabi-v7a-debug.apk?dl=0

# Travlendar Server API

## POST travlendar.000webhostapp.com/travlendar/public/token

### Body
grant_type: client_credentials
client_id: travlendar
client_secret: travlendar

### Desc
Request a bearer token to authorize the client application



## POST travlendar.000webhostapp.com/travlendar/public/token


### Body
grant_type: password
client_id: travlendar
client_secret: travlendar
username: user1@mail.it
password: user1

### Desc
Request a bearer token to authorize the user by username and password




## POST travlendar.000webhostapp.com/travlendar/public/api/user/profile

### Headers
Authorization: Bearer <token>

### Body
email: user1@mail.it
password: user1

### Desc
Request user profile
