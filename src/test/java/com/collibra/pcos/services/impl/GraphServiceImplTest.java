package com.collibra.pcos.services.impl;

import com.collibra.pcos.utils.ApplicationTestContext;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GraphServiceImplTest {

    private GraphServiceImpl graph;

    @Before
    public void setUp() {
        ApplicationTestContext.load();
        graph = new GraphServiceImpl();
    }

    @Test
    public void testAddNode() {
        assertTrue(graph.addNode("A"));
        assertFalse(graph.addNode("A"));
    }

    @Test
    public void testRemoveNode() {
        graph.addNode("A");
        assertTrue(graph.removeNode("A"));
        assertFalse(graph.removeNode("A"));
    }

    @Test
    public void testAddEdge() {
        connectNodes("A", "B", 1);
        assertTrue(graph.hasConnection("A", "B"));
    }

    @Test
    public void testRemoveEdge() {
        connectNodes("A", "B", 1);
        graph.removeEdge("A", "B");
        assertFalse(graph.hasConnection("A", "B"));
    }

    @Test
    public void testAddParallelEdge() {
        connectNodes("A", "B", 1);
        connectNodes("A", "B", 2);
        assertEquals(2, graph.edgesConnecting("A", "B").size());
        assertTrue(graph.hasConnection("A", "B"));
    }

    @Test
    public void testAddEqualParallelEdge() {
        connectNodes("A", "B", 1);
        connectNodes("A", "B", 1);
        assertEquals(1, graph.edgesConnecting("A", "B").size());
        assertTrue(graph.hasConnection("A", "B"));
    }

    @Test
    public void testRemoveAllParallelEdges() {
        connectNodes("A", "B", 1);
        connectNodes("A", "B", 2);
        assertTrue(graph.removeEdge("A", "B"));
        assertFalse(graph.hasConnection("A", "B"));
    }

    @Test
    public void testEdgeDirection() {
        connectNodes("A", "B", 1);
        graph.removeEdge("B", "A");
        assertTrue(graph.hasConnection("A", "B"));
        assertFalse(graph.hasConnection("B", "A"));
    }

    @Test
    public void testAddEdgeDoesNotCreateNodes() {
        graph.addEdge("A", "B", 1);
        assertFalse(graph.hasNode("A"));
        assertFalse(graph.hasNode("B"));
    }

    @Test
    public void testSelfLoop() {
        connectNodes("A", "A", 1);
        assertTrue(graph.hasConnection("A", "A"));
    }

    @Test
    public void testShortestPath() {
        createTestGraph1();
        assertEquals(20, graph.shortestPath("A", "D"));
        assertEquals(15, graph.shortestPath("B", "D"));
        assertEquals(10, graph.shortestPath("B", "C"));
        assertEquals(30,  graph.shortestPath("C", "A"));
        assertEquals(25,  graph.shortestPath("D", "B"));
        assertEquals(0,  graph.shortestPath("A", "A"));
        assertEquals(0,  graph.shortestPath("D", "D"));
    }

    @Test
    public void testCloserThan() {
        createTestGraph2();
        List<String> nodes = graph.closerThan(8, "Mark");
        assertTrue(nodes.contains("Michael"));
        assertTrue(nodes.contains("Madeleine"));
        assertEquals(2, nodes.size());
    }

    @Test
    public void testCloserThanSortedAlphabetically() {
        createTestGraph2();
        List<String> nodes = graph.closerThan(8, "Mark");
        assertEquals("Madeleine,Michael", String.join(",", nodes));
    }

    private void createTestGraph1() {

        //         ,---------30-------|
        //         ,---------50-------+---5-----\
        //         A --5--> B --10--> C --10--> D <--1-- D
        //         |        ^--------25---------|
        //         |------------40--------------^
        //
        graph.addNode("A");
        graph.addNode("B");
        graph.addNode("C");
        graph.addNode("D");
        graph.addEdge("A", "B", 5);
        graph.addEdge("B", "C", 10);
        graph.addEdge("C", "D", 10);
        graph.addEdge("C", "D", 5);
        graph.addEdge("A", "D", 40);
        graph.addEdge("C", "A", 50);
        graph.addEdge("C", "A", 30);
        graph.addEdge("D", "B", 25);
        graph.addEdge("D", "D", 1);
    }

    public void createTestGraph2() {

        // Mark -5-> Michael -2-> Madeleine -8-> Mufasa

        graph.addNode("Mark");
        graph.addNode("Michael");
        graph.addNode("Madeleine");
        graph.addNode("Mufasa");
        graph.addEdge("Mark", "Michael", 5);
        graph.addEdge("Michael", "Madeleine", 2);
        graph.addEdge("Madeleine", "Mufasa", 8);
    }

    private void connectNodes(String from, String to, int weight) {
        graph.addNode(from);
        graph.addNode(to);
        graph.addEdge(from, to, weight);
    }

}
