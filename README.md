GridKA 2014 Android Workshop
============================

This is the sample app for the GridKA workshop "Getting started with Android and App Engine".

You can find the main activity at: [MainActivity.java](app/src/main/java/de/timroes/training/ican/MainActivity.java)

This Activity also has the AsynTasks to load data from the App Engine endpoint as inner classes.

App Engine
-----------
The actual App Engine endpoint can be found in: [MyEndpoint.java](backend/src/main/java/de/timroes/training/ican/backend/MyEndpoint.java)

To use your own backend, a new project must be created in the [Google Developers Console](https://console.developers.google.com).
Then you need to add your own App Engine project id to the [appengine-web.xml](backend/src/main/webapp/WEB-INF/appengine-web.xml) file. Also the AsyncTasks in the MainActivity have the name of the cloud endpoint and must be changed.

The backend is built and deployed to App Engine with the Gradle task appengineUpdate executed from a terminal. Make sure that you are in the root directory of the project and execute the following command:
```sh
./gradlew backend:appengineUpdate
```
The task will open up a browser and load the URL for the authentication. Depending on the system, the URL is displayed only in the terminal. Copy the code displayed in the browser to the terminal and press enter. The backend is now deployed and can be used. In your browser, the project can be called with the address **your-project-id.appspot.com**.