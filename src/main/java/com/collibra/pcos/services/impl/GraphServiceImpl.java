package com.collibra.pcos.services.impl;

import com.collibra.pcos.graph.Edge;
import com.collibra.pcos.graph.ShortestPathsAlgorithm;
import com.collibra.pcos.services.GraphService;
import com.google.common.graph.MutableNetwork;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.collibra.pcos.utils.ApplicationContext.getBean;

/**
 * Synchronized {@link com.collibra.pcos.services.GraphService}  implementation
 */
public class GraphServiceImpl implements GraphService<String> {

    private final MutableNetwork<String, Edge> graph = getBean("graph");
    private final ShortestPathsAlgorithm shortPathsAlgo = getBean("graphAlgorithm");

    @Override
    public synchronized boolean addNode(String node) {
        return graph.addNode(node);
    }

    @Override
    public synchronized boolean removeNode(String node) {
        return graph.removeNode(node);
    }

    @Override
    public synchronized boolean addEdge(String nodeFrom, String nodeTo, int weight) {
        if (hasNode(nodeFrom) && hasNode(nodeTo)) {
            graph.addEdge(nodeFrom, nodeTo, new Edge(nodeFrom, nodeTo, weight));
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean removeEdge(String nodeFrom, String nodeTo) {
        if (hasNode(nodeFrom) && hasNode(nodeTo)) {
            Set<Edge> edges = new HashSet<>(graph.edgesConnecting(nodeFrom, nodeTo));
            edges.stream().forEach(graph::removeEdge);
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean hasNode(String node) {
        return graph.nodes().contains(node);
    }

    @Override
    public synchronized boolean hasConnection(String nodeFrom, String nodeTo) {
        return graph.hasEdgeConnecting(nodeFrom, nodeTo);
    }

    public synchronized Set<Edge> edgesConnecting(String nodeFrom, String nodeTo) {
        return graph.edgesConnecting(nodeFrom, nodeTo);
    }

    @Override
    public synchronized int shortestPath(String nodeFrom, String nodeTo) {
        final Map<String, Integer> minWeightMap = shortPathsAlgo.getMinWeightMap(graph, nodeFrom);
        return minWeightMap.getOrDefault(nodeTo, Integer.MAX_VALUE);
    }

    @Override
    public synchronized List<String> closerThan(int weight, String node) {
        final Map<String, Integer> minWeightMap = shortPathsAlgo.getMinWeightMap(graph, node);
        minWeightMap.remove(node); // not including the starting point
        return minWeightMap.entrySet().stream()
                .filter((e) -> e.getValue() < weight)
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.toList());
    }

}
