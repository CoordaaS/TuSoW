package it.unibo.coordination.linda.logic;

import alice.tuprolog.Term;
import it.unibo.coordination.linda.core.Match;

public interface LogicMatch extends Match<LogicTuple, LogicTemplate, String, Term> {
    static LogicMatch failed() {
        throw new IllegalStateException("not implemented");
    }
}
