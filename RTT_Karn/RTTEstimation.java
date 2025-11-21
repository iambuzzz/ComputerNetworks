package CN.RTT_Karn;

import java.util.*;

public class RTTEstimation {

    static class TCPConnection {
        double estimatedRTT = 0;
        double devRTT = 0;
        double timeoutInterval = 1000; // Initial RTO
        boolean firstSample = true;

        // Karn/Partridge: ignore retransmitted segments
        boolean isRetransmission = false;

        // RFC 6298 constants
        final double ALPHA = 0.125; // Weight for EWMA
        final double BETA = 0.25; // Weight for deviation
        final int K = 4; // Safety factor

        void updateRTT(double sampleRTT) {
            // Karn/Partridge Algorithm: Skip RTT updates for retransmissions
            if (isRetransmission) {
                System.out.println("  [KARN/PARTRIDGE] Ignoring RTT sample (retransmission)");
                return;
            }

            System.out.println("\n--- RTT Update ---");
            System.out.println("Sample RTT: " + sampleRTT + " ms");

            if (firstSample) {
                // First measurement
                estimatedRTT = sampleRTT;
                devRTT = sampleRTT / 2;
                firstSample = false;
            } else {
                // Exponential Weighted Moving Average (EWMA)
                devRTT = (1 - BETA) * devRTT + BETA * Math.abs(sampleRTT - estimatedRTT);
                estimatedRTT = (1 - ALPHA) * estimatedRTT + ALPHA * sampleRTT;
            }

            // Calculate RTO
            timeoutInterval = estimatedRTT + K * devRTT;

            System.out.println("Estimated RTT: " + String.format("%.2f", estimatedRTT) + " ms");
            System.out.println("Dev RTT: " + String.format("%.2f", devRTT) + " ms");
            System.out.println("RTO (Timeout): " + String.format("%.2f", timeoutInterval) + " ms");
        }

        void sendPacket(int seqNum, boolean retrans) {
            isRetransmission = retrans;
            if (retrans) {
                System.out.println("\n[SENDER] 🔄 Retransmitting packet " + seqNum);
            } else {
                System.out.println("\n[SENDER] 📤 Sending packet " + seqNum);
            }
        }

        void receiveAck(int ackNum, double rtt) {
            System.out.println("[RECEIVER] ✅ ACK " + ackNum + " received (RTT: " + rtt + " ms)");
            updateRTT(rtt);
        }
    }

    public static void main(String[] args) {
        System.out.println("===== RTT/RTO ESTIMATION (Karn/Partridge) =====\n");

        TCPConnection conn = new TCPConnection();
        Random rand = new Random();

        // Simulate packet transmissions
        conn.sendPacket(1, false);
        conn.receiveAck(1, 100 + rand.nextInt(20));

        conn.sendPacket(2, false);
        conn.receiveAck(2, 120 + rand.nextInt(20));

        conn.sendPacket(3, false);
        conn.receiveAck(3, 90 + rand.nextInt(20));

        // Simulate timeout and retransmission
        System.out.println("\n⏱️  TIMEOUT! Packet 4 lost...");
        conn.sendPacket(4, true); // Retransmission
        conn.receiveAck(4, 150 + rand.nextInt(20)); // RTT ignored due to Karn/Partridge

        conn.sendPacket(5, false);
        conn.receiveAck(5, 110 + rand.nextInt(20));

        System.out.println("\n✅ Karn/Partridge prevents false RTT measurements from retransmissions!");
    }
}
