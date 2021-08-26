package com.collibra.pcos.utils.annotations.scanners;

import com.collibra.pcos.utils.LoggerUtils;
import com.collibra.pcos.utils.annotations.Command;
import com.collibra.pcos.utils.annotations.Handler;
import com.google.common.reflect.ClassPath;
import org.slf4j.Logger;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class AnnotationBasedHandlerScannerImpl implements HandlerScanner {

    private static final Logger LOGGER = LoggerUtils.getLogger();
    private static final String DEFAULT_PACKAGE_FOR_SCANNING = "com.collibra.pcos.handlers";

    private final Set<Method> handlers;

    public AnnotationBasedHandlerScannerImpl() {
        this(DEFAULT_PACKAGE_FOR_SCANNING);
    }

    public AnnotationBasedHandlerScannerImpl(String packageForScanning) {
        Set<Method> foundMethods = scan(packageForScanning);
        handlers = Collections.unmodifiableSet(foundMethods);
        LOGGER.debug("registered handlers: {}", handlers);
    }

    private Set<Method> scan(String packageForScanning) {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            return scan(ClassPath.from(classLoader), packageForScanning);
        } catch (IOException e) {
            LOGGER.error("class scanning exception", e);
            return Collections.emptySet();
        }
    }

    private Set<Method> scan(ClassPath classPath, String packageForScanning) {
        return classPath.getTopLevelClasses(packageForScanning).stream()
                .map(ClassPath.ClassInfo::load)
                .filter(clazz -> clazz.getAnnotation(Handler.class) != null)
                .flatMap(clazz -> Stream.of(clazz.getMethods()))
                .filter(method -> method.getDeclaredAnnotation(Command.class) != null)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Method> getHandlers() {
        return handlers;
    }

}
