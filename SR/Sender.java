package CN.SR;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat; // For Date Formatting
import java.util.Date;

public class Sender {

    // Helper to generate timestamp string
    private static String getTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        String host = "localhost";
        int port = 9999;

        Socket socket = new Socket(host, port);
        // Timer set to 2 seconds (Timeout duration)
        socket.setSoTimeout(2000);

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        String[] packets = { "Packet0", "Packet1", "Packet2", "Packet3", "Packet4", "Packet5" };

        int windowSize = 3;
        int base = 0;
        int nextSeqNum = 0;

        // To track which packets have been ACKed (True = ACKed, False = Pending)
        boolean[] ackReceived = new boolean[packets.length];

        System.out.println("[" + getTimestamp() + "] --- Selective Repeat Sender Started ---");

        while (base < packets.length) {

            // 1. SEND NEW PACKETS (if window allows)
            while (nextSeqNum < base + windowSize && nextSeqNum < packets.length) {
                // Only send if not already ACKed (safe check)
                if (!ackReceived[nextSeqNum]) {
                    System.out.println("[" + getTimestamp() + "] Sending New Frame: " + nextSeqNum + " ("
                            + packets[nextSeqNum] + ")");
                    out.println(nextSeqNum + "|" + packets[nextSeqNum]);
                    Thread.sleep(500); // Small delay to separate logs visually
                }
                nextSeqNum++;
            }

            // 2. WAIT FOR ACKS
            try {
                String ackStr = in.readLine();
                if (ackStr != null) {
                    int ack = Integer.parseInt(ackStr);
                    System.out.println("[" + getTimestamp() + "] --> ✅ Received ACK for: " + ack);

                    // Mark this specific packet as ACKed
                    if (ack < packets.length) {
                        ackReceived[ack] = true;
                    }

                    // Slide window ONLY if the base is ACKed
                    while (base < packets.length && ackReceived[base]) {
                        System.out.println("[" + getTimestamp() + "] --> ⏩ Window Slide! Old Base: " + base
                                + ", New Base: " + (base + 1));
                        base++; // Move base forward
                    }
                }

            } catch (SocketTimeoutException e) {
                // 3. SELECTIVE RETRANSMIT LOGIC
                System.out.println("\n[" + getTimestamp() + "] !!! ⏰ TIMEOUT !!! ACK missing for Base Frame " + base);
                System.out.println(
                        "[" + getTimestamp() + "] !!! Selective Repeat: Resending ONLY Packet " + base + " !!!");

                // Resend only the base packet (the one causing the stall)
                if (base < packets.length) {
                    out.println(base + "|" + packets[base]);
                }

                // Pause to visualize the penalty
                Thread.sleep(1000);
            }
        }

        System.out.println("\n[" + getTimestamp() + "] All data sent and acknowledged.");
        socket.close();
    }
}