package it.unibo.coordination.tusow.presentation;

import it.unibo.coordination.linda.core.Match;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractMatchRepresentation<T extends TupleRepresentation, TT extends TemplateRepresentation, K, V> extends AbstractRepresentation implements MatchRepresentation<T, TT, K, V> {

    private final Match<T, TT, K, V> match;

    AbstractMatchRepresentation(Match<T, TT, K, V> match) {
        this.match = Objects.requireNonNull(match);
    }

    @Override
    public Optional<T> getTuple() {
        return match.getTuple();
    }

    @Override
    public TT getTemplate() {
        return match.getTemplate();
    }

    @Override
    public boolean isMatching() {
        return match.isMatching();
    }

    @Override
    public Optional<V> get(K key) {
        return match.get(key);
    }

    @Override
    public Map<K, V> toMap() {
        return match.toMap();
    }

    @Override
    public String toXMLString() {
        return writeAsXML(toObject());
    }

    @Override
    public HashMap<String, Object> toObject() {
        var dynamicObject = new HashMap<String, Object>();
        var map = toMap().entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> valueToDynamicObject(entry.getValue())
                ));
        dynamicObject.put("match", isMatching());
        dynamicObject.put("tuple", getTuple().map(TupleRepresentation::toObject).orElse(null));
        dynamicObject.put("template", getTemplate().toObject());
        dynamicObject.put("map", map);

        return dynamicObject;
    }

    protected abstract Object valueToDynamicObject(V value);

    @Override
    public String toJSONString() {
        return writeAsJSON(toObject());
    }

    @Override
    public String toYAMLString() {
        return writeAsYAML(toObject());
    }
}
