package it.unibo.coordination.linda.string;

import it.unibo.coordination.linda.core.Match;
import it.unibo.coordination.linda.core.impl.AbstractTupleSpace;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.multiset.HashMultiSet;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

class DeterministicStringSpace extends AbstractTupleSpace<StringTuple, RegexTemplate, Object, String> implements InspectableStringSpace {

    private final MultiSet<PendingRequest> pendingRequests = new HashMultiSet<>();
    private final MultiSet<StringTuple> tuples = new HashMultiSet<>();

    public DeterministicStringSpace(String name, ExecutorService executor) {
        super(name, executor);
    }

    @Override
    protected Collection<PendingRequest> getPendingRequests() {
        return pendingRequests;
    }

    @Override
    protected Stream<? extends Match<StringTuple, RegexTemplate, Object, String>> lookForTuples(RegexTemplate template, int limit) {
        return tuples.stream()
                .map(template::matchWith)
                .filter(RegularMatch::isMatching)
                .limit(limit);
    }

    @Override
    protected Match<StringTuple, RegexTemplate, Object, String> lookForTuple(RegexTemplate template) {
        return lookForTuples(template, 1)
                .findFirst()
                .map(RegularMatch.class::cast)
                .orElseGet(() -> RegularMatch.failed(template));
    }

    @Override
    protected Stream<? extends Match<StringTuple, RegexTemplate, Object, String>> retrieveTuples(RegexTemplate template, int limit) {
        final var i = tuples.iterator();
        Stream.Builder<RegularMatch> result = Stream.builder();
        var j = 0;

        while (j < limit && i.hasNext()) {
            final var tuple = i.next();
            final var match = template.matchWith(tuple);

            if (match.isMatching()) {
                result.accept(match);
                i.remove();
                j++;
            }
        }

        return result.build();
    }

    @Override
    protected Match<StringTuple, RegexTemplate, Object, String> retrieveTuple(RegexTemplate template) {
        return retrieveTuples(template, 1).findFirst()
                .map(RegularMatch.class::cast)
                .orElseGet(() -> RegularMatch.failed(template));
    }

    @Override
    protected Match<StringTuple, RegexTemplate, Object, String> match(RegexTemplate template, StringTuple tuple) {
        return template.matchWith(tuple);
    }

    @Override
    protected void insertTuple(StringTuple tuple) {
        tuples.add(tuple);
    }

    @Override
    protected Stream<StringTuple> getAllTuples() {
        return tuples.stream();
    }

    @Override
    protected int countTuples() {
        return tuples.size();
    }

    @Override
    protected Match<StringTuple, RegexTemplate, Object, String> failedMatch(RegexTemplate template) {
        return RegularMatch.failed(template);
    }
}
