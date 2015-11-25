package calendar.rest;

import calendar.database.ScheduleDB;
import calendar.rest.Book.Booking;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;

import javax.ws.rs.core.Response;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class BookTest {

    @Mock
    private ScheduleDB scheduleDB;

    private Book book;

    private Booking validBooking = new Booking(UUID.randomUUID(), UUID.randomUUID(), 100, 1000);
    private Booking invalidBooking = new Booking(UUID.randomUUID(), UUID.randomUUID(), 10, 10);

    @BeforeClass
    public void setUp() throws Exception {
        initMocks(this);
        book = new Book(scheduleDB);

        when(scheduleDB.available(
                validBooking.getConsultantId(),
                validBooking.getStartTime(),
                validBooking.getStartTime() + validBooking.getDurationMillis())).thenReturn(true);

        when(scheduleDB.available(
                invalidBooking.getConsultantId(),
                invalidBooking.getStartTime(),
                invalidBooking.getStartTime() + invalidBooking.getDurationMillis())).thenReturn(false);
    }

    @Test
    public void testBookValid() throws Exception {
        Response response = book.book(validBooking);
        assertEquals("Checking that a valid booking returns status code ok", Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testBookInvalid() throws Exception {
        Response response = book.book(invalidBooking);
        assertEquals("Checking that a invalid booking returns status code conflict", Response.Status.CONFLICT.getStatusCode(), response.getStatus());
    }
}