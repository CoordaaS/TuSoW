package it.unibo.coordination.tusow.presentation;

import java.io.Writer;

public interface Marshaller<T> {

    MIMETypes getSupportedMIMEType();

    Object toDynamicObject(T object);

    String toString(T object);

    void write(T object, Writer writer);
}
