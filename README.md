GridKA 2014 Android Workshop
============================

This is the sample app for the GridKA workshop "Getting started with Android and App Engine".

You can find the main activity at: [MainActivity.java](app/src/main/java/de/timroes/training/ican/MainActivity.java)

This Activity also has the AsynTasks to load data from the App Engine endpoint as inner classes.

The actual App Engine endpoint can be found in: [MyEndpoint.java](backend/src/main/java/de/timroes/training/ican/backend/MyEndpoint.java)

You will need to add your own App Engine project id to the [appengine-web.xml](backend/src/main/webapp/WEB-INF/appengine-web.xml) file,
to use this with your own backend. Also the AsyncTasks in the MainActivity have the name of the cloud endpoint.