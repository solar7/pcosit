package com.collibra.pcos.utils;

import org.slf4j.Logger;

import java.io.Closeable;
import java.io.IOException;

public class IOUtils {

    private static final Logger LOGGER = LoggerUtils.getLogger();

    public static void close(Closeable...closeables) {
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    LOGGER.error("closing i/o", e);
                }
            }
        }
    }

}
