package cal;

import java.util.Date;
import java.util.List;

public class Schedule {
    private Date startDate;
    private Date endDate;

    private List<ScheduleDelta> deltas;

    public Schedule(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }
}
