package it.unibo.coordination.linda.core;

public interface Tuple {
    default boolean matches(final Template template) {
        return template.matches(this);
    }

    Object getValue();
}
