package graph.export;

import graph.model.GraphData;
import graph.metrics.Metrics;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;
import java.util.*;


public class JSONExporter {
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public static void exportCompleteResults(String filename, String datasetName,
                                             GraphData graphData, List<List<Integer>> sccs,
                                             List<Integer> componentOrder, List<Integer> taskOrder,
                                             int source, int[] distances, List<Integer> optimalPath,
                                             List<Integer> criticalPath, int criticalPathLength,
                                             long sccTime, long condensationTime, long topoTime,
                                             long spTime, long cpTime, long totalTime,
                                             Metrics sccMetrics, Metrics topoMetrics,
                                             Metrics spMetrics, Metrics cpMetrics) throws IOException {

        Map<String, Object> results = new LinkedHashMap<>();

        // 1. Metadata
        results.put("analysis", "Smart City Scheduling Graph Analysis");
        results.put("dataset", datasetName);
        results.put("timestamp", new Date().toString());
        results.put("version", "1.0");

        // 2. Graph Information
        Map<String, Object> graphInfo = new LinkedHashMap<>();
        graphInfo.put("nodes", graphData.getN());
        graphInfo.put("edges", graphData.getEdges().size());
        graphInfo.put("source", source);
        graphInfo.put("weightModel", graphData.getWeightModel());
        graphInfo.put("directed", graphData.isDirected());
        results.put("graph", graphInfo);

        // 3. SCC Analysis
        Map<String, Object> sccAnalysis = new LinkedHashMap<>();
        sccAnalysis.put("totalComponents", sccs.size());

        List<Map<String, Object>> components = new ArrayList<>();
        for (int i = 0; i < sccs.size(); i++) {
            Map<String, Object> component = new LinkedHashMap<>();
            component.put("id", i);
            component.put("size", sccs.get(i).size());
            component.put("nodes", sccs.get(i));
            components.add(component);
        }
        sccAnalysis.put("components", components);
        results.put("stronglyConnectedComponents", sccAnalysis);

        // 4. Topological Ordering
        Map<String, Object> topological = new LinkedHashMap<>();
        topological.put("componentOrder", componentOrder);
        topological.put("taskOrder", taskOrder);
        results.put("topologicalOrdering", topological);

        // 5. Shortest Paths
        Map<String, Object> shortestPaths = new LinkedHashMap<>();
        shortestPaths.put("source", source);

        Map<String, Object> distanceMap = new LinkedHashMap<>();
        boolean hasReachable = false;
        for (int i = 0; i < distances.length; i++) {
            if (distances[i] != Integer.MAX_VALUE && i != source) {
                distanceMap.put("component_" + i, distances[i]);
                hasReachable = true;
            }
        }
        shortestPaths.put("distances", hasReachable ? distanceMap : "No reachable components");

        if (optimalPath != null && !optimalPath.isEmpty()) {
            shortestPaths.put("optimalPathExample", optimalPath);
        }
        results.put("shortestPaths", shortestPaths);

        // 6. Critical Path
        Map<String, Object> criticalPathResult = new LinkedHashMap<>();
        criticalPathResult.put("path", criticalPath);
        criticalPathResult.put("length", criticalPathLength);
        results.put("criticalPath", criticalPathResult);

        // 7. Performance Metrics
        Map<String, Object> performance = new LinkedHashMap<>();

        // Real time measurements
        Map<String, Object> realTiming = new LinkedHashMap<>();
        realTiming.put("scc", sccTime);
        realTiming.put("condensation", condensationTime);
        realTiming.put("topologicalSort", topoTime);
        realTiming.put("shortestPaths", spTime);
        realTiming.put("criticalPath", cpTime);
        realTiming.put("total", totalTime);
        performance.put("realTimeNs", realTiming);

        // Operation counts
        Map<String, Object> operations = new LinkedHashMap<>();
        operations.put("dfsVisits", sccMetrics.getOperationCount("dfs_visit"));
        operations.put("edgeTraversals", sccMetrics.getOperationCount("edge_traversal"));
        operations.put("queueOperations",
                topoMetrics.getOperationCount("queue_push") + topoMetrics.getOperationCount("queue_pop"));
        operations.put("relaxations",
                spMetrics.getOperationCount("relaxation") + cpMetrics.getOperationCount("relaxation"));
        performance.put("operationCounts", operations);

        results.put("performance", performance);

        mapper.writeValue(new File(filename), results);
    }

    public static void exportSummaryResults(String filename, List<Map<String, Object>> summaryData)
            throws IOException {

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("title", "Smart City Scheduling - Dataset Summary Report");
        summary.put("generated", new Date().toString());
        summary.put("totalDatasets", summaryData.size());

        // Calculate statistics
        int totalNodes = summaryData.stream().mapToInt(d -> (Integer)d.get("nodes")).sum();
        int totalEdges = summaryData.stream().mapToInt(d -> (Integer)d.get("edges")).sum();
        int totalSCCs = summaryData.stream().mapToInt(d -> (Integer)d.get("sccs")).sum();
        long totalTime = summaryData.stream().mapToLong(d -> (Long)d.get("totalTime")).sum();

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalNodes", totalNodes);
        stats.put("totalEdges", totalEdges);
        stats.put("totalSCCs", totalSCCs);
        stats.put("totalProcessingTimeNs", totalTime);
        stats.put("averageTimePerDatasetNs", totalTime / summaryData.size());
        summary.put("statistics", stats);

        // Individual dataset results
        List<Map<String, Object>> datasets = new ArrayList<>();
        for (Map<String, Object> data : summaryData) {
            Map<String, Object> dataset = new LinkedHashMap<>();
            dataset.put("name", data.get("dataset"));
            dataset.put("nodes", data.get("nodes"));
            dataset.put("edges", data.get("edges"));
            dataset.put("sccs", data.get("sccs"));
            dataset.put("criticalPathLength", data.get("criticalPathLength"));

            Map<String, Object> timing = new LinkedHashMap<>();
            timing.put("total", data.get("totalTime"));
            timing.put("scc", data.get("sccTime"));
            timing.put("condensation", data.get("condensationTime"));
            timing.put("topological", data.get("topoTime"));
            timing.put("shortestPaths", data.get("spTime"));
            timing.put("criticalPath", data.get("cpTime"));
            dataset.put("processingTimeNs", timing);

            datasets.add(dataset);
        }
        summary.put("datasets", datasets);

        mapper.writeValue(new File(filename), summary);
    }
}