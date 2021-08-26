package com.collibra.pcos.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerUtils {

    public static Logger getLogger() {
        return LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
    }

}
