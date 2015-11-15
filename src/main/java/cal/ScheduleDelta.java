package cal;

import org.joda.time.Instant;

public abstract class ScheduleDelta {
    private Instant time;
    private Constants.STATUS toStatus;

    public ScheduleDelta(Instant time, Constants.STATUS toStatus) {
        this.time = time;
        this.toStatus = toStatus;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public Constants.STATUS getToStatus() {
        return toStatus;
    }

    public void setToStatus(Constants.STATUS toStatus) {
        this.toStatus = toStatus;
    }
}
