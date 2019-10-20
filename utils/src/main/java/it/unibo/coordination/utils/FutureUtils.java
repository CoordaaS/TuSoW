package it.unibo.coordination.utils;

import java.time.Duration;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class FutureUtils {

    public static <X> CompletableFuture<X> onCancel(CompletableFuture<X> promise, Consumer<? super Throwable> action) {
        promise.whenComplete((result, exception) -> {
            if (exception instanceof CancellationException) {
                action.accept(exception);
            }
        });
        return promise;
    }

    public static void propagateCancel(CompletableFuture<?> promise1, CompletableFuture<?> promise2) {
        onCancel(promise1, e -> promise2.cancel(true));
    }

    public static <X, Y> CompletableFuture<Y> applyAndPropagateCancel(CompletableFuture<X> promise, Function<X, Y> mapper) {
        var application = promise.thenApplyAsync(mapper);
        propagateCancel(promise, application);
        return application;
    }

    public static <X, Y> CompletableFuture<Y> applyAndPropagateCancelAsync(CompletableFuture<X> promise, Function<X, Y> mapper, Executor executor) {
        var application = promise.thenApplyAsync(mapper, executor);
        propagateCancel(promise, application);
        return application;
    }

    public static <X, Y> CompletableFuture<Y> applyAndPropagateCancelAsync(CompletableFuture<X> promise, Function<X, Y> mapper) {
        return applyAndPropagateCancelAsync(promise, mapper, promise.defaultExecutor());
    }

    public static <X> X await(Future<X> future) {
        try {
            return future.get();
        } catch (InterruptedException e) {
            throw new SilentInterruptedException(e);
        } catch (ExecutionException e) {
            throw new SilentExecutionException(e);
        }
    }

    public static <X> X await(Future<X> future, Duration duration) {
        try {
            return future.get(duration.toMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new SilentInterruptedException(e);
        } catch (ExecutionException e) {
            throw new SilentExecutionException(e);
        } catch (TimeoutException e) {
            throw new SilentTimeoutException(e);
        }
    }

    public static class SilentExecutionException extends SilentException {

        public SilentExecutionException(String message, ExecutionException cause) {
            super(message, cause);
        }

        public SilentExecutionException(ExecutionException cause) {
            super(cause);
        }

        public SilentExecutionException(String message, ExecutionException cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }

        @Override
        public synchronized ExecutionException getCause() {
            return (ExecutionException) super.getCause();
        }
    }

    public static class SilentInterruptedException extends SilentException {
        public SilentInterruptedException(String message, InterruptedException cause) {
            super(message, cause);
        }

        public SilentInterruptedException(InterruptedException cause) {
            super(cause);
        }

        public SilentInterruptedException(String message, InterruptedException cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }

        @Override
        public synchronized InterruptedException getCause() {
            return (InterruptedException) super.getCause();
        }
    }

    public static class SilentTimeoutException extends SilentException {
        public SilentTimeoutException(String message, TimeoutException cause) {
            super(message, cause);
        }

        public SilentTimeoutException(TimeoutException cause) {
            super(cause);
        }

        public SilentTimeoutException(String message, TimeoutException cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }

        @Override
        public synchronized TimeoutException getCause() {
            return (TimeoutException) super.getCause();
        }
    }
}
