package CN.CRC;

import java.util.*;

public class SimpleCRC {

    // Step 1: XOR two binary strings
    public static String xor(String a, String b) {
        StringBuilder result = new StringBuilder();

        // Start from index 1 (skip first bit)
        for (int i = 1; i < b.length(); i++) {
            // Same bits = 0, Different bits = 1
            if (a.charAt(i) == b.charAt(i)) {
                result.append("0");
            } else {
                result.append("1");
            }
        }
        return result.toString();
    }

    // Step 2: Divide using XOR (Modulo-2 Division)
    public static String divide(String data, String divisor) {
        int divisorLen = divisor.length();

        // Pick first 'n' bits from data
        String temp = data.substring(0, divisorLen);

        int i = divisorLen;
        while (i <= data.length()) {
            // If first bit is 1, XOR with divisor
            if (temp.charAt(0) == '1') {
                temp = xor(divisor, temp);
            } else {
                // If first bit is 0, XOR with zeros
                temp = xor("0".repeat(divisorLen), temp);
            }

            // Bring down next bit (if available)
            if (i < data.length()) {
                temp = temp + data.charAt(i);
            }
            i++;
        }

        return temp; // This is the remainder (CRC)
    }

    // Step 3: Generate CRC code (Sender Side)
    public static String generateCRC(String data, String divisor) {
        int numZeros = divisor.length() - 1;

        // Append zeros to data
        String appendedData = data + "0".repeat(numZeros);

        System.out.println("Data + Zeros: " + appendedData);

        // Get remainder after division
        String remainder = divide(appendedData, divisor);

        System.out.println("Remainder (CRC): " + remainder);

        // Final code = Original data + Remainder
        return data + remainder;
    }

    // Step 4: Check for errors (Receiver Side)
    public static boolean checkError(String receivedData, String divisor) {
        // Divide received data by divisor
        String remainder = divide(receivedData, divisor);

        System.out.println("Remainder at receiver: " + remainder);

        // If remainder has any '1', error detected
        for (char bit : remainder.toCharArray()) {
            if (bit == '1') {
                return true; // Error found
            }
        }
        return false; // No error
    }

    // Main function
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter data (binary): ");
        String data = sc.next();

        System.out.print("Enter divisor/key (binary): ");
        String divisor = sc.next();

        // Sender Side
        System.out.println("\n===== SENDER =====");
        String crcCode = generateCRC(data, divisor);
        System.out.println("Transmitted Code: " + crcCode);

        // Receiver Side
        System.out.println("\n===== RECEIVER =====");
        System.out.print("Enter received code: ");
        String receivedCode = sc.next();

        boolean hasError = checkError(receivedCode, divisor);

        if (hasError) {
            System.out.println("❌ ERROR DETECTED!");
        } else {
            System.out.println("✅ NO ERROR - Data is correct!");
        }

        sc.close();
    }
}
