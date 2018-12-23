package it.unibo.coordination.linda.logic;

import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import alice.tuprolog.Var;
import it.unibo.coordination.linda.core.Template;
import it.unibo.coordination.linda.core.Tuple;

import java.util.Objects;

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
    LogicMatch matchWith(Tuple tuple);

    Struct asTerm();

    Term getTemplate();

    default LogicTuple toTuple() {
        return LogicTuple.of(getTemplate());
    }

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();

    static boolean equals(LogicTemplate t1, LogicTemplate t2) {
        if (t1 == t2) return true;
        if (t1 == null || t2 == null) return false;
        return Objects.equals(t1.asTerm(), t2.asTerm());
    }

    static int hashCode(LogicTemplate t) {
        return Objects.hashCode(t.asTerm().toString());
    }
}
