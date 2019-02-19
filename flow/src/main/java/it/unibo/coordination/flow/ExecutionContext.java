package it.unibo.coordination.flow;

import java.util.Objects;
import java.util.Optional;

public final class ExecutionContext<A, C, R> {

    private final A arguments;
    private final Activity<A, C, R> activity;
    private Continuation<C> context = Continuation.start(null);
    private R result;
    private Exception exception;

    private ExecutionContext(A arguments, Activity<A, C, R> activity) {
        this.arguments = arguments;
        this.activity = Objects.requireNonNull(activity);
    }

    public void schedule() {
        try {
            if (context.isStart()) {
                context = Objects.requireNonNull(activity.onBegin(arguments));
            } else if (context.isNext()) {
                context = Objects.requireNonNull(activity.onStep(context.getValue()));
            } else if (context.isStop()) {
                result = activity.onEnd(context.getValue(), Optional.ofNullable(exception));
            }
        } catch (Exception e) {
            exception = e;
            context = Objects.requireNonNull(activity.onError(context.getValue(), e));
        }
    }

    public boolean isDone() {
        return result 
    }

    public static <A, C, R> ExecutionContext<A, C, R> of(Activity<A, C, R> activity, A arguments) {
        return new ExecutionContext<>(arguments, activity);
    }
}
