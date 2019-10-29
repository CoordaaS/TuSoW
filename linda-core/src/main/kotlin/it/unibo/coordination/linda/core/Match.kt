package it.unibo.coordination.linda.core

import java.util.*

interface Match<T : Tuple, TT : Template, K, V> {

    val tuple: Optional<T>

    val template: TT

    val isMatching: Boolean

    operator fun get(key: K): Optional<V>

    fun toMap(): Map<K, V>

    companion object {

        @JvmStatic
        fun <T : Tuple, TT : Template, K, V, M : Match<T, TT, K, V>> equals(m1: M?, m2: M?): Boolean {
            return when {
                m1 === m2 -> true
                m1 == null || m2 == null -> false
                else -> m1.template == m2.template
                        && m1.tuple == m2.tuple
                        && m1.isMatching == m2.isMatching
            }

        }

        @JvmStatic
        fun <T : Tuple, TT : Template, K, V, M : Match<T, TT, K, V>> hashCode(match: M): Int {
            return Objects.hash(match.isMatching, match.template, match.tuple)
        }
    }
}