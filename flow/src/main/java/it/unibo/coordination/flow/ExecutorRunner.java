package it.unibo.coordination.flow;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

class ExecutorRunner<A, C, R> extends SyncRunner<A, C, R> {

    private final Executor executor;

    public ExecutorRunner(Activity<A, C, R> activity, A args, Executor executor) {
        super(activity, args);
        this.executor = Objects.requireNonNull(executor);
    }


    @Override
    public CompletableFuture<R> start() {
        run();
        return getContext().getResult();
    }

    private void run() {
        executor.execute(() -> {
            getContext().schedule();
            run();
        });
    }
}
