package it.unibo.coordination.tusow.presentation;

import it.unibo.coordination.prologx.PrologUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static it.unibo.coordination.tusow.presentation.MIMETypes.*;

public class ListOfLogicTupleRepresentation extends ListRepresentation<LogicTupleRepresentation> {

    public ListOfLogicTupleRepresentation() {
    }

    public ListOfLogicTupleRepresentation(Collection<? extends LogicTupleRepresentation> collection) {
        super(collection);
    }

    public ListOfLogicTupleRepresentation(Stream<? extends LogicTupleRepresentation> stream) {
        super(stream);
    }

    public ListOfLogicTupleRepresentation(LogicTupleRepresentation element1, LogicTupleRepresentation... elements) {
        super(element1, elements);
    }

    public List<LogicTupleRepresentation> getTuples() {
        return getItems();
    }

    public ListOfLogicTupleRepresentation setTuples(List<LogicTupleRepresentation> tuples) {
        setItems(tuples);
        return this;
    }

    @Override
    public String toJSONString() {
        return writeAsJSON(Map.of("tuples", toObject()));
    }

    @Override
    public String toYAMLString() {
        return writeAsYAML(Map.of("tuples", toObject()));
    }

    @Override
    public String toXMLString() {
        return writeAsXML(Map.of("tuples", toObject()));
    }

    public static ListOfLogicTupleRepresentation fromObject(Object object) {
        if (object instanceof Map) {
            var objectMap = (Map<String, ?>) object;

            if (objectMap.containsKey("tuples")) {
                if (objectMap.get("tuples") instanceof List) {
                    var items = (List<?>) objectMap.get("tuples");
                    return new ListOfLogicTupleRepresentation(
                        items.stream().map(LogicTupleRepresentation::fromObject)
                    );
                }
            }
        }
        throw new IllegalArgumentException();
    }

    public static ListOfLogicTupleRepresentation fromJSON(String representation) throws IOException {
        return fromObject(
                AbstractRepresentation.fromJSON(representation, Object.class)
        );
    }

    public static ListOfLogicTupleRepresentation fromYAML(String representation) throws IOException {
        return fromObject(
                AbstractRepresentation.fromYAML(representation, Object.class)
        );
    }

    public static ListOfLogicTupleRepresentation fromXML(String representation) throws IOException {
        return fromObject(
                AbstractRepresentation.fromXML(representation, Object.class)
        );
    }

    public static ListOfLogicTupleRepresentation parse(String mimeType, String representation) throws IOException {
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
