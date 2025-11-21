package CN.GBN;

import java.io.*;
import java.net.*;

public class Sender {
    public static void main(String[] args) throws IOException, InterruptedException { // InterruptedException add kiya
        String host = "localhost";
        int port = 9999;

        Socket socket = new Socket(host, port);
        socket.setSoTimeout(3000); // Timer ko 3 sec kar diya taaki aapko padhne ka time mile

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        // Data packets
        String[] packets = { "Packet0", "Packet1", "Packet2", "Packet3", "Packet4", "Packet5", "Packet6", "Packet7",
                "Packet8" };

        int windowSize = 4; // Window size 4 rakhi hai
        int base = 0;
        int nextSeqNum = 0;

        System.out.println("--- Go-Back-N Sender Started ---");
        System.out.println("Window Size: " + windowSize);

        while (base < packets.length) {

            // 1. SENDING LOOP: Window full hone tak packets bhejo
            while (nextSeqNum < base + windowSize && nextSeqNum < packets.length) {
                System.out.println("\nSending Frame: " + nextSeqNum + " (" + packets[nextSeqNum] + ")");
                out.println(nextSeqNum + "|" + packets[nextSeqNum]);
                nextSeqNum++;

                // --- DELAY 1: Har packet ke beech mein ---
                System.out.println("... (waiting 1.5s) ...");
                Thread.sleep(1500);
            }

            // 2. WAITING LOOP: ACKs ka wait karo
            try {
                // Hum yahan wait kar rahe hain. Agar ACK nahi aaya toh Timeout hoga.
                String ackStr = in.readLine();

                if (ackStr != null) {
                    int ack = Integer.parseInt(ackStr);
                    System.out.println("--> ACK Received for Seq: " + ack);

                    // Cumulative ACK logic
                    if (ack >= base) {
                        base = ack + 1;
                        System.out.println(
                                "--> Window Slides! New Base: " + base + " (Next packet to send: " + nextSeqNum + ")");
                    }
                }

            } catch (SocketTimeoutException e) {
                // 3. TIMEOUT LOGIC (GO-BACK-N)
                System.out.println("\n------------------------------------------------");
                System.out.println("!!! TIMEOUT !!! ACK nahi mila.");
                System.out.println("!!! Go-Back-N Triggered: Resending from Seq " + base + " !!!");
                System.out.println("------------------------------------------------\n");

                // Next sequence ko wapas base par set karo (Resetting Window)
                nextSeqNum = base;

                // --- DELAY 2: Error message padhne ke liye ---
                Thread.sleep(3000);
            }
        }

        System.out.println("\nAll data sent successfully.");
        socket.close();
    }
}