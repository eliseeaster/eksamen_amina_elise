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
}
