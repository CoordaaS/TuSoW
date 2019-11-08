package it.unibo.coordination.linda.core

import java.util.function.Predicate

interface Template<T : Tuple<T>> : Predicate<T> {

    @JvmDefault
    fun matches(tuple: T): Boolean {
        return matchWith(tuple).isMatching
    }

    fun matchWith(tuple: T): Match<T, out Template<T>, *, *>

    @JvmDefault
    override fun test(tuple: T): Boolean {
        return matches(tuple)
    }
}