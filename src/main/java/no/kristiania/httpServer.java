package no.kristiania;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class httpServer {

    public httpServer(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);

        new Thread(() -> {
            while (true) {
                try {
                    handleRequest(serverSocket.accept());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        }

    private void handleRequest(Socket clientSocket) throws IOException {

        String requestLine = httpClient.readLine(clientSocket);
        System.out.println(requestLine);

        String requestTarget = requestLine.split(" ")[1];
        String statusCode = "200";

        int questionPos = requestTarget.indexOf('?');
        if (questionPos != -1) {
            String queryString = requestTarget.substring(questionPos+1);
            int equalPos = queryString.indexOf('=');
            String parameterName = queryString.substring(0, equalPos);
            String parameterValue = queryString.substring(equalPos+1);
            statusCode = parameterValue;

        }

            String response = "HTTP/1.1 " + statusCode + " OK\r\n" +
                    "Content-Length: 12\r\n" +
                    "Content-Type: text/plain\r\n" +
                    "\r\n" +
                    "Hello <strong>World</strong>!";

            clientSocket.getOutputStream().write(response.getBytes());
        }



    public static void main(String[] args) throws IOException{
        new httpServer(8080);
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