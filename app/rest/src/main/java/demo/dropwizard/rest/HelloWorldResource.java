package demo.dropwizard.rest;

import demo.dropwizard.model.Saying;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by sudhiry on 9/30/18.
 */
@Path("/hello-world")
@Produces(MediaType.APPLICATION_JSON)
public class HelloWorldResource {

    @GET
    public Saying getSaying(){
        return new Saying(100, "Foo");
    }
}
