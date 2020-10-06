package no.kristiania;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpServerTest {

    @Test
    void shouldReturnSuccessfulErrorCode() throws IOException {
        new HttpServer(10001);
        HttpClient client = new HttpClient("/echo", "localhost", 10001);
        assertEquals(200, client.getStatusCode());
    }

    @Test
    void shouldReturnUnsuccessfulErrorCode() throws IOException {
        new HttpServer(10002);
        HttpClient client = new HttpClient("/echo?status=404", "localhost", 10002);
        assertEquals(404, client.getStatusCode());
    }

    @Test
    void shouldReturnContentLength() throws IOException {
        new HttpServer(10003);
        HttpClient client = new HttpClient("/echo?body=HelloWorld", "localhost", 10003);
        assertEquals("10", client.getResponseHeader("Content-Length"));
    }

    @Test
    void shouldReturnResponseBody() throws IOException {
        new HttpServer(10004);
        HttpClient client = new HttpClient("/echo?body=HelloWorld", "localhost", 10004);
        assertEquals("HelloWorld", client.getResponseBody());
    }

    @Test
    void shouldReturnFileFromDesk() throws IOException {
        HttpServer server = new HttpServer(10005);
        File contentRoot = new File("target/");
        server.setContentRoot(contentRoot);

        String fileContent = "Hello World" + new Date();
        Files.writeString(new File(contentRoot, "test.txt").toPath(), fileContent);

        HttpClient client = new HttpClient("/test.txt", "localhost", 10005);
        assertEquals(fileContent, client.getResponseBody());
        assertEquals("text/plain", client.getResponseHeader("Content-Type"));
    }

    @Test
    void shouldReturnCorrectContentType() throws IOException {
        HttpServer server = new HttpServer(10006);
        File contentRoot = new File("target/");
        server.setContentRoot(contentRoot);

        Files.writeString(new File(contentRoot, "index.html").toPath(), "<h2>Hello World</h2>");

        HttpClient client = new HttpClient("/index.html", "localhost", 10006);
        assertEquals("text/html", client.getResponseHeader("Content-Type"));
    }

    @Test
    void shouldReturn404IfFileNotFound() throws IOException {
        HttpServer server = new HttpServer(10007);
        File contentRoot = new File("target/");
        server.setContentRoot(contentRoot);

        HttpClient client = new HttpClient("/notFound.txt", "localhost", 10007);
        assertEquals(404, client.getStatusCode());
    }

    @Test
    void shouldPostNewWorker() throws IOException {
        HttpServer server = new HttpServer(10008);
        HttpClient client = new HttpClient("/api/newWorker", "localhost", 10008, "POST", "full_name=amina&email_address=amina@gmail");
        assertEquals(200, client.getStatusCode());
        assertEquals(List.of("amina"), server.getWorkerNames());
    }

    @Test
    void shouldReturnExistingWorker() throws IOException {
        HttpServer server = new HttpServer(10009);
        server.getWorkerNames().add("elise");
        HttpClient client = new HttpClient("/api/workers", "localhost", 10009);
        assertEquals("<ul><li>elise</li></ul>", client.getResponseBody());

        
    }
}