package it.unibo.coordination.tuples.core;

import java.util.Optional;
import java.util.function.Predicate;

public interface Template extends Predicate<Tuple> {

    default boolean matches(Tuple tuple) {
        return matchWith(tuple).isSuccess();
    }

    Match matchWith(Tuple tuple);

    @Override
    default boolean test(Tuple tuple) {
        return matches(tuple);
    }

    interface Match {

        boolean isSuccess();

        <X> Optional<X> get(Object key);

    }
}
