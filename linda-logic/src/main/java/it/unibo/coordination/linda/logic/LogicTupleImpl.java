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
        return asTerm().toString();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof LogicTuple
                && LogicTuple.equals(this, (LogicTuple) o);
    }

    @Override
    public int hashCode() {
        return LogicTuple.hashCode(this);
    }

    public Struct asTerm() {
        return term;
    }

    public Term getValue() {
        return asTerm().getArg(0);
    }
}
