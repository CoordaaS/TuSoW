package it.unibo.coordination.flow;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

final class ActivityContextImpl<A, C, R> implements ActivityContext<A, C, R> {

    private final A arguments;
    private final Activity<A, C, R> activity;
    private final CompletableFuture<R> result = new CompletableFuture<>();

    private Continuation<C> context = Continuation.start(null);
    private Exception exception;

    public ActivityContextImpl(A arguments, Activity<A, C, R> activity) {
        this.arguments = arguments;
        this.activity = Objects.requireNonNull(activity);
    }

    public Continuation<C> schedule() {
        if (context != null) {
            try {
                if (context.isStart()) {
                    context = Objects.requireNonNull(activity.onBegin(arguments));
                } else if (context.isNext()) {
                    context = Objects.requireNonNull(activity.onStep(context.getValue()));
                } else if (context.isStop()) {
                    result.complete(activity.onEnd(context.getValue(), Optional.ofNullable(exception)));
                    context = null;
                }
            } catch (Exception e) {
                exception = e;
                context = Objects.requireNonNull(activity.onError(context.getValue(), e));
            }
        }
        return context;
    }

    public boolean isDone() {
        return result.isDone();
    }

    public CompletableFuture<R> getResult() {
        return result;
    }

    public Activity<A, C, R> getActivity() {
        return activity;
    }

    public A getArguments() {
        return arguments;
    }

}
