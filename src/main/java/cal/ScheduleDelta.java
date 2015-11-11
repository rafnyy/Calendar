package cal;

import java.util.Date;

public abstract class ScheduleDelta {
    private Date time;
    private Constants.STATUS toStatus;

    public ScheduleDelta(Date time, Constants.STATUS toStatus) {
        this.time = time;
        this.toStatus = toStatus;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Constants.STATUS getToStatus() {
        return toStatus;
    }

    public void setToStatus(Constants.STATUS toStatus) {
        this.toStatus = toStatus;
    }
}
