package it.unibo.coordination.linda.core

import it.unibo.coordination.Promise
import java.util.*

interface NegatedTupleSpace<T : Tuple, TT : Template, K, V> : LindaTupleSpace<T, TT, K, V> {

    fun absent(template: TT): Promise<Match<T, TT, K, V>>

    @JvmDefault
    fun absent(template: String): Promise<Match<T, TT, K, V>> =
            absent(template.toTemplate())

    @JvmDefault
    fun absentTemplate(template: TT): Promise<TT> {
        return absent(template).thenApplyAsync { it.template }
    }

    @JvmDefault
    fun absentTemplate(template: String): Promise<TT> =
            absentTemplate(template.toTemplate())

    fun tryAbsent(template: TT): Promise<Match<T, TT, K, V>>

    @JvmDefault
    fun tryAbsent(template: String): Promise<Match<T, TT, K, V>> =
            tryAbsent(template.toTemplate())

    @JvmDefault
    fun tryAbsentTuple(template: TT): Promise<Optional<T>> {
        return tryAbsent(template).thenApplyAsync { it.tuple }
    }

    @JvmDefault
    fun tryAbsentTuple(template: String): Promise<Optional<T>> =
            tryAbsentTuple(template.toTemplate())
}