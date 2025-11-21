package CN.SNW;

import java.io.*;
import java.net.*;
import java.util.Random;

public class Receiver {
    public static void main(String[] args) throws IOException {
        int port = 9999;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Receiver waiting on port " + port + "...");

        Socket socket = serverSocket.accept();
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        Random random = new Random();
        int expectedSequence = 0;

        while (true) {
            String receivedFrame = in.readLine();
            if (receivedFrame == null)
                break;

            String[] parts = receivedFrame.split("\\|");
            int seqNum = Integer.parseInt(parts[0]);
            String data = parts[1];

            System.out.println("\nReceived Frame [Seq: " + seqNum + ", Data: " + data + "]");

            // --- LOGIC START ---

            // Case 1: Correct Frame Received
            if (seqNum == expectedSequence) {
                System.out.println("--> Data Processed: " + data);

                // Update expected sequence (0->1 or 1->0)
                expectedSequence = (expectedSequence + 1) % 2;

                // *** SIMULATE ACK LOSS ***
                // 20% chance that we process the data, but the ACK gets "Lost"
                if (random.nextInt(10) < 2) {
                    System.out.println("--> [SIMULATION] ACK Lost! (Sender will timeout)");
                    // We do NOT send the ACK line. We just loop back.
                    continue;
                }

                // Normal case: Send ACK
                out.println("ACK" + seqNum);
                System.out.println("--> ACK" + seqNum + " sent.");

            }
            // Case 2: Duplicate Frame (Because previous ACK was lost)
            else {
                System.out.println("--> Duplicate Frame detected (Seq " + seqNum + "). Discarding data.");
                // We MUST resend the ACK for this sequence so the sender stops waiting
                out.println("ACK" + seqNum);
                System.out.println("--> Re-sent ACK" + seqNum);
            }
            // --- LOGIC END ---
        }
        socket.close();
        serverSocket.close();
    }
}