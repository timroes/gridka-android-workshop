package de.timroes.training.ican.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * The entity, which contains the necessary data to represent a link.
 * @author Tim Roes <mail@timroes.de>
 */
// Annotation for Objectify to label this class as an entity.
@Entity
public class Link {

    // Each entity requires an identifier, which can be either a string or an integer.
	@Id
	private String id;

	private String url;
	private String description;
	private String author;

    // There must be a no-arg constructor for Objectify to persist the data.
	public Link() { }

    /**
     * Constructor to create a new link object. Prepends "http://" to the url if the url doesn't
     * start with http or https protocol.
     * @param id the generated id of the entity
     * @param author the author name
     * @param description the description for the url
     * @param url the url
     */
	public Link(String id, String author, String description, String url) {
		this.id = id;
		this.author = author;
		this.description = description;
		// If the url doesn't start with http:// or https:// prepend "http://".
		if(!url.startsWith("http://") && !url.startsWith("https://")) {
			this.url = "http://" + url;
		} else {
			this.url = url;
		}
	}

    /**
     * Returns the url of the link. This method is necessary to serialize the field.
     * @return the url
     */
	public String getUrl() {
		return url;
	}

    /**
     * Returns the description of the link. This method is necessary to serialize the field.
     * @return the description
     */
	public String getDescription() {
		return description;
	}

    /**
     * Returns the author of the link. This method is necessary to serialize the field.
     * @return the author
     */
	public String getAuthor() {
		return author;
	}
}
