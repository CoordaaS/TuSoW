package it.unibo.coordination.utils;

import java.util.*;
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CollectionUtils {

    private static final Random RAND = new Random();

    @SafeVarargs
    public static <T> Set<T> setOf(T... items) {
        return Stream.of(items).collect(Collectors.toSet());
    }

    @SafeVarargs
    public static <T> List<T> listOf(T... items) {
        return Stream.of(items).collect(Collectors.toList());
    }

    public static <K, V> Map<K, V> mapOf(Map.Entry<K, V>... entries) {
        return Stream.of(entries).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static <K, V> Map<K, V> mapOf(K key, V value) {
        return mapOf(pairOf(key, value));
    }

    public static <K, V> Map<K, V> mapOf(K key1, V value1, K key2, V value2) {
        return mapOf(new Map.Entry[]{ pairOf(key1, value1), pairOf(key2, value2) });
    }

    public static <K, V> Map.Entry<K, V> pairOf(K key, V value) {
        return new AbstractMap.SimpleImmutableEntry<>(key, value);
    }

    public static <T> Iterator<T> randomIterator(List<T> list) {
        if (list.isEmpty()) {
            return list.iterator();
        } else {
            return new Iterator<T>() {

                private PrimitiveIterator.OfInt iterator = RAND.ints(0, list.size())
                        .distinct()
                        .limit(list.size())
                        .iterator();

                private int lastIndex;

                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public T next() {
                    lastIndex = iterator.next();
                    return list.get(lastIndex);
                }

                @Override
                public void remove() {
                    final int currentIndex = lastIndex;
                    iterator = mapIf(iterator, i -> i - 1, i -> i >= currentIndex);
                    list.remove(lastIndex);
                }
            };
        }
    }

    private static PrimitiveIterator.OfInt mapIf(PrimitiveIterator.OfInt iter, IntUnaryOperator op, IntPredicate cond) {
        return new PrimitiveIterator.OfInt() {

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public int nextInt() {
                final int next = iter.nextInt();
                if (cond.test(next)) {
                    return op.applyAsInt(next);
                } else {
                    return next;
                }
            }
        };
    }

}
