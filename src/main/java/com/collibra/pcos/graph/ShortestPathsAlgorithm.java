package com.collibra.pcos.graph;

import com.google.common.graph.Network;

import java.util.Map;

public interface ShortestPathsAlgorithm<N, E> {

    /**
     * Algorithm for finding the shortest paths between node
     * and all other connected nodes in a graph
     * @return minimal weight matrix where key => node, value => minWeight
     */
    Map<String, Integer> getMinWeightMap(Network<N, E> graph, N nodeFrom);

}
