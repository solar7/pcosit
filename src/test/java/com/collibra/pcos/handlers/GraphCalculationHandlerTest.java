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

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GraphCalculationHandlerTest {

    @Mock
    private GraphService<String> graphService;

    private final Session session =  new Session("SESSION-ID");

    private GraphCalculationHandler handler;

    @BeforeClass
    public static void init() {
        ApplicationTestContext.load();
    }

    @Before
    public void setUp() {
        ApplicationTestContext.clearAndPut("graphService", graphService);
        handler = new GraphCalculationHandler();
    }

    @Test
    public void testShortestPathOk() {
        when(graphService.shortestPath( "A", "B")).thenReturn(100);
        when(graphService.hasNode("A")).thenReturn(true);
        when(graphService.hasNode("B")).thenReturn(true);

        ExecResult result = handler.shortestPath(session, "A", "B");
        assertEquals(ExecResult.intermediate("100"), result);
    }

    @Test
    public void testShortestPathErr() {
        ExecResult resultErr = handler.shortestPath(session, "A", "B");
        assertEquals(ExecResult.intermediate("ERROR: NODE NOT FOUND"), resultErr);
    }

    @Test
    public void testCloserThanOkEmpty() {
        when(graphService.closerThan( 100, "A")).thenReturn(Collections.emptyList());
        when(graphService.hasNode("A")).thenReturn(true);
        ExecResult resultErr = handler.closerThan(session, "100", "A");
        assertEquals(ExecResult.intermediate(""), resultErr);
    }

    @Test
    public void testCloserThanOkList() {
        when(graphService.closerThan( 100, "A")).thenReturn(Arrays.asList("B", "C", "D"));
        when(graphService.hasNode("A")).thenReturn(true);
        ExecResult resultErr = handler.closerThan(session, "100", "A");
        assertEquals(ExecResult.intermediate("B,C,D"), resultErr);
    }

    @Test
    public void testCloserThanErr() {
        ExecResult resultErr = handler.closerThan(session, "100", "A");
        assertEquals(ExecResult.intermediate("ERROR: NODE NOT FOUND"), resultErr);
    }


    @Test(expected = NumberFormatException.class)
    public void testAddEdgeNonIntWeight() {
        handler.closerThan(session,  "1.5", "A");
    }

}
