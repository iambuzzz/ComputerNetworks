package CN.SNW;

import java.io.*;
import java.net.*;
import java.util.Random;
import java.text.SimpleDateFormat; // Import for Date Formatting
import java.util.Date;

public class Receiver {

    // Helper function to get current timestamp
    private static String getTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
    }

    public static void main(String[] args) throws IOException {
        int port = 9999;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("[" + getTimestamp() + "] Receiver waiting on port " + port + "...");

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

            System.out.println("\n[" + getTimestamp() + "] Received Frame [Seq: " + seqNum + ", Data: " + data + "]");

            // --- LOGIC START ---

            // Case 1: Correct Frame Received
            if (seqNum == expectedSequence) {
                System.out.println("[" + getTimestamp() + "] --> Data Processed: " + data);

                // Update expected sequence (0->1 or 1->0)
                expectedSequence = (expectedSequence + 1) % 2;

                // *** SIMULATE ACK LOSS ***
                // 20% chance that we process the data, but the ACK gets "Lost"
                if (random.nextInt(10) < 2) {
                    System.out.println("[" + getTimestamp() + "] --> ❌ [SIMULATION] ACK Lost! (Sender will timeout)");
                    // We do NOT send the ACK line. We just loop back.
                    continue;
                }

                // Normal case: Send ACK
                out.println("ACK" + seqNum);
                System.out.println("[" + getTimestamp() + "] --> ACK" + seqNum + " sent.");

            }
            // Case 2: Duplicate Frame (Because previous ACK was lost)
            else {
                System.out.println("[" + getTimestamp() + "] --> ⚠️ Duplicate Frame detected (Seq " + seqNum
                        + "). Discarding data.");
                // We MUST resend the ACK for this sequence so the sender stops waiting
                out.println("ACK" + seqNum);
                System.out.println("[" + getTimestamp() + "] --> Re-sent ACK" + seqNum);
            }
            // --- LOGIC END ---
        }
        socket.close();
        serverSocket.close();
    }
}