package CN.Nagle_SillyWindow;

import java.util.*;

public class NagleAlgorithm {

    static class TCPSender {
        Queue<Byte> sendBuffer = new LinkedList<>();
        boolean waitingForAck = false;
        int bytesSent = 0;
        int mss = 20; // Maximum Segment Size

        // Without Nagle's Algorithm
        void sendWithoutNagle(byte data) {
            System.out.println("[WITHOUT NAGLE] Sending 1 byte: " + data);
            bytesSent++;
            // Sends immediately - inefficient!
        }

        // With Nagle's Algorithm
        void sendWithNagle(byte data) {
            sendBuffer.offer(data);
            System.out.println("[WITH NAGLE] Buffered 1 byte: " + data +
                    " (Buffer size: " + sendBuffer.size() + ")");

            // Nagle's conditions
            if (!waitingForAck && sendBuffer.size() >= mss) {
                flush();
            } else if (!waitingForAck && sendBuffer.size() > 0) {
                // Send first byte, wait for ACK
                System.out.println("[WITH NAGLE] Sending first byte, waiting for ACK...");
                waitingForAck = true;
            }
        }

        void receiveAck() {
            System.out.println("[WITH NAGLE] ✅ ACK received!");
            waitingForAck = false;

            if (!sendBuffer.isEmpty()) {
                flush();
            }
        }

        void flush() {
            int size = sendBuffer.size();
            System.out.println("[WITH NAGLE] 📤 Sending " + size + " bytes in one segment");
            sendBuffer.clear();
            bytesSent += size;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("===== SILLY WINDOW SYNDROME & NAGLE'S ALGORITHM =====\n");

        TCPSender sender = new TCPSender();

        // Scenario: User typing slowly (1 byte at a time)
        System.out.println("--- WITHOUT NAGLE'S ALGORITHM (Silly Window) ---");
        for (byte i = 1; i <= 5; i++) {
            sender.sendWithoutNagle(i);
            Thread.sleep(100);
        }
        System.out.println("Total segments sent: 5 (Very inefficient! 😞)\n");

        System.out.println("\n--- WITH NAGLE'S ALGORITHM ---");
        sender = new TCPSender();

        // Send first byte
        sender.sendWithNagle((byte) 1);
        Thread.sleep(100);

        // Send more bytes while waiting for ACK
        sender.sendWithNagle((byte) 2);
        sender.sendWithNagle((byte) 3);
        sender.sendWithNagle((byte) 4);

        // ACK arrives
        Thread.sleep(500);
        sender.receiveAck();

        System.out.println("\nTotal segments sent: 2 (Efficient! 👍)");
        System.out.println("\n✅ Nagle's Algorithm reduces network overhead!");
    }
}
