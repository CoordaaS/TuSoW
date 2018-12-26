package it.unibo.coordination.linda.logic;

import alice.tuprolog.Term;
import it.unibo.coordination.linda.core.Match;

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
            };
        }
    }
}
