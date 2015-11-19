package calendar.user;

import java.util.UUID;

public class Client extends AbstractUser {
    public Client(UUID uuid, String firstName, String lastName, String email) {
        super(uuid, firstName, lastName, email, true);
    }
}