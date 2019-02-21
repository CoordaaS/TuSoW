package it.unibo.coordination.flow;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class Activities {

    private Activities() { }

    public static <A, C, R> Runner<A, C, R> synchronousRunner(A args, Activity<A, C, R> activity) {
        return Runner.sync(activity, args);
    }

    public static <A, C, R> Runner<A, C, R> backgroundRunner(A args, Activity<A, C, R> activity) {
        return Runner.background(activity, args);
    }

    public static <A, C, R> Runner<A, C, R> executorRunner(Executor executor, A args, Activity<A, C, R> activity) {
        return Runner.executor(activity, args, executor);
    }

    public static <A, C, R> CompletableFuture<R> runSynchronously(A args, Activity<A, C, R> activity) {
        return synchronousRunner(args, activity).start();
    }

    public static <A, C, R> CompletableFuture<R> runInBackground(A args, Activity<A, C, R> activity) {
        return backgroundRunner(args, activity).start();
    }

    public static <A, C, R> CompletableFuture<R> runOnExecutor(Executor executor, A args, Activity<A, C, R> activity) {
        return executorRunner(executor, args, activity).start();
    }
}
