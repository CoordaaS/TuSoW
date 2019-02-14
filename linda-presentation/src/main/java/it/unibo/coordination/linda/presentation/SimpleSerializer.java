package it.unibo.coordination.linda.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Objects;

public class SimpleSerializer<T> implements Serializer<T> {

    private final MIMETypes mimeType;
    private final ObjectMapper mapper;

    public SimpleSerializer(MIMETypes mimeType, ObjectMapper mapper) {
        this.mimeType = Objects.requireNonNull(mimeType);
        this.mapper = Objects.requireNonNull(mapper);
    }

    @Override
    public MIMETypes getSupportedMIMEType() {
        return mimeType;
    }

    @Override
    public Object toDynamicObject(T object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString(T object) {
        final StringWriter sw = new StringWriter();
        write(object, sw);
        return sw.toString();
    }

    @Override
    public String toString(Collection<? extends T> objects) {
        final StringWriter sw = new StringWriter();
        write(objects, sw);
        return sw.toString();
    }

    @Override
    public void write(T object, Writer writer) {
        writeImpl(object, writer);
    }

    protected void writeImpl(Object object, Writer writer) {
        try {
            mapper.writeValue(writer, object);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Cannot convert " + object + " into " + mimeType, e);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot convert " + object + " into " + mimeType, e);
        }
    }

    @Override
    public void write(Collection<? extends T> objects, Writer writer) {
        writeImpl(objects, writer);
    }

    protected final ObjectMapper getMapper() {
        return mapper;
    }

    public final <X> Serializer<X> getSerializer(Class<X> klass) {
        return Presentation.getSerializer(klass, mimeType);
    }
}
