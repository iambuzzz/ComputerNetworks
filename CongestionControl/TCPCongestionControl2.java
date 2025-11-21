package CN.CongestionControl;

import java.util.*;

public class TCPCongestionControl2 {
    public static void main(String[] args) {
        int cwnd = 1; // Congestion Window (in segments)
        int ssthresh = 8; // Slow Start Threshold (in segments)
        int totalData = 50; // Total segments to send
        int dataSent = 0;
        int ackNum = 0; // For duplicate ACKs
        int dupAckCount = 0;
        Random rand = new Random();

        String state = "SLOW_START"; // Start phase

        System.out.println("TCP Congestion Control Simulation");
        System.out.println("SSTHRESH = " + ssthresh + " segments\n");

        while (dataSent < totalData) {
            System.out.println("=======================================");
            System.out.println("State: " + state + ", cwnd=" + cwnd + ", ssthresh=" + ssthresh);

            // Simulate sending of cwnd segments
            int segmentsToSend = Math.min(cwnd, totalData - dataSent);
            boolean loss = false;
            System.out.print("Transmitting Segments " + (dataSent + 1) + " to " + (dataSent + segmentsToSend) + ": ");

            // For demo, randomly select one segment is lost if cwnd>4
            int dropIndex = -1;
            if (cwnd >= 4 && rand.nextInt(8) == 3) {
                dropIndex = rand.nextInt(segmentsToSend);
            }

            for (int i = 0; i < segmentsToSend; i++) {
                if (i == dropIndex) {
                    System.out.print("[LOSS] ");
                    loss = true;
                } else {
                    System.out.print((dataSent + 1 + i) + " ");
                }
            }
            System.out.println();

            if (!loss) {
                // All segments ACKed
                dupAckCount = 0;
                if (state.equals("SLOW_START")) {
                    cwnd *= 2;
                    if (cwnd >= ssthresh) {
                        state = "CONGESTION_AVOIDANCE";
                    }
                } else if (state.equals("CONGESTION_AVOIDANCE")) {
                    cwnd += 1;
                } else if (state.equals("FAST_RECOVERY")) {
                    cwnd = ssthresh;
                    state = "CONGESTION_AVOIDANCE";
                }
                dataSent += segmentsToSend;
                System.out.println("All ACKs received (cwnd now " + cwnd + ")\n");
            } else {
                // Simulated loss: fast retransmit if 3 duplicate ACKs, else timeout/slow start
                System.out.println("Duplicate ACKs are received...");
                dupAckCount += 3; // Simulate 3 dup ACKs

                if (dupAckCount >= 3) {
                    // Fast Retransmit + Fast Recovery
                    System.out.println(">>> FAST RETRANSMIT (3 duplicate ACKs)");
                    ssthresh = Math.max(cwnd / 2, 1);
                    cwnd = ssthresh + 3; // Inflate window
                    state = "FAST_RECOVERY";
                    System.out.println("ssthresh set to " + ssthresh + ", cwnd inflated to " + cwnd);

                    // "Recover" - one ACK (partial ack)
                    cwnd = ssthresh; // Deflate after recovery
                    state = "CONGESTION_AVOIDANCE";
                    dupAckCount = 0;
                    System.out.println(">>> FAST RECOVERY completes (cwnd back to ssthresh=" + cwnd + ")");
                } else {
                    // Timeout (no ACKs) - go to Slow Start
                    System.out.println(">>> TIMEOUT: Enter slow start");
                    ssthresh = Math.max(cwnd / 2, 1);
                    cwnd = 1;
                    state = "SLOW_START";
                    dupAckCount = 0;
                }
                // Simulate loss recovery (don't move window)
            }

            // Prevent runaway
            if (cwnd < 1)
                cwnd = 1;
        }
        System.out.println("\n== All data sent. Congestion control demo complete! ==");
    }
}
