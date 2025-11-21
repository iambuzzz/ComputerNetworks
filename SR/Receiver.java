package CN.SR;

import java.io.*;
import java.net.*;
import java.util.Random;

public class Receiver {
    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 9999;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Selective Repeat Receiver waiting on port " + port + "...");

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
            Thread.sleep(1000);

            // 1. Simulate Packet Loss (20% chance)
            // Note: In SR, if we lose a packet, we just don't ACK it.
            if (random.nextInt(10) < 2) {
                System.out.println("\n--> [SIMULATION] Packet " + seqNum + " Lost/Corrupted! (No ACK sent)");
                continue;
            }

            // 2. Process Valid Packet
            // In SR, we accept ANY packet inside the window, even out of order.
            System.out.println("\nReceived Frame: " + seqNum + " (" + data + ")");

            // 3. Send Individual ACK
            out.println(seqNum); // Send "2" for Packet 2
            System.out.println("--> Sent ACK " + seqNum);
        }
        socket.close();
        serverSocket.close();
    }
}