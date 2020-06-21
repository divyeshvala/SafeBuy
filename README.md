# SafeBuy

### Basic flow of the app
- When App starts MainActivity.java file will be launched. MainActivity is located in  app\src\main\java\com\example\app\Activities\MainActivity.java
- Right now we are requesting necessary permissions in MainActivity. It will change once UI is ready.
- Then control goes to HomeActivity.java. Here we are setting up location listener to get location updates whenever location changes. 
  - Once HomeActivity gets broadcast of the location, it will upload request to get nearby ATMs on Firebase realtime database using GetNearbyATMs object.
  - A local server written in python will fetch this request from database. Now this server will use VISA ATM Locator API to get list of nearby ATMs.
  - After getting that list python server will upload this list to firebase server.
  - Now App will fetch this list and delete this request from firebase server.
  - In HomeActivity it will be checked which ATMs lie in containment zone and which doesn't. List will be displayed accordingly.
  
 #### Note - There are utility files like DirectionJSONParser.java, GetDirections.java and MapsActivity.java which haven't been used yet. They will be used later for showing directions.
  
