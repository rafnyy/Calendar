package users;

import java.util.UUID;

public class Client extends UserDTO {
    public Client(UUID uuid, String firstName, String lastName, String email) {
        super(uuid, firstName, lastName, email, true);
    }
}