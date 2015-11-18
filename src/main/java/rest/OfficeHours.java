package rest;

import cal.Constants;
import cal.Schedule;
import cal.ScheduleDelta;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import database.DatabaseConnection;
import database.NamedParameterStatement;
import org.joda.time.Instant;
import users.Consultant;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Path("/office-hours")
@Singleton
public class OfficeHours {
    private final DatabaseConnection connection;
    private final User user;

    private final static String END = "end";

    @Inject
    public OfficeHours(DatabaseConnection connection, User user) {
        this.connection = connection;
        this.user = user;
    }

    @POST
    @Path("/set")
    @Consumes(MediaType.APPLICATION_JSON)
    public void set(Long startDate, Long endDate) {

    }

    @GET
    @Path("/{consultantId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Schedule checkAppointments(@PathParam("consultantId") UUID consultantId, @QueryParam("startDate") long startDate, @QueryParam("endDate") long endDate) {
        Consultant consultant = user.getConsultantFromUUID(consultantId);
        Constants.Schedule.STATUS startStatus = this.checkInstant(consultantId, startDate);
        List<ScheduleDelta> deltas = new ArrayList<>();

        try
        {
            String getRowSQL = "SELECT * FROM \"Schedule\" WHERE "
                    + Constants.Schedule.CONSULTANT_ID + "=:" + Constants.Schedule.CONSULTANT_ID
                    + " AND " + Constants.Schedule.START + ">=:" + Constants.Schedule.START
                    + " AND " + Constants.Schedule.START + "<:" + END
                    + " ORDER BY " + Constants.Schedule.START + " ASC";

            NamedParameterStatement preparedStatement = new NamedParameterStatement(connection.getConnection(), getRowSQL);
            preparedStatement.setObject(Constants.Schedule.CONSULTANT_ID, consultantId);
            preparedStatement.setTimestamp(Constants.Schedule.START, new Timestamp(startDate));
            preparedStatement.setTimestamp(END, new Timestamp(endDate));

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                Timestamp time = resultSet.getTimestamp(Constants.Schedule.START);
                Instant start = new Instant(time.getTime());
                Constants.Schedule.STATUS status = Constants.Schedule.STATUS.valueOf(resultSet.getString(Constants.Schedule.TO_STATUS));

                deltas.add(new ScheduleDelta(start, status));
            }
        }
        catch (SQLException se) {
            System.err.println("Threw a SQLException booking an appointment.");
            System.err.println(se.getMessage());
        }

        return new Schedule(consultant, new Instant(startDate), new Instant(endDate), deltas, startStatus);
    }

    @GET
    @Path("/{consultantId}/instant/")
    @Produces(MediaType.APPLICATION_JSON)
    public Constants.Schedule.STATUS checkInstant(@PathParam("consultantId") UUID consultantId, @QueryParam("startDate") long startDate) {
        Constants.Schedule.STATUS status = Constants.Schedule.STATUS.AVAILABLE;

        try
        {
            String getRowSQL = "SELECT " + Constants.Schedule.TO_STATUS + " FROM \"Schedule\" WHERE "
                    + Constants.Schedule.CONSULTANT_ID + "=:" + Constants.Schedule.CONSULTANT_ID +
                    " AND " + Constants.Schedule.START + "<:" + Constants.Schedule.START
                    + " ORDER BY " + Constants.Schedule.START + " DESC";

            NamedParameterStatement preparedStatement = new NamedParameterStatement(connection.getConnection(), getRowSQL);
            preparedStatement.setObject(Constants.Schedule.CONSULTANT_ID, consultantId);
            preparedStatement.setTimestamp(Constants.Schedule.START, new Timestamp(startDate));

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                status = Constants.Schedule.STATUS.valueOf(resultSet.getString(Constants.Schedule.TO_STATUS));
            }
        }
        catch (SQLException se) {
            System.err.println("Threw a SQLException booking an appointment.");
            System.err.println(se.getMessage());
        }

        return status;
    }


    @GET
    @Path("/{consultantId}/available")
    @Produces(MediaType.APPLICATION_JSON)
    public boolean available(@PathParam("consultantId") UUID consultantId, @QueryParam("startDate") long startDate, @QueryParam("endDate") long endDate) {


        return true;
    }
}