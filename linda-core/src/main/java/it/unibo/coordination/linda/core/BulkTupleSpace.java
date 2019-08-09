package it.unibo.coordination.linda.core;

import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.multiset.HashMultiSet;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static it.unibo.coordination.utils.ListUtils.listOf;

public interface BulkTupleSpace<T extends Tuple, TT extends Template, K, V> extends TupleSpace<T, TT, K, V> {

    CompletableFuture<Collection<? extends Match<T, TT, K, V>>> readAll(TT template);

    default CompletableFuture<MultiSet<T>> readAllTuples(TT template) {
        return readAll(template).thenApplyAsync(matches ->
                    matches.stream().map(m -> m.getTuple().get())
                        .collect(Collectors.toCollection(HashMultiSet::new))
                );
    }

    CompletableFuture<Collection<? extends Match<T, TT, K, V>>> takeAll(TT template);

    default CompletableFuture<MultiSet<T>> takeAllTuples(TT template) {
        return takeAll(template).thenApplyAsync(matches ->
                matches.stream().map(m -> m.getTuple().get())
                        .collect(Collectors.toCollection(HashMultiSet::new))
        );
    }

    CompletableFuture<MultiSet<T>> writeAll(Collection<? extends T> tuples);

    default CompletableFuture<MultiSet<T>> writeAll(final T tuple1, final T... otherTuples) {
        return writeAll(listOf(tuple1, otherTuples));
    }
}
