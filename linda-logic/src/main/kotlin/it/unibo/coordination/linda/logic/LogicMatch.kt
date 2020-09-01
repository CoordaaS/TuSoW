package it.unibo.coordination.linda.logic

import it.unibo.coordination.linda.core.Match
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import java.util.*

interface LogicMatch : Match<LogicTuple, LogicTemplate, String, Term> {

    @JvmDefault
    fun asTerm(): Struct = Scope.empty {
        structOf("match",
                structOf("success", if (isMatching) atomOf("yes") else atomOf("no")),
                template.asTerm(),
                tuple.map { it.asTerm() }.orElse(atomOf("empty")),
                listOf(toMap().entries.map { (k, v) -> structOf("=", atomOf(k), v) })
        )
    }

    companion object {

        @JvmStatic
        fun failed(template: LogicTemplate): LogicMatch = LogicMatchImpl(template, null)

        @JvmStatic
        fun wrap(match: Match<LogicTuple, LogicTemplate, String, Term>): LogicMatch =
                if (match is LogicMatch) {
                    match
                } else {
                    object : LogicMatch {
                        override val tuple: Optional<LogicTuple>
                            get() = match.tuple

                        override val template: LogicTemplate
                            get() = match.template

                        override val isMatching: Boolean
                            get() = match.isMatching

                        override fun get(key: String): Optional<Term> {
                            return match[key]
                        }

                        override fun toMap(): Map<String, Term> {
                            return match.toMap()
                        }

                        override fun toString(): String {
                            return toString(this)
                        }

                        override fun equals(other: Any?): Boolean {
                            return other is LogicMatch && Match.equals(this, other as LogicMatch?)
                        }

                        override fun hashCode(): Int {
                            return Match.hashCode(this)
                        }

                    }
                }

        @JvmStatic
        val pattern: Struct
            get() = Scope.empty {
                structOf(
                        "match",
                        structOf("success", varOf("Success")),
                        LogicTemplate.pattern,
                        varOf("Tuple"),
                        varOf("Mappings")
                )
            }

        @JvmStatic
        fun toString(match: LogicMatch): String = match.asTerm().toString()
    }
}
