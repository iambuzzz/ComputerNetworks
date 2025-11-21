package CN.CongestionControl;

import java.util.Random;

public class TCPCongestionControl2 {
    static int cwnd = 1;
    static int ssthresh = 8;
    static String mode = "SLOW_START";
    static int totalData = 50;
    static int dataSent = 0;

    public static void main(String[] args) {
        Random rand = new Random();

        System.out.println("TCP Congestion Control Simulation");
        System.out.println("SSTHRESH = " + ssthresh + " segments\n");

        while (dataSent < totalData) {
            int segmentsToSend = Math.min(cwnd, totalData - dataSent);
            int endSegment = dataSent + segmentsToSend;

            System.out.println("=======================================");
            // 1. Header Line (Screenshot Style)
            System.out.println("State: " + mode + ", cwnd=" + cwnd + ", ssthresh=" + ssthresh);

            // 2. Transmission Line with Range
            System.out.print("Transmitting Segments " + (dataSent + 1) + " to " + endSegment + ": ");

            // --- PROBABILITY LOGIC ---
            // Total range 0-9.
            // 0,1,2,3,4,5,6,7 (60%) -> Success
            // 8 (10%) -> 3 Duplicate ACKs (Loss)
            // 9 (10%) -> Timeout
            int event = rand.nextInt(10);

            int lossIndex = -1;
            if (event >= 8) {
                // Agar loss ya timeout hai, to randomly kisi ek packet ko "Gira" do
                lossIndex = rand.nextInt(segmentsToSend);
            }

            // --- PACKET PRINTING LOOP ---
            for (int i = 0; i < segmentsToSend; i++) {
                if (i == lossIndex) {
                    // Agar ye wahi packet hai jo girna hai
                    if (event >= 9)
                        System.out.print("[TIMEOUT] ");
                    else
                        System.out.print("[LOSS] ");
                } else {
                    System.out.print((dataSent + 1 + i) + " ");
                }
            }
            System.out.println(); // New line

            // --- HANDLING THE OUTCOME ---
            if (event < 8) {
                // SUCCESS
                dataSent += segmentsToSend;
                handleNewAck();
                System.out.println("All ACKs received (cwnd now " + cwnd + ")");
            } else if (event < 9) {
                // 3 DUPLICATE ACKS (10% Chance)
                System.out.println("Duplicate ACKs received (Packet Loss detected)");
                handleTripleDuplicateAck();
                System.out.println(">>> FAST RETRANSMIT triggered (ssthresh=" + ssthresh + ", cwnd=" + cwnd + ")");
                // dataSent badhega nahi, loop wapas chalega wahi se
            } else {
                // TIMEOUT (10% Chance)
                System.out.println(">>> TIMEOUT OCCURRED (No ACKs)");
                handleTimeout();
                System.out.println(">>> Entering Slow Start (cwnd reset to 1)");
                // dataSent badhega nahi, loop wapas chalega wahi se
            }

            try {
                Thread.sleep(800);
            } catch (Exception e) {
            }
        }
        System.out.println("\n== All data sent. Simulation complete! ==");
    }

    // --- LOGIC METHODS ---

    public static void handleNewAck() {
        if (cwnd < ssthresh) {
            mode = "SLOW_START";
            cwnd *= 2; // Double
        } else {
            mode = "CONGESTION_AVOIDANCE";
            cwnd += 1; // Linear
        }
    }

    public static void handleTripleDuplicateAck() {
        // Reno Logic: Halve the threshold, set cwnd to new threshold
        ssthresh = Math.max(cwnd / 2, 2);
        cwnd = ssthresh;
        mode = "CONGESTION_AVOIDANCE";
    }

    public static void handleTimeout() {
        // Tahoe/Reno Logic: Halve threshold, CRASH cwnd to 1
        ssthresh = Math.max(cwnd / 2, 2);
        cwnd = 1;
        mode = "SLOW_START";
    }
}