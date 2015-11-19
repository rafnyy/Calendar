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

@Path(Constants.Api.BOOK)
@Singleton
public class Book {
    private final ScheduleDB scheduleDB;

    @Inject
    public Book(ScheduleDB scheduleDB) {
        this.scheduleDB = scheduleDB;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void book(Booking booking) {
        try {
            if (!scheduleDB.available(booking.getConsultantId(), booking.getStartTime(), booking.getStartTime() + booking.getDurationMillis())) {
                //TODO: throw or some other status code
            }

            scheduleDB.insertStatusChange(booking.getClientId(), booking.getConsultantId(), new Timestamp(booking.getStartTime()), Constants.Schedule.STATUS.BOOKED);

            scheduleDB.insertStatusChange(booking.getClientId(), booking.getConsultantId(), new Timestamp(booking.getStartTime() + booking.getDurationMillis()), Constants.Schedule.STATUS.AVAILABLE);
        } catch (SQLException se) {
            System.err.println("Threw a SQLException booking an appointment.");
            System.err.println(se.getMessage());
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