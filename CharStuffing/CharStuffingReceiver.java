package CN.CharStuffing;

import java.io.*;
import java.net.*;

public class CharStuffingReceiver {

    // --- YOUR LOGIC: Destuffing ---
    public static String charDestuffing(String frame) {
        String DLE = "DLE";
        String STX = "STX";
        String ETX = "ETX";

        // 1. Remove Start Flag (DLE STX)
        frame = frame.replaceFirst(DLE + STX, "");

        // 2. Remove End Flag (DLE ETX)
        int endIndex = frame.lastIndexOf(DLE + ETX);
        if (endIndex != -1) {
            frame = frame.substring(0, endIndex);
        }

        // 3. Remove stuffed DLE (DLE DLE -> DLE)
        frame = frame.replace(DLE + DLE, DLE);

        return frame;
    }
    // ------------------------------

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 9999;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("--- Receiver Waiting for Character Stuffed Frame ---");

        Socket socket = serverSocket.accept();
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Read the incoming frame
        String receivedFrame = in.readLine();

        if (receivedFrame != null) {
            System.out.println("\n1. Received Stuffed Frame: " + receivedFrame);

            // --- DELAY FOR VISUALIZATION ---
            System.out.println("...Identifying Flags (DLE STX / DLE ETX)...");
            Thread.sleep(1500);

            System.out.println("...Removing Duplicate DLEs...");
            Thread.sleep(1500);

            // Perform Destuffing
            String originalData = charDestuffing(receivedFrame);

            System.out.println("2. Destuffed (Original) Data: " + originalData);
            System.out.println("\n--> Data Recovery Successful.");
        }

        socket.close();
        serverSocket.close();
    }
}