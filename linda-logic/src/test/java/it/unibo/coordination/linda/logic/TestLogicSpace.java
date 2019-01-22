package it.unibo.coordination.linda.logic;

import alice.tuprolog.Term;
import it.unibo.coordination.linda.test.TestTupleSpace;

import java.util.concurrent.ExecutorService;

public class TestLogicSpace extends TestTupleSpace<LogicTuple, LogicTemplate, String, Term, LogicSpace> {

    @Override
    protected LogicSpace getTupleSpace(ExecutorService executor) {
        return LogicSpace.deterministic(executor);
    }

    public TestLogicSpace() {
        super(new LogicTupleTemplateFactory());
    }
}
