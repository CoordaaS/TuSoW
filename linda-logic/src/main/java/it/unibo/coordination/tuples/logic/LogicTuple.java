package it.unibo.coordination.tuples.logic;

import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import alice.tuprolog.Var;
import it.unibo.coordination.tuples.core.Tuple;

import java.util.Objects;

public final class LogicTuple implements Tuple, Comparable<LogicTuple> {
    private static final String TUPLE_WRAPPER = "tuple";
    private static Struct PATTERN = new Struct(TUPLE_WRAPPER, new Var("T"));
    private final Struct term;

    private LogicTuple(final Term term) {
        Objects.requireNonNull(term);
        if (term instanceof Struct && ((Struct) term).getName().equals(TUPLE_WRAPPER) && ((Struct) term).getArity() == 1) {
            this.term = (Struct) term;
        } else {
            this.term = new Struct(TUPLE_WRAPPER, term);
        }
    }

    public static Struct getPattern() {
        return PATTERN;
    }

    public static Struct getPattern(Term term) {
        return new Struct(TUPLE_WRAPPER, term);
    }

    public static LogicTuple of(String tuple) {
        return LogicTuple.of(Term.createTerm(Objects.requireNonNull(tuple)));
    }

    public static LogicTuple of(Term term) {
        return new LogicTuple(term);
    }

    @Override
    public String toString() {
        return term.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogicTuple that = (LogicTuple) o;
        return Objects.equals(term, that.term);
    }

    @Override
    public int hashCode() {
        return Objects.hash(term.toString());
    }

    public Struct asTerm() {
        return term;
    }

    public Term getTuple() {
        return asTerm().getArg(0);
    }

    @Override
    public int compareTo(LogicTuple o) {
        return getTuple().toString().compareTo(o.getTuple().toString());
    }
}
