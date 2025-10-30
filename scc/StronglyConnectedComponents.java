package graph.scc;

import graph.model.Graph;
import graph.metrics.Metrics;
import java.util.*;

public interface StronglyConnectedComponents {
    List<List<Integer>> findSCCs(Graph graph);
    Graph buildCondensationGraph(Graph graph, List<List<Integer>> sccs);
    Metrics getMetrics();
}