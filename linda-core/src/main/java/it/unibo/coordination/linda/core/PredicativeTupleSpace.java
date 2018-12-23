package it.unibo.coordination.linda.core;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface PredicativeTupleSpace<T extends Tuple, TT extends Template, K, V> extends TupleSpace<T, TT, K, V> {
    CompletableFuture<Match<T, TT, K, V>> tryTake(TT template);

    default CompletableFuture<Optional<T>> tryTakeTuple(TT template) {
        return tryTake(template).thenApplyAsync(Match::getTuple);
    }

    CompletableFuture<Match<T, TT, K, V>> tryRead(TT template);

    default CompletableFuture<Optional<T>> tryReadTuple(TT template) {
        return tryRead(template).thenApplyAsync(Match::getTuple);
    }
}
