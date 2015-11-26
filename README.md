# Calendar

This calendar has three layers. UI, API, and DB. The design principle was the put something together that not only accomplishes the basic goals of the calendar (booking appointments and setting office hours) but also allows for easy addition or replacement of features. I wanted future API and DB schema changes to be additive. I also wanted to be able to replace pieces of technology if required. For eaxmple, switching from Postgres to any other database technology should be fairly painless. Only files in the database directory need to be changed. If it is a SQL based solution, only configuration details need to be changed.
between running in the IDE vs. the executable jar in regards to loading the static content. It would also provide what port to run on and the connection information for the database.

As for running the application, currently it is set up to run as a simple Java program. Since the DB is on AWS no set up there is needed. One only needs to compile to *.java and execute the main method in the Calendar class. This will serve the website at http://localhost:8080. This is probably most easily done through some sort of IDE. Eclipse, IntelliJ, or NetBeans should be able to read the pom.xml and set everything up automatically.

#Database
The database is two simple Postgres databases. 

Users table is a simple table with the user's name and UUID. The email column is unique as it acts as the log credentials.

Schedule table is a little more complex. It is a list of status deltas. The primary key is the combination of the consultantId and the start time. This combination must be unique. The same consultant cannot have their status change to different types of status at the same time. The clientId column is only relevant for the BOOKED status. There are 3 status types, BOOKED, AVAILABLE, and UNAVAILABLE.

CREATE TABLE "Users"
(
  first_name text,
  last_name text,
  email text,
  client boolean,
  uuid uuid NOT NULL,
  CONSTRAINT "Users_pkey" PRIMARY KEY (uuid),
  CONSTRAINT "Users_email_key" UNIQUE (email)
)
WITH (
  OIDS=FALSE
);

CREATE TABLE "Schedule"
(
  client_id uuid,
  consultant_id uuid NOT NULL,
  start timestamp with time zone NOT NULL,
  to_status text,
  CONSTRAINT "Schedule_pkey" PRIMARY KEY (start, consultant_id)
)
WITH (
  OIDS=FALSE
);

# API
The API is written in Java and is RESTful. The only acceptable way to interact with the database and book an appointment or set avaialability is through this interface. This layer also has the web container, which is embedded Jetty. For simplicity the API actually serves the UI through the same web container as the API.

The REST calls take most 2 main things to perform all operations. uuids and longs that represent milliseconds. Booking, for example, takes 2 uuids to represent the client and consultant. It also takes start time in milliseconds from the epoch, and duration in milliseconds. This should allow us to be timezone independent. Things like user registration have other values in the call.

# UI
Written in AngularJS, the UI utilizes an open source package called fullcalendar http://fullcalendar.io/ and it's Angular wrapper https://github.com/angular-ui/ui-calendar. The UI only interacts with the persistant data in the database through the REST API.

# TODO list to be production ready
* Uncaught TypeError: Cannot read property 'regional' of null
* angular refactoring (cut down on duplicate and similar code regarding fullcalendar)
* cancel appointment, unset office hours
* Show human readable information whenever a clientId or consultantId shows up in the UI (can be retrieved via simple REST GET)
* css styling, this project is ugly so far
* all rest endpoint java methods should return a Response with the relevant data in the body
* get executable jar functional, path issues with static content, is it even being included?
* client should have phone number
* finish unit tests
* finish end to end tests
* have end to end tests not require Calendar already running (stand it up)
* UI tests (karma)
* extensive UI error code handling
* authentication and authorization, sign in with passwords
* protect rest endpoints (use filter, look for auth token)
* ssl
* CI pipeline to automatically test and generate deployable artifacts on commit
* about page with build/version info
* Arivale branding, copyright, listening requirements
