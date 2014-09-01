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

/**
 * The endpoint class with the descriptions of the REST interface.
 */
// Description of the REST interface, starting with the "@Api" annotation.
// name: the name of the REST interface.
// version: the version of the endpoint/REST interface, which should be changed when changing the API.
// namespace: causes the generated client libraries to have the specified namespace/package name.
@Api(name = "ican", version = "v1", namespace = @ApiNamespace(ownerDomain = "backend.ican.training.timroes.de", ownerName = "backend.ican.training.timroes.de", packagePath=""))
public class MyEndpoint {

	private Objectify objectify;

    /**
     * Helper method to ensure that the entity is registered before using Objectify.
     * @return the objectify object
     */
	private Objectify ofy() {
        // If the objectify variable hasn't been initialized, register the link entity.
		if(objectify == null) {
			ObjectifyService.register(Link.class);
			objectify = ObjectifyService.ofy();
		}
		return objectify;
	}

    /**
     * The method that adds a new link and persist the data with Objectify.
     * @param description the description for the url
     * @param url the url
     * @param author the author name
     * @return the created link object with the given arguments
     */
    // The @ApiMethod annotation allows an API configuration.
    // name: the name for this method in the generated client library and API.
    // @Named: this annotation is required for all non-entity type parameters passed to backend methods.
	@ApiMethod(name = "addLink")
	public Link addNewLink(@Named("description") String description, @Named("url") String url, @Named("author") String author) {
        // Creates a link object with a random UUID
		Link l = new Link(UUID.randomUUID().toString(), author, description, url);

        // The save() method starts a save command chain, which allows you to save multiple entity objects.
        // The entity(l) method saves a single entity in the datastore and retrieves the result with the now() method.
		ofy().save().entity(l).now();
		return l;
	}

    /**
     * The method that loads and returns all link entries.
     * @return a list of all link entries
     */
    // The @ApiMethod annotation allows an API configuration.
    // name: the name for this method in the generated client library and API.
	@ApiMethod(name = "links")
	public List<Link> getLinks() {
        // The load() method starts a load command chain to fetch data from the datastore.
        // The type(Link.class) method restricts the find operation to entities of the link entity.
        // It returns the results as a java.util.List with the list() method.
		return ofy().load().type(Link.class).list();
	}

}
