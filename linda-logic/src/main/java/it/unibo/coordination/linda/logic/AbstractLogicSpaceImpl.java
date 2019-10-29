package it.unibo.coordination.linda.logic;

import alice.tuprolog.Prolog;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import it.unibo.coordination.linda.core.Match;
import it.unibo.coordination.linda.core.impl.AbstractTupleSpace;
import it.unibo.coordination.prologx.PrologUtils;

import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

abstract class AbstractLogicSpaceImpl extends AbstractTupleSpace<LogicTuple, LogicTemplate, String, Term> implements InspectableLogicSpace {

    private final Prolog engine = new Prolog();

    public AbstractLogicSpaceImpl(String name, ExecutorService executor) {
        super(name, executor);
    }

    @Override
    protected Stream<LogicMatch> lookForTuples(LogicTemplate template, int limit) {
        return PrologUtils.solutionsStream(engine, template.toTuple().asTerm())
                .map(Struct.class::cast)
                .map(LogicTuple::of)
                .map(template::matchWith)
                .limit(limit);
    }

    @Override
    protected final LogicMatch lookForTuple(LogicTemplate template) {
        return lookForTuples(template, 1).findAny().orElseGet(() -> LogicMatch.failed(template));
    }

    @Override
    protected final Stream<LogicMatch> retrieveTuples(LogicTemplate template, int limit) {
        return PrologUtils.solutionsStream(engine, PrologUtils.retractTerm(template.toTuple().asTerm()))
                .map(Struct.class::cast)
                .map(s -> s.getArg(0))
                .map(LogicTuple::of)
                .map(template::matchWith)
                .limit(limit);
    }

    @Override
    protected final LogicMatch retrieveTuple(LogicTemplate template) {
        return retrieveTuples(template, 1).findFirst().orElseGet(() -> LogicMatch.failed(template));
    }

    @Override
    protected void insertTuple(LogicTuple tuple) {
        PrologUtils.assertOn(engine, tuple.asTerm());
    }

    @Override
    protected final Stream<LogicTuple> getAllTuples() {
        return PrologUtils.solutionsStream(engine, LogicTuple.getPattern())
                .map(Struct.class::cast)
                .map(LogicTuple::of);
    }

    @Override
    protected int countTuples() {
        return (int) getAllTuples().count();
    }

    @Override
    protected Match<LogicTuple, LogicTemplate, String, Term> match(LogicTemplate template, LogicTuple tuple) {
        return template.matchWith(tuple);
    }

    @Override
    protected Match<LogicTuple, LogicTemplate, String, Term> failedMatch(LogicTemplate template) {
        return LogicMatch.failed(template);
    }


}
