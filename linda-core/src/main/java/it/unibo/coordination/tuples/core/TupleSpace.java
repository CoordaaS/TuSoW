package it.unibo.coordination.tuples.core;

import org.apache.commons.collections4.MultiSet;

import java.util.concurrent.CompletableFuture;

public interface TupleSpace<T extends Tuple, TT extends Template> {
    CompletableFuture<T> read(TT template);

    CompletableFuture<T> take(TT template);

    CompletableFuture<T> write(T tuple);

    CompletableFuture<MultiSet<? extends T>> get();

    CompletableFuture<Integer> getSize();

    String getName();
}

