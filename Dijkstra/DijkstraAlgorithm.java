package CN.Dijkstra;

import java.util.*;

public class DijkstraAlgorithm {

    static class Node implements Comparable<Node> {
        int vertex;
        int distance;

        Node(int vertex, int distance) {
            this.vertex = vertex;
            this.distance = distance;
        }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.distance, other.distance);
        }
    }

    public static void dijkstra(int[][] graph, int source) {
        int V = graph.length;
        int[] dist = new int[V];
        boolean[] visited = new boolean[V];
        int[] parent = new int[V];

        // Initialize distances
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);
        dist[source] = 0;

        PriorityQueue<Node> pq = new PriorityQueue<>();
        pq.add(new Node(source, 0));

        System.out.println("===== DIJKSTRA'S ALGORITHM =====\n");
        System.out.println("Source Node: " + source);

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            int u = current.vertex;

            if (visited[u])
                continue;
            visited[u] = true;

            System.out.println("\nVisiting Node " + u + " (Distance: " + dist[u] + ")");

            // Check all adjacent vertices
            for (int v = 0; v < V; v++) {
                if (graph[u][v] != 0 && !visited[v]) {
                    int newDist = dist[u] + graph[u][v];

                    if (newDist < dist[v]) {
                        dist[v] = newDist;
                        parent[v] = u;
                        pq.add(new Node(v, newDist));
                        System.out.println("  Updated Node " + v + ": distance = " + newDist);
                    }
                }
            }
        }

        // Print shortest paths
        System.out.println("\n--- SHORTEST PATHS FROM NODE " + source + " ---");
        for (int i = 0; i < V; i++) {
            if (i != source) {
                System.out.print("To Node " + i + ": ");
                if (dist[i] == Integer.MAX_VALUE) {
                    System.out.println("No path");
                } else {
                    System.out.print("Distance = " + dist[i] + ", Path: ");
                    printPath(parent, i);
                    System.out.println();
                }
            }
        }
    }

    private static void printPath(int[] parent, int node) {
        if (parent[node] == -1) {
            System.out.print(node);
            return;
        }
        printPath(parent, parent[node]);
        System.out.print(" -> " + node);
    }

    public static void main(String[] args) {
        // Graph representation (adjacency matrix)
        int[][] graph = {
                { 0, 4, 0, 0, 0, 0, 0, 8, 0 },
                { 4, 0, 8, 0, 0, 0, 0, 11, 0 },
                { 0, 8, 0, 7, 0, 4, 0, 0, 2 },
                { 0, 0, 7, 0, 9, 14, 0, 0, 0 },
                { 0, 0, 0, 9, 0, 10, 0, 0, 0 },
                { 0, 0, 4, 14, 10, 0, 2, 0, 0 },
                { 0, 0, 0, 0, 0, 2, 0, 1, 6 },
                { 8, 11, 0, 0, 0, 0, 1, 0, 7 },
                { 0, 0, 2, 0, 0, 0, 6, 7, 0 }
        };

        dijkstra(graph, 0);
    }
}
