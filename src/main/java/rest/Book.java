package rest;

import cal.Constants;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import database.DatabaseConnection;
import database.NamedParameterStatement;
import org.joda.time.Instant;

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
    public void book() {

        Instant instant = new Instant();

        try
        {
            String insertTableSQL = "INSERT INTO \"Schedule\""
                + "(" + CLIENT_ID + ", " + CONSULTANT_ID + ", " + TO_STATUS + ", " + START + ", " + DURATION_MS + ") VALUES"
                + "(:" + CLIENT_ID + ", :" + CONSULTANT_ID + ", :" + TO_STATUS + ", :" + START + ", :" + DURATION_MS + ")";

            NamedParameterStatement preparedStatement = new NamedParameterStatement(connection.getConnection(), insertTableSQL);
            preparedStatement.setInt(CLIENT_ID, 4);
            preparedStatement.setInt(CONSULTANT_ID, 4);
            preparedStatement.setString(TO_STATUS, "BOOKED");
            preparedStatement.setTimestamp(START, new Timestamp(instant.getMillis()));
            preparedStatement.setInt(DURATION_MS, Constants.millisecondsInAnHour);

            preparedStatement .executeUpdate();
        }
        catch (SQLException se) {
            System.err.println("Threw a SQLException booking an appointment.");
            System.err.println(se.getMessage());
        }
    }
}