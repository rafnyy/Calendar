package rest;

import cal.Constants;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import database.DatabaseConnection;
import database.NamedParameterStatement;
import users.UserDTO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Path("/user")
@Singleton
public class User {
    private final DatabaseConnection connection;

    @Inject
    public User(DatabaseConnection connection) {
        this.connection = connection;
    }

    @GET
    @Path("/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public UserDTO getUserFromUUID(@PathParam("uuid") UUID uuid) {
        UserDTO user = null;

        try
        {
            String getRowSql = "SELECT * FROM \"Users\" WHERE " + Constants.Client.UUID_COL_NAME + "=:" + Constants.Client.UUID_COL_NAME;

            NamedParameterStatement preparedStatement = new NamedParameterStatement(connection.getConnection(), getRowSql);
            preparedStatement.setObject(Constants.Client.UUID_COL_NAME, uuid);

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                String firstName = resultSet.getString(Constants.Client.FIRST_NAME);
                String lastName = resultSet.getString(Constants.Client.LAST_NAME);
                String email = resultSet.getString(Constants.Client.EMAIL);
                boolean isClient = resultSet.getBoolean(Constants.Client.CLIENT);

                user = new UserDTO(uuid, firstName, lastName, email, isClient);
            }
        }
        catch (SQLException se) {
            System.err.println("Threw a SQLException retrieving a user.");
            System.err.println(se.getMessage());
        }

        return user;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public UserDTO getUserFromInfo(@QueryParam("firstName") String firstName, @QueryParam("lastName") String lastName, @QueryParam("email") String email) {
        UserDTO user = null;

        try
        {
            String getRowSql = "SELECT * FROM \"Users\" WHERE " + Constants.Client.FIRST_NAME + "=:" + Constants.Client.FIRST_NAME + " AND " + Constants.Client.LAST_NAME + "=:" + Constants.Client.LAST_NAME + " AND " + Constants.Client.EMAIL + "=:" + Constants.Client.EMAIL;

            NamedParameterStatement preparedStatement = new NamedParameterStatement(connection.getConnection(), getRowSql);
            preparedStatement.setString(Constants.Client.FIRST_NAME, firstName);
            preparedStatement.setString(Constants.Client.LAST_NAME, lastName);
            preparedStatement.setString(Constants.Client.EMAIL, email);

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                UUID uuid = (UUID) resultSet.getObject(Constants.Client.UUID_COL_NAME);

                boolean isClient = resultSet.getBoolean(Constants.Client.CLIENT);

                user = new UserDTO(uuid, firstName, lastName, email, isClient);
            }
        }
        catch (SQLException se) {
            System.err.println("Threw a SQLException retrieving a user.");
            System.err.println(se.getMessage());
        }

        return user;
    }

    @PUT
    @Path("/register-client")
    @Consumes(MediaType.APPLICATION_JSON)
    public void registerClient(UserInfo newUser) {
        registerNewUser(newUser, true);
    }

    @PUT
    @Path("/register-consultant")
    @Consumes(MediaType.APPLICATION_JSON)
    public void registerConsultant(UserInfo newUser) {
        registerNewUser(newUser, false);
    }

    private void registerNewUser(UserInfo newUser, boolean isClient) {
        try
        {
            String insertTableSQL = "INSERT INTO \"Users\""
                    + "(" + Constants.Client.FIRST_NAME + ", " + Constants.Client.LAST_NAME + ", " + Constants.Client.EMAIL + ", " + Constants.Client.UUID_COL_NAME + ", " + Constants.Client.CLIENT + ") VALUES"
                    + "(:" +  Constants.Client.FIRST_NAME + ", :" + Constants.Client.LAST_NAME + ", :" + Constants.Client.EMAIL + ", :" + Constants.Client.UUID_COL_NAME + ", :" + Constants.Client.CLIENT + ")";

            NamedParameterStatement preparedStatement = new NamedParameterStatement(connection.getConnection(), insertTableSQL);
            preparedStatement.setString(Constants.Client.FIRST_NAME, newUser.getFirstName());
            preparedStatement.setString(Constants.Client.LAST_NAME, newUser.getLastName());
            preparedStatement.setString(Constants.Client.EMAIL, newUser.getEmail());
            preparedStatement.setObject(Constants.Client.UUID_COL_NAME, UUID.randomUUID());
            preparedStatement.setBoolean(Constants.Client.CLIENT, isClient);

            preparedStatement.executeUpdate();
        }
        catch (SQLException se) {
            System.err.println("Threw a SQLException registering a new user.");
            System.err.println(se.getMessage());
        }
    }

    private static class UserInfo {
        private static String firstName;
        private static String lastName;
        private static String email;

        public UserInfo() {

        }

        public UserInfo(String firstName, String lastName, String email) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}