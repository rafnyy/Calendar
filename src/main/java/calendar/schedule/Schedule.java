package calendar.schedule;

import calendar.Constants;
import calendar.user.Consultant;
import org.joda.time.Instant;

import java.util.List;

public class Schedule {
    private Consultant consultant;
    private Instant startDate;
    private Instant endDate;

    private List<ScheduleDelta> deltas;
    private Constants.Schedule.STATUS startStatus;

    public Schedule(Consultant consultant, Instant startDate, Instant endDate, List<ScheduleDelta> deltas, Constants.Schedule.STATUS startStatus) {
        this.consultant = consultant;
        this.startDate = startDate;
        this.endDate = endDate;
        this.deltas = deltas;
        this.startStatus = startStatus;
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

    public List<ScheduleDelta> getDeltas() {
        return deltas;
    }

    public Constants.Schedule.STATUS getStartStatus() {
        return startStatus;
    }
}