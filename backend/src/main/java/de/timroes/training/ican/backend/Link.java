package de.timroes.training.ican.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * @author Tim Roes <mail@timroes.de>
 */
@Entity
public class Link {

	@Id
	private String id;

	private String url;
	private String description;
	private String author;

	public Link() { }

	public Link(String id, String author, String description, String url) {
		this.id = id;
		this.author = author;
		this.description = description;
		// If the url doesn't start with http:// or https:// append an http:// in front of it.
		if(!url.startsWith("http://") && !url.startsWith("https://")) {
			this.url = "http://" + url;
		} else {
			this.url = url;
		}
	}

	public String getUrl() {
		return url;
	}

	public String getDescription() {
		return description;
	}

	public String getAuthor() {
		return author;
	}
}
