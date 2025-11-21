package CN.DistanceVectorRouting;

import java.util.*;

public class DistanceVectorRouting3 {
    static final int INF = Integer.MAX_VALUE;

    static class Router {
        int id;
        int[] distanceVector; // Shortest known distance to each node
        int[] nextHop; // Next hop node for each destination

        Router(int id, int nodeCount) {
            this.id = id;
            distanceVector = new int[nodeCount];
            nextHop = new int[nodeCount];
            Arrays.fill(distanceVector, INF);
            Arrays.fill(nextHop, -1);
            distanceVector[id] = 0;
            nextHop[id] = id;
        }

        void updateFromNeighbors(List<Router> neighbors, int[][] graph) {
            for (Router neighbor : neighbors) {
                int toNeighbor = graph[id][neighbor.id];
                if (toNeighbor == INF)
                    continue; // not a neighbor
                for (int dest = 0; dest < graph.length; dest++) {
                    // Avoid overflow with INF
                    if (neighbor.distanceVector[dest] != INF
                            && toNeighbor + neighbor.distanceVector[dest] < distanceVector[dest]) {
                        distanceVector[dest] = toNeighbor + neighbor.distanceVector[dest];
                        nextHop[dest] = neighbor.id;
                    }
                }
            }
        }

        void printTable() {
            System.out.println("Routing table for Node " + id + ":");
            System.out.println("Dest\tDist\tNextHop");
            for (int i = 0; i < distanceVector.length; i++) {
                String distStr = distanceVector[i] == INF ? "∞" : "" + distanceVector[i];
                String nextStr = nextHop[i] == -1 ? "-" : "" + nextHop[i];
                System.out.println(i + "\t" + distStr + "\t" + nextStr);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        // Graph: adjacency matrix (replace these delays with your own)
        // INF means no direct link
        int[][] graph = {
                { 0, 2, 5, INF },
                { 2, 0, 3, 2 },
                { 5, 3, 0, 3 },
                { INF, 2, 3, 0 }
        };
        int nodeCount = graph.length;
        List<Router> routers = new ArrayList<>();

        for (int i = 0; i < nodeCount; i++)
            routers.add(new Router(i, nodeCount));

        // Initially routing tables with direct distances
        System.out.println("Initial Routing Tables:");
        for (Router router : routers)
            router.printTable();

        // Converge
        boolean changed = true;
        int iter = 1;
        while (changed) {
            changed = false;
            System.out.println("Iteration " + iter++);
            for (int i = 0; i < nodeCount; i++) {
                // Neighbors (direct link only)
                List<Router> neighbors = new ArrayList<>();
                for (int j = 0; j < nodeCount; j++)
                    if (i != j && graph[i][j] != INF)
                        neighbors.add(routers.get(j));

                // Copy for change detection
                int[] oldVector = routers.get(i).distanceVector.clone();

                routers.get(i).updateFromNeighbors(neighbors, graph);
                // Check if any distance changed
                if (!Arrays.equals(oldVector, routers.get(i).distanceVector))
                    changed = true;
            }
            for (Router router : routers)
                router.printTable();
        }
        System.out.println("Converged.");
    }
}
