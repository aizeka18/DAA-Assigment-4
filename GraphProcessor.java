package graph;

import graph.scc.KosarajuSCC;
import graph.topo.KahnsTopologicalSort;
import graph.dagsp.DAGShortestPath;
import graph.dagsp.CriticalPath;
import graph.model.Graph;
import graph.model.GraphData;
import graph.metrics.Metrics;
import graph.export.CSVExporter;
import graph.export.JSONExporter;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Main processor that executes all graph tasks on a given dataset with proper time measurement
 */
public class GraphProcessor {
    private final ObjectMapper mapper;

    public GraphProcessor() {
        this.mapper = new ObjectMapper();
    }

    public ProcessingResult processDataset(String datasetPath) throws IOException {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("PROCESSING DATASET: " + datasetPath);
        System.out.println("=".repeat(80));

        // Load graph data from JSON
        GraphData graphData = mapper.readValue(new File(datasetPath), GraphData.class);
        Graph originalGraph = buildGraphFromData(graphData);

        ProcessingResult result = new ProcessingResult();
        result.datasetName = new File(datasetPath).getName();
        result.originalGraph = originalGraph;
        result.graphData = graphData;

        // Measure total processing time
        long totalStartTime = System.nanoTime();

        // 1.1 SCC Detection with proper time measurement
        System.out.println("\n1. STRONGLY CONNECTED COMPONENTS ANALYSIS");
        System.out.println("-".repeat(50));

        long sccStartTime = System.nanoTime();
        KosarajuSCC sccFinder = new KosarajuSCC();
        List<List<Integer>> sccs = sccFinder.findSCCs(originalGraph);
        long sccEndTime = System.nanoTime();
        result.sccs = sccs;
        result.sccMetrics = sccFinder.getMetrics();
        result.sccRealTime = sccEndTime - sccStartTime;

        System.out.println("Found " + sccs.size() + " SCCs:");
        for (int i = 0; i < sccs.size(); i++) {
            System.out.println("  SCC " + i + ": " + sccs.get(i) + " (size: " + sccs.get(i).size() + ")");
        }

        // 1.1 Build Condensation Graph with time measurement
        long condensationStartTime = System.nanoTime();
        Graph condensationGraph = sccFinder.buildCondensationGraph(originalGraph, sccs);
        long condensationEndTime = System.nanoTime();
        result.condensationGraph = condensationGraph;
        result.condensationTime = condensationEndTime - condensationStartTime;
        System.out.println("Condensation graph built: " + condensationGraph.getVertices() + " components");

        // 1.2 Topological Sort with time measurement
        System.out.println("\n2. TOPOLOGICAL SORTING");
        System.out.println("-".repeat(50));

        long topoStartTime = System.nanoTime();
        KahnsTopologicalSort topoSort = new KahnsTopologicalSort();
        List<Integer> componentOrder = topoSort.topologicalOrder(condensationGraph);
        long topoEndTime = System.nanoTime();
        result.componentOrder = componentOrder;
        result.topoMetrics = topoSort.getMetrics();
        result.topoRealTime = topoEndTime - topoStartTime;

        System.out.println("Topological order of components: " + componentOrder);

        // Derive order of original tasks after SCC compression
        List<Integer> taskOrder = deriveTaskOrder(sccs, componentOrder);
        result.taskOrder = taskOrder;
        System.out.println("Derived task order: " + taskOrder);

        // 1.3 Shortest Paths in DAG with time measurement
        System.out.println("\n3. SHORTEST PATHS AND CRITICAL PATH");
        System.out.println("-".repeat(50));

        int source = graphData.getSource() != null ? graphData.getSource() : 0;
        System.out.println("Source component: " + source);

        // Single-source shortest paths
        long spStartTime = System.nanoTime();
        DAGShortestPath shortestPath = new DAGShortestPath();
        int[] distances = shortestPath.shortestPaths(condensationGraph, source, componentOrder);
        long spEndTime = System.nanoTime();
        result.shortestDistances = distances;
        result.shortestPathMetrics = shortestPath.getMetrics();
        result.spRealTime = spEndTime - spStartTime;

        System.out.println("Shortest distances from component " + source + ":");
        for (int i = 0; i < distances.length; i++) {
            if (distances[i] != Integer.MAX_VALUE) {
                System.out.println("  To component " + i + ": " + distances[i]);
            }
        }

        // Reconstruct one optimal path
        if (distances.length > 1) {
            int target = findReachableTarget(distances, source);
            if (target != -1) {
                List<Integer> optimalPath = shortestPath.reconstructPath(
                        distances, target, condensationGraph, componentOrder);
                result.optimalPath = optimalPath;
                System.out.println("Optimal path to component " + target + ": " + optimalPath);
            }
        }

        // Longest path (Critical Path) with time measurement
        long cpStartTime = System.nanoTime();
        CriticalPath criticalPath = new CriticalPath();
        CriticalPath.CriticalPathResult criticalResult =
                criticalPath.findCriticalPath(condensationGraph, componentOrder);
        long cpEndTime = System.nanoTime();
        result.criticalPath = criticalResult.path;
        result.criticalPathLength = criticalResult.length;
        result.criticalPathMetrics = criticalPath.getMetrics();
        result.cpRealTime = cpEndTime - cpStartTime;

        System.out.println("Critical path: " + criticalResult.path);
        System.out.println("Critical path length: " + criticalResult.length);

        long totalEndTime = System.nanoTime();
        result.totalRealTime = totalEndTime - totalStartTime;

        // Print performance metrics
        printPerformanceMetrics(result);

        // Export results to CSV and JSON
        exportResults(result, datasetPath);

        return result;
    }

    private Graph buildGraphFromData(GraphData graphData) {
        Graph graph = new Graph(graphData.getN(), graphData.isDirected());

        for (GraphData.Edge edge : graphData.getEdges()) {
            graph.addEdge(edge.getU(), edge.getV(), edge.getW());
        }

        return graph;
    }

    private List<Integer> deriveTaskOrder(List<List<Integer>> sccs, List<Integer> componentOrder) {
        List<Integer> taskOrder = new ArrayList<>();

        for (int component : componentOrder) {
            taskOrder.addAll(sccs.get(component));
        }

        return taskOrder;
    }

    private int findReachableTarget(int[] distances, int source) {
        for (int i = 0; i < distances.length; i++) {
            if (i != source && distances[i] != Integer.MAX_VALUE) {
                return i;
            }
        }
        return -1;
    }

    private void printPerformanceMetrics(ProcessingResult result) {
        System.out.println("\n4. PERFORMANCE METRICS (REAL TIME)");
        System.out.println("-".repeat(50));

        System.out.println("SCC Algorithm:");
        System.out.println("  Real Time: " + formatNanos(result.sccRealTime));
        System.out.println("  DFS visits: " + result.sccMetrics.getOperationCount("dfs_visit"));
        System.out.println("  Edge traversals: " + result.sccMetrics.getOperationCount("edge_traversal"));

        System.out.println("Condensation Graph:");
        System.out.println("  Real Time: " + formatNanos(result.condensationTime));

        System.out.println("Topological Sort:");
        System.out.println("  Real Time: " + formatNanos(result.topoRealTime));
        System.out.println("  Queue operations: " +
                (result.topoMetrics.getOperationCount("queue_push") +
                        result.topoMetrics.getOperationCount("queue_pop")));

        System.out.println("Shortest Path:");
        System.out.println("  Real Time: " + formatNanos(result.spRealTime));
        System.out.println("  Relaxations: " + result.shortestPathMetrics.getOperationCount("relaxation"));

        System.out.println("Critical Path:");
        System.out.println("  Real Time: " + formatNanos(result.cpRealTime));
        System.out.println("  Relaxations: " + result.criticalPathMetrics.getOperationCount("relaxation"));

        System.out.println("TOTAL PROCESSING TIME: " + formatNanos(result.totalRealTime));
    }

    private String formatNanos(long nanos) {
        if (nanos < 1000) {
            return nanos + " ns";
        } else if (nanos < 1_000_000) {
            return String.format("%.3f μs", nanos / 1000.0);
        } else if (nanos < 1_000_000_000) {
            return String.format("%.3f ms", nanos / 1_000_000.0);
        } else {
            return String.format("%.3f s", nanos / 1_000_000_000.0);
        }
    }

    private void exportResults(ProcessingResult result, String datasetPath) throws IOException {
        String baseName = new File(datasetPath).getName().replace(".json", "");
        String resultsDir = "results";
        new File(resultsDir).mkdirs();
        new File(resultsDir + "/csv").mkdirs();
        new File(resultsDir + "/json").mkdirs();

        // Export to CSV
        exportToCSV(result, baseName, resultsDir);

        // Export to JSON
        exportToJSON(result, baseName, resultsDir);

        System.out.println("\n5. EXPORT RESULTS");
        System.out.println("-".repeat(50));
        System.out.println("CSV files saved to: " + resultsDir + "/csv/" + baseName + "_results.csv");
        System.out.println("JSON files saved to: " + resultsDir + "/json/" + baseName + "_results.json");
    }

    private void exportToCSV(ProcessingResult result, String baseName, String resultsDir)
            throws IOException {

        CSVExporter.exportCompleteResults(
                resultsDir + "/csv/" + baseName + "_results.csv",
                baseName,
                result.graphData,
                result.sccs,
                result.componentOrder,
                result.taskOrder,
                result.graphData.getSource() != null ? result.graphData.getSource() : 0,
                result.shortestDistances,
                result.optimalPath,
                result.criticalPath,
                result.criticalPathLength,
                result.sccRealTime,
                result.condensationTime,
                result.topoRealTime,
                result.spRealTime,
                result.cpRealTime,
                result.totalRealTime,
                result.sccMetrics,
                result.topoMetrics,
                result.shortestPathMetrics,
                result.criticalPathMetrics
        );
    }

    private void exportToJSON(ProcessingResult result, String baseName, String resultsDir)
            throws IOException {

        JSONExporter.exportCompleteResults(
                resultsDir + "/json/" + baseName + "_results.json",
                baseName,
                result.graphData,
                result.sccs,
                result.componentOrder,
                result.taskOrder,
                result.graphData.getSource() != null ? result.graphData.getSource() : 0,
                result.shortestDistances,
                result.optimalPath,
                result.criticalPath,
                result.criticalPathLength,
                result.sccRealTime,
                result.condensationTime,
                result.topoRealTime,
                result.spRealTime,
                result.cpRealTime,
                result.totalRealTime,
                result.sccMetrics,
                result.topoMetrics,
                result.shortestPathMetrics,
                result.criticalPathMetrics
        );
    }

    public void processAllDatasets() throws IOException {
        String[] datasets = {
                "data/small1.json", "data/small2.json", "data/small3.json",
                "data/medium1.json", "data/medium2.json", "data/medium3.json",
                "data/large1.json", "data/large2.json", "data/large3.json"
        };

        List<ProcessingResult> results = new ArrayList<>();

        for (String dataset : datasets) {
            if (new File(dataset).exists()) {
                try {
                    ProcessingResult result = processDataset(dataset);
                    results.add(result);
                } catch (Exception e) {
                    System.err.println("Error processing " + dataset + ": " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("Dataset not found: " + dataset);
            }
        }

        generateSummaryReport(results);
    }

    private void generateSummaryReport(List<ProcessingResult> results) throws IOException {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("SUMMARY REPORT FOR ALL DATASETS");
        System.out.println("=".repeat(80));

        System.out.printf("%-12s %-8s %-8s %-8s %-15s %-12s%n",
                "Dataset", "Nodes", "Edges", "SCCs", "Crit Path Len", "Total Time");
        System.out.println("-".repeat(80));

        long totalTime = 0;
        List<Map<String, Object>> summaryData = new ArrayList<>();

        for (ProcessingResult result : results) {
            System.out.printf("%-12s %-8d %-8d %-8d %-15d %-12s%n",
                    result.datasetName,
                    result.originalGraph.getVertices(),
                    countEdges(result.originalGraph),
                    result.sccs.size(),
                    result.criticalPathLength,
                    formatNanos(result.totalRealTime));

            // Prepare data for export
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("dataset", result.datasetName);
            data.put("nodes", result.originalGraph.getVertices());
            data.put("edges", countEdges(result.originalGraph));
            data.put("sccs", result.sccs.size());
            data.put("criticalPathLength", result.criticalPathLength);
            data.put("totalTime", result.totalRealTime);
            data.put("sccTime", result.sccRealTime);
            data.put("condensationTime", result.condensationTime);
            data.put("topoTime", result.topoRealTime);
            data.put("spTime", result.spRealTime);
            data.put("cpTime", result.cpRealTime);

            summaryData.add(data);
            totalTime += result.totalRealTime;
        }

        System.out.println("-".repeat(80));
        System.out.printf("Total processing time for all datasets: %s%n", formatNanos(totalTime));

        // Export summary results
        exportSummaryResults(summaryData);
    }

    private void exportSummaryResults(List<Map<String, Object>> summaryData) throws IOException {
        String resultsDir = "results";
        new File(resultsDir).mkdirs();

        // Export to CSV
        CSVExporter.exportSummaryResults(resultsDir + "/summary.csv", summaryData);

        // Export to JSON
        JSONExporter.exportSummaryResults(resultsDir + "/summary.json", summaryData);

        System.out.println("\n📊 Summary results exported to:");
        System.out.println("  - " + resultsDir + "/summary.csv");
        System.out.println("  - " + resultsDir + "/summary.json");
    }

    private int countEdges(Graph graph) {
        int count = 0;
        for (int i = 0; i < graph.getVertices(); i++) {
            count += graph.getNeighbors(i).size();
        }
        return count;
    }

    /**
     * Inner class to hold processing results for a single dataset
     */
    public static class ProcessingResult {
        public String datasetName;
        public Graph originalGraph;
        public GraphData graphData;
        public List<List<Integer>> sccs;
        public Graph condensationGraph;
        public List<Integer> componentOrder;
        public List<Integer> taskOrder;
        public int[] shortestDistances;
        public List<Integer> optimalPath;
        public List<Integer> criticalPath;
        public int criticalPathLength;

        // Real time measurements (in nanoseconds)
        public long totalRealTime;
        public long sccRealTime;
        public long condensationTime;
        public long topoRealTime;
        public long spRealTime;
        public long cpRealTime;

        public Metrics sccMetrics;
        public Metrics topoMetrics;
        public Metrics shortestPathMetrics;
        public Metrics criticalPathMetrics;
    }

    public static void main(String[] args) {
        try {
            GraphProcessor processor = new GraphProcessor();

            if (args.length > 0) {
                // Process specific dataset
                processor.processDataset(args[0]);
            } else {
                // Process all datasets
                processor.processAllDatasets();
            }

        } catch (Exception e) {
            System.err.println("Error in graph processing: " + e.getMessage());
            e.printStackTrace();
        }
    }
}