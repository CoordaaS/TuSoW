package it.unibo.coordination.tusow.presentation;

import java.io.Writer;
import java.util.List;
import java.util.stream.Collectors;

public interface Marshaller<T> {

    MIMETypes getSupportedMIMEType();

    Object toDynamicObject(T object);

    default List<Object> toDynamicObject(List<? extends T> objects) {
        return objects.stream().map(this::toDynamicObject).collect(Collectors.toList());
    }

    String toString(T object);

    String toString(List<? extends T> objects);

    void write(T object, Writer writer);

    void write(List<? extends T>  objects, Writer writer);
}
