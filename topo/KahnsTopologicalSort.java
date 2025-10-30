package graph.topo;

import graph.model.Graph;
import graph.metrics.Metrics;
import graph.metrics.OperationCounter;
import java.util.*;

public class KahnsTopologicalSort implements TopologicalSort {
    private Metrics metrics;

    public KahnsTopologicalSort() {
        this.metrics = new OperationCounter();
    }

    @Override
    public List<Integer> topologicalOrder(Graph graph) {
        metrics.reset(); // Reset operation counts only

        int n = graph.getVertices();
        int[] inDegree = new int[n];

        // Calculate in-degrees
        for (int u = 0; u < n; u++) {
            for (Graph.Edge edge : graph.getNeighbors(u)) {
                inDegree[edge.target]++;
                metrics.incrementOperation("in_degree_calc");
            }
        }

        // Initialize queue with nodes having 0 in-degree
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                queue.offer(i);
                metrics.incrementOperation("queue_push");
            }
        }

        List<Integer> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            int node = queue.poll();
            metrics.incrementOperation("queue_pop");
            result.add(node);

            for (Graph.Edge edge : graph.getNeighbors(node)) {
                inDegree[edge.target]--;
                metrics.incrementOperation("in_degree_decrement");

                if (inDegree[edge.target] == 0) {
                    queue.offer(edge.target);
                    metrics.incrementOperation("queue_push");
                }
            }
        }

        // Check for cycles
        if (result.size() != n) {
            throw new IllegalArgumentException("Graph has cycles - topological sort not possible");
        }

        return result;
    }

    @Override
    public Metrics getMetrics() {
        return metrics;
    }
}