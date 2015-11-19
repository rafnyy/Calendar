package calendar.schedule;

import calendar.Constants;
import org.joda.time.Instant;

public class ScheduleDelta {
    private Instant time;
    private Constants.Schedule.STATUS toStatus;

    public ScheduleDelta(Instant time, Constants.Schedule.STATUS toStatus) {
        this.time = time;
        this.toStatus = toStatus;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public Constants.Schedule.STATUS getToStatus() {
        return toStatus;
    }

    public void setToStatus(Constants.Schedule.STATUS toStatus) {
        this.toStatus = toStatus;
    }
}
