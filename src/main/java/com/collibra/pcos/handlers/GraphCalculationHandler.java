package com.collibra.pcos.handlers;

import com.collibra.pcos.services.GraphService;
import com.collibra.pcos.session.ExecResult;
import com.collibra.pcos.session.Session;
import com.collibra.pcos.utils.annotations.Command;
import com.collibra.pcos.utils.annotations.Handler;

import java.util.List;

import static com.collibra.pcos.properties.ApplicationProperties.MESSAGE_NODE_NOT_FOUND;
import static com.collibra.pcos.utils.ApplicationContext.getBean;

@Handler
public class GraphCalculationHandler {

    private final GraphService<String> graph = getBean("graphService");

    @Command(pattern = "SHORTEST PATH ([a-zA-Z0-9_-]+) ([a-zA-Z0-9_-]+)")
    public ExecResult shortestPath(Session session, String nodeFrom, String nodeTo) {
        if (graph.hasNode(nodeFrom) && graph.hasNode(nodeTo)) {
            Integer weight = graph.shortestPath(nodeFrom, nodeTo);
            return ExecResult.intermediate(weight.toString());
        } else {
            return ExecResult.intermediate(MESSAGE_NODE_NOT_FOUND.getValue());
        }
    }

    @Command(pattern = "CLOSER THAN ([0-9]+) ([a-zA-Z0-9_-]+)")
    public ExecResult closerThan(Session session, String weightStr, String node) {
        Integer weight = Integer.parseInt(weightStr);
        if (graph.hasNode(node)) {
            List<String> sortedListOfNodes = graph.closerThan(weight, node);
            String out = String.join(",", sortedListOfNodes);
            return ExecResult.intermediate(out);
        } else {
            return ExecResult.intermediate(MESSAGE_NODE_NOT_FOUND.getValue());
        }
    }

}
