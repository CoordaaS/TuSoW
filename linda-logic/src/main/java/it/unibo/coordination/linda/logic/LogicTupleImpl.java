package it.unibo.coordination.linda.logic;

import alice.tuprolog.Struct;
import alice.tuprolog.Term;

import java.util.Objects;

final class LogicTupleImpl implements LogicTuple {

    private final Struct term;

    LogicTupleImpl(final Term term) {
        Objects.requireNonNull(term);
        if (term instanceof Struct && LogicTuple.getPattern().match(term)) {
            this.term = (Struct) term;
        } else {
            this.term = LogicTuple.getPattern(term);
        }
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
        return Objects.equals(term, that.asTerm());
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
