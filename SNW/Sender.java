package CN.SNW;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat; // Import for Date Formatting
import java.util.Date;

public class Sender {

    // Helper function to get current timestamp
    private static String getTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        String host = "localhost";
        int port = 9999;

        System.out.println("[" + getTimestamp() + "] Sender starting...");
        Socket socket = new Socket(host, port);

        // Timeout for ARQ logic (Wait max 2 seconds for ACK)
        socket.setSoTimeout(2000);

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        String[] dataPackets = { "Hello", "Network", "Lab", "Exam", "Passed" };
        int sequence = 0;
        int i = 0;

        while (i < dataPackets.length) {
            String packet = dataPackets[i];
            boolean ackReceived = false;

            // --- DELAY ADDED HERE ---
            System.out.println("\n[" + getTimestamp() + "] ...Preparing to send next packet (Wait 2s)...");
            Thread.sleep(2000); // Pauses for 2 seconds
            // ------------------------

            while (!ackReceived) {
                try {
                    // 1. Send Frame
                    System.out.println(
                            "[" + getTimestamp() + "] Sending Frame [Seq: " + sequence + " | Data: " + packet + "]");
                    out.println(sequence + "|" + packet);

                    // 2. Wait for ACK
                    System.out.println("[" + getTimestamp() + "] Waiting for ACK...");
                    String ack = in.readLine();

                    // 3. Process ACK
                    if (ack != null && ack.equals("ACK" + sequence)) {
                        System.out.println("[" + getTimestamp() + "] --> ✅ ACK Received: " + ack);
                        ackReceived = true;
                        sequence = (sequence + 1) % 2;
                        i++;
                    }

                } catch (SocketTimeoutException e) {
                    System.out.println("[" + getTimestamp() + "] --> ⏰ TIMEOUT! (ACK not received). Retransmitting...");
                    // Delay before re-trying so you can read the error
                    Thread.sleep(1000);
                }
            }
        }

        System.out.println("\n[" + getTimestamp() + "] All data sent successfully.");
        socket.close();
    }
}