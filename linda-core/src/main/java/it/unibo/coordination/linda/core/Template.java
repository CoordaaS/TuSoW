package it.unibo.coordination.linda.core;

import java.util.function.Predicate;

public interface Template extends Predicate<Tuple> {

    default boolean matches(Tuple tuple) {
        return matchWith(tuple).isMatching();
    }

    <T extends Tuple, TT extends Template, K, V> Match<T, TT, K, V> matchWith(T tuple);

    @Override
    default boolean test(Tuple tuple) {
        return matches(tuple);
    }
}
