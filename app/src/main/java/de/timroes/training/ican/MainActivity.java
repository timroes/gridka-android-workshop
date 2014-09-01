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
//		Link newLink = new Link(authorText, descText, urlText);
//		adapter.add(newLink);
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

	private class GetLinksTask extends AsyncTask<Void, Void, List<Link>> {

		@Override
		protected List<Link> doInBackground(Void... params) {
			Ican.Builder builder = new Ican.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null);
			builder.setApplicationName("timroes-cloud");
			Ican ican = builder.build();
			try {
				LinkCollection links = ican.links().execute();
				return links.getItems();
			} catch (IOException e) {
				Log.e("ICAN", "could not load links", e);
			}
			return new ArrayList<Link>(0);
		}

		@Override
		protected void onPostExecute(List<Link> links) {
			adapter.setLinks(links);
		}
	}

	private class CreateLinkTask extends AsyncTask<Void, Void, Link> {

		private String description;
		private String url;
		private String author;

		private CreateLinkTask(String description, String url, String author) {
			this.description = description;
			this.url = url;
			this.author = author;
		}

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

		@Override
		protected void onPostExecute(Link link) {
			if(link != null) {
				adapter.add(link);
			}
		}
	}
}
