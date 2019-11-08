package it.unibo.coordination.linda.logic

import alice.tuprolog.Struct
import alice.tuprolog.Term
import alice.tuprolog.Var
import it.unibo.coordination.linda.core.Match
import it.unibo.coordination.prologx.PrologUtils
import java.util.*

interface LogicMatch : Match<LogicTuple, LogicTemplate, String, Term> {

    @JvmDefault
    fun asTerm(): Struct {
        return Struct.of("match",
                Struct.of("success", if (isMatching) Struct.atom("yes") else Struct.atom("no")),
                template.asTerm(),
                tuple.map { it.asTerm() }.orElse(Struct.atom("empty")),
                Struct.list(
                        toMap().entries.stream()
                                .map { kv -> PrologUtils.unificationTerm(kv.key, kv.value) }
                )
        )
    }

    companion object {

        @JvmStatic
        fun failed(template: LogicTemplate): LogicMatch {
            return LogicMatchImpl(template, null, null)
        }

        @JvmStatic
        fun wrap(match: Match<LogicTuple, LogicTemplate, String, Term>): LogicMatch {
            return if (match is LogicMatch) {
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
                        return LogicMatch.toString(this)
                    }

                    override fun equals(other: Any?): Boolean {
                        return other is LogicMatch && Match.equals(this, other as LogicMatch?)
                    }

                    override fun hashCode(): Int {
                        return Match.hashCode(this)
                    }

                }
            }
        }

        @JvmStatic
        val pattern: Struct
            get() = Struct.of("match",
                    Struct.of("success", Var.of("Success")),
                    LogicTemplate.pattern,
                    Var.of("Tuple"),
                    Var.of("Mappings")
            )

        @JvmStatic
        fun toString(match: LogicMatch): String {
            return match.asTerm().toString()
        }
    }
}
