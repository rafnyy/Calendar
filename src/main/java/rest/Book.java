package rest;

import cal.Constants;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import database.DatabaseConnection;
import database.NamedParameterStatement;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.sql.Timestamp;

@Path("/book")
@Singleton
public class Book {
    private static final String CLIENT_ID = "client_id";
    private static final String CONSULTANT_ID = "consultant_id";
    private static final String TO_STATUS = "to_status";
    private static final String START = "start";
    private static final String DURATION_MS = "duration_ms";
    private final DatabaseConnection connection;

    @Inject
    public Book(DatabaseConnection connection) {
        this.connection = connection;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void book(Booking booking) {
        try
        {
            String insertTableSQL = "INSERT INTO \"Schedule\""
                + "(" + CLIENT_ID + ", " + CONSULTANT_ID + ", " + TO_STATUS + ", " + START + ", " + DURATION_MS + ") VALUES"
                + "(:" + CLIENT_ID + ", :" + CONSULTANT_ID + ", :" + TO_STATUS + ", :" + START + ", :" + DURATION_MS + ")";

            NamedParameterStatement preparedStatement = new NamedParameterStatement(connection.getConnection(), insertTableSQL);
            preparedStatement.setInt(CLIENT_ID, booking.getClientId());
            preparedStatement.setInt(CONSULTANT_ID, booking.getConsultantId());
            preparedStatement.setString(TO_STATUS, Constants.STATUS.BOOKED.name());
            preparedStatement.setTimestamp(START, new Timestamp(booking.getStartTime()));
            preparedStatement.setInt(DURATION_MS, booking.getDurationMillis());

            preparedStatement.executeUpdate();
        }
        catch (SQLException se) {
            System.err.println("Threw a SQLException booking an appointment.");
            System.err.println(se.getMessage());
        }
    }

    private static class Booking {
        private static int clientId;
        private static int consultantId;
        private static long startTime;
        private static int durationMillis;

        public Booking() {

        }

        public Booking(int clientId, int consultantId, long startTime, int durationMillis) {
            this.clientId = clientId;
            this.consultantId = consultantId;
            this.startTime = startTime;
            this.durationMillis = durationMillis;
        }

        public int getClientId() {
            return clientId;
        }

        public void setClientId(int clientId) {
            this.clientId = clientId;
        }

        public int getConsultantId() {
            return consultantId;
        }

        public void setConsultantId(int consultantId) {
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