package CN.GBN;

import java.io.*;
import java.net.*;
import java.util.Random;

public class Receiver {
    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 9999;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Receiver (Server) waiting on port " + port + "...");

        Socket socket = serverSocket.accept();
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        Random random = new Random();
        int expectedSeq = 0; // The packet we are waiting for

        while (true) {
            String frame = in.readLine();
            if (frame == null)
                break;

            // Frame format: "SeqNum|Data"
            String[] parts = frame.split("\\|");
            int receivedSeq = Integer.parseInt(parts[0]);
            String data = parts[1];

            System.out.println("\nReceived Frame: " + receivedSeq);
            Thread.sleep(500); // Small processing delay

            // 1. Simulate Packet Loss (20% chance)
            if (random.nextInt(10) < 2) {
                System.out.println("--> [SIMULATION] Packet " + receivedSeq + " Lost in network!");
                continue; // We don't send anything. Sender will eventually timeout.
            }

            // 2. Logic: Is it the packet we expected?
            if (receivedSeq == expectedSeq) {
                System.out.println("--> Packet Accepted: " + data);

                // Send ACK for this packet
                // Note: In GBN, ACK n usually means "I expect n+1",
                // but for this lab demo, "ACK n" means "I got n".
                out.println(receivedSeq);
                System.out.println("--> Sent ACK " + receivedSeq);

                expectedSeq++; // Move to next expected
            } else {
                // Out of order packet! Discard it and ACK the LAST valid packet.
                System.out.println("--> Out of Order! Expected " + expectedSeq + " but got " + receivedSeq);
                System.out.println("--> Discarding " + receivedSeq + " and resending ACK " + (expectedSeq - 1));

                // If we haven't received anything yet (expected=0), we can't ack -1.
                if (expectedSeq > 0) {
                    out.println(expectedSeq - 1);
                }
            }
        }
        socket.close();
        serverSocket.close();
    }
}