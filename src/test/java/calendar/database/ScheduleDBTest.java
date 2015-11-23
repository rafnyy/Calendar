package calendar.database;

import org.junit.*;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

import java.sql.Connection;

import static org.junit.Assert.*;




public class ScheduleDBTest {
    @Mock
    private DatabaseConnection databaseConnection;

    @Mock
    private Connection connection;

    @Mock
    private UserDB userDB;

    @BeforeClass
    public void setUp() throws Exception {
        when(databaseConnection.getConnection()).thenReturn(connection);

    }

    @AfterClass
    public void tearDown() throws Exception {

    }

    @Test
    public void testInsertStatusChange() throws Exception {

    }

    @Test
    public void testGetSchedule() throws Exception {

    }

    @Test
    public void testGetStatusAtInstant() throws Exception {

    }

    @Test
    public void testAvailable() throws Exception {

    }

    @Test
    public void testUnbooked() throws Exception {

    }

    @Test
    public void testDelete() throws Exception {

    }
}