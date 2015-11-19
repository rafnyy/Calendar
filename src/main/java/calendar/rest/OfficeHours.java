package calendar.rest;

import calendar.Constants;
import calendar.database.ScheduleDB;
import calendar.database.UserDB;
import calendar.schedule.Schedule;
import calendar.user.Consultant;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

@Path(Constants.Api.OFFICE_HOURS)
@Singleton
public class OfficeHours {
    private final ScheduleDB scheduleDB;
    private final UserDB userDB;

    @Inject
    public OfficeHours(ScheduleDB scheduleDB, UserDB userDB) {
        this.scheduleDB = scheduleDB;
        this.userDB = userDB;
    }

    @POST
    @Path(Constants.Api.SET)
    @Consumes(MediaType.APPLICATION_JSON)
    public void set(Availability availability) {
        try {
            scheduleDB.insertStatusChange(null, availability.getConsultantId(), new Timestamp(availability.getStartTime()), Constants.Schedule.STATUS.AVAILABLE);

            scheduleDB.insertStatusChange(null, availability.getConsultantId(), new Timestamp(availability.getStartTime() + availability.getDurationMillis()), Constants.Schedule.STATUS.UNAVAILABLE);
        } catch (SQLException se) {
            System.err.println("Threw a SQLException booking an appointment.");
            System.err.println(se.getMessage());
        }
    }

    @GET
    @Path("/{consultantId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Schedule checkAppointments(@PathParam("consultantId") UUID consultantId, @QueryParam("startDate") long startDate, @QueryParam("endDate") long endDate) {
        try {
            Consultant consultant = userDB.getConsultant(consultantId);
            return scheduleDB.getSchedule(consultant, new Timestamp(startDate), new Timestamp(endDate));
        } catch (SQLException se) {
            System.err.println("Threw a SQLException booking an appointment.");
            System.err.println(se.getMessage());
        }

        return null;
    }

    @GET
    @Path("/{consultantId}" + Constants.Api.INSTANT)
    @Produces(MediaType.APPLICATION_JSON)
    public Constants.Schedule.STATUS checkInstant(@PathParam("consultantId") UUID consultantId, @QueryParam("startDate") long startDate) {
        try {
            return scheduleDB.getStatusAtInstant(consultantId, new Timestamp(startDate));
        } catch (SQLException se) {
            System.err.println("Threw a SQLException checking status.");
            System.err.println(se.getMessage());
        }

        return null;
    }

    @GET
    @Path("/{consultantId}" + Constants.Api.AVAILABLE)
    @Produces(MediaType.APPLICATION_JSON)
    public boolean available(@PathParam("consultantId") UUID consultantId, @QueryParam("startDate") long startDate, @QueryParam("endDate") long endDate) {
        try {
            return scheduleDB.available(consultantId, startDate, endDate);
        } catch (SQLException se) {
            System.err.println("Threw a SQLException checking availability.");
            System.err.println(se.getMessage());
        }

        return false;
    }

    private static class Availability {
        private static UUID consultantId;
        private static long startTime;
        private static int durationMillis;

        public Availability() {

        }

        public Availability(UUID consultantId, long startTime, int durationMillis) {
            this.consultantId = consultantId;
            this.startTime = startTime;
            this.durationMillis = durationMillis;
        }

        public UUID getConsultantId() {
            return consultantId;
        }

        public void setConsultantId(UUID consultantId) {
            this.consultantId = consultantId;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public int getDurationMillis() {
            return durationMillis;
        }

        public void setDurationMillis(int durationMillis) {
            this.durationMillis = durationMillis;
        }
    }
}