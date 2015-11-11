package rest;

import com.google.inject.Singleton;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import java.util.Date;

@Path("/book")
@Singleton
public class Book {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void book(Date start, int duration) {

    }
}