package it.unibo.coordination.linda.core;

import org.apache.commons.collections4.MultiSet;

import java.util.concurrent.CompletableFuture;

public interface TupleSpace<T extends Tuple, TT extends Template, K, V> {
    CompletableFuture<Match<T, TT, K, V>> read(TT template);

    default CompletableFuture<T> readTuple(TT template) {
        return read(template).thenApplyAsync(match -> match.getTuple().get());
    }

    CompletableFuture<Match<T, TT, K, V>> take(TT template);

    default CompletableFuture<T> takeTuple(TT template) {
        return take(template).thenApplyAsync(match -> match.getTuple().get());
    }

    CompletableFuture<T> write(T tuple);

    CompletableFuture<MultiSet<T>> get();

    CompletableFuture<Integer> getSize();

    String getName();
}

