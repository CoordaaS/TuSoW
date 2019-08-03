package it.unibo.coordination.linda.logic;

import alice.tuprolog.SolveInfo;
import alice.tuprolog.Term;
import alice.tuprolog.Var;
import alice.tuprolog.exceptions.NoSolutionException;
import it.unibo.coordination.linda.core.Match;
import it.unibo.coordination.linda.core.Tuple;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

class LogicMatchImpl implements LogicMatch {

    private final LogicTemplate logicTemplate;
    private final SolveInfo solveInfo;
    private final Tuple tuple;
    private Map<String, Term> cache = null;


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
        return Optional.ofNullable(toMap().get(variableName));
    }

    private Map<String, Term> generateMap() {
        if (solveInfo != null && solveInfo.isSuccess()) {
            try {
                return solveInfo.getBindingVars().stream()
                        .filter(v -> v.getLink() != null)
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
    public Map<String, Term> toMap() {
        if (cache == null) {
            cache = generateMap();
        }
        return cache;
    }

    @Override
    public String toString() {
        return LogicMatch.toString(this);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof LogicMatch && Match.equals(this, (LogicMatch) o);
    }

    @Override
    public int hashCode() {
        return Match.hashCode(this);
    }
}
