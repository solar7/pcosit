package com.collibra.pcos.utils;

import com.collibra.pcos.properties.ApplicationPropertiesLoader;

import java.util.Map;

public class ApplicationTestContext {

    public static void putBean(String beanName, Object bean) {
        ApplicationContext.getBeans().put(beanName, bean);
    }

    public static void clearAndPut(String beanName, Object bean) {
        clear();
        putBean(beanName, bean);
    }

    public static Map<String, Object> getBeans() {
        return ApplicationContext.getBeans();
    }

    public static void clear() {
        ApplicationContext.getBeans().clear();
    }

    public static void load() {
        ApplicationPropertiesLoader.loadProperties();
        ApplicationContext.load();
    }

}
