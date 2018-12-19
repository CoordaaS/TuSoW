package it.unibo.coordination.tuples.objects;


import it.unibo.coordination.tuples.core.Tuple;

import java.util.Objects;

public final class ObjectTuple<T> implements Tuple {
    private final T object;

    private ObjectTuple(T object) {
        this.object = object;
    }

    public static <X> ObjectTuple<X> of(X object) {
        return new ObjectTuple<>(Objects.requireNonNull(object));
    }

    public T get() {
        return object;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjectTuple<?> that = (ObjectTuple<?>) o;
        return Objects.equals(object, that.object);
    }

    @Override
    public int hashCode() {
        return Objects.hash(object);
    }

    @Override
    public String toString() {
        return "ObjectTuple{" + object + '}';
    }
}
