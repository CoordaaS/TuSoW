package it.unibo.coordination.linda.string;

import it.unibo.coordination.Engine;
import it.unibo.coordination.linda.core.ExtendedTupleSpace;
import it.unibo.coordination.linda.core.Match;
import org.apache.commons.collections4.MultiSet;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface StringSpace extends ExtendedTupleSpace<StringTuple, RegexTemplate, Object, String> {

    static StringSpace deterministic(String name, ExecutorService executorService) {
        return new DeterministicStringSpace(name, executorService);
    }

    static StringSpace deterministic(String name) {
        return deterministic(name, Engine.getDefaultEngine());
    }

    static StringSpace deterministic(ExecutorService executorService) {
        return deterministic(null, Engine.getDefaultEngine());
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

    default CompletableFuture<MultiSet<StringTuple>> readAllTuples(String template) {
        return readAllTuples(RegexTemplate.of(template));
    }

    default CompletableFuture<MultiSet<StringTuple>> readAllTuples(Pattern template) {
        return readAllTuples(RegexTemplate.of(template));
    }

    default CompletableFuture<MultiSet<StringTuple>> takeAllTuples(String template) {
        return takeAllTuples(RegexTemplate.of(template));
    }

    default CompletableFuture<MultiSet<StringTuple>> takeAllTuples(Pattern template) {
        return takeAllTuples(RegexTemplate.of(template));
    }

    default CompletableFuture<MultiSet<StringTuple>> writeAll(String tuple, String... tuples) {
        return writeAll(
                Stream.concat(
                        Stream.of(tuple),
                        Stream.of(tuples)
                ).map(StringTuple::of)
                        .collect(Collectors.toList())
        );
    }

    default CompletableFuture<RegexTemplate> absent(final String template) {
        return absent(RegexTemplate.of(template)).thenApplyAsync(Match::getTemplate);
    }

    default CompletableFuture<RegexTemplate> absent(final Pattern template) {
        return absent(RegexTemplate.of(template)).thenApplyAsync(Match::getTemplate);
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

    default CompletableFuture<Collection<? extends Match<StringTuple, RegexTemplate, Object, String>>> readAll(String template) {
        return readAll(RegexTemplate.of(template));
    }

    default CompletableFuture<Collection<? extends Match<StringTuple, RegexTemplate, Object, String>>> readAll(Pattern template) {
        return readAll(RegexTemplate.of(template));
    }

    default CompletableFuture<Collection<? extends Match<StringTuple, RegexTemplate, Object, String>>> takeAll(String template) {
        return takeAll(RegexTemplate.of(template));
    }

    default CompletableFuture<Collection<? extends Match<StringTuple, RegexTemplate, Object, String>>> takeAll(Pattern template) {
        return takeAll(RegexTemplate.of(template));
    }
}
