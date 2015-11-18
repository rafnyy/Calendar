package users;

import java.util.UUID;

public class Consultant extends UserDTO {
    public Consultant(UUID uuid, String firstName, String lastName, String email) {
        super(uuid, firstName, lastName, email, false);
    }
}