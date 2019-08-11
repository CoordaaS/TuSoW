package it.unibo.coordination.utils;

import java.util.*;
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;

public class CollectionUtils {

    private static final Random RAND = new Random();

    public static <X> Collection<X> collectionOf(X first, X... others) {
        return ListUtils.listOf(first, others);
    }

    public static <X> Collection<X> collectionOf(X first, X second, X... others) {
        return ListUtils.listOf(first, second, others);
    }

    public static <X> Collection<X> requireNonEmpty(Collection<X> collection) {
        return ObjectUtils.require(collection, it -> it.size() > 0, "Collection %s cannot be empty", collection);
    }

    public static <X> Collection<X> requireSizeInRange(Collection<X> collection, int minInclusive, int maxExclusive) {
        return requireSizeInRangeInclusive(collection, minInclusive, maxExclusive - 1);
    }

    public static <X> Collection<X> requireSizeAtLeast(Collection<X> collection, int minInclusive) {
        return ObjectUtils.require(collection, it -> it.size() >= minInclusive, "Collection %s size cannot be lower than %d", collection, minInclusive);
    }

    public static <X> Collection<X> requireSizeInRangeInclusive(Collection<X> collection, int minInclusive, int maxInclusive) {
        try {
            NumberUtils.requireInRangeInclusive(collection.size(), minInclusive, maxInclusive);
            return collection;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format(
                    "Collection %s size must be between %d and %d",
                    collection, minInclusive, maxInclusive
            ));
        }
    }

    public static <T> Iterator<T> randomIterator(List<T> list) {
        if (list.isEmpty()) {
            return list.iterator();
        } else {
            return new Iterator<>() {

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
                    final var currentIndex = lastIndex;
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
