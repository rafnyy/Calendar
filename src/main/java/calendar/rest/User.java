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
import java.util.logging.Level;
import java.util.logging.Logger;

@Path(Constants.Api.USER)
@Singleton
public class User {
    private static final Logger log = Logger.getLogger( User.class.getName() );

    private final UserDB userDB;

    @Inject
    public User(UserDB userDB) {
        this.userDB = userDB;
    }

    @GET
    @Path(Constants.Api.CONSULTANT + "/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Consultant getConsultantFromUUID(@PathParam("uuid") UUID uuid) {
        log.log(Level.INFO, "Get consultant user info for {0}", uuid);

        try {
            return userDB.getConsultant(uuid);
        } catch (SQLException se) {
            log.log(Level.SEVERE, "Threw a SQLException retrieving a consultant.");
            log.log(Level.SEVERE, se.getMessage(), se);
        }

        return null;
    }

    @GET
    @Path(Constants.Api.CLIENT + "/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Client getClientFromUUID(@PathParam("uuid") UUID uuid) {
        log.log(Level.INFO, "Get client user info for {0}", uuid);

        try {
            return userDB.getClient(uuid);
        } catch (SQLException se) {
            log.log(Level.SEVERE, "Threw a SQLException retrieving a client.");
            log.log(Level.SEVERE, se.getMessage(), se);
        }

        return null;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public AbstractUser getUserFromInfo(@QueryParam("email") String email) {
        log.log(Level.INFO, "Get user ID for user with email address {2}", new Object[]{email});

        try {
            return userDB.getUserByEmail(email);
        } catch (SQLException se) {
            log.log(Level.SEVERE, "Threw a SQLException retrieving a user.");
            log.log(Level.SEVERE, se.getMessage(), se);
        }

        return null;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public AbstractUser getUserFromInfo(@QueryParam("firstName") String firstName, @QueryParam("lastName") String lastName, @QueryParam("email") String email) {
        log.log(Level.INFO, "Get user ID for {0} {1} with email address {2}", new Object[]{firstName, lastName, email});

        try {
            return userDB.getUserByInfo(firstName, lastName, email);
        } catch (SQLException se) {
            log.log(Level.SEVERE, "Threw a SQLException retrieving a user.");
            log.log(Level.SEVERE, se.getMessage(), se);
        }

        return null;
    }

    @PUT
    @Path(Constants.Api.REGISTER_CONSULTANT)
    @Consumes(MediaType.APPLICATION_JSON)
    public void registerClient(UserInfo newUser) {
        log.log(Level.INFO, "Registering new client {0} {1} with email address {2}", new Object[]{newUser.getFirstName(), newUser.getLastName(), newUser.getEmail()});

        try {
            userDB.registerNewClient(newUser.getFirstName(), newUser.getLastName(), newUser.getEmail());
        } catch (SQLException se) {
            log.log(Level.SEVERE, "Threw a SQLException registering a new client.");
            log.log(Level.SEVERE, se.getMessage(), se);
        }
    }

    @PUT
    @Path(Constants.Api.REGISTER_CLIENT)
    @Consumes(MediaType.APPLICATION_JSON)
    public void registerConsultant(UserInfo newUser) {
        log.log(Level.INFO, "Registering new consultant {0} {1} with email address {2}", new Object[]{newUser.getFirstName(), newUser.getLastName(), newUser.getEmail()});

        try {
            userDB.registerNewConsultant(newUser.getFirstName(), newUser.getLastName(), newUser.getEmail());
        } catch (SQLException se) {
            log.log(Level.SEVERE, "Threw a SQLException registering a new consultant.");
            log.log(Level.SEVERE, se.getMessage(), se);
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