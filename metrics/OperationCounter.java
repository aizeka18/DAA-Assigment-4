
package graph.metrics;



import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class OperationCounter implements Metrics {
    private ConcurrentMap<String, Long> operationCounts;

    public OperationCounter() {
        this.operationCounts = new ConcurrentHashMap<>();
        reset();
    }

    @Override
    public void incrementOperation(String operation) {
        operationCounts.merge(operation, 1L, Long::sum);
    }

    @Override
    public long getOperationCount(String operation) {
        return operationCounts.getOrDefault(operation, 0L);
    }

    @Override
    public void reset() {
        operationCounts.clear();
    }
}