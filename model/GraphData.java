package graph.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class GraphData {
    @JsonProperty("directed")
    private boolean directed;

    @JsonProperty("n")
    private int n;

    @JsonProperty("edges")
    private List<Edge> edges;

    @JsonProperty("source")
    private Integer source;

    @JsonProperty("weight_model")
    private String weightModel;


    public GraphData() {}

    public GraphData(boolean directed, int n, List<Edge> edges, Integer source, String weightModel) {
        this.directed = directed;
        this.n = n;
        this.edges = edges;
        this.source = source;
        this.weightModel = weightModel;
    }


    public boolean isDirected() {
        return directed;
    }

    public void setDirected(boolean directed) {
        this.directed = directed;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public String getWeightModel() {
        return weightModel;
    }

    public void setWeightModel(String weightModel) {
        this.weightModel = weightModel;
    }


    public void save(String filename, ObjectMapper mapper) throws IOException {
        mapper.writeValue(new File(filename), this);
    }

    public static class Edge {
        @JsonProperty("u")
        private int u;

        @JsonProperty("v")
        private int v;

        @JsonProperty("w")
        private int w;


        public Edge() {}

        public Edge(int u, int v, int w) {
            this.u = u;
            this.v = v;
            this.w = w;
        }


        public int getU() {
            return u;
        }

        public void setU(int u) {
            this.u = u;
        }

        public int getV() {
            return v;
        }

        public void setV(int v) {
            this.v = v;
        }

        public int getW() {
            return w;
        }

        public void setW(int w) {
            this.w = w;
        }

        @Override
        public String toString() {
            return "Edge{u=" + u + ", v=" + v + ", w=" + w + '}';
        }
    }

    @Override
    public String toString() {
        return "GraphData{" +
                "directed=" + directed +
                ", n=" + n +
                ", edges=" + edges +
                ", source=" + source +
                ", weightModel='" + weightModel + '\'' +
                '}';
    }
}