package it.unibo.coordination.utils;

import java.util.function.Predicate;

public class ObjectUtils {

    public static <X> void require(X object, Predicate<X> predicate) {
        if (!predicate.test(object)) {
            throw new IllegalArgumentException("" + object);
        }
    }

    public static <X> void require(Condition condition) {
        if (!condition.test()) {
            throw new IllegalArgumentException();
        }
    }

    public static <X> void require(X object, Predicate<X> predicate, String message, Object... args) {
        if (!predicate.test(object)) {
            throw new IllegalArgumentException(String.format(message, args));
        }
    }

    public static <X> void require(Condition condition, String message, Object... args) {
        if (!condition.test()) {
            throw new IllegalArgumentException(String.format(message, args));
        }
    }
}
