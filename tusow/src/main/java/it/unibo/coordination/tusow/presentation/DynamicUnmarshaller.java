package it.unibo.coordination.tusow.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Reader;
import java.util.List;

public abstract class DynamicUnmarshaller<T> extends SimpleUnmarshaller<T> {

    public DynamicUnmarshaller(Class<T> clazz, MIMETypes mimeType, ObjectMapper mapper) {
        super(clazz, mimeType, mapper);
    }

    @Override
    public abstract T fromDynamicObject(Object dynamicObject);

    @Override
    public T read(Reader reader) {
        return fromDynamicObject(readImpl(reader, Object.class));
    }

    @Override
    public List<T> readList(Reader reader) {
        return listFromDynamicObject(readImpl(reader, Object.class));
    }
}
