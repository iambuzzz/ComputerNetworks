package CN.SNW;
import java.util.*;
import java.io.*;
import java.net.*;

public class practive {
    public static void main(String[] args) throws IOException{
        int port = 9999;
        ServerSocket server = new ServerSocket(port);
        System.out.println("waiting for sender on port 9999");
        Socket socket = server.accept();
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        Random random = new Random();
        int expectedSequence = 0;
        while (true) {
            String receivedFrame = in.readLine();
            if (receivedFrame == null)
                break;
            String[] parts = receivedFrame.split("\\|");
            int seqNum = Integer.parseInt(parts[0]);
            String data = parts[1];
            System.out.println("Received Frame [Seq: " + seqNum + ", Data: " + data + "]");
            
        }
    }
}