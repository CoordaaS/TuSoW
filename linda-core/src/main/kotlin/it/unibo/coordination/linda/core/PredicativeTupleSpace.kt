package it.unibo.coordination.linda.core

import it.unibo.coordination.Promise
import java.util.*

interface PredicativeTupleSpace<T : Tuple, TT : Template, K, V> : TupleSpace<T, TT, K, V> {
    fun tryTake(template: TT): Promise<Match<T, TT, K, V>>

    @JvmDefault
    fun tryTakeTuple(template: TT): Promise<Optional<T>> {
        return tryTake(template).thenApplyAsync{ it.tuple }
    }

    fun tryRead(template: TT): Promise<Match<T, TT, K, V>>

    @JvmDefault
    fun tryReadTuple(template: TT): Promise<Optional<T>> {
        return tryRead(template).thenApplyAsync{ it.tuple }
    }
}
