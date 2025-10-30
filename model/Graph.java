package graph.model;

import java.util.*;

public class Graph {
    private int vertices;
    private List<List<Edge>> adjacencyList;
    private boolean directed;

    public Graph(int vertices, boolean directed) {
        this.vertices = vertices;
        this.directed = directed;
        this.adjacencyList = new ArrayList<>();
        for (int i = 0; i < vertices; i++) {
            adjacencyList.add(new ArrayList<>());
        }
    }

    public void addEdge(int u, int v, int weight) {
        adjacencyList.get(u).add(new Edge(v, weight));
        if (!directed) {
            adjacencyList.get(v).add(new Edge(u, weight));
        }
    }

    public List<Edge> getNeighbors(int vertex) {
        return adjacencyList.get(vertex);
    }

    public int getVertices() { return vertices; }

    public static class Edge {
        public final int target;
        public final int weight;

        public Edge(int target, int weight) {
            this.target = target;
            this.weight = weight;
        }
    }
}