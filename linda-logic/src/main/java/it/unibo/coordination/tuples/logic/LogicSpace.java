package it.unibo.coordination.tuples.logic;

import alice.tuprolog.Term;
import it.unibo.coordination.Engine;
import it.unibo.coordination.tuples.core.ExtendedTupleSpace;
import org.apache.commons.collections4.MultiSet;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface LogicSpace extends ExtendedTupleSpace<LogicTuple, LogicTemplate> {

    static LogicSpace create(String name, ExecutorService executorService) {
        return new LogicSpaceImpl(name, executorService);
    }

    static LogicSpace create(String name) {
        return create(name, Engine.getDefaultEngine());
    }

    static LogicSpace create(ExecutorService executorService) {
        return create(null, Engine.getDefaultEngine());
    }

    default CompletableFuture<LogicTuple> write(String template) {
        return write(LogicTuple.of(template));
    }

    default CompletableFuture<LogicTuple> write(Term template) {
        return write(LogicTuple.of(template));
    }

    default CompletableFuture<LogicTuple> read(String template) {
        return read(LogicTemplate.of(template));
    }

    default CompletableFuture<LogicTuple> read(Term template) {
        return read(LogicTemplate.of(template));
    }

    default CompletableFuture<Optional<LogicTuple>> tryRead(String template) {
        return tryRead(LogicTemplate.of(template));
    }

    default CompletableFuture<Optional<LogicTuple>> tryRead(Term template) {
        return tryRead(LogicTemplate.of(template));
    }

    default CompletableFuture<LogicTuple> take(String template) {
        return take(LogicTemplate.of(template));
    }

    default CompletableFuture<LogicTuple> take(Term template) {
        return take(LogicTemplate.of(template));
    }

    default CompletableFuture<Optional<LogicTuple>> tryTake(String template) {
        return tryTake(LogicTemplate.of(template));
    }

    default CompletableFuture<Optional<LogicTuple>> tryTake(Term template) {
        return tryTake(LogicTemplate.of(template));
    }

    default CompletableFuture<MultiSet<? extends LogicTuple>> readAll(String template) {
        return readAll(LogicTemplate.of(template));
    }

    default CompletableFuture<MultiSet<? extends LogicTuple>> readAll(Term template) {
        return readAll(LogicTemplate.of(template));
    }

    default CompletableFuture<MultiSet<? extends LogicTuple>> takeAll(String template) {
        return takeAll(LogicTemplate.of(template));
    }

    default CompletableFuture<MultiSet<? extends LogicTuple>> takeAll(Term template) {
        return takeAll(LogicTemplate.of(template));
    }

    default CompletableFuture<MultiSet<? extends LogicTuple>> writeAll(String template, String... templates) {
        return writeAll(
                Stream.concat(
                        Stream.of(template),
                        Stream.of(templates)
                    ).map(LogicTuple::of)
                    .collect(Collectors.toList())
        );
    }

    default CompletableFuture<LogicTemplate> absent(final String template) {
        return absent(LogicTemplate.of(template));
    }

    default CompletableFuture<LogicTemplate> absent(final Term template) {
        return absent(LogicTemplate.of(template));
    }

    default CompletableFuture<Optional<LogicTuple>> tryAbsent(String template) {
        return tryAbsent(LogicTemplate.of(template));
    }

    default CompletableFuture<Optional<LogicTuple>> tryAbsent(Term template) {
        return tryAbsent(LogicTemplate.of(template));
    }

    default CompletableFuture<MultiSet<? extends LogicTuple>> writeAll(Term template, Term... templates) {
        return writeAll(
                Stream.concat(
                        Stream.of(template),
                        Stream.of(templates)
                    ).map(LogicTuple::of)
                    .collect(Collectors.toList())
        );
    }
}
