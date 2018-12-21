package it.unibo.coordination.tusow.presentation;

public interface Representation {
    Object toObject();

    String toJSONString();

    String toXMLString();

    String toYAMLString();

    String toMIMETypeString(String mimeType);
}
