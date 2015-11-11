package rest;

import cal.Schedule;
import com.google.inject.Singleton;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Date;

@Path("/office-hours")
@Singleton
public class OfficeHours {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void set(Date start, int duration) {

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Schedule checkAppotinments(@QueryParam("start") Date startDate, @QueryParam("end") Date endDate) {
        return new Schedule(startDate, endDate);
    }

}
