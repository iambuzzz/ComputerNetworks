package CN.LAN.sec;

import java.util.*;

public class LANSwitch {

    // The MAC Table: Stores Map of [MAC Address -> Port Number]
    // In a real switch, this is Content Addressable Memory (CAM)
    static Map<String, Integer> forwardingTable = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {
        System.out.println("--- LAN Connectivity (Learning Switch Simulation) ---");
        System.out.println("Switch initialized with empty Forwarding Table.");

        // SIMULATION SCENARIOS

        // Case 1: A sends to B.
        // Switch doesn't know A (Learns it). Switch doesn't know B (Floods).
        sendPacket("A", "B", 1);

        // Case 2: B replies to A.
        // Switch doesn't know B (Learns it). Switch KNOWS A (Unicasts).
        sendPacket("B", "A", 2);

        // Case 3: C sends to B.
        // Switch doesn't know C (Learns it). Switch KNOWS B (Unicasts).
        sendPacket("C", "B", 3);

        // Case 4: A sends to C.
        // Switch knows A (Refreshes timer). Switch KNOWS C (Unicasts).
        sendPacket("A", "C", 1);

        // Case 5: D sends to E (Both unknown).
        sendPacket("D", "E", 4);
    }

    public static void sendPacket(String srcMac, String destMac, int incomingPort) throws InterruptedException {
        System.out.println("\n-------------------------------------------------------------");
        System.out.println(
                "Incoming Frame: [Src: " + srcMac + " -> Dest: " + destMac + "] received on Port " + incomingPort);
        Thread.sleep(1000); // Delay for visualization

        // --- STEP 1: LEARNING PHASE (Look at Source) ---
        if (!forwardingTable.containsKey(srcMac)) {
            forwardingTable.put(srcMac, incomingPort);
            System.out.println(
                    "   [LEARNING] New Device! Added (MAC: " + srcMac + " -> Port: " + incomingPort + ") to Table.");
        } else {
            // If already exists, we just refresh/update (in case it moved)
            if (forwardingTable.get(srcMac) != incomingPort) {
                forwardingTable.put(srcMac, incomingPort);
                System.out.println(
                        "   [LEARNING] Host moved! Updated (MAC: " + srcMac + " -> Port: " + incomingPort + ").");
            } else {
                System.out.println("   [LEARNING] Host " + srcMac + " is already known. Table unchanged.");
            }
        }

        // --- STEP 2: FORWARDING PHASE (Look at Destination) ---
        Thread.sleep(500);
        if (forwardingTable.containsKey(destMac)) {
            int targetPort = forwardingTable.get(destMac);
            System.out
                    .println("   [FORWARDING] Destination " + destMac + " found in table on Port " + targetPort + ".");
            System.out.println("   --> ACTION: UNICAST to Port " + targetPort);
        } else {
            System.out.println("   [FORWARDING] Destination " + destMac + " NOT found in table.");
            System.out.println("   --> ACTION: FLOOD (Broadcast) to all ports except " + incomingPort);
        }

        printTable();
    }

    public static void printTable() {
        System.out.println("\n   [ Current Switch Table ]");
        System.out.println("   |   MAC    |  Port  |");
        System.out.println("   |----------|--------|");
        if (forwardingTable.isEmpty()) {
            System.out.println("   |   Empty  |   --   |");
        } else {
            for (Map.Entry<String, Integer> entry : forwardingTable.entrySet()) {
                System.out.println("   |    " + entry.getKey() + "     |    " + entry.getValue() + "   |");
            }
        }
    }
}
