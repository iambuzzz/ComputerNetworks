package CN.Checksum;

import java.io.*;
import java.net.*;

public class Receiver {

    // --- YOUR LOGIC STARTED ---
    public static int calculateChecksum(String data) {
        String hexValue;
        int x, checksum = 0;

        System.out.println("\n...Processing Data (Receiver Side)...");

        // Process data in pairs (2 characters at a time)
        int i;
        for (i = 0; i < data.length() - 1; i += 2) {
            // Convert 2 characters to hex value
            x = (int) data.charAt(i);
            hexValue = Integer.toHexString(x);

            x = (int) data.charAt(i + 1);
            hexValue += Integer.toHexString(x);

            System.out.println(data.charAt(i) + "" + data.charAt(i + 1) + " : " + hexValue);

            // Add to checksum
            x = Integer.parseInt(hexValue, 16);
            checksum += x;
        }

        // Handle odd length string
        if (data.length() % 2 != 0) {
            x = (int) data.charAt(i);
            hexValue = "00" + Integer.toHexString(x);
            System.out.println(data.charAt(i) + " : " + hexValue);
            x = Integer.parseInt(hexValue, 16);
            checksum += x;
        }

        // Handle carry (wrap around if checksum > 16 bits)
        String checksumHex = Integer.toHexString(checksum);
        if (checksumHex.length() > 4) {
            int carry = Integer.parseInt(checksumHex.substring(0, checksumHex.length() - 4), 16);
            checksum = Integer.parseInt(checksumHex.substring(checksumHex.length() - 4), 16);
            checksum += carry;
        }

        // Take 1's complement
        checksum = onesComplement(checksum);
        return checksum;
    }

    // 2. Calculate 1's Complement
    public static int onesComplement(int num) {
        return (0xFFFF - num);
    }
    // --- YOUR LOGIC ENDED ---

    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocket serverSocket = new ServerSocket(9999);
        System.out.println("Receiver waiting for connection...");

        Socket socket = serverSocket.accept();
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        while (true) {
            String line = in.readLine();
            if (line == null)
                break;

            // Split Data and Sender's Checksum
            String[] parts = line.split("\\|");
            String data = parts[0];
            int receivedChecksum = Integer.parseInt(parts[1]);

            System.out.println("\n--------------------------------------------");
            System.out.println("Received Data: " + data);
            System.out.println("Received Checksum: " + Integer.toHexString(receivedChecksum).toUpperCase());

            Thread.sleep(1000); // Delay for visualization

            // RECALCULATE Checksum using your logic
            int calculatedChecksum = calculateChecksum(data);
            System.out.println("Calculated Checksum: " + Integer.toHexString(calculatedChecksum).toUpperCase());

            // VALIDATION
            if (calculatedChecksum == receivedChecksum) {
                System.out.println("\n--> STATUS: MATCH (Data is Clean)");
            } else {
                System.out.println("\n--> STATUS: MISMATCH (Error Detected!)");
            }
            System.out.println("--------------------------------------------");
        }
        serverSocket.close();
    }
}