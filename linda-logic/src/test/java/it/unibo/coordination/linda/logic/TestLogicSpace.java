package it.unibo.coordination.linda.logic;

import alice.tuprolog.Term;
import it.unibo.coordination.linda.test.TestTupleSpace;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.multiset.HashMultiSet;
import org.javatuples.Pair;
import org.javatuples.Quartet;

import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TestLogicSpace extends TestTupleSpace<LogicTuple, LogicTemplate, String, Term, LogicSpace> {


    @Override
    protected LogicSpace getTupleSpace(ExecutorService executor) {
        return LogicSpace.deterministic(executor);
    }

    @Override
    protected LogicTemplate getATemplate() {
        return LogicTemplate.of("Template");
    }

    @Override
    protected LogicTuple getATuple() {
        return LogicTuple.of("tuple");
    }

    @Override
    protected Pair<LogicTuple, LogicTemplate> getATupleAndATemplateMatchingIt() {
        return Pair.with(LogicTuple.of("a(1)"), LogicTemplate.of("a(X)"));
    }

    @Override
    protected LogicTuple messageTuple(String recipient, String payload) {
        return LogicTuple.of(String.format("msg('%s', '%s')", recipient, payload));
    }

    @Override
    protected LogicTemplate messageTemplate(String recipient) {
        return LogicTemplate.of(String.format("msg('%s', Payload)", recipient));
    }

    @Override
    protected LogicTemplate getGeneralMessageTemplate() {
        return LogicTemplate.of("msg(Recipient, Payload)");
    }

    @Override
    protected MultiSet<LogicTuple> getSomeTuples() {
        return Stream.of(
                "a", "b", "c", "d", "e"
            ).map(LogicTuple::of)
            .collect(Collectors.toCollection(HashMultiSet::new));
    }

    @Override
    protected Quartet<MultiSet<LogicTuple>, LogicTemplate, MultiSet<LogicTuple>, LogicTemplate> getSomeTuplesOfTwoSorts() {
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
    protected Pair<MultiSet<LogicTuple>, LogicTemplate> getSomeTuplesOfOneSort() {
        var tuples = IntStream.range(1, 6)
                .mapToObj(i -> String.format("f(%d)", i))
                .map(LogicTuple::of)
                .collect(Collectors.toCollection(HashMultiSet::new));
        return Pair.with(tuples, LogicTemplate.of("f(X)"));
    }
}
