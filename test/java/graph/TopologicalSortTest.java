// src/test/java/graph/TopologicalSortTest.java
package graph;

import graph.topo.KahnsTopologicalSort;
import graph.model.Graph;
import graph.metrics.Metrics;
import org.junit.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.*;

/**
 * Unit tests for topological sorting algorithms
 */
public class TopologicalSortTest {

    @Test
    public void testTopologicalSortLinearDAG() {
        // Create a linear DAG: 0 -> 1 -> 2 -> 3
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 3, 1);

        KahnsTopologicalSort topoSort = new KahnsTopologicalSort();
        List<Integer> result = topoSort.topologicalOrder(graph);

        // Valid topological order for linear DAG
        assertEquals(4, result.size());
        assertEquals(Integer.valueOf(0), result.get(0));
        assertEquals(Integer.valueOf(3), result.get(3));

        // Verify no edges go backwards in topological order
        assertValidTopologicalOrder(graph, result);
    }

    @Test
    public void testTopologicalSortComplexDAG() {
        // Create a more complex DAG
        //     0
        //    / \
        //   1   2
        //    \ /
        //     3
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 2);
        graph.addEdge(0, 2, 3);
        graph.addEdge(1, 3, 1);
        graph.addEdge(2, 3, 4);

        KahnsTopologicalSort topoSort = new KahnsTopologicalSort();
        List<Integer> result = topoSort.topologicalOrder(graph);

        assertEquals(4, result.size());
        assertValidTopologicalOrder(graph, result);

        // 0 must come before 1, 2, 3
        assertTrue(result.indexOf(0) < result.indexOf(1));
        assertTrue(result.indexOf(0) < result.indexOf(2));
        assertTrue(result.indexOf(0) < result.indexOf(3));
        // 1 and 2 must come before 3
        assertTrue(result.indexOf(1) < result.indexOf(3));
        assertTrue(result.indexOf(2) < result.indexOf(3));
    }

    @Test
    public void testTopologicalSortIndependentNodes() {
        // Graph with no edges - all nodes are independent
        Graph graph = new Graph(5, true);

        KahnsTopologicalSort topoSort = new KahnsTopologicalSort();
        List<Integer> result = topoSort.topologicalOrder(graph);

        assertEquals(5, result.size());
        // All nodes should be present in some order
        for (int i = 0; i < 5; i++) {
            assertTrue(result.contains(i));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTopologicalSortWithCycle() {
        // Graph with cycle: 0 -> 1 -> 2 -> 0
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1); // Creates cycle

        KahnsTopologicalSort topoSort = new KahnsTopologicalSort();
        topoSort.topologicalOrder(graph); // Should throw exception
    }

    @Test
    public void testTopologicalSortMetrics() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 3, 1);

        KahnsTopologicalSort topoSort = new KahnsTopologicalSort();
        topoSort.topologicalOrder(graph);

        Metrics metrics = topoSort.getMetrics();

        assertTrue("Should have positive execution time",
                metrics.getElapsedTime() > 0);
        assertTrue("Should have queue operations",
                metrics.getOperationCount("queue_push") > 0);
        assertTrue("Should have queue operations",
                metrics.getOperationCount("queue_pop") > 0);
    }

    @Test
    public void testTopologicalSortMultipleSources() {
        // DAG with multiple source nodes (no incoming edges)
        // 0    1
        //  \ / |
        //   2  |
        //   |  |
        //   3  4
        Graph graph = new Graph(5, true);
        graph.addEdge(0, 2, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(1, 4, 1);
        graph.addEdge(2, 3, 1);

        KahnsTopologicalSort topoSort = new KahnsTopologicalSort();
        List<Integer> result = topoSort.topologicalOrder(graph);

        assertEquals(5, result.size());
        assertValidTopologicalOrder(graph, result);

        // Sources (0,1) should come before their descendants
        assertTrue(result.indexOf(0) < result.indexOf(2));
        assertTrue(result.indexOf(0) < result.indexOf(3));
        assertTrue(result.indexOf(1) < result.indexOf(2));
        assertTrue(result.indexOf(1) < result.indexOf(3));
        assertTrue(result.indexOf(1) < result.indexOf(4));
    }

    /**
     * Helper method to validate that a topological order is correct
     * For every edge u->v, u should appear before v in the order
     */
    private void assertValidTopologicalOrder(Graph graph, List<Integer> order) {
        // Create position mapping for quick lookup
        int[] position = new int[graph.getVertices()];
        for (int i = 0; i < order.size(); i++) {
            position[order.get(i)] = i;
        }

        // Check all edges
        for (int u = 0; u < graph.getVertices(); u++) {
            for (Graph.Edge edge : graph.getNeighbors(u)) {
                int v = edge.target;
                assertTrue("Edge " + u + "->" + v + " violates topological order",
                        position[u] < position[v]);
            }
        }
    }

    @Test
    public void testTopologicalSortSingleNode() {
        Graph graph = new Graph(1, true);

        KahnsTopologicalSort topoSort = new KahnsTopologicalSort();
        List<Integer> result = topoSort.topologicalOrder(graph);

        assertEquals(1, result.size());
        assertEquals(Integer.valueOf(0), result.get(0));
    }

    @Test
    public void testTopologicalSortEmptyGraph() {
        Graph graph = new Graph(0, true);

        KahnsTopologicalSort topoSort = new KahnsTopologicalSort();
        List<Integer> result = topoSort.topologicalOrder(graph);

        assertTrue(result.isEmpty());
    }
}