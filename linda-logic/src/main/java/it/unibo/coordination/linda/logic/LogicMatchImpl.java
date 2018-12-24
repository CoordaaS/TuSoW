package it.unibo.coordination.linda.logic;

import alice.tuprolog.NoSolutionException;
import alice.tuprolog.SolveInfo;
import alice.tuprolog.Term;
import alice.tuprolog.Var;
import it.unibo.coordination.linda.core.Tuple;

import java.util.Map;
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
    public boolean isMatching() {
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
    public Map<String, Term> toMap() {
        if (solveInfo != null && solveInfo.isSuccess()) {
            try {
                return solveInfo.getBindingVars().stream()
                        .collect(
                                Collectors.toUnmodifiableMap(
                                        Var::getOriginalName,
                                        Var::getLink
                                )
                            );
            } catch (NoSolutionException e) {
                return Map.of();
            }
        } else {
            return Map.of();
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
