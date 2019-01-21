package com.collibra.pcos;

import com.collibra.pcos.net.AppServer;
import com.collibra.pcos.properties.ApplicationPropertiesLoader;
import com.collibra.pcos.utils.ApplicationContext;

public class Main {

    static {
        ApplicationPropertiesLoader.loadProperties();
    }

    public static void main(String[] args) {
        ApplicationContext.getBean("appServer", AppServer.class).start();
    }
}
