package it.unibo.coordination.linda.logic

import alice.tuprolog.Struct
import alice.tuprolog.Term

internal class LogicTupleImpl(term: Term) : LogicTuple {

    private val term: Struct

    override val value: Term
        get() = asTerm().getArg(0)

    init {
        if (term is Struct && LogicTuple.pattern.match(term)) {
            this.term = term
        } else {
            this.term = LogicTuple.getPattern(term)
        }
    }

    override fun toString(): String {
        return asTerm().toString()
    }

    override fun equals(other: Any?): Boolean {
        return other is LogicTuple && LogicTuple.equals(this, other as LogicTuple?)
    }

    override fun hashCode(): Int {
        return LogicTuple.hashCode(this)
    }

    override fun asTerm(): Struct {
        return term
    }
}