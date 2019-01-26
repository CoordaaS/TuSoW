package it.unibo.coordination.linda.presentation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.Objects;

public class SimpleDeserializer<T> implements Deserializer<T> {

    private final Class<T> clazz;
    private final MIMETypes mimeType;
    private final ObjectMapper mapper;

    public SimpleDeserializer(Class<T> clazz, MIMETypes mimeType, ObjectMapper mapper) {
        this.clazz = Objects.requireNonNull(clazz);
        this.mimeType = Objects.requireNonNull(mimeType);
        this.mapper = Objects.requireNonNull(mapper);
    }

    @Override
    public Class<T> getSupportedType() {
        return clazz;
    }

    @Override
    public MIMETypes getSupportedMIMEType() {
        return mimeType;
    }

    @Override
    public T fromDynamicObject(Object dynamicObject) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T fromString(String string) {
        return read(new StringReader(string));
    }

    @Override
    public List<T> listFromString(String string) {
        return readList(new StringReader(string));
    }

    @Override
    public T read(Reader reader) {
        return readImpl(reader, getSupportedType());
    }

    protected final <X> X readImpl(Reader reader, Class<X> clazz) {
        try {
            return mapper.readValue(reader, clazz);
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot read " + mimeType, e);
        }
    }

    protected final <X> X readImpl(Reader reader, TypeReference<X> clazz) {
        try {
            return mapper.readValue(reader, clazz);
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot read " + mimeType, e);
        }
    }

    @Override
    public List<T> readList(Reader reader) {
        return readImpl(reader, new TypeReference<List<T>>() {});
    }

    public final <X> Deserializer<X> getUnmarshaller(Class<X> klass) {
        return Presentation.getDeserializer(klass, getSupportedMIMEType());
    }
}
