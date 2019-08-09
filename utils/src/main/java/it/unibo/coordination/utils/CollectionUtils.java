package it.unibo.coordination.utils;

import java.util.*;
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;

public class CollectionUtils {

    private static final Random RAND = new Random();

    public static <X> Collection<X> collectionOf(X first, X... others) {
        return ListUtils.listOf(first, others);
    }

    public static <X> void requireNonEmpty(Collection<X> collection) {
        ObjectUtils.require(collection, it -> it.size() > 0, "Collection %s cannot be empty", collection);
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
