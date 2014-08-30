package de.timroes.training.ican;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tim Roes <mail@timroes.de>
 */
public class LinkAdapter extends BaseAdapter {

	private List<Link> links;
	private Activity activity;

	public LinkAdapter(Activity activity) {
		// Store the activity that created that adapter (we will need it later to load layout xml files, to create item views)
		this.activity = activity;
		// Create a dummy list
		links = new ArrayList<Link>(5);
		links.add(new Link("Tim", "This is my blog.", "https://www.timroes.de"));
		links.add(new Link("Daniel", "I also have a blog.", "http://www.dbaelz.de"));
		links.add(new Link("Someone Else", "Aren't you both working for that company?", "http://www.inovex.de"));
		links.add(new Link("Someone Else", "Aren't you both working for that company?", "http://www.inovex.de"));
		links.add(new Link("Someone Else", "Aren't you both working for that company?", "http://www.inovex.de"));
	}

	public void add(Link link) {
		links.add(0, link);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return links.size();
	}

	@Override
	public Object getItem(int position) {
		return links.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			convertView = activity.getLayoutInflater().inflate(R.layout.link_item, parent, false);
		}

		// Get the item, that should be shown in this view (by its position, that we receive as a
		// parameter to this method call)
		Link item = links.get(position);

		// Get the description TextView and fill it with the current items description
		TextView description = (TextView) convertView.findViewById(R.id.description);
		description.setText(item.getDescription());

		// Do the same for the URL
		TextView url = (TextView) convertView.findViewById(R.id.url);
		url.setText(item.getUrl());

		// .. and for the author
		TextView author = (TextView) convertView.findViewById(R.id.author);
		author.setText(item.getAuthor());

		// Return the filled (and created, if it didn't already exist) view that represents that list item
		return convertView;
	}
}
