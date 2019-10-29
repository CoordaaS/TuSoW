package it.unibo.coordination.linda.core

import it.unibo.coordination.linda.Promise
import org.apache.commons.collections4.MultiSet

interface TupleSpace<T : Tuple, TT : Template, K, V> {

    val name: String

    fun read(template: TT): Promise<Match<T, TT, K, V>>

    @JvmDefault
    fun readTuple(template: TT): Promise<T> {
        return read(template).thenApplyAsync { it.tuple.get() }
    }

    fun take(template: TT): Promise<Match<T, TT, K, V>>

    @JvmDefault
    fun takeTuple(template: TT): Promise<T> {
        return take(template).thenApplyAsync { match -> match.tuple.get() }
    }

    fun write(tuple: T): Promise<T>

    fun get(): Promise<MultiSet<T>>

    fun getSize(): Promise<Int>
}