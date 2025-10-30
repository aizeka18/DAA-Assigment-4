package graph.metrics;

public interface Metrics {
    void incrementOperation(String operation);
    long getOperationCount(String operation);
    void reset();
}