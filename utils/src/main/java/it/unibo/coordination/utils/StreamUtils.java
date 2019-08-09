package it.unibo.coordination.utils;

import java.util.stream.Stream;

public class StreamUtils {
    public static <X> Stream<X> streamOf(X first, X... others) {
        return Stream.concat(
                Stream.of(first),
                Stream.of(others)
        );
    }
}
