package it.unibo.coordination.utils;

import java.util.function.IntPredicate;

public class NumberUtils {
    public static int require(int object, IntPredicate predicate) {
        if (!predicate.test(object)) {
            throw new IllegalArgumentException("" + object);
        }
        return object;
    }

    public static int require(int object, IntPredicate predicate, String message, Object... args) {
        if (!predicate.test(object)) {
            throw new IllegalArgumentException(String.format(message, args));
        }
        return object;
    }

    public static int requireInRange(int x, int minInclusive, int maxInclusive) {
        return require(x, y -> y >= minInclusive && y <= maxInclusive, "%d must be between %d and %d", x, minInclusive, maxInclusive);
    }
}
