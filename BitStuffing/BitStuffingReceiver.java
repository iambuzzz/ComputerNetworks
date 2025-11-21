package CN.BitStuffing;

import java.io.*;
import java.net.*;

public class BitStuffingReceiver {

    // --- YOUR LOGIC: Bit Destuffing ---
    public static String bitDestuffing(String stuffedData) {
        int count = 0;
        StringBuilder destuffed = new StringBuilder();

        for (int i = 0; i < stuffedData.length(); i++) {
            char bit = stuffedData.charAt(i);

            if (bit == '1') {
                count++;
                destuffed.append('1');

                // If 5 consecutive 1s found, skip next bit (stuffed 0)
                if (count == 5) {
                    i++; // Skip the next bit (the stuffed 0)
                    count = 0;
                }
            } else {
                count = 0;
                destuffed.append('0');
            }
        }

        return destuffed.toString();
    }
    // ----------------------------------

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 9999;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("--- Receiver Waiting for Bit Stuffed Frame ---");

        Socket socket = serverSocket.accept();
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String receivedFrame = in.readLine();

        if (receivedFrame != null) {
            System.out.println("\n1. Received Stuffed Frame: " + receivedFrame);

            // Simulation Delay
            System.out.println("...Detecting 5 consecutive 1s...");
            Thread.sleep(1500);
            System.out.println("...Removing stuffed '0's...");
            Thread.sleep(1500);

            // Perform De-stuffing
            String originalData = bitDestuffing(receivedFrame);

            System.out.println("2. Destuffed (Original) Data: " + originalData);
            System.out.println("\n--> Data Recovery Successful.");
        }

        socket.close();
        serverSocket.close();
    }
}