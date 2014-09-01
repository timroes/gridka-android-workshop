/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package de.timroes.training.ican.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

import java.util.List;
import java.util.UUID;


/** An endpoint class we are exposing */
@Api(name = "ican", version = "v1", namespace = @ApiNamespace(ownerDomain = "backend.ican.training.timroes.de", ownerName = "backend.ican.training.timroes.de", packagePath=""))
public class MyEndpoint {

	private Objectify objectify;

	private Objectify ofy() {
		if(objectify == null) {
			ObjectifyService.register(Link.class);
			objectify = ObjectifyService.ofy();
		}
		return objectify;
	}

	@ApiMethod(name = "addLink")
	public Link addNewLink(@Named("description") String description, @Named("url") String url, @Named("author") String author) {
		Link l = new Link(UUID.randomUUID().toString(), author, description, url);
		ofy().save().entity(l).now();
		return l;
	}

	@ApiMethod(name = "links")
	public List<Link> getLinks() {
		return ofy().load().type(Link.class).list();
	}

}
