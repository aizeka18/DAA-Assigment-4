package graph.topo;

import graph.model.Graph;
import graph.metrics.Metrics;
import java.util.List;

public interface TopologicalSort {
    List<Integer> topologicalOrder(Graph graph);
    Metrics getMetrics();
}