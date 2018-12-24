package it.unibo.coordination.linda.core;

import java.util.Map;
import java.util.Optional;

public interface Match<T extends Tuple, TT extends Template, K, V> {

    Optional<T> getTuple();

    TT getTemplate();

    boolean isMatching();

    Optional<V> get(K key);

    Map<K, V> toMap();
}
