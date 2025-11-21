package CN.Nagle_SillyWindow;


public class NagleAlgo {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("--- Nagle's Algorithm Simulation ---");

        int MSS = 10; // Maximum Segment Size
        String inputData = "ComputerNetworksLab";
        int bufferSize = 0;
        boolean unAckedDataInFlight = false; // Simulation flag

        System.out.println("Data to Send: " + inputData);
        System.out.println("MSS: " + MSS + "\n");

        for (int i = 0; i < inputData.length(); i++) {
            char c = inputData.charAt(i);
            bufferSize++;
            System.out.print("App wrote: '" + c + "' | Buffer: " + bufferSize + " | ");

            // Nagle's Logic Check
            if (bufferSize >= MSS) {
                System.out.println("Condition: Buffer >= MSS -> [SENDING FULL PACKET]");
                bufferSize = 0;
                unAckedDataInFlight = true;
            } else if (unAckedDataInFlight) {
                System.out.println("Condition: UnAcked Data Exists -> [WAITING/BUFFERING]");
            } else {
                System.out.println("Condition: Network Idle -> [SENDING SMALL PACKET]");
                bufferSize = 0;
                unAckedDataInFlight = true;
            }

            Thread.sleep(500); // Delay for effect

            // Simulate receiving an ACK randomly to clear the line
            if (unAckedDataInFlight && Math.random() > 0.7) {
                System.out.println("\n<< ACK RECEIVED >> (Network is now Idle)\n");
                unAckedDataInFlight = false;
            }
        }
    }
}