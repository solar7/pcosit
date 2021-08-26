package com.collibra.pcos.graph;

import com.google.common.graph.Network;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import static java.lang.Integer.MAX_VALUE;

public class ShortestPathsGraphTraversalImpl implements ShortestPathsAlgorithm<String, Edge> {

    @Override
    public Map<String, Integer> getMinWeightMap(Network<String, Edge> graph, String nodeFrom) {
        final Map<String, Integer> minWeightsMap = new HashMap<>();
        final PriorityQueue<String> nodesToCheck = new PriorityQueue<>();

        minWeightsMap.put(nodeFrom, 0);     // entry point to start
        nodesToCheck.add(nodeFrom);         // from nodeFrom

        while (!nodesToCheck.isEmpty()) {
            String U = nodesToCheck.poll();

            for (Edge edgeUtoV : graph.outEdges(U)) {
                String V = edgeUtoV.getTo();

                Integer weightToU = edgeUtoV.getWeight() + minWeightsMap.getOrDefault(U, MAX_VALUE);
                Integer weightFromPath = minWeightsMap.getOrDefault(V, MAX_VALUE);

                if (weightToU < weightFromPath) {
                    minWeightsMap.put(V, weightToU);

                    // no need to check node V more than once
                    if (!nodesToCheck.contains(V)) {
                        nodesToCheck.add(V);
                    }

                }
            }
        }
        return minWeightsMap;
    }
}
