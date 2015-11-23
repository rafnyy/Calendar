package calendar.rest;

import calendar.Constants;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URI;

import static org.testng.Assert.assertEquals;

public class UserFunctionalTest extends RestFunctionalTest {
    private static final String ROB_USER_JSON = "{\"uuid\":\"653a631d-9a4d-43d6-bbb4-7fb32c8dbaa8\",\"firstName\":\"Rob\",\"lastName\":\"Fusco\",\"email\":\"rob@fusco.com\",\"client\":true}";
    private static final String FAKE_USER_UUID = "5c8d95d1-3ef4-4bdd-85ca-267bb6cd3803";

    @BeforeMethod
    public void setUp() throws Exception {

    }

    @AfterMethod
    public void tearDown() throws Exception {

    }

    @Test
    public void testGetConsultantFromUUID() throws Exception {
        URIBuilder builder = new URIBuilder()
                .setScheme(TestConstants.calendarScheme)
                .setHost(TestConstants.calendarHost)
                .setPort(TestConstants.calendarPort)
                .setPath(Constants.Api.USER + Constants.Api.CONSULTANT + "/5c8d95d1-3ef4-4bdd-85ca-267bb6cd380d");

        URI uri = builder.build();
        HttpGet request = new HttpGet(uri);

        HttpResponse response = client.execute(request);

        assertEquals(response.getStatusLine().getStatusCode(), 200, "Checking response code");
        assertEquals(getResponseContent(response), "{\"uuid\":\"5c8d95d1-3ef4-4bdd-85ca-267bb6cd380d\",\"firstName\":\"George\",\"lastName\":\"Zaius\",\"email\":\"ape@orange.com\",\"client\":false}", "Checking response content");
    }

    @Test
    public void testGetConsultantFromUUIDNoUser() throws Exception {
        URIBuilder builder = new URIBuilder()
                .setScheme(TestConstants.calendarScheme)
                .setHost(TestConstants.calendarHost)
                .setPort(TestConstants.calendarPort)
                .setPath(Constants.Api.USER + Constants.Api.CONSULTANT + "/" + FAKE_USER_UUID);

        URI uri = builder.build();
        HttpGet request = new HttpGet(uri);

        HttpResponse response = client.execute(request);

        assertEquals(response.getStatusLine().getStatusCode(), 204, "Checking response code");
        assertEquals(getResponseContent(response), null, "Checking response content");
    }

    @Test
    public void testGetClientFromUUID() throws Exception {
        URIBuilder builder = new URIBuilder()
                .setScheme(TestConstants.calendarScheme)
                .setHost(TestConstants.calendarHost)
                .setPort(TestConstants.calendarPort)
                .setPath(Constants.Api.USER + Constants.Api.CLIENT + "/653a631d-9a4d-43d6-bbb4-7fb32c8dbaa8");

        URI uri = builder.build();
        HttpGet request = new HttpGet(uri);

        HttpResponse response = client.execute(request);

        assertEquals(response.getStatusLine().getStatusCode(), 200, "Checking response code");
        assertEquals(getResponseContent(response), "{\"uuid\":\"653a631d-9a4d-43d6-bbb4-7fb32c8dbaa8\",\"firstName\":\"Rob\",\"lastName\":\"Fusco\",\"email\":\"rob@fusco.com\",\"client\":true}", "Checking response content");
    }

    @Test
    public void testGetClientFromUUIDNoUser() throws Exception {
        URIBuilder builder = new URIBuilder()
                .setScheme(TestConstants.calendarScheme)
                .setHost(TestConstants.calendarHost)
                .setPort(TestConstants.calendarPort)
                .setPath(Constants.Api.USER + Constants.Api.CLIENT + "/" + FAKE_USER_UUID);

        URI uri = builder.build();
        HttpGet request = new HttpGet(uri);

        HttpResponse response = client.execute(request);

        assertEquals(response.getStatusLine().getStatusCode(), 204, "Checking response code");
        assertEquals(getResponseContent(response), null, "Checking response content");
    }

    @Test
    public void testGetUserFromInfo() throws Exception {
        URIBuilder builder = new URIBuilder()
                .setScheme(TestConstants.calendarScheme)
                .setHost(TestConstants.calendarHost)
                .setPort(TestConstants.calendarPort)
                .setPath(Constants.Api.USER)
                .setParameter("firstName", "Rob")
                .setParameter("lastName", "Fusco")
                .setParameter("email", "rob@fusco.com");

        URI uri = builder.build();
        HttpGet request = new HttpGet(uri);

        HttpResponse response = client.execute(request);

        assertEquals(response.getStatusLine().getStatusCode(), 200, "Checking response code");
        assertEquals(getResponseContent(response), ROB_USER_JSON, "Checking response content");
    }

    @Test
    public void testGetUserFromInfoNoUser() throws Exception {
        URIBuilder builder = new URIBuilder()
                .setScheme(TestConstants.calendarScheme)
                .setHost(TestConstants.calendarHost)
                .setPort(TestConstants.calendarPort)
                .setPath(Constants.Api.USER)
                .setParameter("firstName", "FAKE")
                .setParameter("lastName", "FAKE")
                .setParameter("email", "FAKE");

        URI uri = builder.build();
        HttpGet request = new HttpGet(uri);

        HttpResponse response = client.execute(request);

        assertEquals(response.getStatusLine().getStatusCode(), 204, "Checking response code");
        assertEquals(getResponseContent(response), null, "Checking response content");
    }

    @Test
    public void testRegisterClient() throws Exception {

    }

    @Test
    public void testRegisterConsultant() throws Exception {

    }
}