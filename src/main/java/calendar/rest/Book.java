package calendar.rest;

import calendar.Constants;
import calendar.database.ScheduleDB;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
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
    public void book(Booking booking) {
        Timestamp start = new Timestamp(booking.getStartTime());
        Timestamp end = new Timestamp(booking.getStartTime() + booking.getDurationMillis());

        log.log(Level.INFO, "Booking and appointment for user {0} with consultant {1} from {2} to {3}", new Object[]{booking.getClientId(), booking.getConsultantId(), start, end});

        try {
            if (!scheduleDB.available(booking.getConsultantId(), booking.getStartTime(), booking.getStartTime() + booking.getDurationMillis())) {
                //TODO: throw or some other status code
            }

            scheduleDB.insertStatusChange(booking.getClientId(), booking.getConsultantId(), start, Constants.Schedule.STATUS.BOOKED);
            scheduleDB.insertStatusChange(booking.getClientId(), booking.getConsultantId(), end, Constants.Schedule.STATUS.AVAILABLE);
        } catch (SQLException se) {
            log.log(Level.SEVERE, "Threw a SQLException booking an appointment.");
            log.log(Level.SEVERE, se.getMessage(), se);
        }
    }

    class Booking {
        private UUID clientId;
        private UUID consultantId;
        private long startTime;
        private int durationMillis;

        public Booking() {

        }

        public Booking(UUID clientId, UUID consultantId, long startTime, int durationMillis) {
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

        public int getDurationMillis() {
            return durationMillis;
        }

        public void setDurationMillis(int durationMillis) {
            this.durationMillis = durationMillis;
        }
    }
}