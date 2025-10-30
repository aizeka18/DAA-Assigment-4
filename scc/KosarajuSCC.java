package graph.scc;

import graph.model.Graph;
import graph.metrics.Metrics;
import graph.metrics.OperationCounter;
import java.util.*;

public class KosarajuSCC implements StronglyConnectedComponents {
    private Metrics metrics;

    public KosarajuSCC() {
        this.metrics = new OperationCounter();
    }

    @Override
    public List<List<Integer>> findSCCs(Graph graph) {
        metrics.reset(); // Reset operation counts only

        int n = graph.getVertices();
        boolean[] visited = new boolean[n];
        Stack<Integer> stack = new Stack<>();

        // First DFS pass to fill stack
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                dfsFirstPass(graph, i, visited, stack);
            }
        }

        // Create reversed graph
        Graph reversedGraph = reverseGraph(graph);

        // Second DFS pass on reversed graph
        Arrays.fill(visited, false);
        List<List<Integer>> sccs = new ArrayList<>();

        while (!stack.isEmpty()) {
            int node = stack.pop();
            metrics.incrementOperation("stack_pop");

            if (!visited[node]) {
                List<Integer> scc = new ArrayList<>();
                dfsSecondPass(reversedGraph, node, visited, scc);
                sccs.add(scc);
            }
        }

        return sccs;
    }

    private void dfsFirstPass(Graph graph, int node, boolean[] visited, Stack<Integer> stack) {
        visited[node] = true;
        metrics.incrementOperation("dfs_visit");

        for (Graph.Edge edge : graph.getNeighbors(node)) {
            metrics.incrementOperation("edge_traversal");
            if (!visited[edge.target]) {
                dfsFirstPass(graph, edge.target, visited, stack);
            }
        }
        stack.push(node);
        metrics.incrementOperation("stack_push");
    }

    private void dfsSecondPass(Graph graph, int node, boolean[] visited, List<Integer> scc) {
        visited[node] = true;
        scc.add(node);
        metrics.incrementOperation("dfs_visit");

        for (Graph.Edge edge : graph.getNeighbors(node)) {
            metrics.incrementOperation("edge_traversal");
            if (!visited[edge.target]) {
                dfsSecondPass(graph, edge.target, visited, scc);
            }
        }
    }

    private Graph reverseGraph(Graph original) {
        Graph reversed = new Graph(original.getVertices(), true);

        for (int u = 0; u < original.getVertices(); u++) {
            for (Graph.Edge edge : original.getNeighbors(u)) {
                reversed.addEdge(edge.target, u, edge.weight);
                metrics.incrementOperation("graph_reversal");
            }
        }
        return reversed;
    }

    @Override
    public Graph buildCondensationGraph(Graph graph, List<List<Integer>> sccs) {
        int n = sccs.size();
        Graph condensation = new Graph(n, true);

        // Map each original vertex to its SCC index
        int[] sccIndex = new int[graph.getVertices()];
        for (int i = 0; i < sccs.size(); i++) {
            for (int node : sccs.get(i)) {
                sccIndex[node] = i;
            }
        }

        // Add edges between different SCCs
        Set<String> addedEdges = new HashSet<>();
        for (int u = 0; u < graph.getVertices(); u++) {
            for (Graph.Edge edge : graph.getNeighbors(u)) {
                int v = edge.target;
                int sccU = sccIndex[u];
                int sccV = sccIndex[v];

                if (sccU != sccV) {
                    String edgeKey = sccU + "->" + sccV;
                    if (!addedEdges.contains(edgeKey)) {
                        condensation.addEdge(sccU, sccV, edge.weight);
                        addedEdges.add(edgeKey);
                    }
                }
            }
        }

        return condensation;
    }

    @Override
    public Metrics getMetrics() {
        return metrics;
    }
}