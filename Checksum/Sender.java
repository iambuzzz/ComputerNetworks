package CN.Checksum;

import java.io.*;
import java.net.*;
import java.util.Random;

public class Sender {

    // --- YOUR SPECIFIC LOGIC ---
    public static int calculateChecksum(String data) {
        String hexValue;
        int x, checksum = 0;

        int i;
        for (i = 0; i < data.length() - 1; i += 2) {
            x = (int) data.charAt(i);
            hexValue = Integer.toHexString(x);
            x = (int) data.charAt(i + 1);
            hexValue += Integer.toHexString(x);

            System.out.println(data.charAt(i) + "" + data.charAt(i + 1) + " : " + hexValue);

            x = Integer.parseInt(hexValue, 16);
            checksum += x;
        }
        if (data.length() % 2 != 0) {
            x = (int) data.charAt(i);
            hexValue = "00" + Integer.toHexString(x);
            System.out.println(data.charAt(i) + " : " + hexValue);
            x = Integer.parseInt(hexValue, 16);
            checksum += x;
        }
        String checksumHex = Integer.toHexString(checksum);
        if (checksumHex.length() > 4) {
            int carry = Integer.parseInt(checksumHex.substring(0, checksumHex.length() - 4), 16);
            checksum = Integer.parseInt(checksumHex.substring(checksumHex.length() - 4), 16);
            checksum += carry;
        }
        return (0xFFFF - checksum);
    }
    // ---------------------------

    public static void main(String[] args) throws IOException, InterruptedException {
        Socket socket = new Socket("localhost", 9999);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        String originalData = "FOROUZAN";
        System.out.println("--- Calculating Checksum for: " + originalData + " ---");

        int validChecksum = calculateChecksum(originalData);
        System.out.println("Generated Checksum: " + Integer.toHexString(validChecksum).toUpperCase());

        // --- 30% PROBABILITY LOGIC ---
        Random random = new Random();
        int chance = random.nextInt(100); // Generates 0 to 99

        // If number is 0-29 (30 numbers), we corrupt.
        boolean corruptData = (chance < 30);

        System.out.println("\n...Transmitting (Wait 2s)...");
        Thread.sleep(2000);

        if (corruptData) {
            // 30% Chance Case
            System.out.println(">> [SIMULATION] Random Event (30% hit): NOISE INJECTED!");
            String badData = "FOROUZBM";

            System.out.println("Sending Data: " + badData);
            System.out.println("Sending Checksum: " + Integer.toHexString(validChecksum).toUpperCase());

            out.println(badData + "|" + validChecksum);

        } else {
            // 70% Chance Case
            System.out.println(">> [SIMULATION] Random Event (Safe): CHANNEL CLEAR.");
            System.out.println("Sending Data: " + originalData);
            System.out.println("Sending Checksum: " + Integer.toHexString(validChecksum).toUpperCase());

            out.println(originalData + "|" + validChecksum);
        }

        socket.close();
    }
}