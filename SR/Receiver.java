package CN.SR;

import java.io.*;
import java.net.*;
import java.util.Random;
import java.text.SimpleDateFormat; // For Date Formatting
import java.util.Date;

public class Receiver {

    // Helper to generate timestamp string
    private static String getTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 9999;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("[" + getTimestamp() + "] Selective Repeat Receiver waiting on port " + port + "...");

        Socket socket = serverSocket.accept();
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        Random random = new Random();

        while (true) {
            String frame = in.readLine();
            if (frame == null)
                break;

            // Parse "SeqNum|Data"
            String[] parts = frame.split("\\|");
            int seqNum = Integer.parseInt(parts[0]);
            String data = parts[1];

            // --- DELAY FOR VISUALIZATION ---
            Thread.sleep(800); // Simulating network delay

            // 1. Simulate Packet Loss (20% chance)
            // In SR, loss implies we do NOT send an ACK for this specific packet.
            if (random.nextInt(10) < 2) {
                System.out.println("\n[" + getTimestamp() + "] --> ❌ [SIMULATION] Packet " + seqNum
                        + " Lost/Corrupted! (No ACK sent)");
                continue; // Skip sending ACK
            }

            // 2. Process Valid Packet
            System.out.println("\n[" + getTimestamp() + "] Received Frame: " + seqNum + " (" + data + ")");

            // 3. Send Individual ACK
            out.println(seqNum);
            System.out.println("[" + getTimestamp() + "] --> Sent ACK " + seqNum);
        }
        socket.close();
        serverSocket.close();
    }
}