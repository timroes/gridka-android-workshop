package de.timroes.training.ican;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.timroes.training.ican.backend.ican.Ican;
import de.timroes.training.ican.backend.ican.model.Link;
import de.timroes.training.ican.backend.ican.model.LinkCollection;


public class MainActivity extends Activity {

	private static final String PREFS_USERSETTINGS = "de.timroes.training.ican.USER_SETTINGS";
	private static final String PREFS_USERNAME = "de.timroes.training.ican.USER_SETTINGS.USER_NAME";

	private LinkAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		// Use res/layout/activity_main.xml as layout for this activity
		// This call must be made before any findViewById() calls in this method!
        setContentView(R.layout.activity_main);

		// Create a new adapter for the link list and store it in a field
		adapter = new LinkAdapter(this);

		// Get the reference to the list in the layout and set the adapter we just
		// created to this list
		ListView linkList = (ListView) findViewById(R.id.link_list);
		linkList.setAdapter(adapter);

		// Attach an OnItemClickListener, that will be called whenever the user clicks
		// a list item. The listener gets passed the list (as the AdapterView<?> which is a more generic parent)
		// and the item position, that the user clicked, besides others. Use the adapter to get the link item at
		// the clicked position and open that link in a browser. See the visitLink method below.
		linkList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				visitLink((Link) adapter.getItem(position));
			}
		});

		// Check whether the shared preferences contain a username.
		SharedPreferences sharedPrefs = getSharedPreferences(PREFS_USERSETTINGS, MODE_PRIVATE);
		if(sharedPrefs.contains(PREFS_USERNAME)) {
			// If they do, get a reference to the author input field and insert the stored username to that field.
			// The username is stored in the onPause method at the bottom of this class.
			EditText authorInput = (EditText) findViewById(R.id.link_author);
			authorInput.setText(sharedPrefs.getString(PREFS_USERNAME, ""));
		}

		// Load all links from network when the activity is created
		loadLinks();

	}

	private void loadLinks() {
		// Create a new instance of the GetLinkTask which is an AsyncTask, that loads the links from
		// App Engine in the background. See the documentation of the GetLinkTask below.
		// The execute methods, will start the execution of this task, and call the appropriate methods in their
		// appropriate threads.
		new GetLinksTask().execute();
	}

	/**
	 * Visit a link, by creating an intent with the VIEW action, and the link that should be "viewed"
	 * as the data of the intent. Then use the startActivity method to start an activity, that can
	 * fulfill this action on the given data (what will be most likely a web browser).
	 *
	 * @param link the link that should be visited.
	 */
	private void visitLink(Link link) {
		Intent in = new Intent(Intent.ACTION_VIEW);
		in.setData(Uri.parse(link.getUrl()));
		startActivity(in);
	}


	/**
	 * This method will be called when you click the share button and want to share a link.
	 * We use the android:onClick attribute in res/layout/activity_main.xml on the button to tell it the
	 * name of this method.
	 *
	 * @param view the button that has been clicked. We don't need it, but for android to detect
	 *             this method, it need to have exactly one argument of type View.
	 */
	public void shareLink(View view) {

		// Get a reference to the three input fields (EditText).
		EditText description = (EditText) findViewById(R.id.link_description);
		EditText url = (EditText) findViewById(R.id.link_url);
		EditText author = (EditText) findViewById(R.id.link_author);

		// Since we have a reference to the field now, we can get the text the user entered.
		// getText() will return an object of kind Editor, which has more methods, then just
		// returning the text the user entered. Call toString() on it, to just get the text
		// as a String.
		String descText = description.getText().toString();
		String urlText = url.getText().toString();
		String authorText = author.getText().toString();

		// Create a new Link object from the entered information.
		// Start a new AsyncTask therefor.
		new CreateLinkTask(descText, urlText, authorText).execute();

		// Clear the description and url field.
		description.setText("");
		url.setText("");

	}

	/**
	 * Will be called when the activity hasn't the focus anymore.
	 * We will use this method to store the name the user entered to shared preferences,
	 * so we can restore it the next time the application starts.
	 */
	@Override
	protected void onPause() {
		super.onPause();

		// get a reference to the author input field and check if the user entered at least one character.
		EditText nameField = (EditText) findViewById(R.id.link_author);
		if(nameField.getText().length() > 0) {
			// Get a shared preference object to store the username in
			SharedPreferences sharedPrefs = getSharedPreferences(PREFS_USERSETTINGS, MODE_PRIVATE);
			// Put the username in the PREFS_USERNAME field in these shared preferences.
			sharedPrefs.edit().putString(PREFS_USERNAME, nameField.getText().toString()).apply();
		}

	}

	/**
	 * The AsynTask that loads all links from the App Engine endpoints.
	 * It makes use of the links method in the MyEndpoint class.
	 *
	 * An AsyncTask has several methods, that you can overwrite that will be executed in different threads.
	 * Do the work you need to do in the appropriate method.
	 *
	 * It takes three generic parameters, that will be used to pass data through the class.
	 *
	 * The first parameter (in this case Void for nothing), describes the input that the task takes.
	 * You can pass several parameters of this type to the execute method, when you start this thread.
	 * These parameters will be passed into the doInBackground method.
	 *
	 * The second parameter (again Void here) is used for publishing the progress of the task. You can call
	 * publishProgress and pass it parameters of this type, during the doInBackground method, to refresh your UI,
	 * while the background task is running. If you publish progress you also need to implement onProgressUpdate, which
	 * will get these updates passes as parameters and you can update your UI here.
	 *
	 * The third parameter is the return type of the background task. The doInBackground method, needs to return an object
	 * of that type when it finished doing its background task. That result will be passed to the onPostExecute method,
	 * that runs again in the UI-Thread and can change the UI to represent the result in whatever way.
	 */
	private class GetLinksTask extends AsyncTask<Void, Void, List<Link>> {

		/**
		 * This method is the method, that needs to do all the work, that needs to be done in the background,
		 * i.e. the main work of this task.
		 *
		 * In this case it does all the (possibly slow) network communication.
		 *
		 * It must return the result as specified by the third generic parameter of the class.
		 *
		 * @param params the parameters, that has been passed to the execute method, when starting this task.
		 * @return the result in that case the list of links.
		 */
		@Override
		protected List<Link> doInBackground(Void... params) {
			// The Ican class represents the interface that we defined in MyEndpoint in the backend module.
			// The name will match the name we put in the @Api annotation above the class.
			// We use the Builder inner class to generate a new instance of the interface.
			// In a proper application, we wouldn't regenerate this every time we need to make a call, but build it one
			// time and save it for further requests.
			Ican.Builder builder = new Ican.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null);

			// We need to set the id of the application, that we want to connect to. It is the same, that we specified
			// in the Google developer console and the one we put in the webapp/WEB-INF/appengine-web.xml file in the
			// backend.
			builder.setApplicationName("timroes-cloud");

			// The following code is only required, if you want to develop with a local endpoint (not uploaded to Google),
			// what we haven't shown in the workshop. You need to disable GZip for a local endpoint.
			// We just have this in here, for the case you want to continue developing on the project at home and this
			// might be one of the first issues you would run into.
//			builder.setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
//				@Override
//				public void initialize(AbstractGoogleClientRequest<?> request) throws IOException {
//					request.setDisableGZipContent(true);
//				}
//			});

			// Use the builder to create an instance of the app engine interface with the specified configuration.
			Ican ican = builder.build();

			try {
				// Call the links method on the interface and execute the call.
				// The name of the method (links) is the same, that we specified in the @ApiMethod annotation in the
				// endpoint.
				LinkCollection links = ican.links().execute();
				// From the result returned we can use the getItems method to get the actual list of results.
				return links.getItems();
			} catch (IOException e) {
				// Something failed during networking.. Log this as an error to the console.
				Log.e("ICAN", "Could not load links", e);
			}

			// If something failed (we didn't reach the above return statement) return an empty ArrayList.
			return new ArrayList<Link>(0);
		}

		/**
		 * This method will be executed after doInBackground finished, and will get the result that doInBackground
		 * returned as a parameter.
		 *
		 * This method runs again in the UI-Thread and is meant to do everything that needs to be changed at the UI
		 * to represent the result of the task.
		 *
		 * In this case we just set the received links to the adapter.
		 *
		 * @param links the result as returned from doInBackground
		 */
		@Override
		protected void onPostExecute(List<Link> links) {
			adapter.setLinks(links);
		}


	}

	/**
	 * The AsynTask to create a new link in the backend.
	 * This is basically the same as above so we skip most of the documentation here.
	 *
	 * As mentioned above the first generic parameter can be used to pass values to the execute method, that will be
	 * passed to the doInBackground method. So we could have used String as a first generic type and pass the three required
	 * Strings to the execute method.
	 * We didn't do it, because when using constructor parameters we have a clear method with parameters, that we can
	 * access by their names.
	 */
	private class CreateLinkTask extends AsyncTask<Void, Void, Link> {

		private String description;
		private String url;
		private String author;

		/**
		 * Create a new instance and pass all required parameters to create a link.
		 */
		private CreateLinkTask(String description, String url, String author) {
			this.description = description;
			this.url = url;
			this.author = author;
		}

		/**
		 * Create the link in the background at the endpoint interface. Same as above.
		 */
		@Override
		protected Link doInBackground(Void... params) {

			Ican.Builder builder = new Ican.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null);
			builder.setApplicationName("timroes-cloud");
			Ican ican = builder.build();

			try {
				return ican.addLink(description, url, author).execute();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		/**
		 * Add the link to the adapter in the UI-Thread.
		 */
		@Override
		protected void onPostExecute(Link link) {
			if(link != null) {
				adapter.add(link);
			}
		}
	}
}
