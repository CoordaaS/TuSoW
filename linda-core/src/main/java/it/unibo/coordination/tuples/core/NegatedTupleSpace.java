package it.unibo.coordination.tuples.core;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface NegatedTupleSpace<T extends Tuple, TT extends Template> extends TupleSpace<T, TT> {
    CompletableFuture<TT> absent(TT template);

    CompletableFuture<Optional<T>> tryAbsent(TT template);
}
