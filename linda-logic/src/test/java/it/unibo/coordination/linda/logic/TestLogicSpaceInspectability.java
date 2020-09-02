package it.unibo.coordination.linda.logic;


import it.unibo.coordination.linda.test.TestTupleSpaceInspectability;
import it.unibo.tuprolog.core.Term;

import java.util.concurrent.ExecutorService;

public class TestLogicSpaceInspectability extends TestTupleSpaceInspectability<LogicTuple, LogicTemplate, String, Term, LogicMatch, InspectableLogicSpace> {

    @Override
    protected InspectableLogicSpace getTupleSpace(ExecutorService executor) {
        return InspectableLogicSpace.local(executor);
    }

    public TestLogicSpaceInspectability() {
        super(new LogicTupleTemplateFactory());
    }
}