package it.unibo.coordination.linda.logic;

import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import alice.tuprolog.Var;
import it.unibo.coordination.linda.core.Template;
import it.unibo.coordination.linda.core.Tuple;

import java.util.Objects;
import java.util.Optional;

public interface LogicTemplate extends Template {

    static LogicTemplate of(String template) {
        return of(Term.createTerm(Objects.requireNonNull(template)));
    }

    static LogicTemplate of(Term term) {
        return new LogicTemplateImpl(term);
    }

    static Struct getPattern() {
        return new Struct("template", new Var("T"));
    }

    static Struct getPattern(Term term) {
        return new Struct("template", Objects.requireNonNull(term));
    }

    @Override
    Match matchWith(Tuple tuple);

    Struct asTerm();

    Term getTemplate();

    default LogicTuple toTuple() {
        return LogicTuple.of(getTemplate());
    }

    interface LogicMatch extends Match {

        @Override
        default <X> Optional<X> get(Object key) {
            return get(key.toString()).map(it -> (X)it);
        }

        Optional<Term> get(String variableName);
    }
}
