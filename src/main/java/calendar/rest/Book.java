package calendar.rest;

import calendar.Constants;
import calendar.database.ScheduleDB;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.codehaus.jackson.annotate.JsonCreator;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path(Constants.Api.BOOK)
@Singleton
public class Book {
    private static final Logger log = Logger.getLogger(Book.class.getName());

    private final ScheduleDB scheduleDB;

    @Inject
    public Book(ScheduleDB scheduleDB) {
        this.scheduleDB = scheduleDB;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response book(Booking booking) {
        Timestamp start = new Timestamp(booking.getStartTime());
        Timestamp end = new Timestamp(booking.getStartTime() + booking.getDurationMillis());

        try {
            if (!scheduleDB.available(booking.getConsultantId(), booking.getStartTime(), booking.getStartTime() + booking.getDurationMillis())) {
                log.log(Level.INFO, "Time not available to book an appointment for user {0} with consultant {1} from {2} to {3]", new Object[]{booking.getClientId(), booking.getConsultantId(), start, end});
                return Response.status(Response.Status.CONFLICT).build();
            }

            log.log(Level.INFO, "Booking an appointment for user {0} with consultant {1} from {2} to {3}", new Object[]{booking.getClientId(), booking.getConsultantId(), start, end});

            scheduleDB.insertStatusChange(booking.getClientId(), booking.getConsultantId(), start, Constants.Schedule.STATUS.BOOKED);
            scheduleDB.insertStatusChange(booking.getClientId(), booking.getConsultantId(), end, Constants.Schedule.STATUS.AVAILABLE);

            return Response.ok().build();
        } catch (SQLException se) {
            log.log(Level.SEVERE, "Threw a SQLException booking an appointment.");
            log.log(Level.SEVERE, se.getMessage(), se);
            return Response.serverError().entity("Threw a SQLException booking an appointment.").build();
        }
    }

    private static class Booking {
        private UUID clientId;
        private UUID consultantId;
        private long startTime;
        private long durationMillis;

        public Booking() {

        }

        public Booking(UUID clientId, UUID consultantId, long startTime, long durationMillis) {
            this.clientId = clientId;
            this.consultantId = consultantId;
            this.startTime = startTime;
            this.durationMillis = durationMillis;
        }

        public UUID getClientId() {
            return clientId;
        }

        public void setClientId(UUID clientId) {
            this.clientId = clientId;
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