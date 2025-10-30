
package graph;

import graph.dagsp.DAGShortestPath;
import graph.dagsp.CriticalPath;
import graph.model.Graph;
import graph.metrics.Metrics;
import org.junit.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.*;


public class DAGShortestPathTest {

    @Test
    public void testShortestPathLinearGraph() {
        // Linear graph: 0->1->2->3 with weights 1,2,3
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 2);
        graph.addEdge(2, 3, 3);

        List<Integer> topoOrder = Arrays.asList(0, 1, 2, 3);
        DAGShortestPath shortestPath = new DAGShortestPath();
        int[] distances = shortestPath.shortestPaths(graph, 0, topoOrder);

        assertEquals(0, distances[0]);
        assertEquals(1, distances[1]);
        assertEquals(3, distances[2]);
        assertEquals(6, distances[3]);
    }

    @Test
    public void testShortestPathMultiplePaths() {
        // Graph with multiple paths from source
        //     0
        //    / \
        //   1   2
        //    \ /
        //     3
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 1);  // Path 1: 0-1-3 = 1+4=5
        graph.addEdge(0, 2, 3);  // Path 2: 0-2-3 = 3+1=4
        graph.addEdge(1, 3, 4);
        graph.addEdge(2, 3, 1);

        List<Integer> topoOrder = Arrays.asList(0, 1, 2, 3);
        DAGShortestPath shortestPath = new DAGShortestPath();
        int[] distances = shortestPath.shortestPaths(graph, 0, topoOrder);

        assertEquals(0, distances[0]);
        assertEquals(1, distances[1]);
        assertEquals(3, distances[2]);
        assertEquals(4, distances[3]); // Should take path 0-2-3 (cost 4)
    }

    @Test
    public void testShortestPathUnreachableNodes() {
        Graph graph = new Graph(5, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 2);
        // Nodes 3 and 4 are unreachable from 0

        List<Integer> topoOrder = Arrays.asList(0, 1, 2, 3, 4);
        DAGShortestPath shortestPath = new DAGShortestPath();
        int[] distances = shortestPath.shortestPaths(graph, 0, topoOrder);

        assertEquals(0, distances[0]);
        assertEquals(1, distances[1]);
        assertEquals(3, distances[2]);
        assertEquals(Integer.MAX_VALUE, distances[3]);
        assertEquals(Integer.MAX_VALUE, distances[4]);
    }

    @Test
    public void testPathReconstruction() {
        Graph graph = new Graph(5, true);
        graph.addEdge(0, 1, 2);
        graph.addEdge(1, 2, 3);
        graph.addEdge(0, 3, 1);
        graph.addEdge(3, 2, 1); // Shorter path: 0-3-2 (cost 2) vs 0-1-2 (cost 5)
        graph.addEdge(2, 4, 2);

        List<Integer> topoOrder = Arrays.asList(0, 1, 3, 2, 4);
        DAGShortestPath shortestPath = new DAGShortestPath();
        int[] distances = shortestPath.shortestPaths(graph, 0, topoOrder);

        List<Integer> path = shortestPath.reconstructPath(distances, 4, graph, topoOrder);

        // Expected path: 0->3->2->4
        assertEquals(4, path.size());
        assertEquals(Integer.valueOf(0), path.get(0));
        assertEquals(Integer.valueOf(3), path.get(1));
        assertEquals(Integer.valueOf(2), path.get(2));
        assertEquals(Integer.valueOf(4), path.get(3));

        // Verify path cost
        int calculatedCost = distances[4];
        assertEquals(4, calculatedCost); // 0-3(1) + 3-2(1) + 2-4(2) = 4
    }

    @Test
    public void testCriticalPathLinear() {
        // Linear graph: 0->1->2->3 with weights 1,2,3
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 2);
        graph.addEdge(2, 3, 3);

        List<Integer> topoOrder = Arrays.asList(0, 1, 2, 3);
        CriticalPath criticalPath = new CriticalPath();
        CriticalPath.CriticalPathResult result = criticalPath.findCriticalPath(graph, topoOrder);

        assertEquals(6, result.length); // 1+2+3=6
        assertEquals(Arrays.asList(0, 1, 2, 3), result.path);
    }

    @Test
    public void testCriticalPathMultiplePaths() {
        // Graph with multiple paths
        //     0
        //    / \
        //   1   2
        //    \ /
        //     3
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 1);  // Path 1: 0-1-3 = 1+4=5
        graph.addEdge(0, 2, 3);  // Path 2: 0-2-3 = 3+1=4
        graph.addEdge(1, 3, 4);
        graph.addEdge(2, 3, 1);

        List<Integer> topoOrder = Arrays.asList(0, 1, 2, 3);
        CriticalPath criticalPath = new CriticalPath();
        CriticalPath.CriticalPathResult result = criticalPath.findCriticalPath(graph, topoOrder);

        assertEquals(5, result.length); // Should take longer path 0-1-3
        assertEquals(Arrays.asList(0, 1, 3), result.path);
    }

    @Test
    public void testCriticalPathComplexDAG() {
        // More complex DAG for critical path testing
        //     0
        //    / \
        //   1   2
        //  / \ / \
        // 3   4   5
        //  \ / \ /
        //   6   7
        Graph graph = new Graph(8, true);
        graph.addEdge(0, 1, 2);
        graph.addEdge(0, 2, 3);
        graph.addEdge(1, 3, 1);
        graph.addEdge(1, 4, 4);
        graph.addEdge(2, 4, 2);
        graph.addEdge(2, 5, 3);
        graph.addEdge(3, 6, 2);
        graph.addEdge(4, 6, 1);
        graph.addEdge(4, 7, 3);
        graph.addEdge(5, 7, 2);

        List<Integer> topoOrder = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7);
        CriticalPath criticalPath = new CriticalPath();
        CriticalPath.CriticalPathResult result = criticalPath.findCriticalPath(graph, topoOrder);

        // Expected critical path: 0->1->4->7 (2+4+3=9)
        // or 0->2->5->7 (3+3+2=8) - 0->1->4->7 is longer
        assertEquals(9, result.length);
        assertEquals(Arrays.asList(0, 1, 4, 7), result.path);
    }

    @Test
    public void testShortestPathMetrics() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 2);
        graph.addEdge(2, 3, 3);

        List<Integer> topoOrder = Arrays.asList(0, 1, 2, 3);
        DAGShortestPath shortestPath = new DAGShortestPath();
        shortestPath.shortestPaths(graph, 0, topoOrder);

        Metrics metrics = shortestPath.getMetrics();

        assertTrue("Should have positive execution time",
                metrics.getElapsedTime() > 0);
        assertTrue("Should have relaxation operations",
                metrics.getOperationCount("relaxation") > 0);
    }

    @Test
    public void testCriticalPathMetrics() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 2);
        graph.addEdge(2, 3, 3);

        List<Integer> topoOrder = Arrays.asList(0, 1, 2, 3);
        CriticalPath criticalPath = new CriticalPath();
        criticalPath.findCriticalPath(graph, topoOrder);

        Metrics metrics = criticalPath.getMetrics();

        assertTrue("Should have positive execution time",
                metrics.getElapsedTime() > 0);
        assertTrue("Should have relaxation operations",
                metrics.getOperationCount("relaxation") > 0);
    }

    @Test
    public void testSingleNodeGraph() {
        Graph graph = new Graph(1, true);

        List<Integer> topoOrder = Arrays.asList(0);
        DAGShortestPath shortestPath = new DAGShortestPath();
        int[] distances = shortestPath.shortestPaths(graph, 0, topoOrder);

        assertEquals(0, distances[0]);

        CriticalPath criticalPath = new CriticalPath();
        CriticalPath.CriticalPathResult result = criticalPath.findCriticalPath(graph, topoOrder);

        assertEquals(0, result.length);
        assertEquals(Arrays.asList(0), result.path);
    }

    @Test
    public void testDisconnectedGraph() {
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1, 1); // 0 and 1 are connected, 2 is disconnected

        List<Integer> topoOrder = Arrays.asList(0, 1, 2);
        DAGShortestPath shortestPath = new DAGShortestPath();
        int[] distances = shortestPath.shortestPaths(graph, 0, topoOrder);

        assertEquals(0, distances[0]);
        assertEquals(1, distances[1]);
        assertEquals(Integer.MAX_VALUE, distances[2]);

        CriticalPath criticalPath = new CriticalPath();
        CriticalPath.CriticalPathResult result = criticalPath.findCriticalPath(graph, topoOrder);

        // Critical path should be 0->1 (length 1)
        assertEquals(1, result.length);
        assertEquals(Arrays.asList(0, 1), result.path);
    }

    @Test
    public void testNegativeWeights() {
        // DAG shortest path can handle negative weights (unlike Dijkstra)
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, -2);
        graph.addEdge(1, 2, 3);
        graph.addEdge(0, 2, 2);
        graph.addEdge(2, 3, 1);

        List<Integer> topoOrder = Arrays.asList(0, 1, 2, 3);
        DAGShortestPath shortestPath = new DAGShortestPath();
        int[] distances = shortestPath.shortestPaths(graph, 0, topoOrder);

        // Path 0->1->2->3: -2+3+1=2 vs Path 0->2->3: 2+1=3
        // Should choose path with negative weight
        assertEquals(0, distances[0]);
        assertEquals(-2, distances[1]);
        assertEquals(1, distances[2]); // 0->1->2 = -2+3=1
        assertEquals(2, distances[3]); // 0->1->2->3 = -2+3+1=2
    }
}