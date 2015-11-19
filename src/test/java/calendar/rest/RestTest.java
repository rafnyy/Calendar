package calendar.rest;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.After;
import org.junit.Before;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public abstract class RestTest {
    protected final HttpClient client = HttpClientBuilder.create().build();

    @Before
    public void classSetUp() throws Exception {

    }

    @After
    public void classTearDown() throws Exception {

    }

    protected String getResponseContent(HttpResponse response) throws IOException {
        if(response.getEntity() == null || response.getEntity().getContent() == null) {
            return null;
        }

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        return result.toString();
    }
}
