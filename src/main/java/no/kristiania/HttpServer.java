package no.kristiania;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class HttpServer {

    private File contentRoot;
    private List<String> workerNames = new ArrayList<>();

    public HttpServer(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);

        new Thread(() -> {
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    handleRequest(clientSocket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void handleRequest(Socket clientSocket) throws IOException {
        HttpMessage request = new HttpMessage(clientSocket);
        String requestLine = request.getStartLine();
        System.out.println("REQUEST" + requestLine);

        String requestMethod = requestLine.split(" ")[0];

        String requestTarget = requestLine.split(" ")[1];


        int questionPos = requestTarget.indexOf('?');

        String requestPath = (questionPos != -1) ? requestTarget.substring(0, questionPos) : requestTarget;

        if(requestMethod.equals("POST")){
            QueryString requestParameter = new QueryString(request.getBody());

            workerNames.add(requestParameter.getParameter("full_name"));
            String body = "Okay";
            String response = "HTTP/1.1 200 OK\r\n" +
                    "Content-Length: " + body.length() + "\r\n" +
                    "Connection: close\r\n" +
                    "\r\n" +
                    body;

            clientSocket.getOutputStream().write(response.getBytes());
        } else {
            if (requestPath.equals("/echo")) {
                handleEchoRequest(clientSocket, requestTarget, questionPos);

            } else if (requestPath.equals("/api/workers")) {
                handleGetWorkers(clientSocket);
            } else {
                File file = new File(contentRoot, requestPath);
                if (!file.exists()) {
                    String body = file + " does not exist";
                    String response = "HTTP/1.1 404 Not found\r\n" +
                            "Content-Length: " + body.length() + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n" +
                            body;

                    clientSocket.getOutputStream().write(response.getBytes());
                    return;
                }
                String statusCode = "200";
                String contentType = "text/plain";
                if (file.getName().endsWith(".html")) {
                    contentType = "text/html";
                }

                String response = "HTTP/1.1 " + statusCode + " OK\r\n" +
                        "Content-Length: " + file.length() + "\r\n" +
                        "Connection: close\r\n" +
                        "Content-Type: " + contentType +
                        "\r\n" + "\r\n";

                clientSocket.getOutputStream().write(response.getBytes());


                new FileInputStream(file).transferTo(clientSocket.getOutputStream());
            }

        }

    }

    private void handleGetWorkers(Socket clientSocket) throws IOException {
        String body = "<ul>";
        for (String workerName : workerNames) {
            body += "<li>" + workerName + "</li>";
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
            HttpServer server = new HttpServer(8080);
            server.setContentRoot(new File("src/main/resources"));
        }

    public void setContentRoot(File contentRoot) {
        this.contentRoot = contentRoot;
    }

    List<String> getWorkerNames() {
        return workerNames;
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