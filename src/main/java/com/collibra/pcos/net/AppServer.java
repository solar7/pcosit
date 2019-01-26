package com.collibra.pcos.net;

import com.collibra.pcos.services.SessionFactory;
import com.collibra.pcos.session.Session;
import com.collibra.pcos.utils.ExceptionHandler;
import com.collibra.pcos.utils.IOUtils;
import com.collibra.pcos.utils.LoggerUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.collibra.pcos.properties.ApplicationProperties.SERVER_PORT;
import static com.collibra.pcos.properties.ApplicationProperties.SERVER_SIMULTANEOUS_CONNECTIONS;
import static com.collibra.pcos.properties.ApplicationProperties.SERVER_TIMEOUT;
import static com.collibra.pcos.utils.ApplicationContext.getBean;

public class AppServer {

    private static final Logger LOGGER = LoggerUtils.getLogger();
    private static final int CHECK_IDLE_CONNECTIONS_EVERY_MS = 1_000;

    private final int TIMEOUT = SERVER_TIMEOUT.getIntValue();
    private final int numSimConn = SERVER_SIMULTANEOUS_CONNECTIONS.getIntValue();

    private final ExecutorService executorService;
    private final List<Connection> connections = new CopyOnWriteArrayList<>();
    private final SessionFactory sessionFactory = getBean("sessionFactory");

    public AppServer() {
        executorService = Executors.newFixedThreadPool(numSimConn);
    }

    public void start() {
        final int serverPort = SERVER_PORT.getIntValue();
        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            serverSocket.setSoTimeout(CHECK_IDLE_CONNECTIONS_EVERY_MS);
            LOGGER.info("opened {} port, waiting for connections...", serverPort);
            while (true) {
                waitForConnection(serverSocket);
            }
        } catch (IOException e) {
            LOGGER.error("can't open server socket", e);
        }
    }

    private void waitForConnection(ServerSocket serverSocket) throws IOException {
        try {
            acceptNewClient(serverSocket.accept());
        } catch (SocketTimeoutException e) {
            ExceptionHandler.suppress(e);
            killInactiveSessions();
        } catch (Exception e) {
            throw e;
        }
    }

    private void acceptNewClient(Socket clientSocket) {
        LOGGER.info("new client connected {}", clientSocket.getInetAddress());
        try {
            Session session = sessionFactory.createNewSession();
            Connection connection = new Connection(clientSocket, session);
            ConnectionTask connectionTask = new ConnectionTask(connection);
            executorService.submit(connectionTask);
            connections.add(connection);
        } catch (IOException e) {
            LOGGER.error("can't create connection", e);
            IOUtils.close(clientSocket);
        }
    }

    private void killInactiveSessions() {
        for (Connection connection : connections) {
            if (connection.isActive()) {
                long idleTimeInMs = connection.getSession().getIdleTimeInMs();
                String sessionId = connection.getSession().getSessionId();
                if (idleTimeInMs > TIMEOUT) {
                    LOGGER.info("timeout inactive {}ms, killing connection {}", idleTimeInMs, sessionId);
                    connections.remove(connection);
                    connection.close();
                } else {
                    LOGGER.trace("idle time {}ms, {}", idleTimeInMs, sessionId);
                }
            }
        }
    }

}
