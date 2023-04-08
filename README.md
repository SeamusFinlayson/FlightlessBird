# Flightless Bird
*Developed by Seamus Finlayson & Mike Andrews*

The Flightless Bird app for android allows you to generate a spontaneous vacation using the UK rail network.

## Features
* Retrieves train route data from [TransportAPI](https://developer.transportapi.com)
* Displays route end points on a map using [Google Maps Places API Text Search](https://developers.google.com/maps/documentation/places/web-service/search-text)
* Generates routes recursively using data from the previous TransportAPI call to generate the next call
* Creates user accounts using [Firebase Authentication](https://firebase.google.com/docs/auth)
* Saves routes to [Firebase Firestore](https://firebase.google.com/docs/firestore) automatically upon creation
* Removes vacations form firebase with the click of a button
* Displays rail service provider, departure date, departure time, and arrival time
* Sleek UI design inspired by [Material 3](https://m3.material.io)
* Both light and dark theme
