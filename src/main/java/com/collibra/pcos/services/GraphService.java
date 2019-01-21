package com.collibra.pcos.services;

import java.util.List;

public interface GraphService<T> {

    /**
     * Adds a new node with name to the graph
     * Node names will always contain only alphanumeric characters and the dash character "-"
     * @param node node for adding
     * @return true if succeeded
     */
    boolean addNode(T node);

    /**
     * Removes node from the graph.
     * This should also cleanup all the edges that are connected to this node.
     * @param node node for removing
     * @return true if removed
     */
    boolean removeNode(T node);

    /**
     * Adds an edge from nodeFrom to nodeTo with the given weight.
     * Edges can occur multiple times (even with the same weight),
     * the weights, will always be non-negative positive integers
     * @param nodeFrom edge connecting from
     * @param nodeTo edge connecting to
     * @param weight integer indicating the weight of the edge
     * @return true if was added
     */
    boolean addEdge(T nodeFrom, T nodeTo, int weight);

    /**
     * Removes all the edges from node nodeFrom to nodeTo.
     * Edges are directed and cannot be traversed in the other direction.
     * No error should be given if there were no edges from node nodeFrom to node nodeTo
     * @param nodeFrom edge connecting from
     * @param nodeTo edge connecting to
     * @return true if was removed
     */
    boolean removeEdge(T nodeFrom, T nodeTo);

    /**
     * Check if graph contains specified node
     * @param node to check
     * @return true if exists
     */
    boolean hasNode(T node);

    /**
     * Check nodeFrom is connected to nodeTo
     * @param nodeFrom node from to check
     * @param nodeTo node to to check
     * @return true if conneced
     */
    boolean hasConnection(T nodeFrom, T nodeTo);

    /**
     * Finds the shortest (weighted) path from nodeFrom to nodeTo.
     * @param nodeFrom nodeFrom to calculate the path
     * @param nodeTo nodeTo to calculate the path
     * @return the sum of the weights of the shortest path if succeeded,
     * Integer.MAX_VALUE if nodes not connected
     */
    int shortestPath(T nodeFrom, T nodeTo);

    /**
     * Finds all the nodes that are closer to node than the given weight
     * @param weight weight to calculate list of nodes
     * @param node node to start calculation
     * @return a {@link java.util.List} of the found nodes,
     * sorted according to natural order of <{@link T}>,
     * not including the starting node
     */
    List<T> closerThan(int weight, T node);

}
