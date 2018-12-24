package it.unibo.coordination.linda.logic;

import alice.tuprolog.Term;
import it.unibo.coordination.Engine;
import it.unibo.coordination.linda.core.ExtendedTupleSpace;
import it.unibo.coordination.linda.core.Match;
import org.apache.commons.collections4.MultiSet;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface LogicSpace extends ExtendedTupleSpace<LogicTuple, LogicTemplate, String, Term> {

    static LogicSpace deterministic(String name, ExecutorService executorService) {
        return new DeterministicLogicSpaceImpl(name, executorService);
    }

    static LogicSpace deterministic(String name) {
        return deterministic(name, Engine.getDefaultEngine());
    }

    static LogicSpace deterministic(ExecutorService executorService) {
        return deterministic(null, Engine.getDefaultEngine());
    }

    static LogicSpace nonDeterministic(String name, ExecutorService executorService) {
        throw new UnsupportedOperationException("not implemented");
    }

    static LogicSpace nonDeterministic(String name) {
        return nonDeterministic(name, Engine.getDefaultEngine());
    }

    static LogicSpace nonDeterministic(ExecutorService executorService) {
        return nonDeterministic(null, Engine.getDefaultEngine());
    }


    default CompletableFuture<LogicTuple> write(String template) {
        return write(LogicTuple.of(template));
    }

    default CompletableFuture<LogicTuple> write(Term template) {
        return write(LogicTuple.of(template));
    }

    default CompletableFuture<LogicTuple> readTuple(String template) {
        return readTuple(LogicTemplate.of(template));
    }

    default CompletableFuture<LogicTuple> readTuple(Term template) {
        return readTuple(LogicTemplate.of(template));
    }

    default CompletableFuture<Optional<LogicTuple>> tryReadTuple(String template) {
        return tryReadTuple(LogicTemplate.of(template));
    }

    default CompletableFuture<Optional<LogicTuple>> tryReadTuple(Term template) {
        return tryReadTuple(LogicTemplate.of(template));
    }

    default CompletableFuture<LogicTuple> takeTuple(String template) {
        return takeTuple(LogicTemplate.of(template));
    }

    default CompletableFuture<LogicTuple> takeTuple(Term template) {
        return takeTuple(LogicTemplate.of(template));
    }

    default CompletableFuture<Optional<LogicTuple>> tryTakeTuple(String template) {
        return tryTakeTuple(LogicTemplate.of(template));
    }

    default CompletableFuture<Optional<LogicTuple>> tryTakeTuple(Term template) {
        return tryTakeTuple(LogicTemplate.of(template));
    }

    default CompletableFuture<MultiSet<LogicTuple>> readAllTuples(String template) {
        return readAllTuples(LogicTemplate.of(template));
    }

    default CompletableFuture<MultiSet<LogicTuple>> readAllTuples(Term template) {
        return readAllTuples(LogicTemplate.of(template));
    }

    default CompletableFuture<MultiSet<LogicTuple>> takeAllTuples(String template) {
        return takeAllTuples(LogicTemplate.of(template));
    }

    default CompletableFuture<MultiSet<LogicTuple>> takeAllTuples(Term template) {
        return takeAllTuples(LogicTemplate.of(template));
    }

    default CompletableFuture<MultiSet<LogicTuple>> writeAll(String tuple, String... tuples) {
        return writeAll(
                Stream.concat(
                        Stream.of(tuple),
                        Stream.of(tuples)
                    ).map(LogicTuple::of)
                    .collect(Collectors.toList())
        );
    }

    default CompletableFuture<LogicTemplate> absent(final String template) {
        return absent(LogicTemplate.of(template)).thenApplyAsync(Match::getTemplate);
    }

    default CompletableFuture<LogicTemplate> absent(final Term template) {
        return absent(LogicTemplate.of(template)).thenApplyAsync(Match::getTemplate);
    }

    default CompletableFuture<Optional<LogicTuple>> tryAbsentTuple(String template) {
        return tryAbsentTuple(LogicTemplate.of(template));
    }

    default CompletableFuture<Optional<LogicTuple>> tryAbsentTuple(Term template) {
        return tryAbsentTuple(LogicTemplate.of(template));
    }

}
