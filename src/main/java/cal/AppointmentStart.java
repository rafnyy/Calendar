package cal;


import users.Client;

import java.util.Date;

public class AppointmentStart extends ScheduleDelta{
    private Client client;

    public AppointmentStart(Client client, Date time) {
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
