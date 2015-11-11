package rest;


import cal.Schedule;
import com.google.inject.Singleton;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Date;

@Path("/check")
@Singleton
public class CheckSchedule {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Schedule checkSchedule(@QueryParam("start") Date startDate, @QueryParam("end") Date endDate) {
        return new Schedule(startDate, endDate);
    }

    @GET
    @Path("/available")
    @Produces(MediaType.APPLICATION_JSON)
    public boolean available(@QueryParam("start") Date startDate, @QueryParam("end") Date endDate) {
        return true;
    }


}
