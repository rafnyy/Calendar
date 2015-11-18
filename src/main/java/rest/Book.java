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

    @Inject
    public Book(DatabaseConnection connection) {
        this.connection = connection;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void book(Booking booking) {
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
                    + "(" + Constants.Schedule.CLIENT_ID + ", " + Constants.Schedule.CONSULTANT_ID + ", " + Constants.Schedule.TO_STATUS + ", " + Constants.Schedule.START + ") VALUES"
                    + "(:" + Constants.Schedule.CLIENT_ID + ", :" + Constants.Schedule.CONSULTANT_ID + ", :" + Constants.Schedule.TO_STATUS + ", :" + Constants.Schedule.START + ")";

            NamedParameterStatement endBookingStatement = new NamedParameterStatement(connection.getConnection(), endBookingSQL);
            endBookingStatement.setObject(Constants.Schedule.CLIENT_ID, booking.getClientId());
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

    @PUT
    @Path("/cancel")
    @Consumes(MediaType.APPLICATION_JSON)
    public void cancel(Booking booking) {
        // TODO: implement
    }

    private static class Booking {
        private static UUID clientId;
        private static UUID consultantId;
        private static long startTime;
        private static int durationMillis;

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