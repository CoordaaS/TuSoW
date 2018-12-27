package it.unibo.coordination.tusow.presentation;

import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public interface Marshaller<T> {

    MIMETypes getSupportedMIMEType();

    Object toDynamicObject(T object);

    default List<Object> toDynamicObject(Collection<? extends T> objects) {
        return objects.stream().map(this::toDynamicObject).collect(Collectors.toList());
    }

    String toString(T object);

    String toString(Collection<? extends T> objects);

    void write(T object, Writer writer);

    void write(Collection<? extends T>  objects, Writer writer);
}
