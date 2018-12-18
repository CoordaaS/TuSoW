package it.unibo.coordination.tuples.core;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface PredicativeTupleSpace<T extends Tuple, TT extends Template> extends TupleSpace<T, TT> {
    CompletableFuture<Optional<T>> tryTake(TT template);

    CompletableFuture<Optional<T>> tryRead(TT template);
}
