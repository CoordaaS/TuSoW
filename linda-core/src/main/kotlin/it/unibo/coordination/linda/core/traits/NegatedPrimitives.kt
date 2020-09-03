package it.unibo.coordination.linda.core.traits

import it.unibo.coordination.Promise
import it.unibo.coordination.linda.core.Match
import it.unibo.coordination.linda.core.Template
import it.unibo.coordination.linda.core.Tuple

interface NegatedPrimitives<T : Tuple<T>, TT : Template<T>, K, V, M : Match<T, TT, K, V>>
    : TupleTemplateParsing<T, TT> {

    fun absent(template: TT): Promise<M>

    @JvmDefault
    fun absent(template: String): Promise<M> =
            absent(template.toTemplate())

    @JvmDefault
    fun absentTemplate(template: TT): Promise<TT> {
        return absent(template).thenApplyAsync { it.template }
    }

    @JvmDefault
    fun absentTemplate(template: String): Promise<TT> =
            absentTemplate(template.toTemplate())

}