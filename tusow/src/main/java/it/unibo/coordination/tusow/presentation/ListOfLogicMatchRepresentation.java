package it.unibo.coordination.tusow.presentation;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static it.unibo.coordination.tusow.presentation.MIMETypes.*;

public class ListOfLogicMatchRepresentation extends ListRepresentation<LogicMatchRepresentation> {

    public ListOfLogicMatchRepresentation() {
    }

    public ListOfLogicMatchRepresentation(Collection<? extends LogicMatchRepresentation> collection) {
        super(collection);
    }

    public ListOfLogicMatchRepresentation(Stream<? extends LogicMatchRepresentation> stream) {
        super(stream);
    }

    public ListOfLogicMatchRepresentation(LogicMatchRepresentation element1, LogicMatchRepresentation... elements) {
        super(element1, elements);
    }

    public List<LogicMatchRepresentation> getTuples() {
        return getItems();
    }

    public ListOfLogicMatchRepresentation setTuples(List<LogicMatchRepresentation> tuples) {
        setItems(tuples);
        return this;
    }

    @Override
    public String toJSONString() {
        return writeAsJSON(Map.of("matches", toObject()));
    }

    @Override
    public String toYAMLString() {
        return writeAsYAML(Map.of("matches", toObject()));
    }

    @Override
    public String toXMLString() {
        return writeAsXML(Map.of("matches", toObject()));
    }

    public static ListOfLogicMatchRepresentation fromObject(Object object) {
        if (object instanceof Map) {
            var objectMap = (Map<String, ?>) object;

            if (objectMap.containsKey("matches")) {
                if (objectMap.get("matches") instanceof List) {
                    var items = (List<?>) objectMap.get("matches");
                    return new ListOfLogicMatchRepresentation(
                        items.stream().map(LogicMatchRepresentation::fromObject)
                    );
                }
            }
        }
        throw new IllegalArgumentException();
    }

    public static ListOfLogicMatchRepresentation fromJSON(String representation) throws IOException {
        return fromObject(
                AbstractRepresentation.fromJSON(representation, Object.class)
        );
    }

    public static ListOfLogicMatchRepresentation fromYAML(String representation) throws IOException {
        return fromObject(
                AbstractRepresentation.fromYAML(representation, Object.class)
        );
    }

    public static ListOfLogicMatchRepresentation fromXML(String representation) throws IOException {
        return fromObject(
                AbstractRepresentation.fromXML(representation, Object.class)
        );
    }

    public static ListOfLogicMatchRepresentation parse(String mimeType, String representation) throws IOException {
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
