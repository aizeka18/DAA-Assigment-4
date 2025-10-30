// src/test/java/graph/TestDataGenerator.java
package graph;

import graph.model.GraphData;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class TestDataGenerator {

    public static void main(String[] args) throws Exception {
        generateAllDatasets();
    }

    public static void generateAllDatasets() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        new File("data").mkdirs();

        // Small graphs
        generateSmallGraph1().save("data/small1.json", mapper);
        generateSmallGraph2().save("data/small2.json", mapper);
        generateSmallGraph3().save("data/small3.json", mapper);

        // Medium graphs
        generateMediumGraph1().save("data/medium1.json", mapper);
        generateMediumGraph2().save("data/medium2.json", mapper);
        generateMediumGraph3().save("data/medium3.json", mapper);

        // Large graphs
        generateLargeGraph1().save("data/large1.json", mapper);
        generateLargeGraph2().save("data/large2.json", mapper);
        generateLargeGraph3().save("data/large3.json", mapper);

        System.out.println("Generated 9 test datasets in /data/ directory");
    }

    // Small graph with cycle
    private static GraphData generateSmallGraph1() {
        List<GraphData.Edge> edges = Arrays.asList(
                new GraphData.Edge(0, 1, 3),
                new GraphData.Edge(1, 2, 2),
                new GraphData.Edge(2, 3, 4),
                new GraphData.Edge(3, 1, 1), // Creates cycle 1-2-3-1
                new GraphData.Edge(4, 5, 2),
                new GraphData.Edge(5, 6, 5),
                new GraphData.Edge(6, 7, 1)
        );
        return new GraphData(true, 8, edges, 4, "edge");
    }

    // Pure DAG
    private static GraphData generateSmallGraph2() {
        List<GraphData.Edge> edges = Arrays.asList(
                new GraphData.Edge(0, 1, 2),
                new GraphData.Edge(0, 2, 1),
                new GraphData.Edge(1, 3, 4),
                new GraphData.Edge(2, 3, 3),
                new GraphData.Edge(3, 4, 2),
                new GraphData.Edge(4, 5, 1)
        );
        return new GraphData(true, 6, edges, 0, "edge");
    }

    // Multiple small cycles
    private static GraphData generateSmallGraph3() {
        List<GraphData.Edge> edges = Arrays.asList(
                new GraphData.Edge(0, 1, 1),
                new GraphData.Edge(1, 0, 1), // Cycle 0-1
                new GraphData.Edge(2, 3, 2),
                new GraphData.Edge(3, 2, 2), // Cycle 2-3
                new GraphData.Edge(4, 5, 3),
                new GraphData.Edge(5, 4, 3)  // Cycle 4-5
        );
        return new GraphData(true, 6, edges, 0, "edge");
    }

    // Medium graph with mixed structure
    private static GraphData generateMediumGraph1() {
        List<GraphData.Edge> edges = new ArrayList<>();
        Random rand = new Random(42);

        // Create a cycle
        for (int i = 0; i < 5; i++) {
            edges.add(new GraphData.Edge(i, (i + 1) % 5, rand.nextInt(5) + 1));
        }

        // Add DAG structure
        for (int i = 5; i < 15; i++) {
            if (i < 14) {
                edges.add(new GraphData.Edge(i, i + 1, rand.nextInt(5) + 1));
            }
        }

        // Cross connections
        edges.add(new GraphData.Edge(2, 6, 3));
        edges.add(new GraphData.Edge(8, 3, 2));

        return new GraphData(true, 15, edges, 0, "edge");
    }

    // Medium graph - dense with multiple SCCs
    private static GraphData generateMediumGraph2() {
        List<GraphData.Edge> edges = new ArrayList<>();
        Random rand = new Random(123);

        // Create multiple SCCs
        // SCC 1: 0-1-2 cycle
        edges.add(new GraphData.Edge(0, 1, rand.nextInt(5) + 1));
        edges.add(new GraphData.Edge(1, 2, rand.nextInt(5) + 1));
        edges.add(new GraphData.Edge(2, 0, rand.nextInt(5) + 1));

        // SCC 2: 3-4-5 cycle
        edges.add(new GraphData.Edge(3, 4, rand.nextInt(5) + 1));
        edges.add(new GraphData.Edge(4, 5, rand.nextInt(5) + 1));
        edges.add(new GraphData.Edge(5, 3, rand.nextInt(5) + 1));

        // SCC 3: 6-7 cycle
        edges.add(new GraphData.Edge(6, 7, rand.nextInt(5) + 1));
        edges.add(new GraphData.Edge(7, 6, rand.nextInt(5) + 1));

        // Add connections between SCCs to form DAG of components
        edges.add(new GraphData.Edge(2, 3, rand.nextInt(5) + 1));
        edges.add(new GraphData.Edge(5, 6, rand.nextInt(5) + 1));
        edges.add(new GraphData.Edge(1, 7, rand.nextInt(5) + 1));

        // Add some additional nodes in DAG structure
        for (int i = 8; i < 18; i++) {
            if (i < 17) {
                edges.add(new GraphData.Edge(i, i + 1, rand.nextInt(5) + 1));
            }
        }
        edges.add(new GraphData.Edge(7, 8, rand.nextInt(5) + 1));

        return new GraphData(true, 18, edges, 0, "edge");
    }

    // Medium graph - sparse with tree-like structure
    private static GraphData generateMediumGraph3() {
        List<GraphData.Edge> edges = new ArrayList<>();
        Random rand = new Random(456);

        // Tree-like structure with some cross edges
        edges.add(new GraphData.Edge(0, 1, rand.nextInt(5) + 1));
        edges.add(new GraphData.Edge(0, 2, rand.nextInt(5) + 1));
        edges.add(new GraphData.Edge(1, 3, rand.nextInt(5) + 1));
        edges.add(new GraphData.Edge(1, 4, rand.nextInt(5) + 1));
        edges.add(new GraphData.Edge(2, 5, rand.nextInt(5) + 1));
        edges.add(new GraphData.Edge(2, 6, rand.nextInt(5) + 1));
        edges.add(new GraphData.Edge(3, 7, rand.nextInt(5) + 1));
        edges.add(new GraphData.Edge(3, 8, rand.nextInt(5) + 1));
        edges.add(new GraphData.Edge(4, 9, rand.nextInt(5) + 1));
        edges.add(new GraphData.Edge(5, 10, rand.nextInt(5) + 1));

        // Add a few cross edges to make it a DAG
        edges.add(new GraphData.Edge(4, 10, rand.nextInt(5) + 1));
        edges.add(new GraphData.Edge(6, 9, rand.nextInt(5) + 1));

        return new GraphData(true, 11, edges, 0, "edge");
    }

    // Large graph - performance test 1
    private static GraphData generateLargeGraph1() {
        List<GraphData.Edge> edges = new ArrayList<>();
        Random rand = new Random(789);
        int n = 35;

        // Create a mostly linear structure with some branches
        for (int i = 0; i < n - 1; i++) {
            edges.add(new GraphData.Edge(i, i + 1, rand.nextInt(10) + 1));
        }

        // Add some branch edges
        for (int i = 0; i < n / 2; i++) {
            int from = rand.nextInt(n / 2);
            int to = from + rand.nextInt(n / 2) + 1;
            if (to < n) {
                edges.add(new GraphData.Edge(from, to, rand.nextInt(10) + 1));
            }
        }

        return new GraphData(true, n, edges, 0, "edge");
    }

    // Large graph - performance test 2 with cycles
    private static GraphData generateLargeGraph2() {
        List<GraphData.Edge> edges = new ArrayList<>();
        Random rand = new Random(321);
        int n = 40;

        // Create several small cycles
        for (int i = 0; i < n; i += 4) {
            if (i + 3 < n) {
                edges.add(new GraphData.Edge(i, i + 1, rand.nextInt(5) + 1));
                edges.add(new GraphData.Edge(i + 1, i + 2, rand.nextInt(5) + 1));
                edges.add(new GraphData.Edge(i + 2, i + 3, rand.nextInt(5) + 1));
                edges.add(new GraphData.Edge(i + 3, i, rand.nextInt(5) + 1));
            }
        }

        // Connect cycles in DAG fashion
        for (int i = 0; i < n - 4; i += 4) {
            if (i + 7 < n) {
                edges.add(new GraphData.Edge(i + 1, i + 4, rand.nextInt(5) + 1));
                edges.add(new GraphData.Edge(i + 2, i + 5, rand.nextInt(5) + 1));
            }
        }

        return new GraphData(true, n, edges, 0, "edge");
    }

    // Large graph - performance test 3 dense DAG
    private static GraphData generateLargeGraph3() {
        List<GraphData.Edge> edges = new ArrayList<>();
        Random rand = new Random(654);
        int n = 50;

        // Create dense DAG - each node connects to several later nodes
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (rand.nextDouble() < 0.3) { // 30% density
                    edges.add(new GraphData.Edge(i, j, rand.nextInt(10) + 1));
                }
            }
        }

        return new GraphData(true, n, edges, 0, "edge");
    }
}