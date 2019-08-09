package it.unibo.coordination.linda.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Writer;
import java.util.Collection;

abstract class DynamicSerializer<T> extends SimpleSerializer<T> {


    public DynamicSerializer(MIMETypes mimeType, ObjectMapper mapper) {
        super(mimeType, mapper);
    }

    @Override
    public abstract Object toDynamicObject(T object);

    @Override
    public void write(T object, Writer writer) {
        writeImpl(toDynamicObject(object), writer);
    }

    @Override
    public void write(Collection<? extends T> objects, Writer writer) {
        writeImpl(toDynamicObject(objects), writer);
    }
}
