package it.unibo.coordination.tusow.presentation;

import java.io.Reader;
import java.util.List;
import java.util.stream.Collectors;

public interface Unmarshaller<T> {

    Class<T> getSupportedType();

    MIMETypes getSupportedMIMEType();

    T fromDynamicObject(Object dynamicObject);

    default List<T> listFromDynamicObject(Object dynamicObject) {
        if (!(dynamicObject instanceof List)) {
            throw new IllegalArgumentException();
        }

        var list = (List<?>) dynamicObject;

        return list.stream().map(this::fromDynamicObject).collect(Collectors.toList());
    }

    T fromString(String string);

    List<T>  listFromString(String string);

    T read(Reader reader);

    List<T>  readList(Reader reader);
}
