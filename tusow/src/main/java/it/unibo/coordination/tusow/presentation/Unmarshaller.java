package it.unibo.coordination.tusow.presentation;

import java.io.Reader;

public interface Unmarshaller<T> {

    MIMETypes getSupportedMIMEType();

    T fromDynamicObject(Object dynamicObject);

    T fromString(String string);

    T read(Reader reader);
}
