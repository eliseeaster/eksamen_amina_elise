package no.kristiania;

import no.kristiania.database.Employee;
import no.kristiania.database.EmployeeDao;
import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class HttpServer {

    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

    private EmployeeDao employeeDao;

    public HttpServer(int port, DataSource dataSource) throws IOException {

        employeeDao = new EmployeeDao(dataSource);
        ServerSocket serverSocket = new ServerSocket(port);

        new Thread(() -> {
            while (true) {
                    try (Socket clientSocket = serverSocket.accept()) {

                        handleRequest(clientSocket);
                } catch (IOException | SQLException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void handleRequest(Socket clientSocket) throws IOException, SQLException {
        HttpMessage request = new HttpMessage(clientSocket);
        String requestLine = request.getStartLine();
        System.out.println("REQUEST" + requestLine);

        String requestMethod = requestLine.split(" ")[0];

        String requestTarget = requestLine.split(" ")[1];


        int questionPos = requestTarget.indexOf('?');

        String requestPath = (questionPos != -1) ? requestTarget.substring(0, questionPos) : requestTarget;

        if(requestMethod.equals("POST")){
            QueryString requestParameter = new QueryString(request.getBody());

            Employee employee = new Employee();
            employee.setName(requestParameter.getParameter("full_name"));
            employeeDao.insert(employee);
            String body = "Okay";
            String response = "HTTP/1.1 200 OK\r\n" +
                    "Connection: close\r\n" +
                    "Content-Length: " + body.length() + "\r\n" +
                    "\r\n" +
                    body;

            clientSocket.getOutputStream().write(response.getBytes());
        } else {
            if (requestPath.equals("/echo")) {
                handleEchoRequest(clientSocket, requestTarget, questionPos);

            } else if (requestPath.equals("/api/workers")) {
                handleGetWorkers(clientSocket);
            } else {
                handleFileRequest(clientSocket, requestPath);
            }

        }

    }

    private void handleFileRequest(Socket clientSocket, String requestPath) throws IOException {

        try (InputStream inputStream = getClass().getResourceAsStream(requestPath)) {
            if(inputStream == null){
                String body = requestPath + " does not exist";
                String response = "HTTP/1.1 404 Not found\r\n" +
                        "Content-Length: " + body.length() + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n" +
                        body;

                clientSocket.getOutputStream().write(response.getBytes());
                return;
            }
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            inputStream.transferTo(buffer);

            String contentType = "text/plain";
            if (requestPath.endsWith(".html")) {
                contentType = "text/html";
            }
            String response = "HTTP/1.1 200 OK\r\n" +
                    "Content-Length: " + buffer.toByteArray().length + "\r\n" +
                    "Connection: close\r\n" +
                    "Content-Type: " + contentType +
                    "\r\n" + "\r\n";

            clientSocket.getOutputStream().write(response.getBytes());
            clientSocket.getOutputStream().write(buffer.toByteArray());
        }
    }

    private void handleGetWorkers(Socket clientSocket) throws IOException, SQLException {
        String body = "<ul>";
        for (Employee employee : employeeDao.list()) {
            body += "<li>" + employee.getName() + "</li>";
        }
        body += "</ul>";
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "Content-Type: text/html\r\n" +
                "Connection: close\r\n" +
                "\r\n" +
                body;

        clientSocket.getOutputStream().write(response.getBytes());
    }

    private void handleEchoRequest(Socket clientSocket, String requestTarget, int questionPos) throws IOException {
        String statusCode = "200";
        String body = "Hello <strong>World</strong>!";
        if (questionPos != -1) {

            QueryString queryString = new QueryString(requestTarget.substring(questionPos + 1));
            if (queryString.getParameter("status") != null) {
                statusCode = queryString.getParameter("status");
            }

            if (queryString.getParameter("body") != null) {
                body = queryString.getParameter("body");
            }

        }
        String response = "HTTP/1.1 " + statusCode + " OK\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "Content-Type: text/plain\r\n" +
                "Connection: close\r\n" +
                "\r\n" +
                body;

        clientSocket.getOutputStream().write(response.getBytes());
    }


    public static void main (String[]args) throws IOException {
        Properties properties = new Properties();
        try (FileReader fileReader = new FileReader("pgr203.properties")) {
            properties.load(fileReader);
        }

// OBS! Se video ekstraforelesning, 50:25.

        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(properties.getProperty("dataSource.url"));
        dataSource.setUser(properties.getProperty("dataSource.username"));
        dataSource.setPassword(properties.getProperty("dataSource.password"));
        logger.info("Using database {}", dataSource.getUrl());
        Flyway.configure().dataSource(dataSource).load().migrate();

            HttpServer server = new HttpServer(8080, dataSource);
            logger.info("Started on http://localhost:{}/index.html", 8080);
        }

    public List<Employee> getWorker() throws SQLException {
        return employeeDao.list();
    }
}


/* KODE FRA SLIDES
        new Thread(() -> {
            try{
                Socket socket = serverSocket.accept();
                handleRequest(socket);
            } catch (IOException e){
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) throws IOException {
        new httpServer(8080);

    }

    private static void handleRequest(Socket socket) throws IOException {
        String responseLine = httpClient.readLine(socket);
        System.out.println(responseLine);
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html; charset=utf-8\r\n" +
                "Content-Length: 11\r\n" +
                "\r\n" +
                "Hello world";

        socket.getOutputStream().write(response.getBytes());
    }

    }

    public httpServer(int port) throws IOException {

}*/