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
    @Produces(MediaType.TEXT_PLAIN)
    public Schedule echo(@QueryParam("start") Date startDate, @QueryParam("end") Date endDate) {
        return new Schedule(startDate, endDate);
    }
}
