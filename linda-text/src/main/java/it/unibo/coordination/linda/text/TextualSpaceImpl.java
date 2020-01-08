package it.unibo.coordination.linda.text;

import it.unibo.coordination.linda.core.impl.AbstractTupleSpace;
import it.unibo.coordination.linda.core.impl.LocalPendingRequest;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.multiset.HashMultiSet;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

class TextualSpaceImpl extends AbstractTupleSpace<StringTuple, RegexTemplate, Object, String, RegularMatch> implements InspectableTextualSpace {

    private final MultiSet<LocalPendingRequest<StringTuple, RegexTemplate, RegularMatch>> pendingRequests = new HashMultiSet<>();
    private final MultiSet<StringTuple> tuples = new HashMultiSet<>();

    public TextualSpaceImpl(String name, ExecutorService executor) {
        super(name, executor);
    }

    @Override
    protected Collection<LocalPendingRequest<StringTuple, RegexTemplate, RegularMatch>> getPendingRequests() {
        return pendingRequests;
    }

    @Override
    protected Stream<? extends RegularMatch> lookForTuples(RegexTemplate template, int limit) {
        return tuples.stream()
                .map(template::matchWith)
                .filter(RegularMatch::isMatching)
                .limit(limit);
    }

    @Override
    protected RegularMatch lookForTuple(RegexTemplate template) {
        return lookForTuples(template, 1)
                .findFirst()
                .map(RegularMatch.class::cast)
                .orElseGet(() -> RegularMatch.failed(template));
    }

    @Override
    protected Stream<? extends RegularMatch> retrieveTuples(RegexTemplate template, int limit) {
        final Iterator<StringTuple> i = tuples.iterator();
        Stream.Builder<RegularMatch> result = Stream.builder();
        int j = 0;

        while (j < limit && i.hasNext()) {
            final StringTuple tuple = i.next();
            final RegularMatch match = template.matchWith(tuple);

            if (match.isMatching()) {
                result.accept(match);
                i.remove();
                j++;
            }
        }

        return result.build();
    }

    @Override
    protected RegularMatch retrieveTuple(RegexTemplate template) {
        return retrieveTuples(template, 1).findFirst()
                .map(RegularMatch.class::cast)
                .orElseGet(() -> RegularMatch.failed(template));
    }

    @Override
    protected RegularMatch match(RegexTemplate template, StringTuple tuple) {
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
    protected RegularMatch failedMatch(RegexTemplate template) {
        return RegularMatch.failed(template);
    }
}
