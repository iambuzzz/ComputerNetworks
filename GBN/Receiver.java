package CN.GBN;

import java.io.*;
import java.net.*;
import java.util.Random;
import java.text.SimpleDateFormat; // Date formatting
import java.util.Date;

public class Receiver {

    // Timestamp Helper
    private static String getTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 9999;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("[" + getTimestamp() + "] Receiver (Server) waiting on port " + port + "...");

        Socket socket = serverSocket.accept();
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        Random random = new Random();
        int expectedSeq = 0;

        while (true) {
            String frame = in.readLine();
            if (frame == null)
                break;

            // Frame format: "SeqNum|Data"
            String[] parts = frame.split("\\|");
            int receivedSeq = Integer.parseInt(parts[0]);
            String data = parts[1];

            System.out.println("\n[" + getTimestamp() + "] Received Frame: " + receivedSeq);
            Thread.sleep(500); // Small processing delay

            // 1. Simulate Packet Loss (20% chance)
            if (random.nextInt(10) < 2) {
                System.out.println(
                        "[" + getTimestamp() + "] --> ❌ [SIMULATION] Packet " + receivedSeq + " Lost in network!");
                continue; // Sender will eventually timeout
            }

            // 2. Logic: Is it the packet we expected?
            if (receivedSeq == expectedSeq) {
                System.out.println("[" + getTimestamp() + "] --> ✅ Packet Accepted: " + data);

                // Send ACK
                out.println(receivedSeq);
                System.out.println("[" + getTimestamp() + "] --> Sent ACK " + receivedSeq);

                expectedSeq++;
            } else {
                // Out of order packet! Discard it.
                System.out.println("[" + getTimestamp() + "] --> ⚠️ Out of Order! Expected " + expectedSeq + " but got "
                        + receivedSeq);
                System.out.println("[" + getTimestamp() + "] --> 🗑️ Discarding " + receivedSeq + " and resending ACK "
                        + (expectedSeq - 1));

                if (expectedSeq > 0) {
                    out.println(expectedSeq - 1);
                }
            }
        }
        socket.close();
        serverSocket.close();
    }
}