package CN.Nagle_SillyWindow;

public class SillyWindowSyndrome {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("--- Silly Window Syndrome (Receiver Side Solution) ---");

        int totalBuffer = 1000;
        int currentData = 990; // Buffer is almost full
        int MSS = 100;

        // Receiver Process Loop
        for (int i = 0; i < 10; i++) {
            // 1. Application reads some data (freeing up space)
            int bytesProcessed = 10; // Reading slowly (Silly Window cause)
            currentData -= bytesProcessed;

            int freeSpace = totalBuffer - currentData;

            System.out.println("Buffer State: Used=" + currentData + ", Free=" + freeSpace);
            Thread.sleep(500);

            // 2. Clark's Algorithm Logic
            // Don't advertise until we have space >= MSS OR space >= Total/2
            if (freeSpace >= MSS || freeSpace >= totalBuffer / 2) {
                System.out.println(" -> Logic: Space is significant.");
                System.out.println(" -> ACTION: Advertise Window Size = " + freeSpace + "\n");
            } else {
                System.out.println(" -> Logic: Space too small (Silly Window Risk).");
                System.out.println(" -> ACTION: Advertise Window Size = 0 (Wait)\n");
            }
        }
    }
}