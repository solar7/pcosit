package com.collibra.pcos.net;

import com.collibra.pcos.services.CommandProcessor;
import com.collibra.pcos.session.ExecResult;
import com.collibra.pcos.session.ExecResult.ExecCode;
import com.collibra.pcos.session.Session;
import com.collibra.pcos.utils.LoggerUtils;
import org.slf4j.Logger;

import java.io.IOException;

import static com.collibra.pcos.utils.ApplicationContext.getBean;
import static java.lang.Thread.currentThread;

class ConnectionTask implements Runnable {

    private static final Logger LOGGER = LoggerUtils.getLogger();

    private final Session session;
    private final Connection connection;

    private final CommandProcessor commandProcessor = getBean("commandProcessor");

    public ConnectionTask(Connection connection) {
        this.connection = connection;
        this.session = connection.getSession();
    }

    @Override
    public void run() {
        try {
            afterOpeningConnection();
            mainProcessingCycle();
            beforeClosingConnection();
        } catch (Exception e) {
            LOGGER.error("main session cycle", e);
        } finally {
            connection.close();
        }
    }

    private void mainProcessingCycle() throws IOException {
        while (processCommand(connection.readLn())) {
            connection.getSession().registerClientActivity();
        }
    }

    private void afterOpeningConnection() {
        LOGGER.info("thread [{}] handling new session {}", currentThread().getName(), session.getSessionId());
        ExecResult execResult = commandProcessor.execSayHello(session);
        execResult.getOut().ifPresent(connection::println);
    }

    private boolean processCommand(String command) {
        ExecResult result = commandProcessor.process(command, session);
        result.getOut().ifPresent(connection::println);
        return ExecCode.INTERMEDIATE == result.getCode() ? true : false;
    }

    private void beforeClosingConnection() {
        if (!session.wasGoodByeSaid()) {
            ExecResult execResult = commandProcessor.execSayGoodBye(session);
            execResult.getOut().ifPresent(connection::println);
        }
    }

}
