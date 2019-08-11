package it.unibo.coordination.linda.core;

import it.unibo.coordination.utils.CollectionUtils;
import org.apache.commons.collections4.multiset.HashMultiSet;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static it.unibo.coordination.utils.CollectionUtils.collectionOf;

public interface ExoticTupleSpace<T extends Tuple, TT extends Template, K, V> extends TupleSpace<T, TT, K, V> {

    CompletableFuture<Collection<? extends Match<T, TT, K, V>>> readAtLeast(int threshold, Collection<? extends TT> templates);

    default CompletableFuture<Collection<? extends Match<T, TT, K, V>>> readAtLeast(int threshold, TT template1, TT template2, TT... templates) {
        return readAtLeast(threshold, collectionOf(template1, template2, templates));
    }

    default CompletableFuture<Collection<? extends T>> readAtLeastTuples(int threshold, Collection<? extends TT> templates) {
        return readAtLeast(threshold, templates).thenApplyAsync(matches ->
                matches.stream().map(m -> m.getTuple().get())
                        .collect(Collectors.toCollection(HashMultiSet::new))
        );
    }

    default CompletableFuture<Collection<? extends T>>  readAtLeastTuples(int threshold, TT template1, TT template2, TT... templates) {
        return readAtLeastTuples(threshold, collectionOf(template1, template2, templates));
    }

    default CompletableFuture<Collection<? extends Match<T, TT, K, V>>> readAny(Collection<? extends TT> templates) {
        CollectionUtils.requireNonEmpty(templates);
        return readAtLeast(1, templates);
    }

    default CompletableFuture<Collection<? extends Match<T, TT, K, V>>> readAny(TT template1, TT template2, TT... templates) {
        return readAny(collectionOf(template1, template2, templates));
    }

    default CompletableFuture<Collection<? extends T>> readAnyTuple(Collection<? extends TT> templates) {
        return readAny(templates).thenApplyAsync(matches ->
                matches.stream().map(m -> m.getTuple().get())
                        .collect(Collectors.toCollection(HashMultiSet::new))
        );
    }

    default CompletableFuture<Collection<? extends T>>  readAnyTuple(TT template1, TT template2, TT... templates) {
        return readAnyTuple(collectionOf(template1, template2, templates));
    }

    default CompletableFuture<Collection<? extends Match<T, TT, K, V>>> readEach(Collection<? extends TT> templates) {
        CollectionUtils.requireNonEmpty(templates);
        return readAtLeast(templates.size(), templates);
    }

    default CompletableFuture<Collection<? extends Match<T, TT, K, V>>> readEach(TT template1, TT template2, TT... templates) {
        return readEach(collectionOf(template1, template2, templates));
    }

    default CompletableFuture<Collection<? extends T>> readEachTuple(Collection<? extends TT> templates) {
        return readEach(templates).thenApplyAsync(matches ->
                matches.stream()
                        .map(Match::getTuple)
                        .map(Optional::get)
                        .collect(Collectors.toCollection(HashMultiSet::new))
        );
    }

    default CompletableFuture<Collection<? extends T>>  readEachTuple(TT template, TT... templates) {
        return readEachTuple(collectionOf(template, templates));
    }

    CompletableFuture<Collection<? extends Match<T, TT, K, V>>> takeAtLeast(int threshold, Collection<? extends TT> templates);

    default CompletableFuture<Collection<? extends Match<T, TT, K, V>>> takeAtLeast(int threshold, TT template, TT... templates) {
        return takeAtLeast(threshold, collectionOf(template, templates));
    }

    default CompletableFuture<Collection<? extends T>> takeAtLeastTuples(int threshold, Collection<? extends TT> templates) {
        return takeAtLeast(threshold, templates).thenApplyAsync(matches ->
                matches.stream().map(m -> m.getTuple().get())
                        .collect(Collectors.toCollection(HashMultiSet::new))
        );
    }

    default CompletableFuture<Collection<? extends T>>  takeAtLeastTuples(int threshold, TT template, TT... templates) {
        return takeAtLeastTuples(threshold, collectionOf(template, templates));
    }

    default CompletableFuture<Collection<? extends Match<T, TT, K, V>>> takeAny(Collection<? extends TT> templates) {
        CollectionUtils.requireNonEmpty(templates);
        return takeAtLeast(1, templates);
    }

    default CompletableFuture<Collection<? extends Match<T, TT, K, V>>> takeAny(TT template, TT... templates) {
        return takeAny(collectionOf(template, templates));
    }

    default CompletableFuture<Collection<? extends T>> takeAnyTuple(Collection<? extends TT> templates) {
        return takeAny(templates).thenApplyAsync(matches ->
                matches.stream().map(m -> m.getTuple().get())
                        .collect(Collectors.toCollection(HashMultiSet::new))
        );
    }

    default CompletableFuture<Collection<? extends T>>  takeAnyTuple(TT template, TT... templates) {
        return takeAnyTuple(collectionOf(template, templates));
    }

    default CompletableFuture<Collection<? extends Match<T, TT, K, V>>> takeEach(Collection<? extends TT> templates) {
        CollectionUtils.requireNonEmpty(templates);
        return takeAtLeast(templates.size(), templates);
    }

    default CompletableFuture<Collection<? extends Match<T, TT, K, V>>> takeEach(TT template, TT... templates) {
        return takeEach(collectionOf(template, templates));
    }

    default CompletableFuture<Collection<? extends T>> takeEachTuple(Collection<? extends TT> templates) {
        return takeEach(templates).thenApplyAsync(matches ->
                matches.stream()
                        .map(Match::getTuple)
                        .map(Optional::get)
                        .collect(Collectors.toCollection(HashMultiSet::new))
        );
    }

    default CompletableFuture<Collection<? extends T>>  takeEachTuple(TT template, TT... templates) {
        return takeEachTuple(collectionOf(template, templates));
    }
}
