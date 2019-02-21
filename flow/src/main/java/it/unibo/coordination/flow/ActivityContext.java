package it.unibo.coordination.flow;

import java.util.concurrent.CompletableFuture;

public interface ActivityContext<A, C, R> {
    Continuation<C> schedule();

    boolean isDone();

    CompletableFuture<R> getResult();

    Activity<A, C, R> getActivity();

    A getArguments();

    static <A, C, R> ActivityContext<A, C, R> of(Activity<A, C, R> activity, A arguments) {
        return new ActivityContextImpl<>(arguments, activity);
    }
}
