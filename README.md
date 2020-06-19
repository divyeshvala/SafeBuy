# SafeBuy

### Basic flow of the app
- When App starts MainActivity.java file will be launched. MainActivity is located in  app\src\main\java\com\example\app\Activities\MainActivity.java
- Right now we are requesting necessary permissions in MainActivity. It will change once UI is ready.
- Then control goes to HomeActivity.java. Here we are setting up location listener to get location updates whenever location changes.
- When you click Get Nearby ATMs in HomeActivity.java control goes to MapsActivity.java.
- MapsActivity requests for nearby ATMs :
  - Once it gets broadcast of the location, it will upload request to get nearby ATMs on Firebase realtime database.
  - A local server written in python will fetch this request from database. Now this server will use VISA ATM Locator API to get list of nearby ATMs.
  - After getting that list it will upload this list to firebase server.
  - Now App will fetch this list and delete this request from firebase server.
  - App will display those ATMs.
  
 #### Note - There are utility files like DirectionJSONParser.java, GetDirections.java which haven't been used yet. They will be used later.
  
