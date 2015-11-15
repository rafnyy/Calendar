package cal;

import org.joda.time.Instant;

import java.util.List;

public class Schedule {
    private Instant startDate;
    private Instant endDate;

    private List<ScheduleDelta> deltas;

    public Schedule(Instant startDate, Instant endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }
}
