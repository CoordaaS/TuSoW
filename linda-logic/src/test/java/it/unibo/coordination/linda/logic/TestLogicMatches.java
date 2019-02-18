package it.unibo.coordination.linda.logic;

import alice.tuprolog.Term;
import it.unibo.coordination.linda.test.TestMatch;

public class TestLogicMatches extends TestMatch<LogicTuple, LogicTemplate, String, Term, LogicMatch> {

    public TestLogicMatches() {
        super(new LogicTupleTemplateFactory());
    }
}
