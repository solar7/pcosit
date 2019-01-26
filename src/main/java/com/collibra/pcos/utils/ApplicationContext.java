package com.collibra.pcos.utils;

import com.collibra.pcos.graph.ShortestPathsGraphTraversalImpl;
import com.collibra.pcos.net.AppServer;
import com.collibra.pcos.services.impl.CommandProcessorImpl;
import com.collibra.pcos.services.impl.GraphServiceImpl;
import com.collibra.pcos.services.impl.SessionFactoryImpl;
import com.collibra.pcos.services.impl.UuidBasedSessionIdGenerator;
import com.collibra.pcos.utils.annotations.scanners.DefaultHandlerScannerSingleton;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.graph.NetworkBuilder.directed;

public class ApplicationContext {

    private static final Map<String, Object> BEANS = new ConcurrentHashMap<>();

    static {
        createBeans();
    }

    private static void createBeans() {
        BEANS.put("idGenerator", new UuidBasedSessionIdGenerator());
        BEANS.put("sessionFactory", new SessionFactoryImpl());
        BEANS.put("appServer", new AppServer());
        BEANS.put("messageFormatter", MessageUtils.getMessageFormatter());
        BEANS.put("graph", directed().allowsParallelEdges(true).allowsSelfLoops(true).build());
        BEANS.put("graphAlgorithm", new ShortestPathsGraphTraversalImpl());
        BEANS.put("graphService", new GraphServiceImpl());
        BEANS.put("handlerScanner", DefaultHandlerScannerSingleton.getInstance());
        BEANS.put("commandProcessor", new CommandProcessorImpl());
    }

    public static <T> T getBean(String beanName) {
        Object bean = BEANS.get(beanName);
        Objects.requireNonNull(bean, "bean \"" + beanName + "\" not found");
        return (T) bean;
    }

    public static <T> T getBean(String beanName, Class<T> clazz) {
        return (T) getBean(beanName);
    }

    // default access modifier, these methods were added only for testing purposes

    static Map<String, Object> getBeans() {
        return BEANS;
    }

    static void load() {
        createBeans();
    }

}
