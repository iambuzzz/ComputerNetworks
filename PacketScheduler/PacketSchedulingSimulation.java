package CN.PacketScheduler;

import java.util.*;

// Packet class
class Packet {
    int id;
    String data;
    int priority; // For Priority Queue (1=highest)
    int flowId; // For Fair Queuing
    int size; // Packet size in bytes
    long arrivalTime;

    Packet(int id, String data, int priority, int flowId, int size) {
        this.id = id;
        this.data = data;
        this.priority = priority;
        this.flowId = flowId;
        this.size = size;
        this.arrivalTime = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Packet#" + id + "[P" + priority + ", Flow" + flowId + ", " + size + "B]";
    }
}

// 1. FIFO Scheduling
class FIFOScheduler {
    private Queue<Packet> queue;

    public FIFOScheduler() {
        queue = new LinkedList<>();
    }

    public void enqueue(Packet packet) {
        queue.offer(packet);
        System.out.println("  [FIFO] Enqueued: " + packet);
    }

    public Packet dequeue() {
        if (queue.isEmpty())
            return null;

        Packet packet = queue.poll();
        System.out.println("  [FIFO] Transmitted: " + packet);
        return packet;
    }

    public void displayQueue() {
        System.out.println("\n--- FIFO Queue Status ---");
        System.out.println("Queue size: " + queue.size());
        System.out.println("Packets: " + queue);
    }
}

// 2. Priority Queue Scheduling
class PriorityQueueScheduler {
    private PriorityQueue<Packet> queue;

    public PriorityQueueScheduler() {
        // Lower priority value = higher priority
        queue = new PriorityQueue<>((p1, p2) -> Integer.compare(p1.priority, p2.priority));
    }

    public void enqueue(Packet packet) {
        queue.offer(packet);
        System.out.println("  [PQ] Enqueued: " + packet);
    }

    public Packet dequeue() {
        if (queue.isEmpty())
            return null;

        Packet packet = queue.poll();
        System.out.println("  [PQ] Transmitted (Highest Priority): " + packet);
        return packet;
    }

    public void displayQueue() {
        System.out.println("\n--- Priority Queue Status ---");
        System.out.println("Queue size: " + queue.size());

        List<Packet> temp = new ArrayList<>(queue);
        temp.sort((p1, p2) -> Integer.compare(p1.priority, p2.priority));
        System.out.println("Packets (sorted by priority): " + temp);
    }
}

// 3. Fair Queuing Scheduling
class FairQueueScheduler {
    private Map<Integer, Queue<Packet>> flowQueues;
    private int currentFlow;
    private Set<Integer> activeFlows;

    public FairQueueScheduler() {
        flowQueues = new HashMap<>();
        activeFlows = new HashSet<>();
        currentFlow = 0;
    }

    public void enqueue(Packet packet) {
        int flowId = packet.flowId;

        // Create queue for new flow
        if (!flowQueues.containsKey(flowId)) {
            flowQueues.put(flowId, new LinkedList<>());
        }

        flowQueues.get(flowId).offer(packet);
        activeFlows.add(flowId);

        System.out.println("  [FQ] Enqueued: " + packet + " to Flow" + flowId);
    }

    public Packet dequeue() {
        if (activeFlows.isEmpty())
            return null;

        // Round-robin among flows
        List<Integer> flows = new ArrayList<>(activeFlows);
        Collections.sort(flows);

        // Find next non-empty flow
        int attempts = 0;
        while (attempts < flows.size()) {
            currentFlow = (currentFlow % flows.size());
            int flowId = flows.get(currentFlow);

            Queue<Packet> flowQueue = flowQueues.get(flowId);

            if (!flowQueue.isEmpty()) {
                Packet packet = flowQueue.poll();
                System.out.println("  [FQ] Transmitted from Flow" + flowId + ": " + packet);

                // Remove flow if empty
                if (flowQueue.isEmpty()) {
                    activeFlows.remove(flowId);
                }

                currentFlow = (currentFlow + 1) % flows.size();
                return packet;
            }

            currentFlow++;
            attempts++;
        }

        return null;
    }

    public void displayQueue() {
        System.out.println("\n--- Fair Queue Status ---");
        System.out.println("Active flows: " + activeFlows.size());

        for (Map.Entry<Integer, Queue<Packet>> entry : flowQueues.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                System.out.println("  Flow" + entry.getKey() + ": " + entry.getValue());
            }
        }
    }
}

// 4. Weighted Fair Queuing (WFQ)
class WFQScheduler {
    private Map<Integer, Queue<Packet>> flowQueues;
    private Map<Integer, Integer> flowWeights;
    private Map<Integer, Integer> flowCounters;

    public WFQScheduler() {
        flowQueues = new HashMap<>();
        flowWeights = new HashMap<>();
        flowCounters = new HashMap<>();

        // Default weights for flows (higher = more bandwidth)
        flowWeights.put(1, 4); // Flow 1: weight 4 (high priority)
        flowWeights.put(2, 3); // Flow 2: weight 3 (medium priority)
        flowWeights.put(3, 2); // Flow 3: weight 2 (low priority)
    }

    public void enqueue(Packet packet) {
        int flowId = packet.flowId;

        // Create queue for new flow
        if (!flowQueues.containsKey(flowId)) {
            flowQueues.put(flowId, new LinkedList<>());
            flowCounters.put(flowId, 0);

            // Default weight if not set
            if (!flowWeights.containsKey(flowId)) {
                flowWeights.put(flowId, 1);
            }
        }

        flowQueues.get(flowId).offer(packet);
        System.out.println("  [WFQ] Enqueued: " + packet + " to Flow" + flowId
                + " (Weight=" + flowWeights.get(flowId) + ")");
    }

    public Packet dequeue() {
        // Find flow with packets and weight remaining
        for (Map.Entry<Integer, Queue<Packet>> entry : flowQueues.entrySet()) {
            int flowId = entry.getKey();
            Queue<Packet> queue = entry.getValue();

            if (!queue.isEmpty() && flowCounters.get(flowId) < flowWeights.get(flowId)) {
                Packet packet = queue.poll();
                flowCounters.put(flowId, flowCounters.get(flowId) + 1);

                System.out.println("  [WFQ] Transmitted from Flow" + flowId + ": " + packet
                        + " (" + flowCounters.get(flowId) + "/"
                        + flowWeights.get(flowId) + ")");

                // Check if cycle complete
                if (isCycleComplete()) {
                    resetCounters();
                    System.out.println("  [WFQ] *** Cycle Complete - Resetting counters ***");
                }

                return packet;
            }
        }

        // If all weights exhausted, reset and try again
        resetCounters();
        return null;
    }

    private boolean isCycleComplete() {
        for (Map.Entry<Integer, Integer> entry : flowCounters.entrySet()) {
            int flowId = entry.getKey();
            if (!flowQueues.get(flowId).isEmpty() &&
                    entry.getValue() < flowWeights.get(flowId)) {
                return false;
            }
        }
        return true;
    }

    private void resetCounters() {
        for (int flowId : flowCounters.keySet()) {
            flowCounters.put(flowId, 0);
        }
    }

    public void displayQueue() {
        System.out.println("\n--- WFQ Status ---");

        for (Map.Entry<Integer, Queue<Packet>> entry : flowQueues.entrySet()) {
            int flowId = entry.getKey();
            if (!entry.getValue().isEmpty()) {
                System.out.println("  Flow" + flowId + " (Weight=" + flowWeights.get(flowId)
                        + ", Sent=" + flowCounters.get(flowId) + "): "
                        + entry.getValue());
            }
        }
    }
}

// Main Simulation
public class PacketSchedulingSimulation {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("========== PACKET SCHEDULING SIMULATION ==========\n");
        System.out.println("Choose Scheduling Algorithm:");
        System.out.println("1. FIFO (First In First Out)");
        System.out.println("2. Priority Queue");
        System.out.println("3. Fair Queuing");
        System.out.println("4. WFQ (Weighted Fair Queuing)");
        System.out.print("\nEnter choice (1-4): ");

        int choice = sc.nextInt();

        System.out.print("Enter number of packets to simulate: ");
        int numPackets = sc.nextInt();

        switch (choice) {
            case 1:
                simulateFIFO(numPackets);
                break;
            case 2:
                simulatePriorityQueue(numPackets);
                break;
            case 3:
                simulateFairQueue(numPackets);
                break;
            case 4:
                simulateWFQ(numPackets);
                break;
            default:
                System.out.println("Invalid choice!");
        }

        sc.close();
    }

    // FIFO Simulation
    private static void simulateFIFO(int numPackets) {
        System.out.println("\n===== FIFO SCHEDULING =====\n");
        FIFOScheduler scheduler = new FIFOScheduler();

        // Enqueue packets
        System.out.println("--- Enqueuing Packets ---");
        for (int i = 1; i <= numPackets; i++) {
            Packet p = new Packet(i, "Data" + i, i % 3 + 1, i % 3 + 1, 100 + i * 10);
            scheduler.enqueue(p);
        }

        scheduler.displayQueue();

        // Dequeue packets
        System.out.println("\n--- Transmitting Packets ---");
        while (true) {
            Packet p = scheduler.dequeue();
            if (p == null)
                break;
        }
    }

    // Priority Queue Simulation
    private static void simulatePriorityQueue(int numPackets) {
        System.out.println("\n===== PRIORITY QUEUE SCHEDULING =====\n");
        PriorityQueueScheduler scheduler = new PriorityQueueScheduler();

        System.out.println("--- Enqueuing Packets ---");
        for (int i = 1; i <= numPackets; i++) {
            Packet p = new Packet(i, "Data" + i, i % 3 + 1, i % 3 + 1, 100 + i * 10);
            scheduler.enqueue(p);
        }

        scheduler.displayQueue();

        System.out.println("\n--- Transmitting Packets (Priority Order) ---");
        while (true) {
            Packet p = scheduler.dequeue();
            if (p == null)
                break;
        }
    }

    // Fair Queue Simulation
    private static void simulateFairQueue(int numPackets) {
        System.out.println("\n===== FAIR QUEUING SCHEDULING =====\n");
        FairQueueScheduler scheduler = new FairQueueScheduler();

        System.out.println("--- Enqueuing Packets ---");
        for (int i = 1; i <= numPackets; i++) {
            Packet p = new Packet(i, "Data" + i, i % 3 + 1, i % 3 + 1, 100 + i * 10);
            scheduler.enqueue(p);
        }

        scheduler.displayQueue();

        System.out.println("\n--- Transmitting Packets (Round-Robin per Flow) ---");
        while (true) {
            Packet p = scheduler.dequeue();
            if (p == null)
                break;
        }
    }

    // WFQ Simulation
    private static void simulateWFQ(int numPackets) {
        System.out.println("\n===== WEIGHTED FAIR QUEUING =====\n");
        WFQScheduler scheduler = new WFQScheduler();

        System.out.println("--- Enqueuing Packets ---");
        for (int i = 1; i <= numPackets; i++) {
            Packet p = new Packet(i, "Data" + i, i % 3 + 1, i % 3 + 1, 100 + i * 10);
            scheduler.enqueue(p);
        }

        scheduler.displayQueue();

        System.out.println("\n--- Transmitting Packets (Weighted Round-Robin) ---");
        int transmitted = 0;
        while (transmitted < numPackets) {
            Packet p = scheduler.dequeue();
            if (p != null)
                transmitted++;
        }
    }
}
