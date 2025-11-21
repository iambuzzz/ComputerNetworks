package CN.Checksum;

import java.util.*;

public class Checksum {

    // 1. Generate Checksum (Sender Side)
    public static int generateChecksum(String data) {
        String hexValue;
        int x, checksum = 0;

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
        return (0xFFFF - num); // FFFF - num gives 1's complement
    }

    // 3. Receive and Verify (Receiver Side)
    public static void receive(String data, int receivedChecksum) {
        System.out.println("\n**********RECEIVER**********");
        System.out.println("Received Data: " + data);
        System.out.println("Received Checksum: " + Integer.toHexString(receivedChecksum));

        // Generate checksum for received data
        int generatedChecksum = generateChecksum(data);

        // Take complement of generated checksum
        generatedChecksum = onesComplement(generatedChecksum);

        // Add received checksum and generated checksum
        int syndrome = receivedChecksum + generatedChecksum;

        // Take complement of syndrome
        syndrome = onesComplement(syndrome);

        System.out.println("Syndrome: " + Integer.toHexString(syndrome));

        // Check for errors
        if (syndrome == 0) {
            System.out.println("✓ No Error - Data is correct!");
        } else {
            System.out.println("✗ Error Detected - Data is corrupted!");
        }
    }

    // Main function
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Sender Side
        System.out.println("**********SENDER**********");
        System.out.print("Enter the data to send: ");
        String data = sc.nextLine();

        System.out.println("\nGenerating Checksum...");
        int checksum = generateChecksum(data);

        System.out.println("\nChecksum Generated: " + Integer.toHexString(checksum));
        System.out.println("Data to transmit: " + data);
        System.out.println("Checksum to transmit: " + Integer.toHexString(checksum));

        // Receiver Side
        System.out.print("\nEnter the received data: ");
        String receivedData = sc.nextLine();

        System.out.print("Enter the received checksum (in hex): ");
        String checksumHex = sc.nextLine();
        int receivedChecksum = Integer.parseInt(checksumHex, 16);

        receive(receivedData, receivedChecksum);

        sc.close();
    }
} 
