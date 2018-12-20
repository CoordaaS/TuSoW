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


    protected final Stream<LogicTuple> lookForTuplesImpl(LogicTemplate template, int limit) {
        return PrologUtils.solutionsStream(engine, template.toTuple().asTerm())
                .limit(limit)
                .map(LogicTuple::of);
    }

    @Override
    protected final Optional<LogicTuple> lookForTuple(LogicTemplate template) {
        return lookForTuplesImpl(template, 1).findAny();
    }

    protected final Stream<LogicTuple> retrieveTuplesImpl(LogicTemplate template, int limit) {
        return PrologUtils.solutionsStream(engine, PrologUtils.retractTerm(template.toTuple().asTerm()))
                .limit(limit)
                .map(term -> (Struct)term)
                .map(struct -> struct.getArg(0))
                .map(LogicTuple::of);
    }

    @Override
    protected final Optional<LogicTuple> retrieveTuple(LogicTemplate template) {
        return retrieveTuplesImpl(template, 1).findAny();
    }

    @Override
    protected void insertTuple(LogicTuple tuple) {
        engine.getTheoryManager().assertZ(tuple.asTerm(), true, null, false);
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
