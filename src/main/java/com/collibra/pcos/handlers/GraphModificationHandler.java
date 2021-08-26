package com.collibra.pcos.handlers;

import com.collibra.pcos.services.GraphService;
import com.collibra.pcos.session.ExecResult;
import com.collibra.pcos.session.Session;
import com.collibra.pcos.utils.annotations.Command;
import com.collibra.pcos.utils.annotations.Handler;

import static com.collibra.pcos.properties.ApplicationProperties.MESSAGE_EDGE_ADDED;
import static com.collibra.pcos.properties.ApplicationProperties.MESSAGE_EDGE_REMOVED;
import static com.collibra.pcos.properties.ApplicationProperties.MESSAGE_NODE_ADDED;
import static com.collibra.pcos.properties.ApplicationProperties.MESSAGE_NODE_ALREADY_EXISTS;
import static com.collibra.pcos.properties.ApplicationProperties.MESSAGE_NODE_NOT_FOUND;
import static com.collibra.pcos.properties.ApplicationProperties.MESSAGE_NODE_REMOVED;
import static com.collibra.pcos.utils.ApplicationContext.getBean;

@Handler
public class GraphModificationHandler {

    private final GraphService<String> graphService = getBean("graphService");

    @Command(pattern = "ADD NODE ([a-zA-Z0-9_-]+)")
    public ExecResult addNode(Session session, String node) {
        boolean isAdded = graphService.addNode(node);

        final String out = isAdded ?
                MESSAGE_NODE_ADDED.getValue() : MESSAGE_NODE_ALREADY_EXISTS.getValue();

        return ExecResult.intermediate(out);
    }

    @Command(pattern = "REMOVE NODE ([a-zA-Z0-9_-]+)")
    public ExecResult removeNode(Session session, String node) {
        boolean isRemoved = graphService.removeNode(node);

        final String out = isRemoved ?
                MESSAGE_NODE_REMOVED.getValue() : MESSAGE_NODE_NOT_FOUND.getValue();

        return ExecResult.intermediate(out);
    }

    @Command(pattern = "ADD EDGE ([a-zA-Z0-9_-]+) ([a-zA-Z0-9_-]+) ([0-9]+)")
    public ExecResult addEdge(Session session, String nodeFrom, String nodeTo, String weightStr) {
        int weight = Integer.parseInt(weightStr);

        boolean isAdded = graphService.addEdge(nodeFrom, nodeTo, weight);

        final String out = isAdded ?
                MESSAGE_EDGE_ADDED.getValue() : MESSAGE_NODE_NOT_FOUND.getValue();

        return ExecResult.intermediate(out);
    }

    @Command(pattern = "REMOVE EDGE ([a-zA-Z0-9_-]+) ([a-zA-Z0-9_-]+)")
    public ExecResult removeEdge(Session session, String nodeFrom, String nodeTo) {

        boolean isRemoved = graphService.removeEdge(nodeFrom, nodeTo);

        final String out = isRemoved ?
                MESSAGE_EDGE_REMOVED.getValue() : MESSAGE_NODE_NOT_FOUND.getValue();

        return ExecResult.intermediate(out);
    }

}
