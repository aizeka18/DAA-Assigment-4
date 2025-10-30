package graph.data;

import graph.model.GraphData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class DatasetGenerator {
    private final Random random;
    private final ObjectMapper mapper;

    public DatasetGenerator() {
        this.random = new Random(42);
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void generateAllDatasets() throws IOException {
        new File("data").mkdirs();

        System.out.println("=== Generating 9 Test Datasets ===");

        // Small datasets (6-10 nodes)
        System.out.println("Generating small datasets...");
        generateSmall1().save("data/small1.json", mapper);
        generateSmall2().save("data/small2.json", mapper);
        generateSmall3().save("data/small3.json", mapper);

        // Medium datasets (10-20 nodes)
        System.out.println("Generating medium datasets...");
        generateMedium1().save("data/medium1.json", mapper);
        generateMedium2().save("data/medium2.json", mapper);
        generateMedium3().save("data/medium3.json", mapper);

        // Large datasets (20-50 nodes)
        System.out.println("Generating large datasets...");
        generateLarge1().save("data/large1.json", mapper);
        generateLarge2().save("data/large2.json", mapper);
        generateLarge3().save("data/large3.json", mapper);

        System.out.println("✅ All 9 datasets generated in /data/ directory");
        generateDatasetReport();
    }

    //SMALL DATASETS (6-10 nodes)

    private GraphData generateSmall1() {
        List<GraphData.Edge> edges = Arrays.asList(
                new GraphData.Edge(0, 1, 2),
                new GraphData.Edge(1, 2, 3),
                new GraphData.Edge(2, 0, 1), // Cycle: 0-1-2-0
                new GraphData.Edge(3, 4, 4),
                new GraphData.Edge(4, 5, 2),
                new GraphData.Edge(5, 6, 3)
        );
        return new GraphData(true, 7, edges, 3, "edge"); // Start from node 3 (DAG part)
    }

    private GraphData generateSmall2() {
        List<GraphData.Edge> edges = Arrays.asList(
                new GraphData.Edge(0, 1, 3),
                new GraphData.Edge(0, 2, 1),
                new GraphData.Edge(1, 3, 2),
                new GraphData.Edge(2, 3, 4),
                new GraphData.Edge(3, 4, 2),
                new GraphData.Edge(3, 5, 3),
                new GraphData.Edge(4, 6, 1)
        );
        return new GraphData(true, 7, edges, 1, "edge"); // Start from intermediate node
    }

    private GraphData generateSmall3() {
        List<GraphData.Edge> edges = Arrays.asList(
                new GraphData.Edge(0, 1, 2),
                new GraphData.Edge(1, 0, 3), // Cycle 0-1
                new GraphData.Edge(2, 3, 1),
                new GraphData.Edge(3, 2, 2), // Cycle 2-3
                new GraphData.Edge(4, 5, 4),
                new GraphData.Edge(5, 4, 3), // Cycle 4-5
                new GraphData.Edge(1, 3, 2),
                new GraphData.Edge(3, 5, 1)
        );
        return new GraphData(true, 6, edges, 2, "edge"); // Start from second cycle
    }

    //MEDIUM DATASETS(10-20 nodes)

    private GraphData generateMedium1() {
        List<GraphData.Edge> edges = new ArrayList<>();

        // Create multiple SCCs
        // SCC 1: nodes 0-1-2
        edges.add(new GraphData.Edge(0, 1, 2));
        edges.add(new GraphData.Edge(1, 2, 3));
        edges.add(new GraphData.Edge(2, 0, 1));

        // SCC 2: nodes 3-4-5
        edges.add(new GraphData.Edge(3, 4, 2));
        edges.add(new GraphData.Edge(4, 5, 1));
        edges.add(new GraphData.Edge(5, 3, 3));

        // DAG connections between SCCs
        edges.add(new GraphData.Edge(2, 3, 4));
        edges.add(new GraphData.Edge(1, 6, 2));
        edges.add(new GraphData.Edge(5, 7, 3));

        // Linear DAG part
        for (int i = 6; i < 14; i++) {
            if (i < 13) {
                edges.add(new GraphData.Edge(i, i + 1, random.nextInt(3) + 1));
            }
        }

        return new GraphData(true, 14, edges, 6, "edge"); // Start from DAG part
    }

    private GraphData generateMedium2() {
        List<GraphData.Edge> edges = new ArrayList<>();

        // Dense cyclic structure
        for (int i = 0; i < 12; i++) {
            for (int j = i + 1; j < Math.min(i + 4, 12); j++) {
                edges.add(new GraphData.Edge(i, j, random.nextInt(4) + 1));
                if (random.nextDouble() < 0.3) {
                    edges.add(new GraphData.Edge(j, i, random.nextInt(4) + 1));
                }
            }
        }

        // Additional DAG structure
        for (int i = 12; i < 18; i++) {
            edges.add(new GraphData.Edge(i - 2, i, random.nextInt(3) + 1));
            if (i < 17) {
                edges.add(new GraphData.Edge(i, i + 1, random.nextInt(2) + 1));
            }
        }

        return new GraphData(true, 18, edges, 12, "edge"); // Start from DAG section
    }

    private GraphData generateMedium3() {
        List<GraphData.Edge> edges = new ArrayList<>();

        // Sparse DAG with complex dependencies
        edges.add(new GraphData.Edge(0, 1, 3));
        edges.add(new GraphData.Edge(0, 2, 1));
        edges.add(new GraphData.Edge(1, 3, 2));
        edges.add(new GraphData.Edge(1, 4, 4));
        edges.add(new GraphData.Edge(2, 4, 2));
        edges.add(new GraphData.Edge(2, 5, 3));
        edges.add(new GraphData.Edge(3, 6, 1));
        edges.add(new GraphData.Edge(4, 6, 2));
        edges.add(new GraphData.Edge(4, 7, 3));
        edges.add(new GraphData.Edge(5, 7, 2));
        edges.add(new GraphData.Edge(6, 8, 4));
        edges.add(new GraphData.Edge(7, 8, 1));
        edges.add(new GraphData.Edge(8, 9, 2));
        edges.add(new GraphData.Edge(8, 10, 3));
        edges.add(new GraphData.Edge(9, 11, 1));
        edges.add(new GraphData.Edge(10, 11, 2));

        return new GraphData(true, 12, edges, 8, "edge"); // Start from convergence point
    }

    // ===== LARGE DATASETS (20-50 nodes) =====

    private GraphData generateLarge1() {
        List<GraphData.Edge> edges = new ArrayList<>();
        int n = 25;

        // Mixed structure for performance testing
        // Create some cycles
        for (int i = 0; i < 3; i++) {
            int start = i * 3;
            edges.add(new GraphData.Edge(start, start + 1, random.nextInt(3) + 1));
            edges.add(new GraphData.Edge(start + 1, start + 2, random.nextInt(3) + 1));
            edges.add(new GraphData.Edge(start + 2, start, random.nextInt(3) + 1));
        }

        // DAG connections
        for (int i = 0; i < n - 1; i++) {
            for (int j = 1; j <= 3; j++) {
                if (i + j < n && random.nextDouble() < 0.4) {
                    edges.add(new GraphData.Edge(i, i + j, random.nextInt(5) + 1));
                }
            }
        }

        return new GraphData(true, n, edges, 9, "edge"); // Start from middle
    }

    private GraphData generateLarge2() {
        List<GraphData.Edge> edges = new ArrayList<>();
        int n = 35;

        // Dense DAG for critical path analysis
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < Math.min(i + 8, n); j++) {
                if (random.nextDouble() < 0.6) {
                    edges.add(new GraphData.Edge(i, j, random.nextInt(6) + 1));
                }
            }
        }

        return new GraphData(true, n, edges, 15, "edge"); // Start from middle
    }

    private GraphData generateLarge3() {
        List<GraphData.Edge> edges = new ArrayList<>();
        int n = 40;

        // Complex graph with multiple SCC hierarchies
        // Create hierarchical SCCs
        createHierarchicalSCCs(edges, 0, 5, 0);
        createHierarchicalSCCs(edges, 5, 8, 1);
        createHierarchicalSCCs(edges, 13, 6, 2);

        // Connect SCCs in DAG fashion
        edges.add(new GraphData.Edge(4, 5, 3));
        edges.add(new GraphData.Edge(12, 13, 2));
        edges.add(new GraphData.Edge(8, 19, 4));

        // Add linear DAG structure for remaining nodes
        for (int i = 19; i < n - 1; i++) {
            edges.add(new GraphData.Edge(i, i + 1, random.nextInt(3) + 1));
        }

        // Add some cross connections
        for (int i = 0; i < n; i += 5) {
            if (i + 10 < n) {
                edges.add(new GraphData.Edge(i, i + 10, random.nextInt(4) + 1));
            }
        }

        return new GraphData(true, n, edges, 20, "edge"); // Start from DAG section
    }

    private void createHierarchicalSCCs(List<GraphData.Edge> edges, int start, int size, int level) {
        // Create a strongly connected component
        for (int i = start; i < start + size - 1; i++) {
            edges.add(new GraphData.Edge(i, i + 1, random.nextInt(3) + 1));
        }
        edges.add(new GraphData.Edge(start + size - 1, start, random.nextInt(3) + 1));

        // Add some internal edges
        for (int i = 0; i < size / 2; i++) {
            int u = start + random.nextInt(size);
            int v = start + random.nextInt(size);
            if (u != v) {
                edges.add(new GraphData.Edge(u, v, random.nextInt(2) + 1));
            }
        }
    }

    private void generateDatasetReport() throws IOException {
        StringBuilder report = new StringBuilder();
        report.append("# Graph Dataset Report\n\n");
        report.append("This report describes the 9 generated datasets for testing graph algorithms.\n\n");

        String[] files = {"small1.json", "small2.json", "small3.json",
                "medium1.json", "medium2.json", "medium3.json",
                "large1.json", "large2.json", "large3.json"};

        String[] categories = {"Small", "Small", "Small", "Medium", "Medium", "Medium",
                "Large", "Large", "Large"};

        String[] descriptions = {
                "Simple case with 1 cycle and DAG structure",
                "Pure DAG with multiple paths",
                "Multiple small cycles with connections",
                "Mixed structure with several SCCs",
                "Dense cyclic graph with DAG components",
                "Sparse DAG with complex dependencies",
                "Large mixed graph for performance testing",
                "Dense DAG for critical path analysis",
                "Complex graph with multiple SCC hierarchies"
        };

        report.append("## Dataset Summary\n\n");
        report.append("| Category | Dataset | Nodes | Edges | Source | Description |\n");
        report.append("|----------|---------|-------|-------|--------|-------------|\n");

        for (int i = 0; i < files.length; i++) {
            try {
                GraphData data = mapper.readValue(new File("data/" + files[i]), GraphData.class);
                int edgeCount = data.getEdges().size();
                String cyclicInfo = isCyclic(data) ? "Cyclic" : "Acyclic";

                report.append(String.format("| %s | %s | %d | %d | %d | %s (%s) |\n",
                        categories[i], files[i], data.getN(), edgeCount,
                        data.getSource(), descriptions[i], cyclicInfo));

            } catch (IOException e) {
                report.append(String.format("| %s | %s | - | - | - | Error loading |\n",
                        categories[i], files[i]));
            }
        }

        report.append("\n## Dataset Details\n\n");

        // Add details for each dataset
        for (int i = 0; i < files.length; i++) {
            try {
                GraphData data = mapper.readValue(new File("data/" + files[i]), GraphData.class);
                report.append(String.format("### %s\n\n", files[i]));
                report.append(String.format("- **Category**: %s\n", categories[i]));
                report.append(String.format("- **Nodes**: %d\n", data.getN()));
                report.append(String.format("- **Edges**: %d\n", data.getEdges().size()));
                report.append(String.format("- **Source**: %d\n", data.getSource()));
                report.append(String.format("- **Type**: %s\n", isCyclic(data) ? "Cyclic" : "Acyclic"));
                report.append(String.format("- **Description**: %s\n\n", descriptions[i]));
            } catch (IOException e) {
                report.append(String.format("### %s - Error loading\n\n", files[i]));
            }
        }

        report.append("\n## Usage\n\n");
        report.append("These datasets are used for testing:\n");
        report.append("- Strongly Connected Components (SCC) detection\n");
        report.append("- Topological ordering of DAGs\n");
        report.append("- Shortest paths in DAGs\n");
        report.append("- Critical path analysis\n");

        // Write report to file
        mapper.writeValue(new File("data/DATASET_REPORT.md"), report.toString());
        System.out.println("📊 Dataset report generated: data/DATASET_REPORT.md");
    }

    private boolean isCyclic(GraphData data) {
        // Simple check - if there are any edges where u >= v in a supposed DAG, it might be cyclic
        // This is a heuristic for the report
        for (GraphData.Edge edge : data.getEdges()) {
            if (edge.getU() >= edge.getV()) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        try {
            new DatasetGenerator().generateAllDatasets();
        } catch (IOException e) {
            System.err.println("Error generating datasets: " + e.getMessage());
            e.printStackTrace();
        }
    }
}