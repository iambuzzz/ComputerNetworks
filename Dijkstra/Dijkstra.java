package CN.Dijkstra;

import java.util.*;

public class Dijkstra {

    // A utility function to find the vertex with minimum distance value,
    // from the set of vertices not yet included in the shortest path tree
    private static int findMinDistanceNode(int[] dist, boolean[] visited, int V) {
        int min = Integer.MAX_VALUE;
        int min_index = -1;

        for (int v = 0; v < V; v++) {
            if (!visited[v] && dist[v] <= min) {
                min = dist[v];
                min_index = v;
            }
        }
        return min_index;
    }

    // Recursive function to print the path from Source to Current Node
    private static void printPath(int currentVertex, int[] parents) {
        // Base Case: If currentVertex has no parent (it is the source), return
        if (currentVertex == -1) {
            return;
        }

        // Recursive Step: Print path for the parent first
        printPath(parents[currentVertex], parents);

        // Then print current node
        System.out.print(currentVertex + " ");
    }

    public static void dijkstra(int[][] graph, int src, int V) {
        int[] dist = new int[V]; // Holds shortest distance from src to i
        boolean[] visited = new boolean[V]; // true if vertex i is processed
        int[] parents = new int[V]; // Stores the Predecessor node to print path

        // 1. Initialize
        for (int i = 0; i < V; i++) {
            dist[i] = Integer.MAX_VALUE;
            visited[i] = false;
            parents[i] = -1; // -1 means no parent yet
        }

        // Distance of source vertex from itself is always 0
        dist[src] = 0;

        // 2. Algorithm Loop
        for (int count = 0; count < V - 1; count++) {
            // Pick the minimum distance vertex from the set of unvisited vertices
            int u = findMinDistanceNode(dist, visited, V);

            // If we can't reach any more nodes (graph disconnected), break
            if (u == -1)
                break;

            // Mark the picked vertex as processed
            visited[u] = true;

            // Update dist value of the adjacent vertices of the picked vertex
            for (int v = 0; v < V; v++) {
                // Update dist[v] only if:
                // 1. It is not visited
                // 2. There is an edge from u to v (graph[u][v] != 0)
                // 3. Total weight is smaller than current value of dist[v]
                if (!visited[v] && graph[u][v] != 0 &&
                        dist[u] != Integer.MAX_VALUE &&
                        dist[u] + graph[u][v] < dist[v]) {

                    dist[v] = dist[u] + graph[u][v];
                    parents[v] = u; // KEY STEP: Store where we came from
                }
            }
        }

        // 3. Print Solution
        System.out.println("\n--- Shortest Paths from Source Node " + src + " ---");
        System.out.println("Dest\tCost\tPath");
        System.out.println("----\t----\t----");

        for (int i = 0; i < V; i++) {
            if (i != src) {
                System.out.print(" " + i + " \t " + dist[i] + "\t");

                // Print the path logic
                System.out.print(src + " "); // Start with Source
                printPath(i, parents); // Recursively print rest
                System.out.println();
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter Number of Vertices: ");
        int V = scanner.nextInt();

        int[][] graph = new int[V][V];

        System.out.println("Enter the Adjacency Matrix (put 0 for no edge):");
        for (int i = 0; i < V; i++) {
            for (int j = 0; j < V; j++) {
                graph[i][j] = scanner.nextInt();
            }
        }

        System.out.print("Enter Source Vertex (0 to " + (V - 1) + "): ");
        int src = scanner.nextInt();

        dijkstra(graph, src, V);

        scanner.close();
    }
}