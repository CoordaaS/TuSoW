package it.unibo.coordination.linda.logic

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.unify.Unificator.Companion.matches
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith

internal class LogicTemplateImpl(term: Term) : LogicTemplate {
    private val term: Struct

    override val template: Term
        get() = term[0]

    init {
        if (term is Struct && LogicTemplate.pattern.matches(term)) {
            this.term = term
        } else {
            this.term = LogicTemplate.getPattern(term)
        }
    }

    override fun matchWith(tuple: LogicTuple): LogicMatch {
        return LogicMatchImpl(this, tuple)
    }

    override fun equals(other: Any?): Boolean {
        return other is LogicTemplate && LogicTemplate.equals(this, other as LogicTemplate?)
    }

    override fun hashCode(): Int {
        return LogicTemplate.hashCode(this)
    }

    override fun toString(): String {
        return term.toString()
    }

    override fun asTerm(): Struct {
        return term
    }
}
