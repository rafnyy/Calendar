package calendar;

public class Constants {
    public static class User {
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

    public static class Api {
        public static final String ROOT = "/api";

        // Book
        public static final String BOOK = ROOT + "/book";

        // User
        public static final String USER = ROOT + "/user";
        public static final String CONSULTANT = "/consultant";
        public static final String CLIENT = "/client";
        public static final String REGISTER_CONSULTANT = "/register-consultant";
        public static final String REGISTER_CLIENT = "/register-client";

        //OfficeHours
        public static final String OFFICE_HOURS = ROOT + "/office-hours";
        public static final String SET = "/set";
        public static final String INSTANT = "/instant";
        public static final String AVAILABLE = "available";
    }
}
