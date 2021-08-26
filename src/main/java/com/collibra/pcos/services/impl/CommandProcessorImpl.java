package com.collibra.pcos.services.impl;

import com.collibra.pcos.services.CommandProcessor;
import com.collibra.pcos.session.ExecResult;
import com.collibra.pcos.session.Session;
import com.collibra.pcos.utils.LoggerUtils;
import com.collibra.pcos.utils.annotations.Command;
import com.collibra.pcos.utils.annotations.scanners.HandlerScanner;
import org.slf4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.collibra.pcos.utils.ApplicationContext.getBean;

public class CommandProcessorImpl implements CommandProcessor {

    private static final Logger LOGGER = LoggerUtils.getLogger();

    private static final int MAX_COMMAND_LENGTH = 500;

    private final Map<Method, Object> handlersCache;

    private final HandlerScanner handlerScanner = getBean("handlerScanner");

    public CommandProcessorImpl() {
        Map<Method, Object> cache = handlerScanner.getHandlers().stream()
                .collect(Collectors.toMap(Function.identity(), this::newInstance));
        handlersCache = Collections.unmodifiableMap(cache);
    }

    @Override
    public ExecResult process(String cmd, Session session) {
        Method handler = findAppropriateHandler(cmd);
        Optional<String> pattern = getPatternValueFromAnnotatedMethod(handler);
        return executeCommand(handler, pattern, cmd, session);
    }

    @Override
    public ExecResult execSayHello(Session session) {
        return invokeMethodSilent(getHelloHandler(), session);
    }

    @Override
    public ExecResult execSayGoodBye(Session session) {
        return invokeMethodSilent(getGoodByeHandler(), session);
    }

    private ExecResult invokeMethodSilent(Method method, Object...args) {
        try {
            return invokeMethod(method, args);
        } catch (Exception e) {
            LOGGER.error("invoke silent", e);
            return ExecResult.intermediate();
        }
    }

    private ExecResult invokeMethod(Method method, Object...args) throws IllegalAccessException, InvocationTargetException {
        Object instance = handlersCache.get(method);
        return (ExecResult) method.invoke(instance, args);
    }

    private ExecResult executeCommand(Method handler, Optional<String> pattern, String cmd, Session session) {
        try {
            Object[] args = parseArgs(cmd, pattern, session);
            return invokeMethod(handler, args);
        } catch (InvocationTargetException e) {
            LOGGER.debug("invocation handler", e);
            String message = e.getTargetException().getMessage();
            return ExecResult.intermediate("ERROR: " + message);
        } catch (Exception e) {
            LOGGER.error("execute command", e);
            return ExecResult.intermediate();
        }
    }

    private Method findAppropriateHandler(String cmd) {
        return validate(cmd) ? handlersCache.keySet().stream()
                .filter(m -> matchPattern(m, cmd))
                .findFirst()
                .orElse(getErrorHandler()) : getErrorHandler();
    }

    private Method getHelloHandler() {
        return findMethod("hello", m -> m.getAnnotation(Command.class).hello());
    }

    private Method getGoodByeHandler() {
        return findMethod("goodbye", m -> m.getAnnotation(Command.class).goodbye());
    }

    private Method getErrorHandler() {
        return findMethod("error", m -> m.getAnnotation(Command.class).error());
    }

    private Method findMethod(String type, Predicate<Method> predicate) {
        return handlersCache.keySet().stream()
                .filter(predicate)
                .findFirst()
                .orElseThrow(() -> new RuntimeException(type + " handler was not found"));
    }

    private boolean matchPattern(Method method, String cmd) {
        return getPatternValueFromAnnotatedMethod(method)
                .map(cmd::matches)
                .orElse(false);
    }

    private Optional<String> getPatternValueFromAnnotatedMethod(Method method) {
        String pattern = method.getAnnotation(Command.class).pattern();
        return pattern.isEmpty() ? Optional.empty() : Optional.of(toRegEx(pattern));
    }

    private String toRegEx(String pattern) {
        return "^" + pattern + "$";
    }

    private Object[] parseArgs(String cmd, Optional<String> pattern, Session session) {
        List<Object> args = new ArrayList<>();

        args.add(session); //first argument is always session

        pattern.map(regex -> parseArgs(cmd, regex))
               .ifPresent(args::addAll);

        return args.toArray();
    }

    private List<String> parseArgs(String text, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(text);
        if (matcher.find()) {
            return IntStream.rangeClosed(1, matcher.groupCount())
                    .mapToObj(matcher::group)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private Object newInstance(Method method) {
        try {
            return method.getDeclaringClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean validate(String command) {
        return !command.isEmpty() && command.length() < MAX_COMMAND_LENGTH;
    }

}
