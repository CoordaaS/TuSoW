package it.unibo.coordination.tuples.core;

public interface Tuple {
    default boolean matches(final Template template) {
        return template.matches(this);
    }
}
