package it.unibo.coordination.linda.core

import it.unibo.coordination.Promise
import java.util.*

interface PredicativeTupleSpace<T : Tuple, TT : Template, K, V> : TupleSpace<T, TT, K, V> {
    fun tryTake(template: TT): Promise<Match<T, TT, K, V>>

    @JvmDefault
    fun tryTake(template: String): Promise<Match<T, TT, K, V>> =
            tryTake(template.toTemplate())

    @JvmDefault
    fun tryTakeTuple(template: TT): Promise<Optional<T>> {
        return tryTake(template).thenApplyAsync{ it.tuple }
    }

    @JvmDefault
    fun tryTakeTuple(template: String): Promise<Optional<T>> =
            tryTakeTuple(template.toTemplate())

    fun tryRead(template: TT): Promise<Match<T, TT, K, V>>

    @JvmDefault
    fun tryRead(template: String): Promise<Match<T, TT, K, V>> =
            tryRead(template.toTemplate())

    @JvmDefault
    fun tryReadTuple(template: TT): Promise<Optional<T>> {
        return tryRead(template).thenApplyAsync{ it.tuple }
    }

    @JvmDefault
    fun tryReadTuple(template: String): Promise<Optional<T>> =
            tryReadTuple(template.toTemplate())
}
