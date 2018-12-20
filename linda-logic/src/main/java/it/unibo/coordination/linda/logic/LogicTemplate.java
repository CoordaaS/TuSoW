package it.unibo.coordination.linda.logic;

import alice.tuprolog.*;
import it.unibo.coordination.linda.core.Template;
import it.unibo.coordination.linda.core.Tuple;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public final class LogicTemplate implements Template {

    private static final String TEMPLATE_WRAPPER = "template";
    private static final Prolog ENGINE = new Prolog();
    private static final Match FAILURE = new LogicMatch(null);
    private final Struct term;

    private LogicTemplate(final Term term) {
        Objects.requireNonNull(term);
        if (term instanceof Struct && ((Struct) term).getName().equals(TEMPLATE_WRAPPER) && ((Struct) term).getArity() == 1) {
            this.term = (Struct) term;
        } else {
            this.term = new Struct(TEMPLATE_WRAPPER, term);
        }
    }

    public static LogicTemplate of(String template) {
        return LogicTemplate.of(Term.createTerm(Objects.requireNonNull(template)));
    }

    public static LogicTemplate of(Term term) {
        return new LogicTemplate(term);
    }

    @Override
    public Match matchWith(Tuple tuple) {
        if (tuple instanceof LogicTuple) {
            final SolveInfo si = ENGINE.solve(new Struct("=", getTemplate(), ((LogicTuple) tuple).getTuple()));
            return new LogicMatch(si);
        }

        return FAILURE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogicTemplate that = (LogicTemplate) o;
        return Objects.equals(term, that.term);
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

    public Term getTemplate() {
        return asTerm().getArg(0);
    }

    Struct getTupleTemplate() {
        return LogicTuple.getPattern(getTemplate());
    }

    private static class LogicMatch implements Match {

        private final SolveInfo solveInfo;

        public LogicMatch(SolveInfo solveInfo) {
            this.solveInfo = solveInfo;
        }

        @Override
        public boolean isSuccess() {
            return solveInfo != null && solveInfo.isSuccess();
        }

        @Override
        public <X> Optional<X> get(Object key) {
            try {
                if (solveInfo != null && solveInfo.isSuccess()) {
                    return Optional.ofNullable((X) solveInfo.getVarValue(key.toString()));
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
