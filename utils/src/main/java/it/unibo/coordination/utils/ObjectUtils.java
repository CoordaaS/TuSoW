package it.unibo.coordination.utils;

import java.util.function.Predicate;

public class ObjectUtils {

    public static <X> X require(X object, Predicate<X> predicate) {
        if (!predicate.test(object)) {
            throw new IllegalArgumentException("" + object);
        }
        return object;
    }

    public static <X> void require(Condition condition) {
        if (!condition.test()) {
            throw new IllegalArgumentException();
        }
    }

    public static <X> X require(X object, Predicate<X> predicate, String message, Object... args) {
        if (!predicate.test(object)) {
            throw new IllegalArgumentException(String.format(message, args));
        }
        return object;
    }

    public static <X> void require(Condition condition, String message, Object... args) {
        if (!condition.test()) {
            throw new IllegalArgumentException(String.format(message, args));
        }
    }
}
