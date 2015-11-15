package cal;

import org.joda.time.Instant;

public class Hour extends TimePeriod {

    public Hour(Instant start) {
        super(start, Constants.millisecondsInAnHour);
    }
}