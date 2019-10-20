package it.unibo.coordination.linda.core;

import it.unibo.coordination.utils.FutureUtils;
import org.apache.commons.collections4.MultiSet;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public interface TupleSpace<T extends Tuple, TT extends Template, K, V> {
    CompletableFuture<Match<T, TT, K, V>> read(TT template);

    default CompletableFuture<T> readTuple(TT template) {
        return FutureUtils.applyAndPropagateCancelAsync(
                read(template),
                match -> match.getTuple().get(),
                getExecutor()
        );
    }

    CompletableFuture<Match<T, TT, K, V>> take(TT template);

    default CompletableFuture<T> takeTuple(TT template) {
        return FutureUtils.applyAndPropagateCancelAsync(
                take(template),
                match -> match.getTuple().get(),
                getExecutor()
        );
    }

    CompletableFuture<T> write(T tuple);

    CompletableFuture<MultiSet<T>> get();

    CompletableFuture<Integer> getSize();

    String getName();

    Executor getExecutor();
}

