package cal;

import com.google.inject.Inject;
import database.DatabaseConnection;
import org.joda.time.Instant;

import java.util.List;

import com.google.inject.assistedinject.*;
import users.Consultant;

public class Schedule {
    private Consultant consultant;
    private Instant startDate;
    private Instant endDate;

    private List<ScheduleDelta> deltas;
    private Constants.Schedule.STATUS startStatus;

     public Schedule(@Assisted Consultant consultant, @Assisted("startDate") Instant startDate, @Assisted("endDate") Instant endDate) {
        this.consultant = consultant;
        this.startDate = startDate;
        this.endDate = endDate;


    }

    public Consultant getConsultant() {
        return consultant;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }
}
