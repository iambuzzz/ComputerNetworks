package CN.LAN.CS;

import java.net.*;
import java.io.*;

public class LANClient {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 5000);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("Server says: " + in.readLine());
        socket.close();
    }
}
