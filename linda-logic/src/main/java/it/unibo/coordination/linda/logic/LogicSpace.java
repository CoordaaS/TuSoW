package it.unibo.coordination.linda.logic;

import alice.tuprolog.Term;
import it.unibo.coordination.Engines;
import it.unibo.coordination.linda.core.Match;
import it.unibo.coordination.linda.core.TupleSpace;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public interface LogicSpace extends TupleSpace<LogicTuple, LogicTemplate, String, Term> {

    static LogicSpace deterministic(String name, ExecutorService executorService) {
        return new DeterministicLogicSpaceImpl(name, executorService);
    }

    static LogicSpace deterministic(String name) {
        return deterministic(name, Engines.getDefaultEngine());
    }

    static LogicSpace deterministic(ExecutorService executorService) {
        return deterministic(null, Engines.getDefaultEngine());
    }

    static LogicSpace nonDeterministic(String name, ExecutorService executorService) {
        throw new UnsupportedOperationException("not implemented");
    }

    static LogicSpace nonDeterministic(String name) {
        return nonDeterministic(name, Engines.getDefaultEngine());
    }

    static LogicSpace nonDeterministic(ExecutorService executorService) {
        return nonDeterministic(null, Engines.getDefaultEngine());
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

    @Override
    default LogicTuple toTuple(String $this$toTuple) {
        return LogicTuple.of($this$toTuple);
    }

    @Override
    default LogicTemplate toTemplate(String $this$toTemplate) {
        return LogicTemplate.of($this$toTemplate);
    }
}
