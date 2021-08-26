package com.collibra.pcos.handlers;

import com.collibra.pcos.session.ExecResult;
import com.collibra.pcos.session.ExecResult.ExecCode;
import com.collibra.pcos.session.Session;
import com.collibra.pcos.utils.ApplicationTestContext;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HandshakeSessionHandlerTest {

    private final String sessionId = "SESSION-ID";
    private final String clientName = "CLIENT";
    private final Session session = new Session(sessionId);

    private HandshakeSessionHandler handler = new HandshakeSessionHandler();

    @BeforeClass
    public static void init() {
        ApplicationTestContext.load();
    }

    @Test
    public void testOpenConnection() {
        assertEquals(ExecResult.intermediate("HI, I'M " + sessionId), handler.openConnection(session));
    }

    @Test
    public void testHandshake() {
        assertEquals(ExecResult.intermediate("HI " + clientName), handler.handshake(session, clientName));
    }

    @Test
    public void testCloseSession() {
        session.setClientName(clientName);
        ExecResult execResult = handler.closeSession(session);
        assertEquals(ExecCode.TERMINATE ,execResult.getCode());
        assertTrue(execResult.getOut().get().matches("BYE " + clientName + ", WE SPOKE FOR ([0-9]+) MS"));
    }

    @Test
    public void testUnknownCommand() {
        assertEquals(ExecResult.intermediate("SORRY, I DIDN'T UNDERSTAND THAT"), handler.unknownCommand(session));
    }

}
