package it.unibo.coordination.linda.logic.remote;

import alice.tuprolog.Term;
import it.unibo.coordination.linda.logic.LogicMatch;
import it.unibo.coordination.linda.logic.LogicTemplate;
import it.unibo.coordination.linda.logic.LogicTuple;
import it.unibo.coordination.linda.test.TupleTemplateFactory;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.multiset.HashMultiSet;
import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.javatuples.Triplet;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class LogicTupleTemplateFactory implements TupleTemplateFactory<LogicTuple, LogicTemplate, String, Term, LogicMatch> {

    @Override
    public LogicTemplate getATemplate() {
        return LogicTemplate.of("Template");
    }

    @Override
    public LogicTuple getATuple() {
        return LogicTuple.of("tuple");
    }

    @Override
    public Pair<LogicTuple, LogicTemplate> getATupleAndATemplateMatchingIt() {
        return Pair.with(LogicTuple.of("a(1)"), LogicTemplate.of("a(X)"));
    }

    @Override
    public Triplet<LogicTuple, LogicTemplate, LogicMatch> getSuccessfulMatch() {
        final var tuple = LogicTuple.of("f(1, \"2\", '3', d, e(f), g(h, [4, i]), [x, y, Z])");
        final var template = LogicTemplate.of("f(1, A, B, C, D, g(E, [F | G]), H)");

        return Triplet.with(tuple, template, template.matchWith(tuple));
    }

    @Override
    public Triplet<LogicTuple, LogicTemplate, LogicMatch> getFailedMatch() {
        final var tuple = LogicTuple.of("g(y)");
        final var template = LogicTemplate.of("f(X)");

        return Triplet.with(tuple, template, template.matchWith(tuple));
    }

    @Override
    public Pair<LogicTemplate, LogicMatch> getEmptyMatch() {
        final var template = LogicTemplate.of("f(X)");

        return Pair.with(template, LogicMatch.failed(template));
    }


    @Override
    public LogicTuple getMessageTuple(String recipient, String payload) {
        return LogicTuple.of(String.format("msg('%s', '%s')", recipient, payload));
    }

    @Override
    public LogicTemplate getMessageTemplate(String recipient) {
        return LogicTemplate.of(String.format("msg('%s', Payload)", recipient));
    }

    @Override
    public LogicTemplate getGeneralMessageTemplate() {
        return LogicTemplate.of("msg(Recipient, Payload)");
    }

    @Override
    public MultiSet<LogicTuple> getSomeTuples() {
        return Stream.of(
                "a", "b", "c", "d", "e"
        ).map(LogicTuple::of)
                .collect(Collectors.toCollection(HashMultiSet::new));
    }

    @Override
    public Quartet<MultiSet<LogicTuple>, LogicTemplate, MultiSet<LogicTuple>, LogicTemplate> getSomeTuplesOfTwoSorts() {
        var tuples1 = IntStream.range(1, 6)
                .mapToObj(i -> String.format("a(%d)", i))
                .map(LogicTuple::of)
                .collect(Collectors.toCollection(HashMultiSet::new));
        var tuples2 = IntStream.range(1, 6)
                .mapToObj(i -> String.format("b(%d)", i))
                .map(LogicTuple::of)
                .collect(Collectors.toCollection(HashMultiSet::new));
        return Quartet.with(tuples1, LogicTemplate.of("a(X)"), tuples2, LogicTemplate.of("b(X)"));
    }

    @Override
    public Pair<MultiSet<LogicTuple>, LogicTemplate> getSomeTuplesOfOneSort() {
        var tuples = IntStream.range(1, 6)
                .mapToObj(i -> String.format("f(%d)", i))
                .map(LogicTuple::of)
                .collect(Collectors.toCollection(HashMultiSet::new));
        return Pair.with(tuples, LogicTemplate.of("f(X)"));
    }
}