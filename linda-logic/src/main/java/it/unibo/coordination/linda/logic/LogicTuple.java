package it.unibo.coordination.linda.logic;

import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import alice.tuprolog.Var;
import it.unibo.coordination.linda.core.Tuple;

import java.util.Objects;

public interface LogicTuple extends Tuple, Comparable<LogicTuple> {

    static LogicTuple of(String tuple) {
        return LogicTuple.of(Term.createTerm(Objects.requireNonNull(tuple)));
    }

    static LogicTuple of(Term term) {
        return new LogicTupleImpl(term);
    }

    static Struct getPattern() {
        return new Struct("tuple", new Var("T"));
    }

    static Struct getPattern(Term term) {
        return new Struct("tuple", Objects.requireNonNull(term));
    }

    Struct asTerm();

    Term getTuple();

    default int compareTo(LogicTuple o) {
        return getTuple().toString().compareTo(o.getTuple().toString());
    }

    static boolean equals(LogicTuple t1, LogicTuple t2) {
        if (t1 == t2) return true;
        if (t1 == null || t2 == null) return false;
        return Objects.equals(t1.asTerm(), t2.asTerm());
    }

    static int hashCode(LogicTuple t) {
        return Objects.hashCode(t.asTerm().toString());
    }
}
