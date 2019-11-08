package it.unibo.coordination.linda.logic

import alice.tuprolog.Struct
import alice.tuprolog.Term
import alice.tuprolog.Var
import it.unibo.coordination.linda.core.Template
import java.util.*

interface LogicTemplate : Template<LogicTuple> {

    val template: Term

    override fun matchWith(tuple: LogicTuple): LogicMatch

    fun asTerm(): Struct

    @JvmDefault
    fun toTuple(): LogicTuple {
        return LogicTuple.of(template)
    }

    companion object {

        @JvmStatic
        fun of(template: String): LogicTemplate {
            return of(Term.createTerm(Objects.requireNonNull(template)))
        }

        @JvmStatic
        fun of(term: Term): LogicTemplate {
            return LogicTemplateImpl(term)
        }

        @JvmStatic
        val pattern: Struct
            get() = Struct.of("template", Var.of("T"))

        @JvmStatic
        fun getPattern(term: Term): Struct {
            return Struct.of("template", Objects.requireNonNull(term))
        }

        @JvmStatic
        fun equals(t1: LogicTemplate?, t2: LogicTemplate?): Boolean {
            if (t1 === t2) return true
            return if (t1 == null || t2 == null) false else t1.asTerm() == t2.asTerm()
        }

        @JvmStatic
        fun hashCode(t: LogicTemplate): Int {
            return Objects.hashCode(t.asTerm().toString())
        }

        @JvmStatic
        fun toString(template: LogicTemplate): String {
            return template.template.toString()
        }
    }
}
