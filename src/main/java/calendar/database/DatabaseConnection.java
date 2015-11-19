package calendar.database;

import com.google.inject.Singleton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Singleton
public class DatabaseConnection {
    private static final String DRIVER = "org.postgresql.Driver";
    private static final String URL = "jdbc:postgresql://calendar.crapm6ugbauu.us-west-2.rds.amazonaws.com:5432/Calendar";
    private static final String USERNAME = "rfusco";
    private static final String PASSWORD = "admin123";

    private static Connection conn;

    public DatabaseConnection() throws ClassNotFoundException, SQLException {
        Class.forName(DRIVER);
        conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    public Connection getConnection() {
        return conn;
    }
}
