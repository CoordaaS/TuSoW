package it.unibo.coordination.flow;

import java.util.concurrent.CompletableFuture;

class SyncRunner<A, C, R> implements Runner<A, C, R> {

    private A args;
    private ActivityContext<A, C, R> executionContext;

    public SyncRunner(Activity<A, C, R> activity, A args) {
        executionContext = ActivityContext.of(activity, args);
    }

    @Override
    public A getArgs() {
        return args;
    }

    @Override
    public CompletableFuture<R> start() {
        while(!executionContext.isDone()) {
            executionContext.schedule();
        }
        return executionContext.getResult();
    }

    @Override
    public ActivityContext<A, C, R> getContext() {
        return executionContext;
    }
}
