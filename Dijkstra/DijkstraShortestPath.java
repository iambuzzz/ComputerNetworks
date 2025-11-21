package CN.Dijkstra;

import java.util.*;

public class DijkstraShortestPath {
    // Utility method to print shortest path from source to destination
    private static void printPath(int[] parent, int dest) {
        List<Integer> path = new ArrayList<>();
        int curr = dest;
        while (curr != -1) {
            path.add(curr);
            curr = parent[curr];
        }
        Collections.reverse(path);
        System.out.print("Path: ");
        for (int node : path) {
            System.out.print(node + " ");
        }
    }

    public static void main(String[] args) {
        // ----- Example Graph -----
        // 0---1---2
        // | | /
        // 4---3
        // Adjacency matrix: 0 means no direct edge, otherwise cost
        int[][] graph = {
                // 0 1 2 3 4
                { 0, 2, 0, 1, 4 }, // 0
                { 2, 0, 3, 3, 0 }, // 1
                { 0, 3, 0, 5, 0 }, // 2
                { 1, 3, 5, 0, 1 }, // 3
                { 4, 0, 0, 1, 0 } // 4
        };
        int n = graph.length;

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter source node (0-" + (n - 1) + "): ");
        int src = sc.nextInt();
        System.out.print("Enter destination node (0-" + (n - 1) + ", or -1 for all): ");
        int dest = sc.nextInt();

        int[] dist = new int[n];
        boolean[] visited = new boolean[n];
        int[] parent = new int[n];

        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);
        dist[src] = 0;

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        pq.add(new int[] { src, 0 });

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int u = curr[0];

            if (visited[u])
                continue;
            visited[u] = true;

            for (int v = 0; v < n; v++) {
                if (graph[u][v] != 0 && !visited[v]) {
                    int newDist = dist[u] + graph[u][v];
                    if (newDist < dist[v]) {
                        dist[v] = newDist;
                        parent[v] = u;
                        pq.add(new int[] { v, newDist });
                    }
                }
            }
        }

        if (dest >= 0 && dest < n) {
            System.out.println("Shortest path from " + src + " to " + dest + ":");
            System.out.println("Cost = " + dist[dest]);
            printPath(parent, dest);
        } else {
            System.out.println("\nShortest paths from node " + src + ":");
            for (int i = 0; i < n; i++) {
                System.out.print("To " + i + ": cost=" + dist[i] + " ");
                printPath(parent, i);
                System.out.println();
            }
        }
        sc.close();
    }
}
