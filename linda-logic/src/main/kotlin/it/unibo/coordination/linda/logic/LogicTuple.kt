package it.unibo.coordination.linda.logic

import alice.tuprolog.Struct
import alice.tuprolog.Term
import alice.tuprolog.Var
import it.unibo.coordination.linda.core.Tuple
import java.util.*

interface LogicTuple : Tuple<LogicTuple>, Comparable<LogicTuple> {

    override val value: Term

    fun asTerm(): Struct

    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int

    @JvmDefault
    override fun compareTo(other: LogicTuple): Int {
        return value.toString().compareTo(other.value.toString())
    }

    companion object {

        @JvmStatic
        fun of(tuple: String): LogicTuple {
            return LogicTuple.of(Term.createTerm(Objects.requireNonNull(tuple)))
        }

        @JvmStatic
        fun of(term: Term): LogicTuple {
            return LogicTupleImpl(term)
        }

        @JvmStatic
        val pattern: Struct
            get() = Struct.of("tuple", Var.of("T"))

        @JvmStatic
        fun getPattern(term: Term): Struct {
            return Struct.of("tuple", Objects.requireNonNull(term))
        }

        @JvmStatic
        fun equals(t1: LogicTuple?, t2: LogicTuple?): Boolean {
            if (t1 === t2) return true
            return if (t1 == null || t2 == null) false else t1.value == t2.value
        }

        @JvmStatic
        fun hashCode(t: LogicTuple): Int {
            return Objects.hashCode(t.asTerm())
        }
    }
}