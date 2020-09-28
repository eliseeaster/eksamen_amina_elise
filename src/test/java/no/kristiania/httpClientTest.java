package no.kristiania;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class httpClientTest {

    @Test
    void shouldReturnSuccessfullStatusCode() throws IOException{
        httpClient client = new httpClient("/echo", "urlecho.appspot.com", 80);
        assertEquals(200, client.getStatusCode());
    }

    @Test
    void shouldReturnErrorStatusCode() throws IOException{
        httpClient client = new httpClient("/echo?status=404", "urlecho.appspot.com", 80);
        assertEquals(404, client.getStatusCode());
    }

    @Test
    void shouldReadResponseHeader() throws IOException{
        httpClient client = new httpClient("/echo?body=Kristiania", "urlecho.appspot.com", 80);
        assertEquals("10", client.getResponseHeader("Content-Length"));
    }

    @Test
    void shouldReadResponseBody() throws IOException{
        httpClient client = new httpClient("/echo?body=Kristiania", "urlecho.appspot.com", 80);
        assertEquals("Kristiania", client.getResponseBody());
    }
}
