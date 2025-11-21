package CN.DistanceVectorRouting;

import java.util.*;

public class DistanceVector {
    static class Edge {
        int src, dest, weight;

        Edge(int s, int d, int w) {
            src = s;
            dest = d;
            weight = w;
        }
    }

    public static void main(String[] args) {
        int nodes = 5;
        int INF = 999; // Infinity representation

        // Initialize Graph (Source, Dest, Cost)
        List<Edge> edges = new ArrayList<>();
        edges.add(new Edge(0, 1, 2));
        edges.add(new Edge(0, 2, 4));
        edges.add(new Edge(1, 2, 1)); // Path 0->1->2 (Cost 3) is better than 0->2 (Cost 4)
        edges.add(new Edge(1, 3, 7));
        edges.add(new Edge(2, 4, 3));
        edges.add(new Edge(3, 4, 1));

        System.out.println("--- Distance Vector Routing (Bellman Ford) ---");
        System.out.println("Calculating table for Source Node 0...");

        int[] dist = new int[nodes];
        Arrays.fill(dist, INF);
        dist[0] = 0; // Distance to self is 0

        // Relax edges |V| - 1 times
        for (int i = 1; i < nodes; i++) {
            boolean updated = false;
            for (Edge e : edges) {
                if (dist[e.src] != INF && dist[e.src] + e.weight < dist[e.dest]) {
                    dist[e.dest] = dist[e.src] + e.weight;
                    System.out.println("Iteration " + i + ": Update Node " + e.dest + " distance to " + dist[e.dest]
                            + " (via " + e.src + ")");
                    updated = true;
                }
            }
            if (!updated)
                break; // Optimization: Stop if no changes
        }

        // Print Final Table
        System.out.println("\nFinal Routing Table for Node 0:");
        System.out.println("Dest \t Cost");
        for (int i = 0; i < nodes; i++) {
            System.out.println(i + " \t " + dist[i]);
        }
    }
}