package graph.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

 class ProcessingResult {
    @JsonProperty("dataset_name")
    private String datasetName;

    @JsonProperty("graph_info")
    private GraphInfo graphInfo;

    @JsonProperty("scc_analysis")
    private SCCAnalysis sccAnalysis;

    @JsonProperty("topological_sort")
    private TopologicalSortResult topologicalSort;

    @JsonProperty("shortest_paths")
    private ShortestPathsResult shortestPaths;

    @JsonProperty("critical_path")
    private CriticalPathResult criticalPath;

    @JsonProperty("performance_metrics")
    private PerformanceMetrics performanceMetrics;

    // Getters and setters
    public static class GraphInfo {
        @JsonProperty("nodes")
        private int nodes;

        @JsonProperty("edges")
        private int edges;

        @JsonProperty("source")
        private int source;

        @JsonProperty("weight_model")
        private String weightModel;

        @JsonProperty("is_directed")
        private boolean directed;

        // Constructors, getters, setters
    }

    public static class SCCAnalysis {
        @JsonProperty("components")
        private List<List<Integer>> components;

        @JsonProperty("component_sizes")
        private List<Integer> componentSizes;

        @JsonProperty("total_components")
        private int totalComponents;

        // Constructors, getters, setters
    }

    public static class TopologicalSortResult {
        @JsonProperty("component_order")
        private List<Integer> componentOrder;

        @JsonProperty("task_order")
        private List<Integer> taskOrder;

        // Constructors, getters, setters
    }

    public static class ShortestPathsResult {
        @JsonProperty("source")
        private int source;

        @JsonProperty("distances")
        private Map<Integer, Integer> distances;

        @JsonProperty("optimal_path")
        private List<Integer> optimalPath;

        // Constructors, getters, setters
    }

    public static class CriticalPathResult {
        @JsonProperty("path")
        private List<Integer> path;

        @JsonProperty("length")
        private int length;

        // Constructors, getters, setters
    }

    public static class PerformanceMetrics {
        @JsonProperty("scc_time_ns")
        private long sccTime;

        @JsonProperty("topo_time_ns")
        private long topoTime;

        @JsonProperty("shortest_path_time_ns")
        private long shortestPathTime;

        @JsonProperty("critical_path_time_ns")
        private long criticalPathTime;

        @JsonProperty("total_time_ns")
        private long totalTime;

        @JsonProperty("operation_counts")
        private Map<String, Long> operationCounts;

        // Constructors, getters, setters
    }
}