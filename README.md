# Calendar

This calendar has three layers. UI, API, and DB.

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
Written in AngularJS, the UI utilizies an open source package called fullcalendar http://fullcalendar.io/ and it's Angular wrapper https://github.com/angular-ui/ui-calendar. The UI only interacts with the persistant data in the database through the REST API.

TODO list
* events seem to register in the wrong place on the calendar, they act correctly accoridng to the DB though (timezone issue?) fullcalendar is supposed to handle this and the API only deals with milliseconds from the epoch
* need to click back and forth to get calendar to load events
* Need to return and set clientId or consultantId after registering new user so we can load calendar
* Uncaught TypeError: Cannot read property 'clone' of null
* email address must be unique since it acts as log in
* client calendar should be able to see own bookings
* consultant calendar needs client information on bookings
* client should have phone number
* cancel appointment, unset office hours
* css styling, this project is ugly so far
* angular refactoring (cut down on duplicate and similiar code regarding fullcalendar)
* get executable jar functional, path issues with static content, is it even being included
* unit tests
* finish end to end tests
* have end to end tests not require Calendar already running (stand it up)
* UI tests (karma)
* authentication and authorization, sign in with passwords
* protect rest endpoints (use filter, look for auth token)
* ssl
