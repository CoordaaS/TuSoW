package it.unibo.coordination.linda.core;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface NegatedTupleSpace<T extends Tuple, TT extends Template, K, V> extends TupleSpace<T, TT, K, V> {
    CompletableFuture<Match<T, TT, K, V>> absent(TT template);

    default CompletableFuture<TT> absentTemplate(TT template) {
        return absent(template).thenApplyAsync(Match::getTemplate);
    }

    CompletableFuture<Match<T, TT, K, V>> tryAbsent(TT template);

    default CompletableFuture<Optional<T>> tryAbsentTuple(TT template) {
        return tryAbsent(template).thenApplyAsync(Match::getTuple);
    }
}
