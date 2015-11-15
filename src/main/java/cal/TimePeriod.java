package cal;

import org.joda.time.Instant;

public abstract class TimePeriod {
    protected Instant start;
    protected int durationInMilliseconds;

    public TimePeriod(Instant start, int durationInMilliseconds) {
        this.start = start;
        this.durationInMilliseconds = durationInMilliseconds;
    }

    public void setStart(Instant start) {
        this.start = start;
    }

    public Instant getStart() {
        return start;
    }

    public Instant getEnd() {
        return new Instant(start.getMillis() + durationInMilliseconds);
    }
}
