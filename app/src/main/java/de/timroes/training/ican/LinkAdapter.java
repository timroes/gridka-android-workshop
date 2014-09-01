package de.timroes.training.ican;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.timroes.training.ican.backend.ican.model.Link;

/**
 * This class is an adapter for the Link class.
 *
 * It holds a list of links, and implements all required adapter methods for that list.
 *
 * The view for each item will be created by the getView() method at the bottom of the class.
 *
 * @author Tim Roes <mail@timroes.de>
 */
public class LinkAdapter extends BaseAdapter {

	/**
	 * This list holds all the links in this adapter.
	 */
	private List<Link> links = new ArrayList<Link>(0);

	/**
	 * A reference to the activity, the ListView that uses this adapter is in.
	 * This will be needed to load a layout xml from the resources.
	 */
	private Activity activity;

	/**
	 * Create a new link adapter and store the activity, that created that adapter.
	 *
	 * @param activity the activity that created that adapter.
	 */
	public LinkAdapter(Activity activity) {
		// Store the activity that created that adapter (we will need it later to load layout xml files, to create item views)
		this.activity = activity;
	}

	/**
	 * Add a new link to the adapter.
	 * The link will be added at the first position.
	 * We need to call notifyDataSetChanged() afterwards, so the ListView knows, that the data set has changed,
	 * and that it need to redraw itself (and request new views for new items, or remove some views).
	 *
	 * @param link the link to add to the adapter
	 */
	public void add(Link link) {
		links.add(0, link);
		notifyDataSetChanged();
	}

	/**
	 * Sets a new list of links for this adapter.
	 * Same as the add method we need to tell the listview afterwards, that the data set has changed, so it can redraw
	 * itself.
	 *
	 * @param links the list of links
	 */
	public void setLinks(List<Link> links) {
		this.links = links;
		notifyDataSetChanged();
	}

	/**
	 * Returns the size of elements in this adapter.
	 * In this case it will return the size of the list, that holds all links.
	 *
	 * @return the size of elements in this adapter.
	 */
	@Override
	public int getCount() {
		return links.size();
	}

	/**
	 * Returns the item at a specific position.
	 * This doesn't return the View, that represents the item (see getView() for this), but the item itself,
	 * in this case a Link object.
	 * We just take out that element from the specific position in the list.
	 *
	 * @param position the position of the element.
	 * @return the item at this position.
	 */
	@Override
	public Object getItem(int position) {
		return links.get(position);
	}

	/**
	 * This should return the id for a specific item at the given position.
	 *
	 * In our case the id doesn't need to be "stable" (i.e. the id must always
	 * be the same for a specific item), so we can just return the position as
	 * an id. If elements move in the list, that won't be a problem, due to the
	 * mentioned "unstable" ids.
	 *
	 * In most of the cases you can just return the position here, if you implement
	 * a BaseAdapter.
	 *
	 * @param position the position for which to get the id.
	 * @return the id of the item at the specific position.
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * This method creates the view (or ViewGroup most likely) that should represent a single item in the list.
	 * A list can recycle old views (that scroll out of view for the user) and fill it with new data, so that
	 * the garbage collector doesn't need to run too often.
	 *
	 * The basic layout of this method always looks the same:
	 *
	 * 1) Check if you got a recycled view passed into this method (2nd parameter)
	 *    (a) if not, load the layout from a resource file to create a new view
	 * 2) Get the item, that should be represented by the view, by the given position
	 * 3) Fill all fields with data from the list item
	 * 4) return the recycled or newly created view
	 *
	 * @param position the position of the item to show
	 * @param convertView a view earlier created by this method, that should now be recycled
	 * @param parent the ViewGroup to which the view will be attached (will be needed to load the xml)
	 * @return the recycled or created view
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// If convertView is null, the list doesn't have any old views for us, that can be recycled.
		// In this case we need to create a new view. If there are old views that can be recycled, they will be
		// passed to the convertView parameter. It will be a view that we returned in an earlier call from this
		// method, so it will have the same type.
		if(convertView == null) {
			// Load the layout from the res/layout/link_item.xml and create a view object from it.
			// This is the place where we need the activity. It has a LayoutInflater, that we need to load
			// layouts from resource files.
			// The inflate method will need the container to which the view will be attached as the second parameter.
			// It uses the container to know the width and height it needs for calculations.
			// The third parameter must be false, to tell the method NOT to attach the view to its container.
			// The ListView will do this, once we returned the view from this method.
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
