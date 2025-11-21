package CN.CRC;

import java.io.*;
import java.net.*;

public class CRCReceiver {

    // --- CRC LOGIC: Modulo-2 Binary Division ---
    public static String mod2div(String data, String divisor) {
        int pick = divisor.length();
        StringBuilder sb = new StringBuilder(data);
        int n = sb.length();

        for (int i = 0; i <= n - pick; i++) {
            // If the leading bit is '1', perform XOR
            if (sb.charAt(i) == '1') {
                for (int j = 0; j < pick; j++) {
                    // XOR Logic: Same bits = 0, Different bits = 1
                    if (sb.charAt(i + j) == divisor.charAt(j)) {
                        sb.setCharAt(i + j, '0');
                    } else {
                        sb.setCharAt(i + j, '1');
                    }
                }
            }
        }
        // The last (pick-1) bits are the remainder
        return sb.substring(n - pick + 1);
    }
    // -------------------------------------------

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 9999;
        String divisor = "1101"; // Shared Polynomial

        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("--- CRC Receiver Waiting (Divisor: " + divisor + ") ---");

        Socket socket = serverSocket.accept();
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Read incoming frame (Data + CRC Code appended)
        String receivedFrame = in.readLine();

        if (receivedFrame != null) {
            System.out.println("\nReceived Frame: " + receivedFrame);

            System.out.println("...Checking integrity (Dividing by " + divisor + ")...");
            Thread.sleep(1500); // Visualization Delay

            // Perform Division
            String remainder = mod2div(receivedFrame, divisor);
            System.out.println("Calculated Remainder: " + remainder);

            // CHECK: Remainder must be all zeros
            if (remainder.contains("1")) {
                System.out.println("\n--> STATUS: ERROR DETECTED (Remainder != 0)");
            } else {
                System.out.println("\n--> STATUS: ACCEPTED (Data is Clean)");
                // Extract original data (remove checksum bits)
                String originalData = receivedFrame.substring(0, receivedFrame.length() - (divisor.length() - 1));
                System.out.println("    Extracted Data: " + originalData);
            }
        }

        socket.close();
        serverSocket.close();
    }
}