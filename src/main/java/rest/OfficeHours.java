package rest;

import cal.Constants;
import cal.Schedule;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import database.DatabaseConnection;
import database.NamedParameterStatement;
import factory.CalendarFactory;
import org.joda.time.Instant;
import users.Consultant;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

@Path("/office-hours")
@Singleton
public class OfficeHours {
    private final DatabaseConnection connection;
    private final User user;

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
    public Schedule checkAppointments(@PathParam("consultantId") long consultantId, @QueryParam("startDate") long startDate, @QueryParam("endDate") long endDate) {
//        user.getUserFromUUID(consultantId);
        new Instant(startDate);
        new Instant(endDate);

        try
        {
            String getRowSQL = "SELECT * FROM \"Schedule\" WHERE"
                    + Constants.Schedule.CONSULTANT_ID + "=:" + Constants.Schedule.CONSULTANT_ID;

            NamedParameterStatement preparedStatement = new NamedParameterStatement(connection.getConnection(), getRowSQL);
            preparedStatement.setObject(Constants.Schedule.CONSULTANT_ID, consultantId);
//            preparedStatement.setTimestamp(Constants.Schedule.START, new Timestamp(startDate));


            ResultSet resultSet = preparedStatement.executeQuery();


        }
        catch (SQLException se) {
            System.err.println("Threw a SQLException booking an appointment.");
            System.err.println(se.getMessage());
        }

        return null;
    }

    @GET
    @Path("/{consultantId}/instant/")
    @Produces(MediaType.APPLICATION_JSON)
    public Constants.Schedule.STATUS checkInstant(@PathParam("consultantId") long consultantId, @QueryParam("startDate") long startDate) {
        Constants.Schedule.STATUS status = Constants.Schedule.STATUS.AVAILABLE;

        try
        {
            String getRowSQL = "SELECT " + Constants.Schedule.TO_STATUS + " FROM \"Schedule\" WHERE "
                    + Constants.Schedule.CONSULTANT_ID + "=:" + Constants.Schedule.CONSULTANT_ID + " AND "
                    + Constants.Schedule.TO_STATUS + "<:" + Constants.Schedule.START
                    + " ORDER BY " + Constants.Schedule.START + " DESC";

            NamedParameterStatement preparedStatement = new NamedParameterStatement(connection.getConnection(), getRowSQL);
            preparedStatement.setObject(Constants.Schedule.CONSULTANT_ID, consultantId);
            preparedStatement.setTimestamp(Constants.Schedule.START, new Timestamp(startDate));

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                status = (Constants.Schedule.STATUS) resultSet.getObject(Constants.Schedule.TO_STATUS);
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