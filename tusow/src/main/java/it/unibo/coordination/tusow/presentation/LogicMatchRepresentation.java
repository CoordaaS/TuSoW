package it.unibo.coordination.tusow.presentation;

import alice.tuprolog.Term;
import it.unibo.coordination.linda.core.Match;
import it.unibo.coordination.linda.logic.LogicMatch;
import it.unibo.coordination.linda.logic.LogicTemplate;
import it.unibo.coordination.linda.logic.LogicTuple;
import it.unibo.coordination.prologx.PrologUtils;

import java.io.IOException;
import java.util.Map;

import static it.unibo.coordination.tusow.presentation.MIMETypes.*;

public class LogicMatchRepresentation extends AbstractMatchRepresentation<LogicTuple, LogicTemplate, String, Term> {

    private LogicMatchRepresentation(Match<LogicTuple, LogicTemplate, String, Term> match) {
        super(match);
    }

    @Override
    protected Object valueToDynamicObject(Term value) {
        return PrologUtils.termToDynamicObject(value);
    }

    public static LogicMatchRepresentation fromObject(Object object) {
        if (object instanceof Map) {
            final var objectMap = (Map<String, Object>) object;

            if (objectMap.containsKey("template")) {

                final var template = LogicTemplateRepresentation.fromObject(objectMap.get("template"));
                final var tupleObject = objectMap.get("tuple");

                if (tupleObject != null) {
                    final var tuple = LogicTupleRepresentation.fromObject(tupleObject);

                    return new LogicMatchRepresentation(template.matchWith(tuple));
                }

                return new LogicMatchRepresentation(LogicMatch.failed(template));
            }
        }
        throw new IllegalArgumentException();
    }

    public static LogicMatchRepresentation fromJSON(String representation) throws IOException {
        return fromObject(
                AbstractRepresentation.fromJSON(representation, Object.class)
        );
    }

    public static LogicMatchRepresentation fromYAML(String representation) throws IOException {
        return fromObject(
                AbstractRepresentation.fromYAML(representation, Object.class)
        );
    }

    public static LogicMatchRepresentation fromXML(String representation) throws IOException {
        return fromObject(
                AbstractRepresentation.fromXML(representation, Object.class)
        );
    }

    public static LogicMatchRepresentation parse(String mimeType, String representation) throws IOException {
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
