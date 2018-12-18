package it.unibo.coordination.tuples.logic;

import alice.tuprolog.NoMoreSolutionException;
import alice.tuprolog.NoSolutionException;
import alice.tuprolog.Prolog;
import alice.tuprolog.Var;
import it.unibo.coordination.tuples.core.impl.AbstractTupleSpace;
import it.unibo.tuprolog.utils.PrologUtils;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.multiset.HashMultiSet;

import java.util.Optional;
import java.util.concurrent.ExecutorService;

class LogicSpaceImpl extends AbstractTupleSpace<LogicTuple, LogicTemplate> implements LogicSpace {

    private final Prolog engine = new Prolog();

    public LogicSpaceImpl(String name, ExecutorService executor) {
        super(name, executor);
    }

    @Override
    protected MultiSet<LogicTuple> lookForTuples(LogicTemplate template, int limit) {
        var result = new HashMultiSet<LogicTuple>();

        if (limit <= 0) return result;

        try {
            var si = engine.solve(template.getTupleTemplate());

            for (int i = 1; si.isSuccess(); i++) {
                result.add(LogicTuple.of(si.getSolution()));
                if (i < limit && si.hasOpenAlternatives()) {
                    si = engine.solveNext();
                } else {
                    break;
                }
            }
        } catch (NoSolutionException e) {
            throw new IllegalStateException(e);
        } catch (NoMoreSolutionException e) {
            // it's ok
        } finally {
            engine.solveEnd();
        }

        return result;
    }

    @Override
    protected Optional<LogicTuple> lookForTuple(LogicTemplate template) {
        return lookForTuples(template, 1).stream().findAny();
    }

    @Override
    protected MultiSet<LogicTuple> retrieveTuples(LogicTemplate template, int limit) {
        var result = new HashMultiSet<LogicTuple>();

        if (limit <= 0) return result;

        try {
            var si = engine.solve(PrologUtils.assertTerm(template.getTupleTemplate()));

            for (int i = 1; si.isSuccess(); i++) {
                result.add(LogicTuple.of(si.getSolution()));
                if (i < limit && si.hasOpenAlternatives()) {
                    si = engine.solveNext();
                } else {
                    break;
                }
            }
        } catch (NoSolutionException e) {
            throw new IllegalStateException(e);
        } catch (NoMoreSolutionException e) {
            // it's ok
        } finally {
            engine.solveEnd();
        }

        return result;
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
