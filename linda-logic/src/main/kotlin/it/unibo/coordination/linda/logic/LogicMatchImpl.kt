package it.unibo.coordination.linda.logic

import it.unibo.tuprolog.unify.Unificator.Companion.mguWith
import it.unibo.coordination.linda.core.Match
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import java.util.*

internal class LogicMatchImpl(override val template: LogicTemplate, tuple: LogicTuple?) : LogicMatch {

    override val tuple: Optional<LogicTuple> = Optional.ofNullable(tuple)

    private val cache: Substitution by lazy {
        when (tuple) {
            null -> Substitution.empty()
            else -> template.template mguWith tuple.value
        }
    }

    override val isMatching: Boolean
        get() = cache is Substitution.Unifier

    override fun get(key: String): Optional<Term> {
        return Optional.ofNullable(toMap()[key])
    }


    override fun toMap(): Map<String, Term> = cache.mapKeys { (k, _) -> k.name }

    override fun toString(): String {
        return LogicMatch.toString(this)
    }

    override fun equals(other: Any?): Boolean {
        return other is LogicMatch && Match.equals(this, other as LogicMatch?)
    }

    override fun hashCode(): Int {
        return Match.hashCode(this)
    }
}
