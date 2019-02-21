package it.unibo.coordination.flow;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public interface Runner<A, C, R> {

    A getArgs();

    CompletableFuture<R> start();

    ActivityContext<A, C, R> getContext();

    static <A, C, R> Runner<A, C, R> sync(Activity<A, C, R> activity, A args) {
        return new SyncRunner<>(activity, args);
    }

    static <A, C, R> Runner<A, C, R> background(Activity<A, C, R> activity, A args) {
        return new ThreadRunner<>(activity, args);
    }

    static <A, C, R> Runner<A, C, R> executor(Activity<A, C, R> activity, A args, Executor e) {
        return new ThreadRunner<>(activity, args);
    }
}
