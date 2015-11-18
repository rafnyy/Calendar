package rest;

import cal.Constants;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import database.DatabaseConnection;
import database.NamedParameterStatement;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

@Path("/book")
@Singleton
public class Book {

    private final DatabaseConnection connection;
    private final OfficeHours officeHours;

    @Inject
    public Book(DatabaseConnection connection, OfficeHours officeHours) {
        this.connection = connection;
        this.officeHours = officeHours;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void book(Booking booking) {
        if(!officeHours.available(booking.getConsultantId(), booking.getStartTime(), booking.getStartTime() + booking.getDurationMillis())) {
            //TODO: throw or some other status code
        }

        try
        {
            String startBookingSQL = "INSERT INTO \"Schedule\""
                    + "(" + Constants.Schedule.CLIENT_ID + ", " + Constants.Schedule.CONSULTANT_ID + ", " + Constants.Schedule.TO_STATUS + ", " + Constants.Schedule.START + ") VALUES"
                    + "(:" + Constants.Schedule.CLIENT_ID + ", :" + Constants.Schedule.CONSULTANT_ID + ", :" + Constants.Schedule.TO_STATUS + ", :" + Constants.Schedule.START + ")";

            NamedParameterStatement startBookingStatement = new NamedParameterStatement(connection.getConnection(), startBookingSQL);
            startBookingStatement.setObject(Constants.Schedule.CLIENT_ID, booking.getClientId());
            startBookingStatement.setObject(Constants.Schedule.CONSULTANT_ID, booking.getConsultantId());
            startBookingStatement.setString(Constants.Schedule.TO_STATUS, Constants.Schedule.STATUS.BOOKED.name());
            startBookingStatement.setTimestamp(Constants.Schedule.START, new Timestamp(booking.getStartTime()));

            startBookingStatement.executeUpdate();

            String endBookingSQL = "INSERT INTO \"Schedule\""
                    + "(" + Constants.Schedule.CONSULTANT_ID + ", " + Constants.Schedule.TO_STATUS + ", " + Constants.Schedule.START + ") VALUES"
                    + "(:" + Constants.Schedule.CONSULTANT_ID + ", :" + Constants.Schedule.TO_STATUS + ", :" + Constants.Schedule.START + ")";

            NamedParameterStatement endBookingStatement = new NamedParameterStatement(connection.getConnection(), endBookingSQL);
            endBookingStatement.setObject(Constants.Schedule.CONSULTANT_ID, booking.getConsultantId());
            endBookingStatement.setString(Constants.Schedule.TO_STATUS, Constants.Schedule.STATUS.AVAILABLE.name());
            endBookingStatement.setTimestamp(Constants.Schedule.START, new Timestamp(booking.getStartTime() + booking.getDurationMillis()));

            endBookingStatement.executeUpdate();
        }
        catch (SQLException se) {
            System.err.println("Threw a SQLException booking an appointment.");
            System.err.println(se.getMessage());
        }
    }

    private static class Booking {
        private static UUID clientId;
        private static UUID consultantId;
        private static long startTime;
        private static int durationMillis;

        public Booking() {

        }

        public Booking(UUID clientId, UUID consultantId, long startTime, int durationMillis) {
            Booking.clientId = clientId;
            Booking.consultantId = consultantId;
            Booking.startTime = startTime;
            Booking.durationMillis = durationMillis;
        }

        public UUID getClientId() {
            return clientId;
        }

        public void setClientId(UUID clientId) {
            Booking.clientId = clientId;
        }

        public UUID getConsultantId() {
            return consultantId;
        }

        public void setConsultantId(UUID consultantId) {
            Booking.consultantId = consultantId;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            Booking.startTime = startTime;
        }

        public int getDurationMillis() {
            return durationMillis;
        }

        public void setDurationMillis(int durationMillis) {
            Booking.durationMillis = durationMillis;
        }
    }
}