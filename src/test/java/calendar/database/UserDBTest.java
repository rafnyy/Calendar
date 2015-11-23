package calendar.database;

import org.junit.*;
import org.mockito.Mock;

import java.sql.Connection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class UserDBTest {
    @Mock
    private DatabaseConnection databaseConnection;

    @Mock
    private Connection connection;

    @BeforeClass
    public void setUp() throws Exception {
        when(databaseConnection.getConnection()).thenReturn(connection);

    }

    @AfterClass
    public void tearDown() throws Exception {

    }

    @Test
    public void testGetClient() throws Exception {

    }

    @Test
    public void testGetConsultant() throws Exception {

    }

    @Test
    public void testGetAllConsultants() throws Exception {

    }

    @Test
    public void testGetUser() throws Exception {

    }

    @Test
    public void testGetUserByEmail() throws Exception {

    }

    @Test
    public void testGetUserByInfo() throws Exception {

    }

    @Test
    public void testRegisterNewClient() throws Exception {

    }

    @Test
    public void testRegisterNewConsultant() throws Exception {

    }

    @Test
    public void testRegisterNewUser() throws Exception {

    }
}