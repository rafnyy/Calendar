package calendar.schedule;

import calendar.Constants;

import java.sql.Timestamp;
import java.util.UUID;

public class AppointmentStart extends ScheduleDelta {

    private UUID clientId;

    public AppointmentStart(Timestamp time, Constants.Schedule.STATUS toStatus, UUID clientId) {
        super(time, toStatus);
        this.clientId = clientId;
    }

    public UUID getClientId() {
        return clientId;
    }

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }
}
