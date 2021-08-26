package com.collibra.pcos.net;

import com.collibra.pcos.utils.LoggerUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.collibra.pcos.properties.ApplicationProperties.SERVER_PORT;
import static com.collibra.pcos.properties.ApplicationProperties.SERVER_SIMULTANEOUS_CONNECTIONS;

public class AppServer {

    private static final Logger LOGGER = LoggerUtils.getLogger();

    private final ExecutorService executorService;

    public AppServer() {
        final int numSimConn = SERVER_SIMULTANEOUS_CONNECTIONS.getIntValue();
        executorService = Executors.newFixedThreadPool(numSimConn);
    }

    public void start() {
        final int serverPort = SERVER_PORT.getIntValue();
        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            LOGGER.info("opened {} port, waiting for connections...", serverPort);
            while (true) {
                acceptNewClient(serverSocket.accept());
            }
        } catch (IOException e) {
            LOGGER.error("can't open server socket", e);
        }
    }

    private void acceptNewClient(Socket socket) {
        executorService.submit(new ConnectionTask(socket));
        LOGGER.info("new client connected {}", socket.getInetAddress());
    }

}
