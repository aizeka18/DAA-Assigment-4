package graph.dagsp;

import graph.model.Graph;
import graph.metrics.Metrics;
import graph.metrics.OperationCounter;
import java.util.*;

public class DAGShortestPath {
    private Metrics metrics;

    public DAGShortestPath() {
        this.metrics = new OperationCounter();
    }

    public int[] shortestPaths(Graph graph, int source, List<Integer> topologicalOrder) {
        metrics.reset(); // Reset operation counts only

        int n = graph.getVertices();
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[source] = 0;

        // Find source position in topological order
        int sourceIndex = topologicalOrder.indexOf(source);
        if (sourceIndex == -1) {
            throw new IllegalArgumentException("Source not found in topological order");
        }

        // Process vertices in topological order
        for (int i = sourceIndex; i < n; i++) {
            int u = topologicalOrder.get(i);
            metrics.incrementOperation("topo_processing");

            if (dist[u] != Integer.MAX_VALUE) {
                for (Graph.Edge edge : graph.getNeighbors(u)) {
                    int v = edge.target;
                    int newDist = dist[u] + edge.weight;
                    metrics.incrementOperation("relaxation");

                    if (newDist < dist[v]) {
                        dist[v] = newDist;
                        metrics.incrementOperation("distance_update");
                    }
                }
            }
        }

        return dist;
    }

    public List<Integer> reconstructPath(int[] dist, int target, Graph graph, List<Integer> topologicalOrder) {
        if (dist[target] == Integer.MAX_VALUE) {
            return Collections.emptyList();
        }

        List<Integer> path = new ArrayList<>();
        path.add(target);

        int current = target;
        int currentDist = dist[target];

        // Reconstruct path backwards
        while (currentDist > 0) {
            for (int u : topologicalOrder) {
                if (dist[u] < Integer.MAX_VALUE) {
                    for (Graph.Edge edge : graph.getNeighbors(u)) {
                        if (edge.target == current && dist[u] + edge.weight == currentDist) {
                            path.add(0, u);
                            current = u;
                            currentDist = dist[u];
                            break;
                        }
                    }
                }
                if (currentDist == 0) break;
            }
        }

        return path;
    }

    public Metrics getMetrics() {
        return metrics;
    }
}