package it.unibo.coordination.utils;

import java.util.stream.Stream;

public class StreamUtils {
    public static <X> Stream<X> streamOf(X... items) {
        return Stream.of(items);
    }

    public static <X> Stream<X> streamOf(X first, X... others) {
        return Stream.concat(
                Stream.of(first),
                Stream.of(others)
        );
    }

    public static <X> Stream<X> streamOf(X first, X second, X... others) {
        return Stream.concat(
                Stream.of(first, second),
                Stream.of(others)
        );
    }
}
