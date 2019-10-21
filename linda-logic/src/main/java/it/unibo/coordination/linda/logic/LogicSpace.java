package it.unibo.coordination.linda.logic;

import alice.tuprolog.Term;
import it.unibo.coordination.Engine;
import it.unibo.coordination.linda.core.ExtendedTupleSpace;
import it.unibo.coordination.linda.core.Match;
import org.apache.commons.collections4.MultiSet;

import java.util.Collection;
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


    default CompletableFuture<LogicTuple> write(String tuple) {
        return write(LogicTuple.of(tuple));
    }

    default CompletableFuture<LogicTuple> write(Term tuple) {
        return write(LogicTuple.of(tuple));
    }



    default CompletableFuture<LogicTuple> readTuple(String template) {
        return readTuple(LogicTemplate.of(template));
    }

    default CompletableFuture<LogicTuple> readTuple(Term template) {
        return readTuple(LogicTemplate.of(template));
    }

    default CompletableFuture<Match<LogicTuple, LogicTemplate, String, Term>> read(String template) {
        return read(LogicTemplate.of(template));
    }

    default CompletableFuture<Match<LogicTuple, LogicTemplate, String, Term>> tryRead(String template) {
        return tryRead(LogicTemplate.of(template));
    }

    default CompletableFuture<Match<LogicTuple, LogicTemplate, String, Term>> tryRead(Term template) {
        return tryRead(LogicTemplate.of(template));
    }

    default CompletableFuture<Match<LogicTuple, LogicTemplate, String, Term>> tryAbsent(String template) {
        return tryAbsent(LogicTemplate.of(template));
    }

    default CompletableFuture<Match<LogicTuple, LogicTemplate, String, Term>> tryAbsent(Term template) {
        return tryAbsent(LogicTemplate.of(template));
    }

    default CompletableFuture<Match<LogicTuple, LogicTemplate, String, Term>> tryTake(String template) {
        return tryTake(LogicTemplate.of(template));
    }

    default CompletableFuture<Match<LogicTuple, LogicTemplate, String, Term>> tryTake(Term template) {
        return tryTake(LogicTemplate.of(template));
    }

    default CompletableFuture<Match<LogicTuple, LogicTemplate, String, Term>> read(Term template) {
        return read(LogicTemplate.of(template));
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

    default CompletableFuture<Match<LogicTuple, LogicTemplate, String, Term>> take(String template) {
        return take(LogicTemplate.of(template));
    }

    default CompletableFuture<Match<LogicTuple, LogicTemplate, String, Term>> take(Term template) {
        return take(LogicTemplate.of(template));
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

    default CompletableFuture<Match<LogicTuple, LogicTemplate, String, Term>> absent(final String template) {
        return absent(LogicTemplate.of(template));
    }

    default CompletableFuture<Match<LogicTuple, LogicTemplate, String, Term>> absent(final Term template) {
        return absent(LogicTemplate.of(template));
    }

    default CompletableFuture<Optional<LogicTuple>> tryAbsentTuple(String template) {
        return tryAbsentTuple(LogicTemplate.of(template));
    }

    default CompletableFuture<Optional<LogicTuple>> tryAbsentTuple(Term template) {
        return tryAbsentTuple(LogicTemplate.of(template));
    }

    default CompletableFuture<Collection<? extends Match<LogicTuple, LogicTemplate, String, Term>>> readAll(String template) {
        return readAll(LogicTemplate.of(template));
    }

    default CompletableFuture<Collection<? extends Match<LogicTuple, LogicTemplate, String, Term>>> readAll(Term template) {
        return readAll(LogicTemplate.of(template));
    }

    default CompletableFuture<Collection<? extends Match<LogicTuple, LogicTemplate, String, Term>>> takeAll(String template) {
        return takeAll(LogicTemplate.of(template));
    }

    default CompletableFuture<Collection<? extends Match<LogicTuple, LogicTemplate, String, Term>>> takeAll(Term template) {
        return takeAll(LogicTemplate.of(template));
    }
}
