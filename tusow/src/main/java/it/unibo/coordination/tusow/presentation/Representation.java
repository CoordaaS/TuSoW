package it.unibo.coordination.tusow.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.StringWriter;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static it.unibo.coordination.tusow.presentation.MIMETypes.*;

public abstract class Representation {

    private static final ObjectMapper xmlMapper = new XmlMapper();
    private static final ObjectMapper jsonMapper = new ObjectMapper();
    private static final ObjectMapper yamlMapper = new YAMLMapper();

    public String toXMLString() {
        final StringWriter writer = new StringWriter();
        try {
            xmlMapper.writeValue(writer, this);
            return writer.toString();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public String toJSONString() {
        final StringWriter writer = new StringWriter();
        try {
            jsonMapper.writeValue(writer, this);
            return writer.toString();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public String toYAMLString() {
        final StringWriter writer = new StringWriter();
        try {
            yamlMapper.writeValue(writer, this);
            return writer.toString();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public String toMIMETypeString(String mimeType){
        if (mimeType.equals(APPLICATION_JSON)) {
            return toJSONString();
        } else if (mimeType.equals(APPLICATION_XML)) {
            return toXMLString();
        }  else if (mimeType.equals(APPLICATION_YAML)) {
            return toYAMLString();
        } else {
            throw new IllegalArgumentException(String.format("Cannot convert %s into '%s'", this, mimeType));
        }
    }


    protected static <X, Y> Y ifNonNull(X input, Function<? super X, ? extends Y> map) {
        if (input != null) {
            return map.apply(input);
        }
        return null;
    }

    protected static <T> boolean assignIfNonNull(Supplier<? extends T> getter, Consumer<? super T> setter) {
        final T value = getter.get();
        if (value != null) {
            setter.accept(value);
            return true;
        }
        return false;
    }

    protected static <R extends Representation> R fromJSON(String representation, Class<R> type) throws IOException {
        return jsonMapper.readValue(representation, type);
    }

    protected static <R extends Representation> R fromYAML(String representation, Class<R> type) throws IOException {
        return yamlMapper.readValue(representation, type);
    }

    protected static <R extends Representation> R fromXML(String representation, Class<R> type) throws IOException {
        return xmlMapper.readValue(representation, type);
    }

    protected static <R extends Representation> R parse(String mimeType, String representation, Class<R> type) throws IOException {
        if (APPLICATION_JSON.equals(mimeType) || APPLICATION_ANY.equals(mimeType) || ANY.equals(mimeType)) {
            return fromJSON(representation, type);
        } else if (APPLICATION_XML.equals(mimeType)) {
            return fromXML(representation, type);
        } else if (APPLICATION_YAML.equals(mimeType)) {
            return fromYAML(representation, type);
        } else {
            throw new IllegalArgumentException(String.format("Cannot parse \'%s\' as '%s'", representation, mimeType));
        }
    }

    static {
        final JavaTimeModule timeModule = new JavaTimeModule();
        jsonMapper.registerModule(timeModule);
        xmlMapper.registerModule(timeModule);
        yamlMapper.registerModule(timeModule);
    }
}
