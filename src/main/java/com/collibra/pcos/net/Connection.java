package com.collibra.pcos.net;

import com.collibra.pcos.session.Session;
import com.collibra.pcos.utils.LoggerUtils;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

public class Connection implements Closeable {

    private static final Logger LOGGER = LoggerUtils.getLogger();

    private static final String DEFAULT_CHARSET_NAME = "ASCII";

    private final Socket socket;
    private final PrintWriter output;
    private final BufferedReader input;
    private final Consumer<Connection> closingListener;
    private final Session session;

    public Connection(Socket clientSocket, Session theSession, Consumer<Connection> closingListener) throws IOException {
        this.socket = clientSocket;
        this.session = theSession;
        this.closingListener = closingListener;
        this.output = new PrintWriter(clientSocket.getOutputStream(), true);
        this.input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), DEFAULT_CHARSET_NAME));
    }

    public Session getSession() {
        return session;
    }

    public void println(String str) {
        output.println(str);
    }

    public boolean isReady() throws IOException {
        return input.ready();
    }

    public String readLn() throws IOException {
        return input.readLine();
    }

    @Override
    public void close() {
        closingListener.accept(this);
        close(input, output, socket);
    }

    private void close(Closeable...closeables) {
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    LOGGER.error("closing I/O", e);
                }
            }
        }
    }

}
