package cal;

import java.util.Date;

public class Hour {
    private Date start;
    private final int hour = 1000 * 60 * 60;

    public Hour(Date start) {
        this.start = start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return new Date(start.getTime() + hour);
    }
}
