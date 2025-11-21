package CN.DistanceVectorRouting;

import java.util.Scanner;

public class DistanceVectorRouting2 {

    // We use a large number for Infinity (but not Integer.MAX_VALUE to prevent
    // overflow when adding)
    static final int INF = 999;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // 1. GET NETWORK DETAILS
        System.out.println("--- Distance Vector Routing Simulation ---");
        System.out.print("Enter number of nodes (routers): ");
        int nodes = scanner.nextInt();

        // adjMatrix[i][j] represents the direct link cost (delay) from i to j
        int[][] costMatrix = new int[nodes][nodes];

        // routingTable[i][j] represents the shortest distance from i to j
        int[][] distance = new int[nodes][nodes];

        // nextHop[i][j] represents: to go from i to j, which node comes next?
        int[][] nextHop = new int[nodes][nodes];

        System.out.println("\nEnter the Cost Matrix (Delay between nodes):");
        System.out.println("(Enter 999 for no direct link, 0 for self)");

        for (int i = 0; i < nodes; i++) {
            for (int j = 0; j < nodes; j++) {
                costMatrix[i][j] = scanner.nextInt();

                // INITIALIZATION STEP
                // Each node initially only knows direct paths to neighbors
                distance[i][j] = costMatrix[i][j];

                // Initialize Next Hop
                if (costMatrix[i][j] != INF && i != j) {
                    nextHop[i][j] = j; // Direct neighbor is the next hop
                } else {
                    nextHop[i][j] = -1; // No path yet
                }
            }
        }

        // 2. THE ALGORITHM (Bellman-Ford Relaxation)
        // We repeat until the network converges (no more changes)
        int iterations = 0;
        boolean updated;

        do {
            updated = false;
            iterations++;

            // For every node (Source 'i')
            for (int i = 0; i < nodes; i++) {
                // For every destination (Dest 'j')
                for (int j = 0; j < nodes; j++) {
                    // Check paths via every other node (Intermediate 'k')
                    // Formula: D(i,j) = min( Cost(i,k) + D(k,j) )
                    for (int k = 0; k < nodes; k++) {

                        // Calculate potential new cost:
                        // Cost from i->k (Direct) + Cost from k->j (Known Distance)
                        int newDist = costMatrix[i][k] + distance[k][j];

                        if (newDist < distance[i][j]) {
                            distance[i][j] = newDist;
                            nextHop[i][j] = k; // Update next hop to 'k' because it gave a better path
                            updated = true; // A change happened, so we must check again
                        }
                    }
                }
            }
        } while (updated); // Repeat until stable

        // 3. PRINT FINAL ROUTING TABLES
        System.out.println("\nNetwork Converged in " + iterations + " iterations.");

        for (int i = 0; i < nodes; i++) {
            System.out.println("\n========================================");
            System.out.println("   ROUTING TABLE FOR NODE " + (char) ('A' + i));
            System.out.println("========================================");
            System.out.println("Dest\tCost\tNext Hop");
            System.out.println("----\t----\t--------");

            for (int j = 0; j < nodes; j++) {
                if (i == j)
                    continue; // Don't print path to self

                String destName = String.valueOf((char) ('A' + j));
                String hopName = (nextHop[i][j] == -1) ? "-" : String.valueOf((char) ('A' + nextHop[i][j]));
                String costStr = (distance[i][j] == INF) ? "INF" : String.valueOf(distance[i][j]);

                System.out.println(" " + destName + "\t " + costStr + "\t  " + hopName);
            }
        }

        scanner.close();
    }
}