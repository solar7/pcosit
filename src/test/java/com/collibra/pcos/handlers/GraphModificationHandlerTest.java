package com.collibra.pcos.handlers;

import com.collibra.pcos.services.GraphService;
import com.collibra.pcos.session.ExecResult;
import com.collibra.pcos.session.Session;
import com.collibra.pcos.utils.ApplicationTestContext;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GraphModificationHandlerTest {

    @Mock
    private GraphService<String> graphService;

    private final Session session =  new Session("SESSION-ID");

    private GraphModificationHandler handler;

    @BeforeClass
    public static void init() {
        ApplicationTestContext.load();
    }

    @Before
    public void setUp() {
        ApplicationTestContext.clearAndPut("graphService", graphService);
        handler = new GraphModificationHandler();
    }

    @Test
    public void testAddNodeOk() {
        when(graphService.addNode("A")).thenReturn(true);
        ExecResult added = handler.addNode(session, "A");
        assertEquals(ExecResult.intermediate("NODE ADDED"), added);
    }

    @Test
    public void testAddNodeErr() {
        when(graphService.addNode("A")).thenReturn(false);
        ExecResult error = handler.addNode(session, "A");
        assertEquals(ExecResult.intermediate("ERROR: NODE ALREADY EXISTS"), error);
    }

    @Test
    public void testRemoveNodeOk() {
        when(graphService.removeNode("A")).thenReturn(true);
        ExecResult removed = handler.removeNode(session, "A");
        assertEquals(ExecResult.intermediate("NODE REMOVED"), removed);
    }
    @Test
    public void testRemoveNodeErr() {
        when(graphService.removeNode("B")).thenReturn(false);
        ExecResult error = handler.removeNode(session, "B");
        assertEquals(ExecResult.intermediate("ERROR: NODE NOT FOUND"), error);
    }


    @Test
    public void testAddEdgeOk() {
        when(graphService.addEdge("A", "B", 1)).thenReturn(true);
        ExecResult added = handler.addEdge(session, "A", "B", "1");
        assertEquals(ExecResult.intermediate("EDGE ADDED"), added);
    }

    @Test
    public void testAddEdgeErr() {
        ExecResult notFound = handler.addEdge(session,"A", "B", "1");
        assertEquals(ExecResult.intermediate("ERROR: NODE NOT FOUND"), notFound);
    }

    @Test(expected = NumberFormatException.class)
    public void testAddEdgeNonIntWeight() {
        handler.addEdge(session, "A", "B", "1.5");
    }

    @Test
    public void testRemoveEdgeOk() {
        when(graphService.removeEdge("A", "B")).thenReturn(true);
        ExecResult removed = handler.removeEdge(session, "A", "B");
        assertEquals(ExecResult.intermediate("EDGE REMOVED"), removed);
    }

    @Test
    public void testRemoveEdgeErr() {
        when(graphService.removeEdge("A", "B")).thenReturn(false);
        ExecResult notFound = handler.removeEdge(session,"A", "B");
        assertEquals(ExecResult.intermediate("ERROR: NODE NOT FOUND"), notFound);
    }

}
