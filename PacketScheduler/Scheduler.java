package CN.PacketScheduler;

import java.util.*;
public class Scheduler {
    // A simple class to represent a Network Packet
    static class Packet implements Comparable<Packet> {
        String id;
        int priority; // Used for Priority Queue (Higher number = Higher priority)
        int size; // Used for WFQ logic (optional, but good for realism)

        public Packet(String id, int priority) {
            this.id = id;
            this.priority = priority;
            this.size = 100; // Default size
        }

        @Override
        public String toString() {
            return "[ID: " + id + " | Prio: " + priority + "]";
        }

        // Logic to sort by Priority (For Priority Queue Algo)
        @Override
        public int compareTo(Packet other) {
            // Descending order: Higher priority comes first
            return Integer.compare(other.priority, this.priority);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        //FIFO
        Queue<Packet> buffer = new LinkedList<>();
        System.out.println("--- FIFO (First-In, First-Out) Simulation ---");
        // 1. Arrival Phase
        System.out.println("Packet Arrival:");
        buffer.add(new Packet("P1 (Email)", 1));
        System.out.println("Arrived: P1");
        buffer.add(new Packet("P2 (Video)", 10));
        System.out.println("Arrived: P2");
        buffer.add(new Packet("P3 (Web)", 5));
        System.out.println("Arrived: P3");

        System.out.println("\n...Starting Processing (Wait 1s)...");
        Thread.sleep(1000);

        // 2. Processing Phase
        while (!buffer.isEmpty()) {
            Packet p = buffer.poll(); // Remove from HEAD
            System.out.println("Processing: " + p.id);
            Thread.sleep(1000); // Delay
        }
        System.out.println("Buffer Empty.");

        //Priority
        // Java's PriorityQueue uses the compareTo method in Packet class
        PriorityQueue<Packet> buffer1 = new PriorityQueue<>();
        System.out.println("--- Priority Queueing Simulation ---");
        // 1. Arrival (Mixed Order)
        System.out.println("Packet Arrival:");
        
        Packet p1 = new Packet("P1 (Email)", 1); // Low Prio
        System.out.println("Arrived: " + p1);
        buffer1.add(p1);

        Packet p2 = new Packet("P2 (Video)", 10); // High Prio
        System.out.println("Arrived: " + p2);
        buffer1.add(p2);

        Packet p3 = new Packet("P3 (Voice)", 8);  // Medium Prio
        System.out.println("Arrived: " + p3);
        buffer1.add(p3);

        System.out.println("\n...Router Sorting Packets (Wait 2s)...");
        Thread.sleep(2000);

        // 2. Processing Phase
        System.out.println("\nProcessing Order (Highest Priority First):");
        while (!buffer1.isEmpty()) {
            Packet p = buffer1.poll(); // Pulls the highest priority element
            System.out.println("Transmitting: " + p);
            Thread.sleep(1000);
        }

        //FairQueuing(RoundRobin)
        System.out.println("--- Fair Queuing (Round Robin) ---");
        // Create queues for different flows
        Queue<String> flow1 = new LinkedList<>();
        Queue<String> flow2 = new LinkedList<>();
        Queue<String> flow3 = new LinkedList<>();
        // Fill buffers
        flow1.add("F1-Packet1"); flow1.add("F1-Packet2"); flow1.add("F1-Packet3");
        flow2.add("F2-Packet1"); // Only 1 packet
        flow3.add("F3-Packet1"); flow3.add("F3-Packet2");
        // Put flows into a list to iterate
        List<Queue<String>> allFlows = new ArrayList<>();
        allFlows.add(flow1);
        allFlows.add(flow2);
        allFlows.add(flow3);

        System.out.println("Flow 1 has 3 packets.");
        System.out.println("Flow 2 has 1 packet.");
        System.out.println("Flow 3 has 2 packets.");
        System.out.println("\n...Starting Round Robin Logic...");
        Thread.sleep(2000);

        boolean queuesNotEmpty = true;
        
        while (queuesNotEmpty) {
            queuesNotEmpty = false;
            // Visit every flow once per round
            for (int i = 0; i < allFlows.size(); i++) {
                Queue<String> currentFlow = allFlows.get(i);
                if (!currentFlow.isEmpty()) {
                    String packet = currentFlow.poll();
                    System.out.println("Processing Flow " + (i+1) + ": " + packet);
                    queuesNotEmpty = true; // If we found a packet, keep looping
                    Thread.sleep(1000);
                }
            }
            System.out.println("--- End of Round ---");
        }
        System.out.println("All queues empty.");

        // Weighted Fair Queuing (WFQ)
        System.out.println("--- Weighted Fair Queuing (WFQ) ---");

        // Define Queues
        Queue<String> premiumQueue = new LinkedList<>();
        Queue<String> standardQueue = new LinkedList<>();

        // Define Weights
        int weightPremium = 3; // Premium gets 3 turns
        int weightStandard = 1; // Standard gets 1 turn

        // Fill Queues
        for (int i = 1; i <= 10; i++)
            premiumQueue.add("Premium-P" + i);
        for (int i = 1; i <= 5; i++)
            standardQueue.add("Standard-P" + i);

        System.out.println("Premium Queue: Weight " + weightPremium + " (10 Packets)");
        System.out.println("Standard Queue: Weight " + weightStandard + " (5 Packets)");

        System.out.println("\n...Starting Simulation...");
        Thread.sleep(2000);

        while (!premiumQueue.isEmpty() || !standardQueue.isEmpty()) {

            // 1. Serve Premium Queue based on Weight
            System.out.println("\n[Turn: Premium Flow (Weight 3)]");
            for (int i = 0; i < weightPremium; i++) {
                if (!premiumQueue.isEmpty()) {
                    System.out.println("Processing: " + premiumQueue.poll());
                    Thread.sleep(800);
                }
            }

            // 2. Serve Standard Queue based on Weight
            System.out.println("[Turn: Standard Flow (Weight 1)]");
            for (int i = 0; i < weightStandard; i++) {
                if (!standardQueue.isEmpty()) {
                    System.out.println("Processing: " + standardQueue.poll());
                    Thread.sleep(800);
                }
            }
        }
        System.out.println("\nAll packets processed.");
    }
}
