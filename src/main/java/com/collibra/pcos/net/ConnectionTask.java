package com.collibra.pcos.net;

import com.collibra.pcos.services.CommandProcessor;
import com.collibra.pcos.services.SessionFactory;
import com.collibra.pcos.session.ExecResult;
import com.collibra.pcos.session.ExecResult.ExecCode;
import com.collibra.pcos.session.Session;
import com.collibra.pcos.utils.LoggerUtils;
import org.slf4j.Logger;

import java.net.Socket;

import static com.collibra.pcos.utils.ApplicationContext.getBean;
import static java.lang.Thread.currentThread;

class ConnectionTask implements Runnable {

    private static final Logger LOGGER = LoggerUtils.getLogger();

    private final Socket socket;

    private final CommandProcessor commandProcessor = getBean("commandProcessor");
    private final SessionFactory sessionFactory = getBean("sessionFactory");

    public ConnectionTask(Socket clientSocket) {
        socket = clientSocket;
    }

    @Override
    public void run() {
        Session session = sessionFactory.createNewSession();
        try (Connection connection = new Connection(socket, session, this::beforeClosingConnection)) {
            afterOpeningConnection(connection);
            while (!currentThread().isInterrupted()) {
                if (connection.isReady()) {
                    session.registerClientActivity();
                    processCommand(connection.readLn(), connection);
                }
            }
        } catch (Exception e) {
            LOGGER.error("main session cycle", e);
        }
    }

    private void afterOpeningConnection(Connection connection) {
        Session session = connection.getSession();
        LOGGER.info("thread [{}] opened new session {}", currentThread().getName(), session.getSessionId());
        ExecResult execResult = commandProcessor.execSayHello(session);
        execResult.getOut().ifPresent(connection::println);
    }

    private void processCommand(String command, Connection connection) {
        Session session = connection.getSession();
        LOGGER.info("session {} >> [{}]", session.getSessionId(), command);

        ExecResult result = commandProcessor.process(command, session);
        result.getOut().ifPresent((out) -> printOutput(out, connection));

        if (ExecCode.TERMINATE == result.getCode()) {
            handleTerminateCommand(session);
        }
    }

    private void printOutput(String out, Connection connection) {
        connection.println(out);
        LOGGER.info("session {} << [{}]", connection.getSession().getSessionId(), out);
    }

    private void handleTerminateCommand(Session session) {
        session.goodByeWasSaid();
        currentThread().interrupt();
    }

    private void beforeClosingConnection(Connection connection) {
        sayGoodBye(connection);
        connection.getSession().stopSessionWatchDog();
        LOGGER.info("successfully closed {}", connection.getSession().getSessionId());
    }

    private void sayGoodBye(Connection connection) {
        Session session = connection.getSession();
        if (!session.wasGoodByeSaid()) {
            ExecResult execResult = commandProcessor.execSayGoodBye(session);
            execResult.getOut().ifPresent(connection::println);
        }
    }

}
