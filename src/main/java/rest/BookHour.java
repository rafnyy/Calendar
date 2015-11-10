package rest;

import cal.Hour;
import com.google.inject.Singleton;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/hour")
@Singleton
public class BookHour {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Hour echo(Hour hour) {
        return hour;
    }
}