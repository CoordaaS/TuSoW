package it.unibo.coordination.linda.presentation;

import java.util.stream.Stream;

public enum MIMETypes {

    APPLICATION_JSON("application", "json"),

    APPLICATION_YAML("application", "yaml"),

    APPLICATION_XML("application", "xml"),

    APPLICATION_PROLOG("application", "prolog"),

    TEXT_HTML("text", "html"),

    TEXT_PLAIN("text", "plain"),

    ANY("*", "*"),

    APPLICATION_ANY("application", "*");

    private final String type, subtype;

    MIMETypes(String type, String subtype) {
        this.type = type;
        this.subtype = subtype;
    }

    public String getType() {
        return type;
    }

    public String getSubtype() {
        return subtype;
    }

    @Override
    public String toString() {
        return type + "/" + subtype;
    }

    public boolean matches(String other) {
        return match(this, other);
    }

    public static boolean match(MIMETypes mime, String other) {
        if (other == null || !other.contains("/")) return false;

        final String[] parts = other.split("/");

        if (parts.length != 2) return false;

        return match(mime, parts[0], parts[1]);
    }

    private static boolean match(MIMETypes mime, String type, String subtype) {
        return (Stream.of(mime.getType(), type).anyMatch("*"::equals)
                || mime.getType().equalsIgnoreCase(type))
                && (Stream.of(mime.getSubtype(), subtype).anyMatch("*"::equals)
                        || mime.getSubtype().equalsIgnoreCase(subtype));
    }

    public static MIMETypes parse(String string) {
        return Stream.of(values()).filter(it -> it.toString().equalsIgnoreCase(string)).findAny().orElseGet(() -> {
           throw new IllegalArgumentException(string);
        });
    }
}
