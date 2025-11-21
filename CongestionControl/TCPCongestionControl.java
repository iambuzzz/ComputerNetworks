package CN.CongestionControl;

import java.util.Scanner;

public class TCPCongestionControl {

    static int cwnd = 1; // Congestion Window (in MSS)
    static int ssthresh = 16; // Slow Start Threshold (initially high)
    static int dupAcks = 0; // Counter for Duplicate ACKs
    static String mode = "SLOW START"; // Current State

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("--- TCP CONGESTION CONTROL SIMULATION ---");
        System.out.println("1. Slow Start (Exponential Growth)");
        System.out.println("2. Congestion Avoidance (Linear Growth)");
        System.out.println("3. Fast Retransmit/Recovery (3 Dup ACKs)");
        System.out.println("4. Timeout (Severe Congestion)");
        System.out.println("-----------------------------------------");

        while (true) {
            // 1. Display Current State
            printState();

            // 2. Get User Event
            System.out.println("\nSimulate Event:");
            System.out.println("[a] ACK Received (Successful Transmission)");
            System.out.println("[d] 3 Duplicate ACKs (Packet Loss - Light)");
            System.out.println("[t] Timeout (Packet Loss - Severe)");
            System.out.println("[q] Quit");
            System.out.print("Enter Choice: ");

            char choice = scanner.next().charAt(0);

            switch (choice) {
                case 'a':
                    handleNewAck();
                    break;
                case 'd':
                    handleTripleDuplicateAck();
                    break;
                case 't':
                    handleTimeout();
                    break;
                case 'q':
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid Input.");
            }
        }
    }

    // --- LOGIC: ACK RECEIVED ---
    public static void handleNewAck() {
        dupAcks = 0; // Reset duplicate counter on new ACK

        if (cwnd < ssthresh) {
            // MODE: SLOW START
            // Concept: CWND doubles every RTT.
            // In simulation, we assume this "ACK" represents a full RTT of ACKs.
            mode = "SLOW START";
            cwnd = cwnd * 2;
            System.out.println(">> Event: ACK Received. Doubling Window (Exponential).");
        } else {
            // MODE: CONGESTION AVOIDANCE
            // Concept: CWND increases by 1 MSS per RTT.
            mode = "CONGESTION AVOIDANCE";
            cwnd = cwnd + 1;
            System.out.println(">> Event: ACK Received. Increasing Window by 1 (Linear).");
        }
    }

    // --- LOGIC: 3 DUPLICATE ACKS (Fast Retransmit + Fast Recovery) ---
    public static void handleTripleDuplicateAck() {
        System.out.println(">> Event: 3 Duplicate ACKs detected!");
        System.out.println(">> Action: Fast Retransmit triggered.");

        // 1. Set Threshold to half of current CWND
        ssthresh = Math.max(cwnd / 2, 2); // Minimum 2

        // 2. FAST RECOVERY:
        // Instead of dropping CWND to 1 (like Timeout),
        // we drop it to the new ssthresh (half).
        cwnd = ssthresh;

        mode = "FAST RECOVERY -> CONGESTION AVOIDANCE";

        System.out.println(">> Mode switched to Congestion Avoidance directly.");
    }

    // --- LOGIC: TIMEOUT (Severe Congestion) ---
    public static void handleTimeout() {
        System.out.println(">> Event: TIMEOUT Occurred!");
        System.out.println(">> Action: Severe Congestion assumed.");

        // 1. Set Threshold to half of current CWND
        ssthresh = Math.max(cwnd / 2, 2);

        // 2. Hard Reset CWND to 1
        cwnd = 1;

        // 3. Go back to Slow Start
        mode = "SLOW START";

        System.out.println(">> Restarting with CWND = 1.");
    }

    // --- VISUALIZATION ---
    public static void printState() {
        System.out.println("\n=========================================");
        System.out.println(" CURRENT STATE: " + mode);
        System.out.println("-----------------------------------------");
        System.out.println(" CWND (Window Size) : " + cwnd);
        System.out.println(" SSTHRESH (Threshold): " + ssthresh);

        // Visual Bar Graph
        System.out.print(" Visual: [");
        for (int i = 0; i < cwnd; i++)
            System.out.print("#");
        System.out.println("]");
        System.out.println("=========================================");
    }
}