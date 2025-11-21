package CN.SR;

import java.io.*;
import java.net.*;

public class Sender {
    public static void main(String[] args) throws IOException, InterruptedException {
        String host = "localhost";
        int port = 9999;

        Socket socket = new Socket(host, port);
        // Timer set to 2 seconds
        socket.setSoTimeout(2000);

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        String[] packets = { "Packet0", "Packet1", "Packet2", "Packet3", "Packet4", "Packet5" };

        int windowSize = 3;
        int base = 0;
        int nextSeqNum = 0;

        // To track which packets have been ACKed (True = ACKed, False = Pending)
        boolean[] ackReceived = new boolean[packets.length];

        System.out.println("--- Selective Repeat Sender Started ---");

        while (base < packets.length) {

            // 1. SEND NEW PACKETS (if window allows)
            // Only send if we haven't sent it before (checked via nextSeqNum)
            while (nextSeqNum < base + windowSize && nextSeqNum < packets.length) {
                // Check if we already have an ACK (could happen in retransmission scenarios
                // logic, but safely ignored here)
                if (!ackReceived[nextSeqNum]) {
                    System.out.println("\nSending New Frame: " + nextSeqNum + " (" + packets[nextSeqNum] + ")");
                    out.println(nextSeqNum + "|" + packets[nextSeqNum]);
                    Thread.sleep(1000); // Delay for visualization
                }
                nextSeqNum++;
            }

            // 2. WAIT FOR ACKS
            try {
                String ackStr = in.readLine();
                if (ackStr != null) {
                    int ack = Integer.parseInt(ackStr);
                    System.out.println("--> Received ACK for: " + ack);

                    // Mark this specific packet as ACKed
                    if (ack < packets.length) {
                        ackReceived[ack] = true;
                    }

                    // Slide window ONLY if the base is ACKed
                    while (base < packets.length && ackReceived[base]) {
                        base++; // Move base forward to the next un-acked packet
                        System.out.println("--> Window Slide! New Base: " + base);
                    }
                }

            } catch (SocketTimeoutException e) {
                // 3. SELECTIVE RETRANSMIT LOGIC
                // Instead of resending ALL, we find the ONE that is missing (base)

                System.out.println("\n!!! TIMEOUT !!! ACK missing for " + base);
                System.out.println("!!! Selective Repeat: Resending ONLY Packet " + base + " !!!");

                // Resend only the base packet (the oldest un-acked one)
                // Note: Real SR maintains timers for every packet, but this simulates the
                // behavior sufficiently.
                if (base < packets.length) {
                    out.println(base + "|" + packets[base]);
                }

                Thread.sleep(2000); // Pause so you can see the retransmission
            }
        }

        System.out.println("\nAll data sent and acknowledged.");
        socket.close();
    }
}