package it.unibo.coordination.linda.core.traits

import it.unibo.coordination.Promise
import it.unibo.coordination.linda.core.Match
import it.unibo.coordination.linda.core.Template
import it.unibo.coordination.linda.core.Tuple
import java.util.*

interface PredicativePrimitives<T : Tuple<T>, TT : Template<T>, K, V, M : Match<T, TT, K, V>>
    : TupleTemplateParsing<T, TT> {

    fun tryTake(template: TT): Promise<M>

    @JvmDefault
    fun tryTake(template: String): Promise<M> =
            tryTake(template.toTemplate())

    @JvmDefault
    fun tryTakeTuple(template: TT): Promise<Optional<T>> {
        return tryTake(template).thenApplyAsync { it.tuple }
    }

    @JvmDefault
    fun tryTakeTuple(template: String): Promise<Optional<T>> =
            tryTakeTuple(template.toTemplate())

    fun tryRead(template: TT): Promise<M>

    @JvmDefault
    fun tryRead(template: String): Promise<M> =
            tryRead(template.toTemplate())

    @JvmDefault
    fun tryReadTuple(template: TT): Promise<Optional<T>> {
        return tryRead(template).thenApplyAsync { it.tuple }
    }

    @JvmDefault
    fun tryReadTuple(template: String): Promise<Optional<T>> =
            tryReadTuple(template.toTemplate())
}