package rest;

import cal.Schedule;
import com.google.inject.Singleton;
import org.joda.time.Instant;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/office-hours")
@Singleton
public class OfficeHours {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void set(Instant start, int duration) {

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Schedule checkAppointments(@QueryParam("start") Instant startDate, @QueryParam("end") Instant endDate) {
        return new Schedule(startDate, endDate);
    }

}
