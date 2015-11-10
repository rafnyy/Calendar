package rest;

import com.google.inject.Singleton;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/rest")
@Singleton
public class TestResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String echo(@QueryParam("text") String text) {
        return text;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ComplexObject echo(ComplexObject obj) {
        return obj;
    }
}

class ComplexObject {
    String key;
    int value;
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public int getValue() {
        return value;
    }
    public void setValue(int value) {
        this.value = value;
    }
}