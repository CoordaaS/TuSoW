package it.unibo.coordination.linda.logic;

import alice.tuprolog.Prolog;
import alice.tuprolog.SolveInfo;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import it.unibo.coordination.linda.core.Tuple;
import it.unibo.coordination.prologx.PrologUtils;

import java.util.Objects;

final class LogicTemplateImpl implements LogicTemplate {

    private static final Prolog ENGINE = new Prolog();
    private final Struct term;

    LogicTemplateImpl(final Term term) {
        Objects.requireNonNull(term);

        if (term instanceof Struct && LogicTemplate.getPattern().match(term)) {
            this.term = (Struct) term;
        } else {
            this.term = LogicTemplate.getPattern(term);
        }
    }

    @Override
    public LogicMatch matchWith(Tuple tuple) {
        if (tuple instanceof LogicTuple) {
            final LogicTuple logicTuple = (LogicTuple) tuple;
            final SolveInfo si = ENGINE.solve(PrologUtils.unificationTerm(getTemplate(), logicTuple.getTuple()));
            return new LogicMatchImpl(this, si, logicTuple);
        }

        return new LogicMatchImpl(this, null, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogicTemplate that = (LogicTemplate) o;
        return Objects.equals(term, that.asTerm());
    }

    @Override
    public int hashCode() {
        return Objects.hash(term.toString());
    }

    @Override
    public String toString() {
        return term.toString();
    }

    public Struct asTerm() {
        return term;
    }

    @Override
    public Term getTemplate() {
        return term.getArg(0);
    }

}
