package calendar.user;

import java.util.UUID;

public class Consultant extends AbstractUser {
    public Consultant(UUID uuid, String firstName, String lastName, String email) {
        super(uuid, firstName, lastName, email, false);
    }
}