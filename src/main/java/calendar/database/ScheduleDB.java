package calendar.database;

import calendar.Constants;
import calendar.schedule.Schedule;
import calendar.schedule.ScheduleDelta;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.joda.time.Instant;
import calendar.user.Consultant;

import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Singleton
public class ScheduleDB {
    private final DatabaseConnection connection;
    private final UserDB userDB;

    private static final String SCHEDULE_TABLE = "\"Schedule\"";
    private final static String END = "end";

    @Inject
    public ScheduleDB(DatabaseConnection connection, UserDB userDB) {
        this.connection = connection;
        this.userDB = userDB;
   }

    public void insertStatusChange(UUID clientId, UUID consultantId, Timestamp time, Constants.Schedule.STATUS status) throws SQLException {
        String clientColName = "";
        String clientNamedParameter = "";
        if(status.equals(Constants.Schedule.STATUS.BOOKED)){
            clientColName = Constants.Schedule.CLIENT_ID + ", ";
            clientNamedParameter = ":" + clientColName;
        }

        String insertSQL = "INSERT INTO " + SCHEDULE_TABLE + " "
                + "(" + clientColName + Constants.Schedule.CONSULTANT_ID + ", " + Constants.Schedule.TO_STATUS + ", " + Constants.Schedule.START + ") VALUES"
                + "(" + clientNamedParameter + ":" + Constants.Schedule.CONSULTANT_ID + ", :" + Constants.Schedule.TO_STATUS + ", :" + Constants.Schedule.START + ")";

        NamedParameterStatement namedParameterStatement = new NamedParameterStatement(connection.getConnection(), insertSQL);
        if(status.equals(Constants.Schedule.STATUS.BOOKED)){
            namedParameterStatement.setObject(Constants.Schedule.CLIENT_ID, clientId);
        }
        namedParameterStatement.setObject(Constants.Schedule.CONSULTANT_ID, consultantId);
        namedParameterStatement.setString(Constants.Schedule.TO_STATUS, status.name());
        namedParameterStatement.setTimestamp(Constants.Schedule.START, time);

        namedParameterStatement.executeUpdate();
    }

    public Schedule getSchedule(Consultant consultant, Timestamp startTime, Timestamp endTime) throws SQLException {
        Constants.Schedule.STATUS startStatus = getStatusAtInstant(consultant.getUUID(), startTime);
        List<ScheduleDelta> deltas = new ArrayList<>();

        String getRowSQL = "SELECT * FROM " + SCHEDULE_TABLE + " WHERE "
                + Constants.Schedule.CONSULTANT_ID + "=:" + Constants.Schedule.CONSULTANT_ID
                + " AND " + Constants.Schedule.START + ">=:" + Constants.Schedule.START
                + " AND " + Constants.Schedule.START + "<:" + END
                + " ORDER BY " + Constants.Schedule.START + " ASC";

        NamedParameterStatement preparedStatement = new NamedParameterStatement(connection.getConnection(), getRowSQL);
        preparedStatement.setObject(Constants.Schedule.CONSULTANT_ID, consultant.getUUID());
        preparedStatement.setTimestamp(Constants.Schedule.START, startTime);
        preparedStatement.setTimestamp(END, endTime);

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            Timestamp time = resultSet.getTimestamp(Constants.Schedule.START);
            Instant start = new Instant(time.getTime());
            Constants.Schedule.STATUS status = Constants.Schedule.STATUS.valueOf(resultSet.getString(Constants.Schedule.TO_STATUS));

            deltas.add(new ScheduleDelta(start, status));
        }

        return new Schedule(consultant, new Instant(startTime.getTime()), new Instant(endTime.getTime()), deltas, startStatus);
    }

    public Constants.Schedule.STATUS getStatusAtInstant(UUID consultantId, Timestamp time) throws SQLException {
        Constants.Schedule.STATUS status = Constants.Schedule.STATUS.UNAVAILABLE;

        String getRowSQL = "SELECT " + Constants.Schedule.TO_STATUS + " FROM " + SCHEDULE_TABLE + " WHERE "
                + Constants.Schedule.CONSULTANT_ID + "=:" + Constants.Schedule.CONSULTANT_ID +
                " AND " + Constants.Schedule.START + "<:" + Constants.Schedule.START
                + " ORDER BY " + Constants.Schedule.START + " DESC";

        NamedParameterStatement preparedStatement = new NamedParameterStatement(connection.getConnection(), getRowSQL);
        preparedStatement.setObject(Constants.Schedule.CONSULTANT_ID, consultantId);
        preparedStatement.setTimestamp(Constants.Schedule.START, time);

        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            status = Constants.Schedule.STATUS.valueOf(resultSet.getString(Constants.Schedule.TO_STATUS));
        }

        return status;
    }

    public boolean available(UUID consultantId, long startDate, long endDate) throws SQLException {
        Consultant consultant = userDB.getConsultant(consultantId);
        Schedule schedule =  getSchedule(consultant, new Timestamp(startDate), new Timestamp(endDate));

        if (!schedule.getStartStatus().equals(Constants.Schedule.STATUS.AVAILABLE)) {
            return false;
        }

        if (schedule.getDeltas().size() > 0) {
            return false;
        }

        return true;
    }

    public boolean unbooked(UUID consultantId, long startDate, long endDate) throws SQLException {
        Consultant consultant = userDB.getConsultant(consultantId);
        Schedule schedule =  getSchedule(consultant, new Timestamp(startDate), new Timestamp(endDate));

        if (schedule.getStartStatus().equals(Constants.Schedule.STATUS.BOOKED)) {
            return false;
        }

        Iterator<ScheduleDelta> iterator = schedule.getDeltas().iterator();

        while(iterator.hasNext()) {
            if(iterator.next().getToStatus().equals(Constants.Schedule.STATUS.BOOKED)) {
                return false;
            }
        }

        return true;
    }
}
