package it.unibo.coordination.linda.core.events

import it.unibo.coordination.linda.core.Template
import it.unibo.coordination.linda.core.Tuple
import it.unibo.coordination.linda.core.TupleSpace
import java.util.*

class TupleEvent<T : Tuple, TT : Template>
    private constructor(tupleSpace: TupleSpace<T, TT, *, *>, val isBefore: Boolean, val effect: Effect, val tuple: T?, val template: TT?)
        : TupleSpaceEvent<T, TT>(tupleSpace) {

    enum class Effect {
        WRITTEN, READ, TAKEN, ABSENT
    }

    val isAfter: Boolean
        get() = !isBefore

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        if (!super.equals(o)) return false
        val that = o as TupleEvent<*, *>?
        return isBefore == that!!.isBefore &&
                effect == that.effect &&
                tuple == that.tuple &&
                template == that.template
    }

    override fun toString(): String {
        return "TupleEvent{" +
                "tupleSpace=" + tupleSpaceName +
                ", effect=" + effect +
                ", before=" + isBefore +
                ", tuple=" + tuple +
                ", template=" + template +
                "}"
    }

    override fun hashCode(): Int {
        return Objects.hash(effect, isBefore, tuple, template)
    }

    companion object {

        @JvmStatic
        fun <X : Tuple, Y : Template> of(tupleSpace: TupleSpace<X, Y, *, *>, before: Boolean, effect: Effect, tuple: X, template: Y): TupleEvent<X, Y> {
            return TupleEvent(tupleSpace, before, effect, tuple, template)
        }

        @JvmStatic
        fun <X : Tuple, Y : Template> beforeWriting(tupleSpace: TupleSpace<X, Y, *, *>, tuple: X): TupleEvent<X, Y> {
            return TupleEvent(tupleSpace, true, Effect.WRITTEN, tuple, null)
        }

        @JvmStatic
        fun <X : Tuple, Y : Template> afterWriting(tupleSpace: TupleSpace<X, Y, *, *>, tuple: X): TupleEvent<X, Y> {
            return TupleEvent(tupleSpace, false, Effect.WRITTEN, tuple, null)
        }

        @JvmStatic
        fun <X : Tuple, Y : Template> beforeTaking(tupleSpace: TupleSpace<X, Y, *, *>, tuple: X): TupleEvent<X, Y> {
            return TupleEvent(tupleSpace, true, Effect.TAKEN, tuple, null)
        }

        @JvmStatic
        fun <X : Tuple, Y : Template> afterTaking(tupleSpace: TupleSpace<X, Y, *, *>, tuple: X): TupleEvent<X, Y> {
            return TupleEvent(tupleSpace, false, Effect.TAKEN, tuple, null)
        }

        @JvmStatic
        fun <X : Tuple, Y : Template> beforeReading(tupleSpace: TupleSpace<X, Y, *, *>, tuple: X): TupleEvent<X, Y> {
            return TupleEvent(tupleSpace, true, Effect.READ, tuple, null)
        }

        @JvmStatic
        fun <X : Tuple, Y : Template> afterReading(tupleSpace: TupleSpace<X, Y, *, *>, tuple: X): TupleEvent<X, Y> {
            return TupleEvent(tupleSpace, false, Effect.READ, tuple, null)
        }

        @JvmStatic
        fun <X : Tuple, Y : Template> beforeAbsent(tupleSpace: TupleSpace<X, Y, *, *>, template: Y): TupleEvent<X, Y> {
            return TupleEvent(tupleSpace, true, Effect.ABSENT, null, template)
        }

        @JvmStatic
        fun <X : Tuple, Y : Template> afterAbsent(tupleSpace: TupleSpace<X, Y, *, *>, template: Y): TupleEvent<X, Y> {
            return TupleEvent(tupleSpace, false, Effect.ABSENT, null, template)
        }

        @JvmStatic
        fun <X : Tuple, Y : Template> beforeAbsent(tupleSpace: TupleSpace<X, Y, *, *>, template: Y, counterexample: X): TupleEvent<X, Y> {
            return TupleEvent(tupleSpace, true, Effect.ABSENT, counterexample, template)
        }

        @JvmStatic
        fun <X : Tuple, Y : Template> afterAbsent(tupleSpace: TupleSpace<X, Y, *, *>, template: Y, counterexample: X): TupleEvent<X, Y> {
            return TupleEvent(tupleSpace, false, Effect.ABSENT, counterexample, template)
        }
    }
}
