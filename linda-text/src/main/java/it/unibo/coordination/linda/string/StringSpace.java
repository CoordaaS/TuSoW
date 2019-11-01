package it.unibo.coordination.linda.string;

import it.unibo.coordination.Engines;
import it.unibo.coordination.linda.core.Match;
import it.unibo.coordination.linda.core.TupleSpace;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;

public interface StringSpace extends TupleSpace<StringTuple, RegexTemplate, Object, String> {

    static StringSpace deterministic(String name, ExecutorService executorService) {
        return new DeterministicStringSpace(name, executorService);
    }

    static StringSpace deterministic(String name) {
        return deterministic(name, Engines.getDefaultEngine());
    }

    static StringSpace deterministic(ExecutorService executorService) {
        return deterministic(null, Engines.getDefaultEngine());
    }

    default CompletableFuture<StringTuple> write(String template) {
        return write(StringTuple.of(template));
    }

    default CompletableFuture<StringTuple> readTuple(String template) {
        return readTuple(RegexTemplate.of(template));
    }

    default CompletableFuture<StringTuple> readTuple(Pattern template) {
        return readTuple(RegexTemplate.of(template));
    }

    default CompletableFuture<Match<StringTuple, RegexTemplate, Object, String>> read(String template) {
        return read(RegexTemplate.of(template));
    }

    default CompletableFuture<Match<StringTuple, RegexTemplate, Object, String>> read(Pattern template) {
        return read(RegexTemplate.of(template));
    }

    default CompletableFuture<Optional<StringTuple>> tryReadTuple(String template) {
        return tryReadTuple(RegexTemplate.of(template));
    }

    default CompletableFuture<Optional<StringTuple>> tryReadTuple(Pattern template) {
        return tryReadTuple(RegexTemplate.of(template));
    }

    default CompletableFuture<StringTuple> takeTuple(String template) {
        return takeTuple(RegexTemplate.of(template));
    }

    default CompletableFuture<StringTuple> takeTuple(Pattern template) {
        return takeTuple(RegexTemplate.of(template));
    }

    default CompletableFuture<Match<StringTuple, RegexTemplate, Object, String>> take(String template) {
        return take(RegexTemplate.of(template));
    }

    default CompletableFuture<Match<StringTuple, RegexTemplate, Object, String>> take(Pattern template) {
        return take(RegexTemplate.of(template));
    }

    default CompletableFuture<Optional<StringTuple>> tryTakeTuple(String template) {
        return tryTakeTuple(RegexTemplate.of(template));
    }

    default CompletableFuture<Optional<StringTuple>> tryTakeTuple(Pattern template) {
        return tryTakeTuple(RegexTemplate.of(template));
    }

    default CompletableFuture<Match<StringTuple, RegexTemplate, Object, String>> absent(final String template) {
        return absent(RegexTemplate.of(template));
    }

    default CompletableFuture<Match<StringTuple, RegexTemplate, Object, String>> absent(final Pattern template) {
        return absent(RegexTemplate.of(template));
    }

    default CompletableFuture<Optional<StringTuple>> tryAbsentTuple(String template) {
        return tryAbsentTuple(RegexTemplate.of(template));
    }

    default CompletableFuture<Optional<StringTuple>> tryAbsentTuple(Pattern template) {
        return tryAbsentTuple(RegexTemplate.of(template));
    }

    default CompletableFuture<Match<StringTuple, RegexTemplate, Object, String>> tryRead(String template) {
        return tryRead(RegexTemplate.of(template));
    }

    default CompletableFuture<Match<StringTuple, RegexTemplate, Object, String>> tryRead(Pattern template) {
        return tryRead(RegexTemplate.of(template));
    }

    default CompletableFuture<Match<StringTuple, RegexTemplate, Object, String>> tryTake(String template) {
        return tryTake(RegexTemplate.of(template));
    }

    default CompletableFuture<Match<StringTuple, RegexTemplate, Object, String>> tryTake(Pattern template) {
        return tryTake(RegexTemplate.of(template));
    }

    default CompletableFuture<Match<StringTuple, RegexTemplate, Object, String>> tryAbsent(String template) {
        return tryAbsent(RegexTemplate.of(template));
    }

    default CompletableFuture<Match<StringTuple, RegexTemplate, Object, String>> tryAbsent(Pattern template) {
        return tryAbsent(RegexTemplate.of(template));
    }

    @Override
    default StringTuple toTuple(String $this$toTuple) {
        return StringTuple.of($this$toTuple);
    }

    @Override
    default RegexTemplate toTemplate(String $this$toTemplate) {
        return RegexTemplate.of($this$toTemplate);
    }
}
