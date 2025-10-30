package graph.model;


public class Vertex {
    private final int id;
    private String taskName;
    private int duration; // For node duration weight model
    private VertexType type;

    public Vertex(int id) {
        this.id = id;
        this.taskName = "Task_" + id;
        this.duration = 1; // Default duration
        this.type = VertexType.STANDARD;
    }

    public Vertex(int id, String taskName, int duration, VertexType type) {
        this.id = id;
        this.taskName = taskName;
        this.duration = duration;
        this.type = type;
    }

    // Getters
    public int getId() { return id; }
    public String getTaskName() { return taskName; }
    public int getDuration() { return duration; }
    public VertexType getType() { return type; }

    // Setters
    public void setTaskName(String taskName) { this.taskName = taskName; }
    public void setDuration(int duration) { this.duration = duration; }
    public void setType(VertexType type) { this.type = type; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vertex vertex = (Vertex) obj;
        return id == vertex.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return String.format("Vertex{id=%d, name='%s', duration=%d, type=%s}",
                id, taskName, duration, type);
    }

    public enum VertexType {
        STREET_CLEANING,
        REPAIR,
        SENSOR_MAINTENANCE,
        ANALYTICS,
        STANDARD
    }
}