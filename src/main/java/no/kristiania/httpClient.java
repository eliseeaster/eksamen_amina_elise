package no.kristiania;

import java.io.IOException;
import java.net.Socket;

public class httpClient {

    private int statusCode;

    public httpClient(String requestTarget, String hostName, int port) throws IOException {

        Socket socket = new Socket("urlecho.appspot.com", 80);

        String request = "GET " + requestTarget + " HTTP/1.1\r\n" +
                "Host:" + hostName + "\r\n\r\n";

        socket.getOutputStream().write(request.getBytes());

        int c;
        while ((c = socket.getInputStream().read()) != -1) {
            System.out.print((char) c);
        }
    }

    public static void main(String[] args) throws IOException {

            new httpClient("/echo?status=200&body=Hello%20world!", "urlecho.appspot.com", 80);
        }

    public int getStatusCode() {
        return statusCode;
    }
}
