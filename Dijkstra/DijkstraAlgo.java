package CN.Dijkstra;


public class DijkstraAlgo {

    // Function to find the vertex with minimum distance value
    static int minDistance(int dist[], Boolean visited[], int V) {
        int min = Integer.MAX_VALUE, min_index = -1;

        for (int v = 0; v < V; v++)
            if (!visited[v] && dist[v] <= min) {
                min = dist[v];
                min_index = v;
            }
        return min_index;
    }

    public static void main(String[] args) {
        // Example Graph (Adjacency Matrix)
        // 0 is Source
        int graph[][] = new int[][] {
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

        int V = 9; // Number of vertices
        int dist[] = new int[V];
        Boolean visited[] = new Boolean[V];

        // Initialize all distances as INFINITE and visited[] as false
        for (int i = 0; i < V; i++) {
            dist[i] = Integer.MAX_VALUE;
            visited[i] = false;
        }

        // Distance of source vertex from itself is always 0
        dist[0] = 0;

        System.out.println("--- Dijkstra Algorithm (Source Node: 0) ---");

        // Find shortest path for all vertices
        for (int count = 0; count < V - 1; count++) {
            // Pick the minimum distance vertex from the set of unvisited vertices
            int u = minDistance(dist, visited, V);
            visited[u] = true; // Mark as processed

            // Update dist value of the adjacent vertices of the picked vertex
            for (int v = 0; v < V; v++) {
                // Update dist[v] only if:
                // 1. Not visited
                // 2. There is an edge (graph[u][v] != 0)
                // 3. Total weight is smaller than current value of dist[v]
                if (!visited[v] && graph[u][v] != 0 &&
                        dist[u] != Integer.MAX_VALUE &&
                        dist[u] + graph[u][v] < dist[v]) {

                    dist[v] = dist[u] + graph[u][v];
                    System.out.println("Updated distance to Node " + v + " -> " + dist[v]);
                }
            }
        }

        // Print Solution
        System.out.println("\nVertex \t Distance from Source");
        for (int i = 0; i < V; i++)
            System.out.println(i + " \t " + dist[i]);
    }
}