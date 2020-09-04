package it.unibo.cooordination.respect.logic

import it.unibo.coordination.linda.core.Match
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith
import java.util.*

class LogicSpecificationMatch private constructor(
        override val template: LogicSpecificationTemplate,
        override val tuple: Optional<LogicSpecificationTuple> = Optional.empty()
) : Match<LogicSpecificationTuple, LogicSpecificationTemplate, String, Term> {

    companion object {
        @JvmStatic
        fun of(template: LogicSpecificationTemplate, tuple: LogicSpecificationTuple): LogicSpecificationMatch {
            return LogicSpecificationMatch(template, Optional.of(tuple))
        }

        @JvmStatic
        fun failed(template: LogicSpecificationTemplate): LogicSpecificationMatch {
            return LogicSpecificationMatch(template)
        }
    }

    private val substitution: Substitution by lazy {
        tuple.map {
            template.template mguWith it.value
        }.orElseGet(Substitution.Companion::failed)
    }

    override val isMatching: Boolean
        get() = substitution.isSuccess

    override fun get(key: String): Optional<Term> {
        return Optional.ofNullable(toMap()[key])
    }

    override fun toMap(): Map<String, Term> =
            substitution.mapKeys { (k, _) -> k.name }
}