package it.unibo.coordination.tusow.presentation;

import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import it.unibo.coordination.linda.logic.LogicTuple;
import it.unibo.coordination.prologx.PrologUtils;

import java.io.IOException;
import java.util.*;

import static it.unibo.coordination.tusow.presentation.MIMETypes.*;

public class LogicTupleRepresentation extends AbstractRepresentation implements LogicTuple, TupleRepresentation {

    private final LogicTuple adapted;

    public static LogicTupleRepresentation wrap(LogicTuple tuple) {
        if (tuple instanceof LogicTupleRepresentation) {
            return (LogicTupleRepresentation) tuple;
        } else {
            return new LogicTupleRepresentation(tuple);
        }
    }

    public static LogicTupleRepresentation of(Term tuple) {
        return wrap(LogicTuple.of(tuple));
    }

    public static LogicTupleRepresentation of(String tuple) {
        return wrap(LogicTuple.of(tuple));
    }

    public LogicTupleRepresentation(LogicTuple adapted) {
        this.adapted = Objects.requireNonNull(adapted);
    }

    @Override
    public Struct asTerm() {
        return adapted.asTerm();
    }

    @Override
    public Term getTuple() {
        return adapted.getTuple();
    }

    @Override
    public String toString() {
        return asTerm().toString();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof LogicTuple
                && LogicTuple.equals(this, (LogicTuple) o);
    }

    @Override
    public int hashCode() {
        return LogicTuple.hashCode(this);
    }

    @Override
    public String toXMLString() {
        return writeAsXML(toObject());
    }

    @Override
    public Object toObject() {
        return PrologUtils.termToObject(asTerm());
    }

    @Override
    public String toJSONString() {
        return writeAsJSON(toObject());
    }

    @Override
    public String toYAMLString() {
        return writeAsYAML(toObject());
    }

    public LogicTuple getWrapped() {
        return adapted;
    }

    public static LogicTupleRepresentation fromObject(Object object) {
        return LogicTupleRepresentation.of(PrologUtils.objectToTerm(object));
    }

    public static LogicTupleRepresentation fromJSON(String representation) throws IOException {
        return fromObject(
                AbstractRepresentation.fromJSON(representation, Object.class)
        );
    }

    public static LogicTupleRepresentation fromYAML(String representation) throws IOException {
        return fromObject(
                AbstractRepresentation.fromYAML(representation, Object.class)
        );
    }

    public static LogicTupleRepresentation fromXML(String representation) throws IOException {
        return fromObject(
                AbstractRepresentation.fromXML(representation, Object.class)
        );
    }

    protected static LogicTupleRepresentation parse(String mimeType, String representation) throws IOException {
        if (APPLICATION_JSON.equals(mimeType) || APPLICATION_ANY.equals(mimeType) || ANY.equals(mimeType)) {
            return fromJSON(representation);
        } else if (APPLICATION_XML.equals(mimeType)) {
            return fromXML(representation);
        } else if (APPLICATION_YAML.equals(mimeType)) {
            return fromYAML(representation);
        } else {
            throw new IllegalArgumentException(String.format("Cannot parse \'%s\' as '%s'", representation, mimeType));
        }
    }
}
