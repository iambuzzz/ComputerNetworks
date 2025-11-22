package CN.FlowControl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class NetworkLogsARQ {

    // Global Simulation Clock (Starts at current system time)
    static long virtualTime = System.currentTimeMillis();
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    static Random rand = new Random();

    // Constants for Simulation Delays (in milliseconds)
    static final int PROPAGATION_DELAY = 50; // Time to travel one way
    static final int PROCESSING_DELAY = 10; // Receiver thinks before ACK
    static final int TIMEOUT_DURATION = 300; // Time before sender gives up

    // Helper to print logs with timestamps
    public static void log(String actor, String message) {
        System.out.println(String.format("[%s] %-10s : %s", sdf.format(new Date(virtualTime)), actor, message));
    }

    // -------------------------------------------------------
    // 1. STOP AND WAIT (SNW)
    // -------------------------------------------------------
    public static void stopAndWait(int totalFrames) {
        System.out.println("\n========== STOP AND WAIT PROTOCOL LOGS ==========");
        int currentFrame = 0;

        while (currentFrame < totalFrames) {
            // STEP 1: Sender Sends
            log("SENDER", "Transmitting Frame " + currentFrame);
            long sendTime = virtualTime;

            // Network Decision: Loss or Success?
            boolean packetLost = rand.nextDouble() < 0.3; // 30% loss chance

            if (packetLost) {
                // Packet travel time passed... but it got lost
                virtualTime += PROPAGATION_DELAY;
                log("NETWORK", ">>> X Frame " + currentFrame + " LOST in transit!");

                // Wait for Timeout
                long timeUntilTimeout = TIMEOUT_DURATION - PROPAGATION_DELAY;
                virtualTime += timeUntilTimeout;

                log("SENDER", "!!! Timeout expired. No ACK for Frame " + currentFrame);
                log("SENDER", "Re-initiating transmission...");
                // Loop repeats for same frame
            } else {
                // STEP 2: Receiver Gets it
                virtualTime += PROPAGATION_DELAY;
                log("RECEIVER", "Received Frame " + currentFrame);

                // STEP 3: Receiver Sends ACK
                virtualTime += PROCESSING_DELAY;
                log("RECEIVER", "Sending ACK for Frame " + currentFrame);

                // ACK travels back
                boolean ackLost = rand.nextDouble() < 0.1; // 10% ACK loss chance

                if (ackLost) {
                    virtualTime += PROPAGATION_DELAY;
                    log("NETWORK", ">>> X ACK for Frame " + currentFrame + " LOST in transit!");
                    virtualTime += (TIMEOUT_DURATION - (PROPAGATION_DELAY * 2) - PROCESSING_DELAY);
                    log("SENDER", "!!! Timeout expired. Re-sending Frame " + currentFrame);
                } else {
                    // STEP 4: Sender Gets ACK
                    virtualTime += PROPAGATION_DELAY;
                    log("SENDER", "ACK Received for Frame " + currentFrame + ". Sequence Complete.");
                    currentFrame++;
                    virtualTime += 100; // Small pause before next frame
                }
            }
        }
    }

    // -------------------------------------------------------
    // 2. GO-BACK-N (GBN)
    // -------------------------------------------------------
    public static void goBackN(int totalFrames, int windowSize) {
        System.out.println("\n========== GO-BACK-N PROTOCOL LOGS ==========");
        int base = 0;

        while (base < totalFrames) {
            int end = Math.min(base + windowSize, totalFrames);
            int lossIndex = -1; // Tracks first packet lost in this batch

            log("INFO", "--- Sending Window [" + base + " to " + (end - 1) + "] ---");

            // BATCH SENDING
            for (int i = base; i < end; i++) {
                log("SENDER", "Transmitting Frame " + i);
                virtualTime += 20; // Burst gap

                if (lossIndex == -1 && rand.nextDouble() < 0.3) {
                    lossIndex = i; // This one is lost
                }
            }

            virtualTime += PROPAGATION_DELAY; // Time for batch to reach receiver

            // RECEIVING LOGIC
            if (lossIndex != -1) {
                // Print successful ones before the loss
                for (int i = base; i < lossIndex; i++) {
                    log("RECEIVER", "Received Frame " + i + ". Sending ACK " + (i + 1));
                }
                // Print the loss moment
                log("NETWORK", ">>> X Frame " + lossIndex + " LOST! Connection broken.");

                // Print discard logs for subsequent packets
                for (int i = lossIndex + 1; i < end; i++) {
                    log("RECEIVER", "Received Frame " + i + " -> DISCARDED (Out of Order). Expected: " + lossIndex);
                }

                virtualTime += TIMEOUT_DURATION;
                log("SENDER", "!!! Timeout on Frame " + lossIndex + ". Rolling back window to " + lossIndex);
                base = lossIndex; // Go Back N
            } else {
                // All good
                for (int i = base; i < end; i++) {
                    log("RECEIVER", "Received Frame " + i + ". Sending ACK " + (i + 1));
                    virtualTime += 5; // Processing
                }
                virtualTime += PROPAGATION_DELAY; // ACKs travel back
                log("SENDER", "Received Cumulative ACKs. Moving Window Forward.");
                base = end;
            }
            virtualTime += 200; // Gap between windows
        }
    }

    // -------------------------------------------------------
    // 3. SELECTIVE REPEAT (SR)
    // -------------------------------------------------------
    public static void selectiveRepeat(int totalFrames, int windowSize) {
        System.out.println("\n========== SELECTIVE REPEAT PROTOCOL LOGS ==========");
        boolean[] ackReceived = new boolean[totalFrames];
        int base = 0;

        while (base < totalFrames) {
            int end = Math.min(base + windowSize, totalFrames);

            // Sending Loop
            for (int i = base; i < end; i++) {
                if (!ackReceived[i]) {
                    log("SENDER", "Transmitting Frame " + i);
                    boolean isLost = rand.nextDouble() < 0.3;

                    // Calculate theoretical arrival time
                    long arrivalTime = virtualTime + PROPAGATION_DELAY;

                    if (isLost) {
                        log("NETWORK",
                                ">>> X Frame " + i + " sent at " + sdf.format(new Date(virtualTime)) + " but LOST.");
                    } else {
                        // Simulate realistic async arrival
                        // We update virtualTime temporarily just for printing logic
                        String arrivalTimeStr = sdf.format(new Date(arrivalTime));
                        String ackTimeStr = sdf.format(new Date(arrivalTime + PROCESSING_DELAY + PROPAGATION_DELAY));

                        System.out.println(String.format("[%s] %-10s : Received Frame %d. Buffering...", arrivalTimeStr,
                                "RECEIVER", i));
                        System.out.println(
                                String.format("[%s] %-10s : Sending ACK for Frame %d", arrivalTimeStr, "RECEIVER", i));
                        System.out.println(
                                String.format("[%s] %-10s : ACK Received for Frame %d", ackTimeStr, "SENDER", i));

                        ackReceived[i] = true;
                    }
                    virtualTime += 20; // Gap between sends
                }
            }

            // Check Window Slide
            if (ackReceived[base]) {
                log("SENDER", "Base Frame " + base + " is ACKed. Sliding window.");
                while (base < totalFrames && ackReceived[base]) {
                    base++;
                }
            } else {
                // Only Base Timeout Simulation
                virtualTime += TIMEOUT_DURATION;
                log("SENDER", "!!! Timeout waiting for Frame " + base + ". Will resend ONLY Frame " + base);
            }

            System.out.println("--------------------------------------------------");
        }
    }

    public static void main(String[] args) {
        // Run any one simulation to see the logs
        stopAndWait(3);
        // goBackN(5, 3);
        // selectiveRepeat(5, 3);
    }
}