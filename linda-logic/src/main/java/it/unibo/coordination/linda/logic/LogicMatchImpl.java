package it.unibo.coordination.linda.logic;

import alice.tuprolog.NoSolutionException;
import alice.tuprolog.SolveInfo;
import alice.tuprolog.Term;
import it.unibo.coordination.linda.core.Tuple;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

class LogicMatchImpl implements LogicMatch {

    private final LogicTemplate logicTemplate;
    private final SolveInfo solveInfo;
    private final Tuple tuple;

    LogicMatchImpl(LogicTemplate logicTemplate, SolveInfo solveInfo, Tuple tuple) {
        this.logicTemplate = Objects.requireNonNull(logicTemplate);
        this.solveInfo = solveInfo;
        this.tuple = tuple;
    }

    @Override
    public Optional<LogicTuple> getTuple() {
        return tuple instanceof LogicTuple ? Optional.of((LogicTuple) tuple) : Optional.empty();
    }

    @Override
    public LogicTemplate getTemplate() {
        return logicTemplate;
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
            if (solveInfo != null && solveInfo.isSuccess()) {
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
