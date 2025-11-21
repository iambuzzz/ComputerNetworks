package CN.DistanceVectorRouting;

import java.util.*;

public class DistanceVectorRouting {

    static class Router {
        int id;
        Map<Integer, Integer> routingTable; // destination -> distance
        Map<Integer, Integer> nextHop; // destination -> next hop

        Router(int id) {
            this.id = id;
            this.routingTable = new HashMap<>();
            this.nextHop = new HashMap<>();
        }

        void initializeTable(int[][] graph, int numRouters) {
            // Initialize with direct neighbors
            for (int i = 0; i < numRouters; i++) {
                if (graph[id][i] != 0 && i != id) {
                    routingTable.put(i, graph[id][i]);
                    nextHop.put(i, i);
                } else if (i == id) {
                    routingTable.put(i, 0);
                    nextHop.put(i, i);
                } else {
                    routingTable.put(i, Integer.MAX_VALUE);
                }
            }
        }

        boolean updateTable(Router neighbor, int[][] graph) {
            boolean updated = false;
            int costToNeighbor = graph[id][neighbor.id];

            for (Map.Entry<Integer, Integer> entry : neighbor.routingTable.entrySet()) {
                int dest = entry.getKey();
                int neighborDist = entry.getValue();

                if (neighborDist != Integer.MAX_VALUE) {
                    int newDist = costToNeighbor + neighborDist;

                    if (newDist < routingTable.get(dest)) {
                        routingTable.put(dest, newDist);
                        nextHop.put(dest, neighbor.id);
                        updated = true;
                    }
                }
            }

            return updated;
        }

        void printTable() {
            System.out.println("\n--- Router " + id + " Routing Table ---");
            System.out.println("Dest\tDistance\tNext Hop");
            for (int dest : routingTable.keySet()) {
                if (dest != id) {
                    int dist = routingTable.get(dest);
                    String distStr = (dist == Integer.MAX_VALUE) ? "∞" : String.valueOf(dist);
                    System.out.println(dest + "\t" + distStr + "\t\t" + nextHop.get(dest));
                }
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("===== DISTANCE VECTOR ROUTING =====\n");

        int numRouters = 4;

        // Graph: 0-1-2-3 network
        int[][] graph = {
                { 0, 1, 0, 0 },
                { 1, 0, 2, 0 },
                { 0, 2, 0, 3 },
                { 0, 0, 3, 0 }
        };

        // Create routers
        Router[] routers = new Router[numRouters];
        for (int i = 0; i < numRouters; i++) {
            routers[i] = new Router(i);
            routers[i].initializeTable(graph, numRouters);
        }

        System.out.println("Initial Routing Tables:");
        for (Router r : routers) {
            r.printTable();
        }

        // Iterate until convergence
        boolean updated = true;
        int iteration = 1;

        while (updated) {
            updated = false;
            System.out.println("\n\n========== ITERATION " + iteration + " ==========");

            for (int i = 0; i < numRouters; i++) {
                for (int j = 0; j < numRouters; j++) {
                    if (graph[i][j] != 0 && i != j) {
                        if (routers[i].updateTable(routers[j], graph)) {
                            updated = true;
                        }
                    }
                }
            }

            if (updated) {
                for (Router r : routers) {
                    r.printTable();
                }
            }

            iteration++;
        }

        System.out.println("\n\n✅ CONVERGED - Routing tables are stable!");
    }
}
