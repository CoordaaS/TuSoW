package it.unibo.coordination.flow;

import java.util.concurrent.CompletableFuture;

class ThreadRunner<A, C, R> extends SyncRunner<A, C, R> {


    private final Thread thread = new Thread(super::start);

    public ThreadRunner(Activity<A, C, R> activity, A args) {
        super(activity, args);
    }

    @Override
    public CompletableFuture<R> start() {
        final var result = getContext().getResult();
        thread.start();
        return result;
    }
}
