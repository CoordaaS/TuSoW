package it.unibo.coordination.tusow.presentation;

import it.unibo.coordination.prologx.PrologUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static it.unibo.coordination.tusow.presentation.MIMETypes.*;

public class ListOfLogicTemplateRepresentation extends ListRepresentation<LogicTemplateRepresentation> {

    public ListOfLogicTemplateRepresentation() {
    }

    public ListOfLogicTemplateRepresentation(Collection<? extends LogicTemplateRepresentation> collection) {
        super(collection);
    }

    public ListOfLogicTemplateRepresentation(Stream<? extends LogicTemplateRepresentation> stream) {
        super(stream);
    }

    public ListOfLogicTemplateRepresentation(LogicTemplateRepresentation element1, LogicTemplateRepresentation... elements) {
        super(element1, elements);
    }

    public List<LogicTemplateRepresentation> getTemplates() {
        return getItems();
    }

    public ListOfLogicTemplateRepresentation setTuples(List<LogicTemplateRepresentation> tuples) {
        setItems(tuples);
        return this;
    }

    public Map<String, Object> toObject() {
        return Map.of(
                "templates",
                getTemplates().stream().map(PrologUtils::objectToTerm)
        );
    }

    @Override
    public String toJSONString() {
        return writeAsJSON(
            toObject()
        );
    }

    @Override
    public String toYAMLString() {
        return writeAsJSON(
                toObject()
        );
    }

    @Override
    public String toXMLString() {
        return writeAsJSON(
                toObject()
        );
    }

    public static ListOfLogicTemplateRepresentation fromObject(Object object) {
        if (object instanceof Map) {
            var objectMap = (Map<String, ?>) object;

            if (objectMap.containsKey("tuples")) {
                if (objectMap.get("tuples") instanceof List) {
                    var items = (List<?>) objectMap.get("tuples");
                    return new ListOfLogicTemplateRepresentation(
                        items.stream().map(LogicTemplateRepresentation::fromObject)
                    );
                }
            }
        }
        throw new IllegalArgumentException();
    }

    public static ListOfLogicTemplateRepresentation fromJSON(String representation) throws IOException {
        return fromObject(
                AbstractRepresentation.fromJSON(representation, Object.class)
        );
    }

    public static ListOfLogicTemplateRepresentation fromYAML(String representation) throws IOException {
        return fromObject(
                AbstractRepresentation.fromYAML(representation, Object.class)
        );
    }

    public static ListOfLogicTemplateRepresentation fromXML(String representation) throws IOException {
        return fromObject(
                AbstractRepresentation.fromXML(representation, Object.class)
        );
    }

    protected static ListOfLogicTemplateRepresentation parse(String mimeType, String representation) throws IOException {
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
