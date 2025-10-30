package graph.export;

import graph.model.GraphData;
import graph.metrics.Metrics;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class CSVExporter {

    public static void exportCompleteResults(String filename, String datasetName,
                                             GraphData graphData, List<List<Integer>> sccs,
                                             List<Integer> componentOrder, List<Integer> taskOrder,
                                             int source, int[] distances, List<Integer> optimalPath,
                                             List<Integer> criticalPath, int criticalPathLength,
                                             long sccTime, long condensationTime, long topoTime,
                                             long spTime, long cpTime, long totalTime,
                                             Metrics sccMetrics, Metrics topoMetrics,
                                             Metrics spMetrics, Metrics cpMetrics) throws IOException {

        try (FileWriter writer = new FileWriter(filename)) {
            // Header with dataset info
            writer.write("SMART CITY SCHEDULING ANALYSIS RESULTS\n");
            writer.write("=======================================\n");
            writer.write("Dataset: " + datasetName + "\n");
            writer.write("Generated: " + new Date() + "\n\n");

            // 1. GRAPH INFORMATION
            writer.write("1. GRAPH INFORMATION\n");
            writer.write("-------------------\n");
            writer.write("Nodes," + graphData.getN() + "\n");
            writer.write("Edges," + graphData.getEdges().size() + "\n");
            writer.write("Source," + source + "\n");
            writer.write("Weight Model," + graphData.getWeightModel() + "\n");
            writer.write("Directed," + graphData.isDirected() + "\n\n");

            // 2. STRONGLY CONNECTED COMPONENTS
            writer.write("2. STRONGLY CONNECTED COMPONENTS\n");
            writer.write("--------------------------------\n");
            writer.write("Total Components," + sccs.size() + "\n");
            writer.write("Component ID,Size,Nodes\n");
            for (int i = 0; i < sccs.size(); i++) {
                writer.write(i + "," + sccs.get(i).size() + ",\"" + formatList(sccs.get(i)) + "\"\n");
            }
            writer.write("\n");

            // 3. TOPOLOGICAL ORDERING
            writer.write("3. TOPOLOGICAL ORDERING\n");
            writer.write("-----------------------\n");
            writer.write("Component Order,\"" + formatList(componentOrder) + "\"\n");
            writer.write("Task Order,\"" + formatList(taskOrder) + "\"\n\n");

            // 4. SHORTEST PATHS
            writer.write("4. SHORTEST PATHS FROM SOURCE " + source + "\n");
            writer.write("----------------------------------------\n");
            writer.write("Target Component,Distance\n");
            boolean hasReachable = false;
            for (int i = 0; i < distances.length; i++) {
                if (distances[i] != Integer.MAX_VALUE && i != source) {
                    writer.write(i + "," + distances[i] + "\n");
                    hasReachable = true;
                }
            }
            if (!hasReachable) {
                writer.write("No reachable components from source\n");
            }

            if (optimalPath != null && !optimalPath.isEmpty()) {
                writer.write("\nOptimal Path Example,\"" + formatList(optimalPath) + "\"\n");
            }
            writer.write("\n");

            // 5. CRITICAL PATH
            writer.write("5. CRITICAL PATH ANALYSIS\n");
            writer.write("-------------------------\n");
            writer.write("Critical Path,\"" + formatList(criticalPath) + "\"\n");
            writer.write("Critical Path Length," + criticalPathLength + "\n\n");

            // 6. PERFORMANCE METRICS
            writer.write("6. PERFORMANCE METRICS\n");
            writer.write("----------------------\n");
            writer.write("Algorithm,Time (ns),Operations\n");

            long sccOps = sccMetrics.getOperationCount("dfs_visit") +
                    sccMetrics.getOperationCount("edge_traversal");
            writer.write("SCC," + sccTime + "," + sccOps + "\n");

            writer.write("Condensation Graph," + condensationTime + ",-\n");

            long topoOps = topoMetrics.getOperationCount("queue_push") +
                    topoMetrics.getOperationCount("queue_pop");
            writer.write("Topological Sort," + topoTime + "," + topoOps + "\n");

            long spOps = spMetrics.getOperationCount("relaxation");
            writer.write("Shortest Path," + spTime + "," + spOps + "\n");

            long cpOps = cpMetrics.getOperationCount("relaxation");
            writer.write("Critical Path," + cpTime + "," + cpOps + "\n");

            long totalOps = sccOps + topoOps + spOps + cpOps;
            writer.write("TOTAL," + totalTime + "," + totalOps + "\n");
        }
    }

    public static void exportSummaryResults(String filename, List<Map<String, Object>> summaryData)
            throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("SMART CITY SCHEDULING - DATASET SUMMARY\n");
            writer.write("=======================================\n");
            writer.write("Generated: " + new Date() + "\n\n");

            writer.write("Dataset,Nodes,Edges,SCCs,Critical Path Length,Total Time (ns),SCC Time,Condensation Time,Topo Time,SP Time,CP Time\n");

            long totalProcessingTime = 0;
            int totalNodes = 0;
            int totalEdges = 0;
            int totalSCCs = 0;

            for (Map<String, Object> data : summaryData) {
                writer.write(String.format("%s,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d\n",
                        data.get("dataset"),
                        data.get("nodes"),
                        data.get("edges"),
                        data.get("sccs"),
                        data.get("criticalPathLength"),
                        data.get("totalTime"),
                        data.get("sccTime"),
                        data.get("condensationTime"),
                        data.get("topoTime"),
                        data.get("spTime"),
                        data.get("cpTime")
                ));

                totalNodes += (Integer) data.get("nodes");
                totalEdges += (Integer) data.get("edges");
                totalSCCs += (Integer) data.get("sccs");
                totalProcessingTime += (Long) data.get("totalTime");
            }

            // Add summary statistics (только если есть данные)
            if (!summaryData.isEmpty()) {
                writer.write("\nSUMMARY STATISTICS\n");
                writer.write("------------------\n");
                writer.write("Total Datasets," + summaryData.size() + "\n");
                writer.write("Total Nodes," + totalNodes + "\n");
                writer.write("Total Edges," + totalEdges + "\n");
                writer.write("Total SCCs," + totalSCCs + "\n");
                writer.write("Total Processing Time," + totalProcessingTime + " ns\n");
                writer.write("Average Time per Dataset," + (totalProcessingTime / summaryData.size()) + " ns\n");
            }
        }
    }

    private static String formatList(List<Integer> list) {
        if (list == null || list.isEmpty()) {
            return "[]";
        }
        return list.toString().replace("[", "").replace("]", "");
    }
}