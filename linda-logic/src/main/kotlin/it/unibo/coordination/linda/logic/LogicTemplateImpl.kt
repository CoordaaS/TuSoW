package it.unibo.coordination.linda.logic

import alice.tuprolog.Prolog
import alice.tuprolog.Struct
import alice.tuprolog.Term
import it.unibo.coordination.prologx.PrologUtils

internal class LogicTemplateImpl(term: Term) : LogicTemplate {
    private val term: Struct

    override val template: Term
        get() = term.getArg(0)

    init {
        if (term is Struct && LogicTemplate.pattern.match(term)) {
            this.term = term
        } else {
            this.term = LogicTemplate.getPattern(term)
        }
    }

    override fun matchWith(tuple: LogicTuple): LogicMatch {
        val si = ENGINE.solve(PrologUtils.unificationTerm(template, tuple.value))
        return LogicMatchImpl(this, si, tuple)
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

    companion object {

        private val ENGINE = Prolog()
    }

}
