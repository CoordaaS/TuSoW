package it.unibo.coordination.linda.text;

import it.unibo.coordination.Engines;
import it.unibo.coordination.linda.core.TupleSpace;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;

public interface TextualSpace extends TupleSpace<StringTuple, RegexTemplate, Object, String, RegularMatch> {

    static TextualSpace local(String name, ExecutorService executorService) {
        return new TextualSpaceImpl(name, executorService);
    }

    static TextualSpace local(String name) {
        return local(name, Engines.getDefaultEngine());
    }

    static TextualSpace local(ExecutorService executorService) {
        return local(null, executorService);
    }

    static TextualSpace local() {
        return local(null, Engines.getDefaultEngine());
    }

    default CompletableFuture<StringTuple> readTuple(Pattern template) {
        return readTuple(RegexTemplate.of(template));
    }

    default CompletableFuture<RegularMatch> read(Pattern template) {
        return read(RegexTemplate.of(template));
    }

    default CompletableFuture<Optional<StringTuple>> tryReadTuple(Pattern template) {
        return tryReadTuple(RegexTemplate.of(template));
    }

    default CompletableFuture<StringTuple> takeTuple(Pattern template) {
        return takeTuple(RegexTemplate.of(template));
    }

    default CompletableFuture<RegularMatch> take(Pattern template) {
        return take(RegexTemplate.of(template));
    }

    default CompletableFuture<Optional<StringTuple>> tryTakeTuple(Pattern template) {
        return tryTakeTuple(RegexTemplate.of(template));
    }

    default CompletableFuture<RegularMatch> absent(final Pattern template) {
        return absent(RegexTemplate.of(template));
    }

    default CompletableFuture<Optional<StringTuple>> tryAbsentTuple(Pattern template) {
        return tryAbsentTuple(RegexTemplate.of(template));
    }

    default CompletableFuture<RegularMatch> tryRead(Pattern template) {
        return tryRead(RegexTemplate.of(template));
    }

    default CompletableFuture<RegularMatch> tryTake(Pattern template) {
        return tryTake(RegexTemplate.of(template));
    }

    default CompletableFuture<RegularMatch> tryAbsent(Pattern template) {
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
