package it.unibo.coordination.linda.logic;

import alice.tuprolog.Term;
import it.unibo.coordination.Engine;
import it.unibo.coordination.linda.core.InspectableTupleSpace;

import java.util.concurrent.ExecutorService;

public interface InspectableLogicSpace extends LogicSpace, InspectableTupleSpace<LogicTuple, LogicTemplate, String, Term> {

    static InspectableLogicSpace create(String name, ExecutorService executorService) {
        return new DeterministicLogicSpaceImpl(name, executorService);
    }

    static InspectableLogicSpace create(String name) {
        return create(name, Engine.getDefaultEngine());
    }

    static InspectableLogicSpace create(ExecutorService executorService) {
        return create(null, executorService);
    }

}
