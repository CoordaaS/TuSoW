package it.unibo.coordination.linda.logic;

import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import alice.tuprolog.Var;
import it.unibo.coordination.linda.core.Match;
import it.unibo.coordination.prologx.PrologUtils;

import java.util.Map;
import java.util.Optional;

public interface LogicMatch extends Match<LogicTuple, LogicTemplate, String, Term> {

    static LogicMatch failed(LogicTemplate template) {
        return new LogicMatchImpl(template, null, null);
    }

    static LogicMatch wrap(Match<LogicTuple, LogicTemplate, String, Term> match) {
        if (match instanceof LogicMatch) {
            return (LogicMatch) match;
        } else {
            return new LogicMatch() {
                @Override
                public Optional<LogicTuple> getTuple() {
                    return match.getTuple();
                }

                @Override
                public LogicTemplate getTemplate() {
                    return match.getTemplate();
                }

                @Override
                public boolean isMatching() {
                    return match.isMatching();
                }

                @Override
                public Optional<Term> get(String key) {
                    return match.get(key);
                }

                @Override
                public Map<String, Term> toMap() {
                    return match.toMap();
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

            };
        }
    }

    static Struct getPattern() {
        return new Struct("match",
                new Struct("success", new Var("Success")),
                LogicTemplate.getPattern(),
                new Var("Tuple"),
                new Struct("Mappings")
        );
    }

    static String toString(LogicMatch match) {
        return match.asTerm().toString();
    }

    default Struct asTerm() {
        return new Struct("match",
                new Struct("success", isMatching() ? new Struct("yes") : new Struct("no")),
                getTemplate().asTerm(),
                getTuple().map(LogicTuple::asTerm).orElse(new Struct("empty")),
                new Struct(
                        toMap().entrySet().stream()
                                .map(kv -> PrologUtils.unificationTerm(kv.getKey(), kv.getValue()))
                                .toArray(Term[]::new)
                )
        );
    }
}
