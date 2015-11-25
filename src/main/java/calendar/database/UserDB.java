package calendar.database;

import calendar.Constants;
import calendar.user.AbstractUser;
import calendar.user.Client;
import calendar.user.Consultant;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Singleton
public class UserDB {
    private final DatabaseConnection connection;

    private static final String USERS_TABLE = "\"Users\"";

    @Inject
    public UserDB(DatabaseConnection connection) {
        this.connection = connection;
    }

    public Client getClient(UUID uuid) throws SQLException {
        return (Client) getUser(uuid, true);
    }

    public Consultant getConsultant(UUID uuid) throws SQLException {
        return (Consultant) getUser(uuid, false);
    }

    public Set<Consultant> getAllConsultants() throws SQLException {
        Set<Consultant> consultants = new HashSet<>();

        String getRowSql = "SELECT * FROM " + USERS_TABLE + " WHERE "
                + Constants.User.IS_CLIENT + " IS FALSE";

        PreparedStatement preparedStatement = connection.getConnection().prepareStatement(getRowSql);

        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            String firstName = resultSet.getString(Constants.User.FIRST_NAME);
            String lastName = resultSet.getString(Constants.User.LAST_NAME);
            String email = resultSet.getString(Constants.User.EMAIL);
            UUID uuid = (UUID)resultSet.getObject(Constants.User.UUID_COL_NAME);

            consultants.add(new Consultant(uuid, firstName, lastName, email));
        }

        return consultants;
    }

    public AbstractUser getUser(UUID uuid, boolean isClient) throws SQLException {
        AbstractUser user = null;

        String getRowSql = "SELECT * FROM " + USERS_TABLE + " WHERE "
                + Constants.User.UUID_COL_NAME + "=:" + Constants.User.UUID_COL_NAME +
                " AND " + Constants.User.IS_CLIENT + " IS " + isClient;

        NamedParameterStatement preparedStatement = new NamedParameterStatement(connection.getConnection(), getRowSql);
        preparedStatement.setObject(Constants.User.UUID_COL_NAME, uuid);

        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            String firstName = resultSet.getString(Constants.User.FIRST_NAME);
            String lastName = resultSet.getString(Constants.User.LAST_NAME);
            String email = resultSet.getString(Constants.User.EMAIL);

            user = getUser(firstName, lastName, email, uuid, isClient);
        }

        return user;
    }

    public AbstractUser getUserByEmail(String email) throws SQLException {
        String getRowSql = "SELECT * FROM " + USERS_TABLE + " WHERE "
                 + Constants.User.EMAIL + "=:" + Constants.User.EMAIL;

        NamedParameterStatement preparedStatement = new NamedParameterStatement(connection.getConnection(), getRowSql);
        preparedStatement.setString(Constants.User.EMAIL, email);

        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            UUID uuid = (UUID) resultSet.getObject(Constants.User.UUID_COL_NAME);
            String firstName = resultSet.getString(Constants.User.FIRST_NAME);
            String lastName = resultSet.getString(Constants.User.LAST_NAME);

            boolean isClient = resultSet.getBoolean(Constants.User.IS_CLIENT);

            return getUser(firstName, lastName, email, uuid, isClient);
        }

        return null;
    }

    public AbstractUser getUserByInfo(String firstName, String lastName, String email) throws SQLException {
        String getRowSql = "SELECT * FROM " + USERS_TABLE + " WHERE "
                + Constants.User.FIRST_NAME + "=:" + Constants.User.FIRST_NAME
                + " AND " + Constants.User.LAST_NAME + "=:" + Constants.User.LAST_NAME
                + " AND " + Constants.User.EMAIL + "=:" + Constants.User.EMAIL;

        NamedParameterStatement preparedStatement = new NamedParameterStatement(connection.getConnection(), getRowSql);
        preparedStatement.setString(Constants.User.FIRST_NAME, firstName);
        preparedStatement.setString(Constants.User.LAST_NAME, lastName);
        preparedStatement.setString(Constants.User.EMAIL, email);

        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            UUID uuid = (UUID) resultSet.getObject(Constants.User.UUID_COL_NAME);

            boolean isClient = resultSet.getBoolean(Constants.User.IS_CLIENT);

            return getUser(firstName, lastName, email, uuid, isClient);
        }

        return null;
    }

    private AbstractUser getUser(String firstName, String lastName, String email, UUID uuid, boolean isClient) {
        AbstractUser user;
        if (isClient) {
            user = new Client(uuid, firstName, lastName, email);
        } else {
            user = new Consultant(uuid, firstName, lastName, email);
        }
        return user;
    }

    public Client registerNewClient(String firstName, String lastName, String email) throws SQLException {
        return (Client)registerNewUser(firstName, lastName, email, true);
    }

    public Consultant registerNewConsultant(String firstName, String lastName, String email) throws SQLException {
        return (Consultant)registerNewUser(firstName, lastName, email, false);
    }

    public AbstractUser registerNewUser(String firstName, String lastName, String email, boolean isClient) throws SQLException {
        String insertTableSQL = "INSERT INTO " + USERS_TABLE
                + "(" + Constants.User.FIRST_NAME + ", " + Constants.User.LAST_NAME + ", " + Constants.User.EMAIL + ", " + Constants.User.UUID_COL_NAME + ", " + Constants.User.IS_CLIENT + ") VALUES"
                + "(:" + Constants.User.FIRST_NAME + ", :" + Constants.User.LAST_NAME + ", :" + Constants.User.EMAIL + ", :" + Constants.User.UUID_COL_NAME + ", :" + Constants.User.IS_CLIENT + ")";

        NamedParameterStatement preparedStatement = new NamedParameterStatement(connection.getConnection(), insertTableSQL);
        preparedStatement.setString(Constants.User.FIRST_NAME, firstName);
        preparedStatement.setString(Constants.User.LAST_NAME, lastName);
        preparedStatement.setString(Constants.User.EMAIL, email);
        preparedStatement.setObject(Constants.User.UUID_COL_NAME, UUID.randomUUID());
        preparedStatement.setBoolean(Constants.User.IS_CLIENT, isClient);

        preparedStatement.executeUpdate();

        return getUserByInfo(firstName, lastName, email);
    }
}
