package it.unibo.coordination.tusow.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Writer;
import java.util.Collection;

public abstract class DynamicMarshaller<T> extends SimpleMarshaller<T> {


    public DynamicMarshaller(MIMETypes mimeType, ObjectMapper mapper) {
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
