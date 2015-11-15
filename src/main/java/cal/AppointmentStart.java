package cal;

import org.joda.time.Instant;
import users.Client;

public class AppointmentStart extends ScheduleDelta {
    private Client client;

    public AppointmentStart(Client client, Instant time) {
        super(time, Constants.STATUS.BOOKED);
        this.client = client;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
