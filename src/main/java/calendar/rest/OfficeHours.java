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
import java.util.logging.Level;
import java.util.logging.Logger;


@Path(Constants.Api.OFFICE_HOURS)
@Singleton
public class OfficeHours {
    private static final Logger log = Logger.getLogger(OfficeHours.class.getName());

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
            log.log(Level.SEVERE, "Threw a SQLException setting availability.");
            log.log(Level.SEVERE, se.getMessage(), se);
        }
    }

    @GET
    @Path("/{consultantId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Schedule checkAppointments(@PathParam("consultantId") UUID consultantId, @QueryParam("startDate") long startDate, @QueryParam("endDate") long endDate) {
        Timestamp start = new Timestamp(startDate);
        Timestamp end = new Timestamp(endDate);

        log.log(Level.INFO, "Checking appointments for consultant {0} between {1} and {2}", new Object[]{consultantId, start, end});
        try {
            Consultant consultant = userDB.getConsultant(consultantId);
            return scheduleDB.getSchedule(consultant, start, end);
        } catch (SQLException se) {
            log.log(Level.SEVERE, "Threw a SQLException getting a schedule.");
            log.log(Level.SEVERE, se.getMessage(), se);
        }

        return null;
    }

    @GET
    @Path("/{consultantId}" + Constants.Api.INSTANT)
    @Produces(MediaType.APPLICATION_JSON)
    public Constants.Schedule.STATUS checkInstant(@PathParam("consultantId") UUID consultantId, @QueryParam("startDate") long startDate) {
        Timestamp start = new Timestamp(startDate);
        log.log(Level.INFO, "Checking appointments for consultant {0} at instant {1}", new Object[]{consultantId, start});

        try {
            return scheduleDB.getStatusAtInstant(consultantId, start);
        } catch (SQLException se) {
            log.log(Level.SEVERE, "Threw a SQLException checking status.");
            log.log(Level.SEVERE, se.getMessage(), se);
        }

        return null;
    }

    @GET
    @Path("/{consultantId}" + Constants.Api.AVAILABLE)
    @Produces(MediaType.APPLICATION_JSON)
    public boolean available(@PathParam("consultantId") UUID consultantId, @QueryParam("startDate") long startDate, @QueryParam("endDate") long endDate) {
        Timestamp start = new Timestamp(startDate);
        Timestamp end = new Timestamp(endDate);

        log.log(Level.INFO, "Checking availability for consultant {0} between {1} and {2}", new Object[]{consultantId, start, end});

        try {
            return scheduleDB.available(consultantId, startDate, endDate);
        } catch (SQLException se) {
            log.log(Level.SEVERE, "Threw a SQLException checking availability.");
            log.log(Level.SEVERE, se.getMessage(), se);
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