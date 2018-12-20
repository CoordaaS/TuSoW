package it.unibo.coordination.linda.logic;

import alice.tuprolog.Prolog;
import alice.tuprolog.Struct;
import alice.tuprolog.Var;
import it.unibo.coordination.linda.core.impl.AbstractTupleSpace;
import it.unibo.coordination.prologx.PrologUtils;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

abstract class AbstractLogicSpaceImpl extends AbstractTupleSpace<LogicTuple, LogicTemplate> implements InspectableLogicSpace {

    private final Prolog engine = new Prolog();

    public AbstractLogicSpaceImpl(String name, ExecutorService executor) {
        super(name, executor);
    }

    @Override
    protected Stream<LogicTuple> lookForTuples(LogicTemplate template, int limit) {
        return PrologUtils.solutionsStream(engine, template.toTuple().asTerm())
                .limit(limit)
                .map(Struct.class::cast)
                .map(LogicTuple::of);
    }

    @Override
    protected final Optional<LogicTuple> lookForTuple(LogicTemplate template) {
        return lookForTuples(template, 1).findAny();
    }

    @Override
    protected final Stream<LogicTuple> retrieveTuples(LogicTemplate template, int limit) {
        return PrologUtils.solutionsStream(engine, PrologUtils.retractTerm(template.toTuple().asTerm()))
                .limit(limit)
                .map(Struct.class::cast)
                .map(s -> s.getArg(0))
                .map(LogicTuple::of);
    }

    @Override
    protected final Optional<LogicTuple> retrieveTuple(LogicTemplate template) {
        return retrieveTuple(template.toTuple());
    }

    protected final Optional<LogicTuple> retrieveTuple(LogicTuple tuple) {
        return PrologUtils.retractFrom(engine, tuple.asTerm())
                .map(LogicTupleImpl::new);
    }

    @Override
    protected void insertTuple(LogicTuple tuple) {
        PrologUtils.assertOn(engine, tuple.asTerm());
    }

    @Override
    protected final Stream<LogicTuple> getAllTuples() {
        return lookForTuples(LogicTemplate.of(new Var()), Integer.MAX_VALUE);
    }

    @Override
    protected int countTuples() {
        return (int) getAllTuples().count();
    }
}
