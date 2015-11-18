package cal;

public class Constants {
    public static class Client {
        public static final String UUID_COL_NAME = "uuid";
        public static final String FIRST_NAME = "first_name";
        public static final String LAST_NAME = "last_name";
        public static final String EMAIL = "email";
        public static final String IS_CLIENT = "client";
    }

    public static class Schedule {
        public static final String CLIENT_ID = "client_id";
        public static final String CONSULTANT_ID = "consultant_id";
        public static final String TO_STATUS = "to_status";
        public static final String START = "start";

        public enum STATUS {UNAVAILABLE, AVAILABLE, BOOKED}
    }

    public static final int millisecondsInAnHour = 1000 * 60 * 60;
}
