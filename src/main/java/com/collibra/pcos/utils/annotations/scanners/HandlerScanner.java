package com.collibra.pcos.utils.annotations.scanners;

import java.lang.reflect.Method;
import java.util.Set;

public interface HandlerScanner {

    Set<Method> getHandlers();

}
