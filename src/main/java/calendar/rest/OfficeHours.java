package calendar.rest;

import calendar.Constants;
import calendar.database.ScheduleDB;
import calendar.database.UserDB;
import calendar.schedule.Schedule;
import calendar.schedule.ScheduleDelta;
import calendar.user.Consultant;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Iterator;
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
        Timestamp start = new Timestamp(availability.getStartTime());
        Timestamp end = new Timestamp(availability.getStartTime() + availability.getDurationMillis());
        log.log(Level.INFO, "Setting office hours for consultant {0} between {1} and {2}", new Object[]{availability.getConsultantId(), start, end});

        try {
            boolean availableAtEnd = scheduleDB.getStatusAtInstant(availability.getConsultantId(), end).equals(Constants.Schedule.STATUS.AVAILABLE);

            scheduleDB.insertStatusChange(null, availability.getConsultantId(), start, Constants.Schedule.STATUS.AVAILABLE);

            Schedule schedule = scheduleDB.getSchedule(userDB.getConsultant(availability.getConsultantId()), start, end);

            Iterator<ScheduleDelta> iterator = schedule.getDeltas().iterator();
            while(iterator.hasNext()) {
                ScheduleDelta delta = iterator.next();
                if(delta.getToStatus().equals(Constants.Schedule.STATUS.UNAVAILABLE)) {
                    delete(availability.getConsultantId(), delta.getTime().getTime());
                }
            }

            if(!availableAtEnd) {
                scheduleDB.insertStatusChange(null, availability.getConsultantId(), end, Constants.Schedule.STATUS.UNAVAILABLE);
            }
        } catch (SQLException se) {
            log.log(Level.SEVERE, "Threw a SQLException setting availability.");
            log.log(Level.SEVERE, se.getMessage(), se);
        }
    }

    @POST
    @Path(Constants.Api.UNSET)
    @Consumes(MediaType.APPLICATION_JSON)
    public void unset(Availability availability) {
        Timestamp start = new Timestamp(availability.getStartTime());
        Timestamp end = new Timestamp(availability.getStartTime() + availability.getDurationMillis());
        log.log(Level.INFO, "Unsetting office hours for consultant {0} between {1} and {2}", new Object[]{availability.getConsultantId(), start, end});

        try {
            scheduleDB.insertStatusChange(null, availability.getConsultantId(), start, Constants.Schedule.STATUS.UNAVAILABLE);

            if(!scheduleDB.getStatusAtInstant(availability.getConsultantId(), end).equals(Constants.Schedule.STATUS.UNAVAILABLE)) {
                scheduleDB.insertStatusChange(null, availability.getConsultantId(), end, Constants.Schedule.STATUS.AVAILABLE);
            }
        } catch (SQLException se) {
            log.log(Level.SEVERE, "Threw a SQLException setting availability.");
            log.log(Level.SEVERE, se.getMessage(), se);
        }
    }

    @DELETE
    @Path("/delete")
    public void delete(@PathParam("consultantId") UUID consultantId, @QueryParam("startDate") long startDate) {
        Timestamp start = new Timestamp(startDate);

        log.log(Level.INFO, "Deleting schedule delta for consultant {0} at {1}", new Object[]{consultantId, start});

        try {
            scheduleDB.delete(consultantId, start);
        } catch (SQLException se) {
            log.log(Level.SEVERE, "Threw a SQLException deleting a schedule delta.");
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

    @GET
    @Path("/{consultantId}" + Constants.Api.UNBOOKED)
    @Produces(MediaType.APPLICATION_JSON)
    public boolean unbooked(@PathParam("consultantId") UUID consultantId, @QueryParam("startDate") long startDate, @QueryParam("endDate") long endDate) {
        Timestamp start = new Timestamp(startDate);
        Timestamp end = new Timestamp(endDate);

        log.log(Level.INFO, "Checking availability for consultant {0} between {1} and {2}", new Object[]{consultantId, start, end});

        try {
            return scheduleDB.unbooked(consultantId, startDate, endDate);
        } catch (SQLException se) {
            log.log(Level.SEVERE, "Threw a SQLException checking availability.");
            log.log(Level.SEVERE, se.getMessage(), se);
        }

        return false;
    }

    private static class Availability {
        private static UUID consultantId;
        private static long startTime;
        private static long durationMillis;

        public Availability() {

        }

        public Availability(UUID consultantId, long startTime, long durationMillis) {
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

        public long getDurationMillis() {
            return durationMillis;
        }

        public void setDurationMillis(long durationMillis) {
            this.durationMillis = durationMillis;
        }
    }
}