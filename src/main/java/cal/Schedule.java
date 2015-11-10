package cal;


import java.util.Date;
import java.util.Set;

public class Schedule {
    private Date startDate;
    private Date endDate;

    private Set<Hour> booked;
    private Set<Hour> available;



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

    public Set<Hour> getBooked() {
        return booked;
    }

    public Set<Hour> getAvailable() {
        return available;
    }
}
