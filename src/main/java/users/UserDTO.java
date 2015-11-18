package users;

import java.util.UUID;

public class UserDTO {
    private UUID uuid;

    private String firstName;
    private String lastName;
    private String email;

    private boolean isClient;

    public UserDTO(UUID uuid, String firstName, String lastName, String email, boolean isClient) {
        this.uuid = uuid;

        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;

        this.isClient = isClient;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public UUID getUUID() {
        return uuid;
    }

    public boolean isClient() {
        return isClient;
    }
}