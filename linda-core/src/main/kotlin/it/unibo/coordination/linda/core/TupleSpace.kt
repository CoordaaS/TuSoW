package it.unibo.coordination.linda.core

import it.unibo.coordination.Promise

interface TupleSpace<T : Tuple, TT : Template, K, V> {

    val name: String

    fun read(template: TT): Promise<Match<T, TT, K, V>>

    @JvmDefault
    fun read(template: String): Promise<Match<T, TT, K, V>> =
            read(template.toTemplate())

    @JvmDefault
    fun readTuple(template: TT): Promise<T> {
        return read(template).thenApplyAsync { it.tuple.get() }
    }

    @JvmDefault
    fun readTuple(template: String): Promise<T> =
            readTuple(template.toTemplate())

    fun take(template: TT): Promise<Match<T, TT, K, V>>

    @JvmDefault
    fun take(template: String): Promise<Match<T, TT, K, V>> =
            take(template.toTemplate())

    @JvmDefault
    fun takeTuple(template: String): Promise<T> =
            takeTuple(template.toTemplate())

    @JvmDefault
    fun takeTuple(template: TT): Promise<T> {
        return take(template).thenApplyAsync { it.tuple.get() }
    }

    fun write(tuple: T): Promise<T>

    @JvmDefault
    fun write(tuple: String): Promise<T> =
            write(tuple.toTuple())

    fun get(): Promise<Collection<T>>

    fun getSize(): Promise<Int>

    fun String.toTuple(): T
    fun String.toTemplate(): TT
}