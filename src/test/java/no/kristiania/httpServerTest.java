package no.kristiania;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class httpServerTest {
    @Test
    void shouldReturnSuccessfulErrorCode() throws IOException {
        httpServer server = new httpServer(10001);
        httpClient client = new httpClient("/echo", "localhost", 10001);
        assertEquals(200, client.getStatusCode());
    }
}
