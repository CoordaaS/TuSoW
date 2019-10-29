package it.unibo.coordination.linda.core

import it.unibo.coordination.Promise
import java.util.*

interface NegatedTupleSpace<T : Tuple, TT : Template, K, V> : TupleSpace<T, TT, K, V> {

    fun absent(template: TT): Promise<Match<T, TT, K, V>>

    @JvmDefault
    fun absentTemplate(template: TT): Promise<TT> {
        return absent(template).thenApplyAsync { it.template }
    }

    fun tryAbsent(template: TT): Promise<Match<T, TT, K, V>>

    @JvmDefault
    fun tryAbsentTuple(template: TT): Promise<Optional<T>> {
        return tryAbsent(template).thenApplyAsync { it.tuple }
    }
}