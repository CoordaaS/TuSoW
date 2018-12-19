package it.unibo.coordination.tuples.core;

import org.apache.commons.collections4.MultiSet;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface BulkTupleSpace<T extends Tuple, TT extends Template> extends TupleSpace<T, TT> {
    CompletableFuture<MultiSet<? extends T>> readAll(TT template);

    CompletableFuture<MultiSet<? extends T>> takeAll(TT template);

    CompletableFuture<MultiSet<? extends T>> writeAll(Collection<? extends T> tuples);

    default Future<MultiSet<? extends T>> writeAll(final T tuple1, final T... otherTuples) {
        final List<T> tuples = Stream.concat(Stream.of(tuple1), Stream.of(otherTuples)).collect(Collectors.toList());
        return writeAll(tuples);
    }
}
