package calendar.schedule;

import calendar.Constants;
import calendar.user.Consultant;

import java.sql.Timestamp;
import java.util.List;

public class Schedule {
    private Consultant consultant;
    private Timestamp startDate;
    private Timestamp endDate;

    private List<ScheduleDelta> deltas;
    private Constants.Schedule.STATUS startStatus;

    public Schedule(Consultant consultant, Timestamp startDate, Timestamp endDate, List<ScheduleDelta> deltas, Constants.Schedule.STATUS startStatus) {
        this.consultant = consultant;
        this.startDate = startDate;
        this.endDate = endDate;
        this.deltas = deltas;
        this.startStatus = startStatus;
    }

    public Consultant getConsultant() {
        return consultant;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public List<ScheduleDelta> getDeltas() {
        return deltas;
    }

    public Constants.Schedule.STATUS getStartStatus() {
        return startStatus;
    }
}