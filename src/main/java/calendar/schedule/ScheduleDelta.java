package calendar.schedule;

import calendar.Constants;

import java.sql.Timestamp;

public class ScheduleDelta {
    private Timestamp time;
    private Constants.Schedule.STATUS toStatus;

    public ScheduleDelta(Timestamp time, Constants.Schedule.STATUS toStatus) {
        this.time = time;
        this.toStatus = toStatus;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public Constants.Schedule.STATUS getToStatus() {
        return toStatus;
    }

    public void setToStatus(Constants.Schedule.STATUS toStatus) {
        this.toStatus = toStatus;
    }
}
