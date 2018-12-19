package it.unibo.coordination;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Engine {

    private static ExecutorService defaultEngine;

    private Engine() {
    }

    public static ExecutorService getDefaultEngine() {
        if (defaultEngine == null) {
            defaultEngine = Executors.newSingleThreadExecutor();
        }
        return defaultEngine;
    }
}
