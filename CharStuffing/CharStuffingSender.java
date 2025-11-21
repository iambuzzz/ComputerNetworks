package CN.CharStuffing;

import java.io.*;
import java.net.*;

public class CharStuffingSender {

    // --- YOUR LOGIC: Stuffing ---
    public static String charStuffing(String payload) {
        String DLE = "DLE";
        String STX = "STX";
        String ETX = "ETX";
        StringBuilder frame = new StringBuilder();

        frame.append(DLE).append(STX); // Start Flag

        // If payload contains "DLE", escape it with another "DLE"
        if (payload.contains(DLE)) {
            payload = payload.replace(DLE, DLE + DLE);
        }

        frame.append(payload);
        frame.append(DLE).append(ETX); // End Flag

        return frame.toString();
    }
    // ----------------------------

    public static void main(String[] args) throws IOException, InterruptedException {
        Socket socket = new Socket("localhost", 9999);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        // TEST CASE:
        // We include "DLE" inside the data to test if the code escapes it correctly.
        String originalPayload = "A DLE B";

        System.out.println("Original Payload: " + originalPayload);

        System.out.println("...Applying Character Stuffing...");
        Thread.sleep(1500);

        // Perform Stuffing
        String stuffedData = charStuffing(originalPayload);

        // Result should look like: DLESTXA DLEDLE BDLEETX
        System.out.println("Stuffed Frame:    " + stuffedData);

        // Point out the doubling to the examiner
        System.out.println("(Notice: 'DLE' inside data became 'DLEDLE')");

        System.out.println("\n...Sending Frame (Wait 2s)...");
        Thread.sleep(2000);

        out.println(stuffedData);
        System.out.println("Frame Sent Successfully.");

        socket.close();
    }
}