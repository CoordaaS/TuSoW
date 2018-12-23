package it.unibo.coordination.linda.core;

import java.util.function.Predicate;

public interface Template extends Predicate<Tuple> {

    default boolean matches(Tuple tuple) {
        return matchWith(tuple).isSuccess();
    }

    Match<? extends Tuple, ? extends Template, ?, ?> matchWith(Tuple tuple);

    @Override
    default boolean test(Tuple tuple) {
        return matches(tuple);
    }
}
