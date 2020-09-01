package it.unibo.coordination.linda.logic

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.unify.Unificator.Companion.matches

internal class LogicTemplateImpl(term: Term) : LogicTemplate {

    private val term: Struct by lazy {
        if (term is Struct && LogicTemplate.pattern.matches(term)) {
            term
        } else {
            LogicTemplate.getPattern(term)
        }
    }

    override val template: Term
        get() = term[0]

    override fun matchWith(tuple: LogicTuple): LogicMatch = LogicMatchImpl(this, tuple)

    override fun equals(other: Any?): Boolean =
            other is LogicTemplate && LogicTemplate.equals(this, other as LogicTemplate?)

    override fun hashCode(): Int = LogicTemplate.hashCode(this)

    override fun toString(): String = term.toString()

    override fun asTerm(): Struct = term
}
