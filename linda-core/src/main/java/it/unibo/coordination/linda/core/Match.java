package it.unibo.coordination.linda.core;

import java.util.Optional;

public interface Match<T extends Tuple, TT extends Template, K, V> {

    Optional<T> getTuple();

    TT getTemplate();

    boolean isSuccess();

    Optional<V> get(K key);

}
