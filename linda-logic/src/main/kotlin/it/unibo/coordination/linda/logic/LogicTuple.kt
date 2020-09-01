package it.unibo.coordination.linda.logic

import it.unibo.coordination.linda.core.Tuple
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.parsing.parse
import java.util.*

interface LogicTuple : Tuple<LogicTuple>, Comparable<LogicTuple> {

    override val value: Term

    fun asTerm(): Struct

    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int

    @JvmDefault
    override fun compareTo(other: LogicTuple): Int = value.toString().compareTo(other.value.toString())

    companion object {

        @JvmStatic
        fun of(tuple: String): LogicTuple = of(Term.parse(tuple))

        @JvmStatic
        fun of(term: Term): LogicTuple = LogicTupleImpl(term)

        @JvmStatic
        val pattern: Struct = Struct.of("tuple", Var.of("T"))

        @JvmStatic
        fun getPattern(term: Term): Struct = Struct.of("tuple", term)

        @JvmStatic
        fun equals(t1: LogicTuple?, t2: LogicTuple?): Boolean =
                if (t1 === t2) true
                else if (t1 == null || t2 == null) false
                else t1.value == t2.value

        @JvmStatic
        fun hashCode(t: LogicTuple): Int = Objects.hashCode(t.asTerm())
    }
}