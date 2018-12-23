package it.unibo.coordination.linda.logic;

import alice.tuprolog.*;
import it.unibo.coordination.linda.core.Tuple;
import it.unibo.coordination.prologx.PrologUtils;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
            return new LogicMatchImpl(si, logicTuple);
        }

        return new LogicMatchImpl(null, null);
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

    private class LogicMatchImpl implements LogicMatch {

        private final SolveInfo solveInfo;
        private final Tuple tuple;

        LogicMatchImpl(SolveInfo solveInfo, Tuple tuple) {
            this.solveInfo = solveInfo;
            this.tuple = tuple;
        }

        @Override
        public Optional<LogicTuple> getTuple() {
            return tuple instanceof LogicTuple ? Optional.of((LogicTuple) tuple) : Optional.empty();
        }

        @Override
        public LogicTemplate getTemplate() {
            return LogicTemplateImpl.this;
        }

        @Override
        public boolean isSuccess() {
            return solveInfo != null && solveInfo.isSuccess();
        }

        @Override
        public Optional<Term> get(String variableName) {
            try {
                if (solveInfo != null && solveInfo.isSuccess()) {
                    return Optional.ofNullable(solveInfo.getVarValue(variableName));
                } else {
                    return Optional.empty();
                }
            } catch (NoSolutionException e) {
                return Optional.empty();
            }
        }

        @Override
        public String toString() {
            try {
                if (solveInfo.isSuccess()) {
                    return solveInfo.getBindingVars().stream()
                            .map(v -> String.format("%s/%s", v.getOriginalName(), v.getTerm()))
                            .collect(Collectors.joining("; "));
                } else {
                    return "no";
                }
            } catch (NoSolutionException e) {
                return "no";
            }
        }
    }
}
