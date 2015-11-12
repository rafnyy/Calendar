package rest;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import database.DatabaseConnection;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

@Path("/book")
@Singleton
public class Book {
    private final DatabaseConnection connection;

    @Inject
    public Book(DatabaseConnection connection) {
        this.connection = connection;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void book() {


        try
        {
            Statement st = connection.getConnection().createStatement();
            st.executeQuery("INSERT INTO \"Schedule\"(\n" +
                    "            client_id, consultant_id, to_status, start)\n" +
                    "    VALUES (2, 3, 'BOOKED', NOW());\n");


            st.close();
        }
        catch (SQLException se) {
            System.err.println("Threw a SQLException creating the list of blogs.");
            System.err.println(se.getMessage());
        }
    }
}