package it.unibo.coordination.utils;

import java.util.List;
import java.util.stream.Collectors;

public class ListUtils {
    public static <X> List<X> listOf(X first, X... others) {
        return StreamUtils.streamOf(first, others).collect(Collectors.toList());
    }

    public static <X> List<X> listOf(X first, X second, X... others) {
        return StreamUtils.streamOf(first, second, others).collect(Collectors.toList());
    }
}
