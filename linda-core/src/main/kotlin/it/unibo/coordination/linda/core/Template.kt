package it.unibo.coordination.linda.core

import java.util.function.Predicate

interface Template : Predicate<Tuple> {

    @JvmDefault
    fun matches(tuple: Tuple): Boolean {
        return matchWith<Tuple, Template, Any, Any>(tuple).isMatching
    }

    fun <T : Tuple, TT : Template, K, V> matchWith(tuple: T): Match<T, TT, K, V>

    @JvmDefault
    override fun test(tuple: Tuple): Boolean {
        return matches(tuple)
    }
}