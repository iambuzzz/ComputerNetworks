package CN.BitStuffing;

import java.io.*;
import java.net.*;

public class BitStuffingSender {

    // --- YOUR LOGIC: Bit Stuffing ---
    public static String bitStuffing(String data) {
        int count = 0;
        StringBuilder stuffed = new StringBuilder();

        for (char bit : data.toCharArray()) {
            if (bit == '1') {
                count++;
                stuffed.append('1');
            } else {
                count = 0;
                stuffed.append('0');
            }

            // If we have seen five 1s, insert a 0 immediately
            if (count == 5) {
                stuffed.append('0'); // Stuffing
                count = 0;
            }
        }
        return stuffed.toString();
    }
    // --------------------------------

    public static void main(String[] args) throws IOException, InterruptedException {
        Socket socket = new Socket("localhost", 9999);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        // TEST CASE:
        // We use '111111' (six 1s).
        // Logic should stuff a 0 after the 5th 1.
        // Result should be: 1111101
        String originalData = "111111";

        System.out.println("Original Data: " + originalData);

        System.out.println("...Applying Bit Stuffing Rule (0 after five 1s)...");
        Thread.sleep(1500);

        // Perform Stuffing
        String stuffedData = bitStuffing(originalData);

        System.out.println("Stuffed Data:  " + stuffedData);

        // Optional: Point out exactly where the stuff happened
        System.out.println("(Notice the '0' added after the 5th '1')");

        System.out.println("\n...Sending Frame (Wait 2s)...");
        Thread.sleep(2000);

        out.println(stuffedData);
        System.out.println("Frame Sent Successfully.");

        socket.close();
    }
}