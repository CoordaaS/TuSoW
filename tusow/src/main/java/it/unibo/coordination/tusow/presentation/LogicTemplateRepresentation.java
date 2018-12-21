package it.unibo.coordination.tusow.presentation;

import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import it.unibo.coordination.linda.core.Tuple;
import it.unibo.coordination.linda.logic.LogicTemplate;
import it.unibo.coordination.prologx.PrologUtils;

import java.io.IOException;
import java.util.Objects;

import static it.unibo.coordination.tusow.presentation.MIMETypes.*;

public class LogicTemplateRepresentation extends AbstractRepresentation implements LogicTemplate, TemplateRepresentation {

    private final LogicTemplate adapted;

    public static LogicTemplateRepresentation wrap(LogicTemplate tuple) {
        if (tuple instanceof LogicTemplateRepresentation) {
            return (LogicTemplateRepresentation) tuple;
        } else {
            return new LogicTemplateRepresentation(tuple);
        }
    }

    public static LogicTemplateRepresentation of(Term tuple) {
        return wrap(LogicTemplate.of(tuple));
    }

    public static LogicTemplateRepresentation of(String tuple) {
        return wrap(LogicTemplate.of(tuple));
    }

    @Override
    public Match matchWith(Tuple tuple) {
        return adapted.matchWith(tuple);
    }

    public LogicTemplateRepresentation(LogicTemplate adapted) {
        this.adapted = Objects.requireNonNull(adapted);
    }

    @Override
    public Struct asTerm() {
        return adapted.asTerm();
    }

    @Override
    public Term getTemplate() {
        return adapted.getTemplate();
    }

    @Override
    public String toString() {
        return asTerm().toString();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof LogicTemplate
                && LogicTemplate.equals(this, (LogicTemplate) o);
    }

    @Override
    public int hashCode() {
        return LogicTemplate.hashCode(this);
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

    public LogicTemplate getWrapped() {
        return adapted;
    }

    public static LogicTemplateRepresentation fromObject(Object object) {
        return LogicTemplateRepresentation.of(PrologUtils.objectToTerm(object));
    }

    public static LogicTemplateRepresentation fromJSON(String representation) throws IOException {
        return fromObject(
                AbstractRepresentation.fromJSON(representation, Object.class)
        );
    }

    public static LogicTemplateRepresentation fromYAML(String representation) throws IOException {
        return fromObject(
                AbstractRepresentation.fromYAML(representation, Object.class)
        );
    }

    public static LogicTemplateRepresentation fromXML(String representation) throws IOException {
        return fromObject(
                AbstractRepresentation.fromXML(representation, Object.class)
        );
    }

    public static LogicTemplateRepresentation parse(String mimeType, String representation) throws IOException {
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
