package it.unibo.coordination.tusow.presentation;

import java.io.Reader;
import java.util.List;
import java.util.stream.Collectors;

public interface Unmarshaller<T> {

    Class<T> getSupportedClass();

    MIMETypes getSupportedMIMEType();

    T fromDynamicObject(Object dynamicObject);

    default List<? super T> listFromDynamicObject(Object dynamicObject) {
        if (!(dynamicObject instanceof List)) {
            throw new IllegalArgumentException();
        }

        var list = (List<?>) dynamicObject;

        return list.stream().map(this::fromDynamicObject).collect(Collectors.toList());
    }

    T fromString(String string);

    List<? super T>  listFromString(String string);

    T read(Reader reader);

    List<? super T>  readList(Reader reader);
}
