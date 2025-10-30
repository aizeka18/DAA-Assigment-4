// src/test/java/graph/SCCTest.java
package graph;

import graph.scc.KosarajuSCC;
import graph.model.Graph;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class SCCTest {

    @Test
    public void testSCCSimpleCycle() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 0, 1); // Creates cycle 0-1-2-3-0

        KosarajuSCC scc = new KosarajuSCC();
        List<List<Integer>> sccs = scc.findSCCs(graph);

        assertEquals(1, sccs.size());
        assertEquals(4, sccs.get(0).size());
    }

    @Test
    public void testSCCMultipleComponents() {
        Graph graph = new Graph(6, true);
        // First cycle
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 0, 1);
        // Second cycle
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 2, 1);
        // Third cycle
        graph.addEdge(4, 5, 1);
        graph.addEdge(5, 4, 1);

        KosarajuSCC scc = new KosarajuSCC();
        List<List<Integer>> sccs = scc.findSCCs(graph);

        assertEquals(3, sccs.size());
        for (List<Integer> component : sccs) {
            assertEquals(2, component.size());
        }
    }
}