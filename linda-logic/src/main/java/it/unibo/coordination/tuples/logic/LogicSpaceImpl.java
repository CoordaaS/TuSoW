package it.unibo.coordination.tuples.logic;

import alice.tuprolog.Prolog;
import alice.tuprolog.Struct;
import alice.tuprolog.Var;
import it.unibo.coordination.tuples.core.impl.AbstractTupleSpace;
import it.unibo.tuprolog.utils.PrologUtils;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.multiset.HashMultiSet;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

class LogicSpaceImpl extends AbstractTupleSpace<LogicTuple, LogicTemplate> implements InspectableLogicSpace {

    private final Prolog engine = new Prolog();

    private final List<PendingRequest> pendingQueue = new LinkedList<>();

    public LogicSpaceImpl(String name, ExecutorService executor) {
        super(name, executor);
    }

    @Override
    protected Collection<PendingRequest> getPendingRequests() {
        return pendingQueue;
    }

    @Override
    protected MultiSet<LogicTuple> lookForTuples(LogicTemplate template, int limit) {
        return PrologUtils.solutionsStream(engine, template.getTupleTemplate())
                .limit(limit)
                .map(LogicTuple::of)
                .collect(Collectors.toCollection(HashMultiSet::new));
    }

    @Override
    protected Optional<LogicTuple> lookForTuple(LogicTemplate template) {
        return lookForTuples(template, 1).stream().findAny();
    }

    @Override
    protected MultiSet<LogicTuple> retrieveTuples(LogicTemplate template, int limit) {
        return PrologUtils.solutionsStream(engine, PrologUtils.retractTerm(template.getTupleTemplate()))
                .limit(limit)
                .map(term -> (Struct)term)
                .map(struct -> struct.getArg(0))
                .map(LogicTuple::of)
                .collect(Collectors.toCollection(HashMultiSet::new));
    }

    @Override
    protected Optional<LogicTuple> retrieveTuple(LogicTemplate template) {
        return retrieveTuples(template, 1).stream().findAny();
    }

    @Override
    protected void insertTuple(LogicTuple tuple) {
        engine.getTheoryManager().assertA(tuple.asTerm(), true, null, false);
    }

    @Override
    protected MultiSet<LogicTuple> getAllTuples() {
        return lookForTuples(LogicTemplate.of(new Var()), Integer.MAX_VALUE);
    }

    @Override
    protected int countTuples() {
        return getAllTuples().size();
    }
}
