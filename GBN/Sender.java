package CN.GBN;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat; // Date formatting
import java.util.Date;

public class Sender {

    // Timestamp Helper
    private static String getTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        String host = "localhost";
        int port = 9999;

        Socket socket = new Socket(host, port);
        socket.setSoTimeout(3000); // 3 Seconds Timeout

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        // Data packets
        String[] packets = { "Packet0", "Packet1", "Packet2", "Packet3", "Packet4", "Packet5", "Packet6", "Packet7",
                "Packet8" };

        int windowSize = 4;
        int base = 0;
        int nextSeqNum = 0;

        System.out.println("[" + getTimestamp() + "] --- Go-Back-N Sender Started ---");
        System.out.println("[" + getTimestamp() + "] Window Size: " + windowSize);

        while (base < packets.length) {

            // 1. SENDING LOOP: Fill the window
            while (nextSeqNum < base + windowSize && nextSeqNum < packets.length) {
                System.out.println(
                        "\n[" + getTimestamp() + "] Sending Frame: " + nextSeqNum + " (" + packets[nextSeqNum] + ")");
                out.println(nextSeqNum + "|" + packets[nextSeqNum]);
                nextSeqNum++;

                // --- DELAY: Visual gap between packets ---
                System.out.println("[" + getTimestamp() + "] ... (waiting 1.5s) ...");
                Thread.sleep(1500);
            }

            // 2. WAITING LOOP: Wait for ACKs
            try {
                String ackStr = in.readLine();

                if (ackStr != null) {
                    int ack = Integer.parseInt(ackStr);
                    System.out.println("[" + getTimestamp() + "] --> ✅ ACK Received for Seq: " + ack);

                    // Cumulative ACK logic
                    if (ack >= base) {
                        base = ack + 1;
                        System.out.println("[" + getTimestamp() + "] --> ⏩ Window Slides! New Base: " + base
                                + " (Next packet to send: " + nextSeqNum + ")");
                    }
                }

            } catch (SocketTimeoutException e) {
                // 3. TIMEOUT LOGIC (GO-BACK-N)
                System.out.println("\n------------------------------------------------");
                System.out.println("[" + getTimestamp() + "] !!! ⏰ TIMEOUT !!! ACK not received.");
                System.out.println(
                        "[" + getTimestamp() + "] !!! Go-Back-N Triggered: Resending from Seq " + base + " !!!");
                System.out.println("------------------------------------------------\n");

                // Reset nextSeqNum to base (Re-send everything in window)
                nextSeqNum = base;

                // --- DELAY: Pause to read error ---
                Thread.sleep(3000);
            }
        }

        System.out.println("\n[" + getTimestamp() + "] All data sent successfully.");
        socket.close();
    }
}