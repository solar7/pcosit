package com.collibra.pcos.services.impl;

import com.collibra.pcos.services.CommandProcessor;
import com.collibra.pcos.session.ExecResult;
import com.collibra.pcos.session.Session;
import com.collibra.pcos.utils.ApplicationTestContext;
import com.collibra.pcos.utils.annotations.Command;
import com.collibra.pcos.utils.annotations.scanners.HandlerScanner;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.collibra.pcos.utils.ApplicationTestContext.clearAndPut;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommandProcessorImplTest {

    @Mock
    private HandlerScanner handlerScanner;

    private final Session session =  new Session("SESSION-ID");

    private static final String HELLO = "HELLO";
    private static final String GOODBYE = "GOODBYE";
    private static final String ERROR = "ERROR";
    private static final String OK_CMD = "OK CMD";
    private static final String ARG = "AaBbCb-12345-123dD123";

    private CommandProcessor testCmdProc;

    @BeforeClass
    public static void init() {
        ApplicationTestContext.load();
    }

    @Before
    public void setUp() {
        clearAndPut("handlerScanner", handlerScanner);
        when(handlerScanner.getHandlers()).thenReturn(TestHandler.getMethods());
        testCmdProc = new CommandProcessorImpl();
    }

    @Test
    public void testSayHelloHandler() {
        assertEquals(ExecResult.intermediate(HELLO), testCmdProc.execSayHello(session));
    }

    @Test
    public void testSayGoodByeHandler() {
        assertEquals(ExecResult.terminate(GOODBYE), testCmdProc.execSayGoodBye(session));
    }

    @Test
    public void testErrorHandler() {
        assertEquals(ExecResult.intermediate(ERROR), testCmdProc.process("XYZ", session));
    }

    @Test
    public void testProcessHandler1() {
        assertEquals(ExecResult.intermediate(OK_CMD), testCmdProc.process(OK_CMD, session));
    }

    @Test
    public void testProcessHandler2() {
        assertEquals(ExecResult.intermediate(ARG), testCmdProc.process(OK_CMD + " " + ARG, session));
    }

    static class TestHandler {

        @Command(hello = true)
        public ExecResult hello(Session session) {
            return ExecResult.intermediate(HELLO);
        }

        @Command(goodbye = true)
        public ExecResult goodbye(Session session) {
            return ExecResult.terminate(GOODBYE);
        }

        @Command(error = true)
        public ExecResult error(Session session) {
            return ExecResult.intermediate(ERROR);
        }

        @Command(pattern = OK_CMD)
        public ExecResult okCmd(Session session) {
            return ExecResult.intermediate(OK_CMD);
        }

        @Command(pattern = OK_CMD + " ([a-zA-Z0-9_-]+)")
        public ExecResult okCmdArg(Session session, String nodeName) {
            return ExecResult.intermediate(nodeName);
        }

        static Set<Method> getMethods() {
            return Stream.of(TestHandler.class.getMethods())
                    .filter(m -> m.getAnnotation(Command.class) != null)
                    .collect(Collectors.toSet());
        }

    }

}
