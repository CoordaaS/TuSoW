package it.unibo.coordination.linda.core;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public interface Match<T extends Tuple, TT extends Template, K, V> {

    Optional<T> getTuple();

    TT getTemplate();

    boolean isMatching();

    Optional<V> get(K key);

    Map<K, V> toMap();

    static <T extends Tuple, TT extends Template, K, V, M extends Match<T, TT, K, V>> boolean equals(M m1, M m2) {
        if (m1 == m2) return true;
        if (m1 == null || m2 == null) return false;
        return Objects.equals(m1.getTemplate(), m2.getTemplate())
                && Objects.equals(m1.getTuple(), m2.getTuple())
                && Objects.equals(m1.isMatching(), m2.isMatching());

    }

    static <T extends Tuple, TT extends Template, K, V, M extends Match<T, TT, K, V>> int hashCode(M match) {
        return Objects.hash(match.isMatching(), match.getTemplate(), match.getTuple());
    }
}
