package CN.RTT_Karn;

public class RTTEstimator {

    static double srtt = 0; // Smoothed RTT
    static double rttvar = 0; // RTT Variation
    static double rto = 1000; // Retransmission Timeout (initially 1 sec)

    public static void main(String[] args) {
        System.out.println("--- TCP RTT & Karn's Algorithm Simulation ---");
        System.out.printf("Initial RTO: %.2f ms\n\n", rto);

        // Simulate RTT samples collected from network
        // -1 represents a Timeout/Retransmission
        double[] measuredRTTs = { 100, 120, -1, 110, 80, -1, -1, 150 };

        for (double rttSample : measuredRTTs) {
            if (rttSample == -1) {
                handleRetransmission();
            } else {
                updateRTO(rttSample);
            }
        }
    }

    // Standard Jacobson's Algorithm
    public static void updateRTO(double sampleRTT) {
        System.out.printf("MEASURED RTT: %.2f ms\n", sampleRTT);

        double alpha = 0.125;
        double beta = 0.25;

        if (srtt == 0) {
            srtt = sampleRTT;
            rttvar = sampleRTT / 2;
        } else {
            rttvar = (1 - beta) * rttvar + beta * Math.abs(srtt - sampleRTT);
            srtt = (1 - alpha) * srtt + alpha * sampleRTT;
        }

        rto = srtt + 4 * rttvar;
        System.out.printf(" -> Updated RTO: %.2f ms (SRTT: %.2f)\n\n", rto, srtt);
    }

    // Karn's Algorithm Logic
    public static void handleRetransmission() {
        System.out.println("!!! PACKET LOSS / TIMEOUT DETECTED !!!");
        System.out.println(" -> Karn's Algo: Do NOT update SRTT.");

        rto = rto * 2; // Exponential Backoff

        System.out.printf(" -> Backed Off RTO: %.2f ms\n\n", rto);
    }
}