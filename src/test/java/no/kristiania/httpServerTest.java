package no.kristiania;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class httpServerTest {

    @Test
    void shouldReturnSuccessfulErrorCode() throws IOException {
        new httpServer(10001);
        httpClient client = new httpClient("/echo", "localhost", 10001);
        assertEquals(200, client.getStatusCode());
    }

    @Test
    void shouldReturnUnsuccessfulErrorCode() throws IOException {
        new httpServer(10002);
        httpClient client = new httpClient("/echo?status=404", "localhost", 10002);
        assertEquals(404, client.getStatusCode());
    }

}
