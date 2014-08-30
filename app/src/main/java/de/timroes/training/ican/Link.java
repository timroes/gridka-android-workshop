package de.timroes.training.ican;

/**
 * @author Tim Roes <mail@timroes.de>
 */
public class Link {

	private String url;
	private String description;
	private String author;

	public Link(String author, String description, String url) {
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
