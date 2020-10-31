package no.kristiania;

import no.kristiania.database.Employee;
import no.kristiania.database.EmployeeDao;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class HttpServerTest {

    private JdbcDataSource dataSource;

    @BeforeEach
    void setUp(){
        dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");

        Flyway.configure().dataSource(dataSource).load().migrate();
    }

    @Test
    void shouldReturnSuccessfulErrorCode() throws IOException {
        new HttpServer(10001, dataSource);
        HttpClient client = new HttpClient("/echo", "localhost", 10001);
        assertEquals(200, client.getStatusCode());
    }

    @Test
    void shouldReturnUnsuccessfulErrorCode() throws IOException {
        new HttpServer(10002, dataSource);
        HttpClient client = new HttpClient("/echo?status=404", "localhost", 10002);
        assertEquals(404, client.getStatusCode());
    }

    @Test
    void shouldReturnContentLength() throws IOException {
        new HttpServer(10003, dataSource);
        HttpClient client = new HttpClient("/echo?body=HelloWorld", "localhost", 10003);
        assertEquals("10", client.getResponseHeader("Content-Length"));
    }

    @Test
    void shouldReturnResponseBody() throws IOException {
        new HttpServer(10004, dataSource);
        HttpClient client = new HttpClient("/echo?body=HelloWorld", "localhost", 10004);
        assertEquals("HelloWorld", client.getResponseBody());
    }

    @Test
    void shouldReturnFileFromDesk() throws IOException {
        HttpServer server = new HttpServer(10005, dataSource);
        File contentRoot = new File("target/test-classes");

        String fileContent = "Hello World" + new Date();
        Files.writeString(new File(contentRoot, "test.txt").toPath(), fileContent);

        HttpClient client = new HttpClient("/test.txt", "localhost", 10005);
        assertEquals(fileContent, client.getResponseBody());
        assertEquals("text/plain", client.getResponseHeader("Content-Type"));
    }

    @Test
    void shouldReturnCorrectContentType() throws IOException {
        HttpServer server = new HttpServer(10006, dataSource);
        File contentRoot = new File("target/test-classes");

        Files.writeString(new File(contentRoot, "index.html").toPath(), "<h2>Hello World</h2>");

        HttpClient client = new HttpClient("/index.html", "localhost", 10006);
        assertEquals("text/html", client.getResponseHeader("Content-Type"));
    }

    @Test
    void shouldReturn404IfFileNotFound() throws IOException {
        HttpServer server = new HttpServer(10007, dataSource);
        File contentRoot = new File("target/test-classes");

        HttpClient client = new HttpClient("/notFound.txt", "localhost", 10007);
        assertEquals(404, client.getStatusCode());
    }

    @Test
    void shouldPostNewWorker() throws IOException, SQLException {
        HttpServer server = new HttpServer(10008, dataSource);
        String requestBody = "first_name=amina&email=amina@gmail";
        HttpClient client = new HttpClient("/api/newWorker", "localhost", 10008, "POST", requestBody);
        assertEquals(200, client.getStatusCode());
        assertThat(server.getWorker())
                .filteredOn(employee -> employee.getFirstName().equals("amina"))
                .isNotEmpty()
                .satisfies(employee -> assertThat(employee.get(0).getEmail()).isEqualTo("amina@gmail"));

    }

    @Test
    void shouldReturnExistingWorker() throws IOException, SQLException {
        HttpServer server = new HttpServer(10009, dataSource);
        EmployeeDao employeeDao = new EmployeeDao(dataSource);
        Employee employee = new Employee();
        employee.setFirstName("elise");
        employee.setLastName("Easter");
        employee.setEmail("elise@mail");
        employeeDao.insert(employee);
        HttpClient client = new HttpClient("/api/workers", "localhost", 10009);
        assertThat(client.getResponseBody()).contains("<li>elise Easter elise@mail</li>");
    }
}
