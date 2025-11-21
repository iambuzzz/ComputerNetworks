package CN.LAN.CS;

import java.net.*;
import java.io.*;

public class LANServer {
    public static void main(String[] args) throws Exception {
        try (ServerSocket server = new ServerSocket(5000)) {
            System.out.println("LAN Server started. Waiting for hosts to connect...");
            while (true) {
                Socket client = server.accept();
                System.out.println("Host connected: " + client.getInetAddress());
                PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                out.println("Welcome to the LAN!");
                client.close();
            }
        }
    }
}
