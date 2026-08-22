package com.dcit308.wa.algorithms.graph;

import com.dcit308.wa.structures.GraphADT;

/**
 * Depth-First Search (DFS) Algorithm
 * Traverses a graph using recursion
 */
public class DFS {
    
    private boolean[] visited;
    private int[] traversalOrder;
    private int orderIndex;
    private GraphADT graph;
    
    /**
     * Performs DFS traversal starting from a given vertex
     * @param graph The graph to traverse
     * @param startVertex The starting vertex
     * @return Array of vertices in traversal order
     */
    public int[] traverse(GraphADT graph, int startVertex) {
        if (graph == null) {
            throw new IllegalArgumentException("Graph cannot be null");
        }
        if (graph.isEmpty()) {
            return new int[0];
        }
        
        this.graph = graph;
        int vertexCount = graph.getVertexCount();
        this.visited = new boolean[findMaxVertex(graph) + 1];
        this.traversalOrder = new int[vertexCount];
        this.orderIndex = 0;
        
        // Start DFS from the given vertex
        dfs(startVertex);
        
        // If there are unvisited vertices (disconnected graph), visit them too
        int[] vertices = graph.getVertices();
        for (int vertex : vertices) {
            if (!visited[vertex]) {
                dfs(vertex);
            }
        }
        
        // Trim the traversal order to the actual size
        int[] result = new int[orderIndex];
        System.arraycopy(traversalOrder, 0, result, 0, orderIndex);
        return result;
    }
    
    /**
     * Recursive DFS helper method
     */
    private void dfs(int vertex) {
        if (vertex < 0 || vertex >= visited.length) {
            return;
        }
        
        visited[vertex] = true;
        traversalOrder[orderIndex++] = vertex;
        
        int[] neighbors = graph.getNeighbors(vertex);
        for (int neighbor : neighbors) {
            if (!visited[neighbor]) {
                dfs(neighbor);
            }
        }
    }
    
    /**
     * Performs DFS traversal and returns the traversal trace as a string
     */
    public String traverseWithTrace(GraphADT graph, int startVertex) {
        StringBuilder trace = new StringBuilder();
        trace.append("DFS Traversal Trace\n");
        trace.append("===================\n");
        trace.append("Starting vertex: ").append(startVertex).append("\n\n");
        
        int[] result = traverse(graph, startVertex);
        
        trace.append("Traversal order: ");
        for (int i = 0; i < result.length; i++) {
            trace.append(result[i]);
            if (i < result.length - 1) {
                trace.append(" -> ");
            }
        }
        trace.append("\n");
        trace.append("Visited vertices: ").append(result.length);
        trace.append(" out of ").append(graph.getVertexCount());
        trace.append("\n");
        
        return trace.toString();
    }
    
    /**
     * Finds the maximum vertex index in the graph
     */
    private int findMaxVertex(GraphADT graph) {
        int[] vertices = graph.getVertices();
        int max = -1;
        for (int v : vertices) {
            if (v > max) {
                max = v;
            }
        }
        return max;
    }
    
    /**
     * Checks if the graph is connected using DFS
     */
    public boolean isConnected(GraphADT graph) {
        if (graph == null || graph.isEmpty()) {
            return false;
        }
        
        int[] vertices = graph.getVertices();
        int[] traversal = traverse(graph, vertices[0]);
        return traversal.length == graph.getVertexCount();
    }
}