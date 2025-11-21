package CN.CRC;

import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.Scanner; // Imported Scanner

public class CRCSender {

    // --- CRC LOGIC (Modulo-2 Division) ---
    public static String mod2div(String data, String divisor) {
        int pick = divisor.length();
        StringBuilder sb = new StringBuilder(data);
        int n = sb.length();

        for (int i = 0; i <= n - pick; i++) {
            if (sb.charAt(i) == '1') {
                for (int j = 0; j < pick; j++) {
                    if (sb.charAt(i + j) == divisor.charAt(j)) {
                        sb.setCharAt(i + j, '0');
                    } else {
                        sb.setCharAt(i + j, '1');
                    }
                }
            }
        }
        return sb.substring(n - pick + 1);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Socket socket = new Socket("localhost", 9999);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        Scanner scanner = new Scanner(System.in); // Scanner for Input

        // 1. GET INPUT FROM USER
        System.out.print("Enter Data to Send (Binary only, e.g., 1001): ");
        String data = scanner.nextLine();

        // Standard CRC-4 Divisor (Poly: x^3 + x + 1)
        String divisor = "1101";

        System.out.println("\nOriginal Data: " + data);
        System.out.println("Divisor:       " + divisor);

        // 2. Append Zeros (Length of divisor - 1)
        String appendedData = data;
        for (int i = 0; i < divisor.length() - 1; i++) {
            appendedData += "0";
        }

        System.out.println("Appended Data: " + appendedData);

        // 3. Calculate CRC (Remainder)
        System.out.println("...Calculating CRC...");
        String remainder = mod2div(appendedData, divisor);
        System.out.println("CRC Remainder: " + remainder);

        // 4. Create Final Codeword
        String codeword = data + remainder;
        System.out.println("Final Codeword: " + codeword);

        // 5. SIMULATE ERROR (50% Chance)
        Random random = new Random();
        boolean injectError = random.nextBoolean();

        System.out.println("\n...Transmitting (Wait 2s)...");
        Thread.sleep(2000);

        if (injectError) {
            System.out.println(">> [SIMULATION] Noise Injected! Flipping last bit...");
            char lastBit = codeword.charAt(codeword.length() - 1);
            char flippedBit = (lastBit == '1') ? '0' : '1';

            codeword = codeword.substring(0, codeword.length() - 1) + flippedBit;
            System.out.println("Sending Corrupted: " + codeword);
        } else {
            System.out.println(">> [SIMULATION] Channel Clear.");
            System.out.println("Sending Valid:     " + codeword);
        }

        out.println(codeword);

        socket.close();
        scanner.close();
    }
}