package it.unibo.coordination.linda.logic;

import alice.tuprolog.Term;
import it.unibo.coordination.linda.test.TestTupleSpaceInspectability;

import java.util.concurrent.ExecutorService;

public class TestLogicSpaceInspectability extends TestTupleSpaceInspectability<LogicTuple, LogicTemplate, String, Term, InspectableLogicSpace> {

    @Override
    protected InspectableLogicSpace getTupleSpace(ExecutorService executor) {
        return InspectableLogicSpace.create(executor);
    }

    public TestLogicSpaceInspectability() {
        super(new LogicTupleTemplateFactory());
    }
}