package it.unibo.coordination.linda.logic

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.unify.Unificator.Companion.matches


internal class LogicTupleImpl(term: Term) : LogicTuple {

    private val term: Struct by lazy {
        if (term is Struct && LogicTuple.pattern.matches(term)) {
            term
        } else {
            LogicTuple.getPattern(term)
        }
    }

    override val value: Term
        get() = asTerm()[0]

    override fun toString(): String = asTerm().toString()

    override fun equals(other: Any?): Boolean = other is LogicTuple && LogicTuple.equals(this, other as LogicTuple?)

    override fun hashCode(): Int = LogicTuple.hashCode(this)

    override fun asTerm(): Struct = term
}