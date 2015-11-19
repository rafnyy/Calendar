package calendar.rest;

import calendar.Constants;
import calendar.database.UserDB;
import calendar.user.AbstractUser;
import calendar.user.Client;
import calendar.user.Consultant;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.UUID;

@Path(Constants.Api.USER)
@Singleton
public class User {
    private final UserDB userDB;

    @Inject
    public User(UserDB userDB) {
        this.userDB = userDB;
    }

    @GET
    @Path(Constants.Api.CONSULTANT + "/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Consultant getConsultantFromUUID(@PathParam("uuid") UUID uuid) {
        try {
            return userDB.getConsultant(uuid);
        } catch (SQLException se) {
            System.err.println("Threw a SQLException retrieving a user.");
            System.err.println(se.getMessage());
        }

        return null;
    }

    @GET
    @Path(Constants.Api.CLIENT + "/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Client getClientFromUUID(@PathParam("uuid") UUID uuid) {
        try {
            return userDB.getClient(uuid);
        } catch (SQLException se) {
            System.err.println("Threw a SQLException retrieving a user.");
            System.err.println(se.getMessage());
        }

        return null;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public AbstractUser getUserFromInfo(@QueryParam("firstName") String firstName, @QueryParam("lastName") String lastName, @QueryParam("email") String email) {
        try {
            return userDB.getUserByInfo(firstName, lastName, email);
        } catch (SQLException se) {
            System.err.println("Threw a SQLException retrieving a user.");
            System.err.println(se.getMessage());
        }

        return null;
    }

    @PUT
    @Path(Constants.Api.REGISTER_CONSULTANT)
    @Consumes(MediaType.APPLICATION_JSON)
    public void registerClient(UserInfo newUser) {
        try {
            userDB.registerNewClient(newUser.getFirstName(), newUser.getLastName(), newUser.getEmail());
        } catch (SQLException se) {
            System.err.println("Threw a SQLException registering a new client.");
            System.err.println(se.getMessage());
        }
    }

    @PUT
    @Path(Constants.Api.REGISTER_CLIENT)
    @Consumes(MediaType.APPLICATION_JSON)
    public void registerConsultant(UserInfo newUser) {
        try {
            userDB.registerNewConsultant(newUser.getFirstName(), newUser.getLastName(), newUser.getEmail());
        } catch (SQLException se) {
            System.err.println("Threw a SQLException registering a new consultant.");
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
            UserInfo.firstName = firstName;
            UserInfo.lastName = lastName;
            UserInfo.email = email;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            UserInfo.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            UserInfo.lastName = lastName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            UserInfo.email = email;
        }
    }
}