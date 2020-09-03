package it.unibo.coordination.linda.core.traits

import it.unibo.coordination.Promise
import it.unibo.coordination.linda.core.Match
import it.unibo.coordination.linda.core.NegatedTupleSpace
import it.unibo.coordination.linda.core.Template
import it.unibo.coordination.linda.core.Tuple
import java.util.*

interface NegatedPredicativePrimitives<T : Tuple<T>, TT : Template<T>, K, V, M : Match<T, TT, K, V>>
    : NegatedPrimitives<T, TT, K, V, M>, PredicativePrimitives<T, TT, K, V, M> {

    fun tryAbsent(template: TT): Promise<M>

    @JvmDefault
    fun tryAbsent(template: String): Promise<M> =
            tryAbsent(template.toTemplate())

    @JvmDefault
    fun tryAbsentTuple(template: TT): Promise<Optional<T>> {
        return tryAbsent(template).thenApplyAsync { it.tuple }
    }

    @JvmDefault
    fun tryAbsentTuple(template: String): Promise<Optional<T>> =
            tryAbsentTuple(template.toTemplate())
}