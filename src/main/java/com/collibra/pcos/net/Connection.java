package com.collibra.pcos.net;

import com.collibra.pcos.session.Session;
import com.collibra.pcos.utils.ExceptionHandler;
import com.collibra.pcos.utils.IOUtils;
import com.collibra.pcos.utils.LoggerUtils;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Connection implements Closeable {

    private static final Logger LOGGER = LoggerUtils.getLogger();

    private static final String DEFAULT_CHARSET_NAME = "ASCII";

    private final Socket socket;
    private final PrintWriter output;
    private final BufferedReader input;
    private final Session session;

    private volatile boolean closed;

    public Connection(Socket clientSocket, Session theSession) throws IOException {
        this.output = new PrintWriter(clientSocket.getOutputStream(), true);
        this.input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), DEFAULT_CHARSET_NAME));
        this.socket = clientSocket;
        this.session = theSession;
    }

    public Session getSession() {
        return session;
    }

    public void println(String str) {
        LOGGER.info("session {} << [{}]", session.getSessionId(), str);
        output.println(str);
    }

    public String readLn() throws IOException {
        String command = input.readLine();
        LOGGER.info("session {} >> [{}]", session.getSessionId(), command);
        return command;
    }

    public boolean isActive() {
        return !closed;
    }

    @Override
    public void close() {
        IOUtils.close(input, output, socket);
        closed = true;
    }

}
