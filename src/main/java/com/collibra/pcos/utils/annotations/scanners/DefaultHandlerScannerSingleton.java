package com.collibra.pcos.utils.annotations.scanners;

public class DefaultHandlerScannerSingleton {

    private static final HandlerScanner INSTANCE = new AnnotationBasedHandlerScannerImpl();

    private DefaultHandlerScannerSingleton() {
    }

    public static HandlerScanner getInstance() {
        return INSTANCE;
    }

}
