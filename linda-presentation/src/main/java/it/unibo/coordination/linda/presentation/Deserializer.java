package it.unibo.coordination.linda.presentation;

import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface Deserializer<T> {

    Class<T> getSupportedType();

    MIMETypes getSupportedMIMEType();

    T fromDynamicObject(Object dynamicObject);

    default List<T> listFromDynamicObject(Object dynamicObject) {
        if (!(dynamicObject instanceof List) && !(dynamicObject instanceof Map)) {
            throw new IllegalArgumentException();
        }

        var list = (dynamicObject instanceof List) ? (List<?>) dynamicObject : List.of((Map<?, ?>) dynamicObject);

        return list.stream().map(this::fromDynamicObject).collect(Collectors.toList());
    }

    T fromString(String string);

    List<T>  listFromString(String string);

    T read(Reader reader);

    List<T>  readList(Reader reader);
}
