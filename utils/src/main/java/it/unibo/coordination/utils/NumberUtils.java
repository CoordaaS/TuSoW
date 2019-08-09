package it.unibo.coordination.utils;

import java.util.function.IntPredicate;

public class NumberUtils {
    public static void require(int object, IntPredicate predicate) {
        if (!predicate.test(object)) {
            throw new IllegalArgumentException("" + object);
        }
    }

    public static void require(int object, IntPredicate predicate, String message, Object... args) {
        if (!predicate.test(object)) {
            throw new IllegalArgumentException(String.format(message, args));
        }
    }

    public static void requireInRange(int x, int minInclusive, int maxInclusive) {
        require(x, y -> y >= minInclusive && y <= maxInclusive, "%d must be between %d and %d", x, minInclusive, maxInclusive);
    }
}
