package no.kristiania;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpClientTest {

    @Test
    void shouldReturnSuccessfullStatusCode() throws IOException{
        HttpClient client = new HttpClient("/echo", "urlecho.appspot.com", 80);
        assertEquals(200, client.getStatusCode());
    }

    @Test
    void shouldReturnErrorStatusCode() throws IOException{
        HttpClient client = new HttpClient("/echo?status=404", "urlecho.appspot.com", 80);
        assertEquals(404, client.getStatusCode());
    }

    @Test
    void shouldReadResponseHeader() throws IOException{
        HttpClient client = new HttpClient("/echo?body=Kristiania", "urlecho.appspot.com", 80);
        assertEquals("10", client.getResponseHeader("Content-Length"));
    }

    @Test
    void shouldReadResponseBody() throws IOException{
        HttpClient client = new HttpClient("/echo?body=Kristiania", "urlecho.appspot.com", 80);
        assertEquals("Kristiania", client.getResponseBody());
    }
}
