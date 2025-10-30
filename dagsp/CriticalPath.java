package graph.dagsp;

import graph.model.Graph;
import graph.metrics.Metrics;
import graph.metrics.OperationCounter;
import java.util.*;

public class CriticalPath {
    private Metrics metrics;

    public CriticalPath() {
        this.metrics = new OperationCounter();
    }

    public CriticalPathResult findCriticalPath(Graph graph, List<Integer> topologicalOrder) {
        metrics.reset(); // Reset operation counts only

        int n = graph.getVertices();
        int[] longest = new int[n];
        int[] predecessor = new int[n];
        Arrays.fill(predecessor, -1);

        // Initialize distances
        for (int i = 0; i < n; i++) {
            longest[i] = 0;
        }

        // Find longest paths
        for (int u : topologicalOrder) {
            metrics.incrementOperation("topo_processing");

            for (Graph.Edge edge : graph.getNeighbors(u)) {
                int v = edge.target;
                int newLength = longest[u] + edge.weight;
                metrics.incrementOperation("relaxation");

                if (newLength > longest[v]) {
                    longest[v] = newLength;
                    predecessor[v] = u;
                    metrics.incrementOperation("distance_update");
                }
            }
        }

        // Find the maximum distance and corresponding vertex
        int maxDist = 0;
        int endVertex = 0;
        for (int i = 0; i < n; i++) {
            if (longest[i] > maxDist) {
                maxDist = longest[i];
                endVertex = i;
            }
        }

        // Reconstruct critical path
        List<Integer> path = reconstructPath(predecessor, endVertex);

        return new CriticalPathResult(path, maxDist, longest);
    }

    private List<Integer> reconstructPath(int[] predecessor, int endVertex) {
        List<Integer> path = new ArrayList<>();
        int current = endVertex;

        while (current != -1) {
            path.add(0, current);
            current = predecessor[current];
        }

        return path;
    }

    public Metrics getMetrics() {
        return metrics;
    }

    public static class CriticalPathResult {
        public final List<Integer> path;
        public final int length;
        public final int[] longestPaths;

        public CriticalPathResult(List<Integer> path, int length, int[] longestPaths) {
            this.path = path;
            this.length = length;
            this.longestPaths = longestPaths;
        }
    }
}