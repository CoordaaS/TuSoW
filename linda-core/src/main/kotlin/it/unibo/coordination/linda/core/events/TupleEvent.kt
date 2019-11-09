package it.unibo.coordination.linda.core.events

import it.unibo.coordination.linda.core.Template
import it.unibo.coordination.linda.core.Tuple

sealed class TupleEvent<T : Tuple<T>, TT : Template<T>>(
        override val tupleSpaceName: String,
        open val isBefore: Boolean,
        val effect: Effect,
        open val tuple: T?,
        open val template: TT?
) : TupleSpaceEvent<T, TT> {

    enum class Effect {
        WRITING, READING, TAKING, MISSING
    }

    val isAfter: Boolean
        get() = !isBefore

    data class Writing<T : Tuple<T>, TT : Template<T>>(
            override val tupleSpaceName: String,
            override val isBefore: Boolean = true,
            override val tuple: T
    ) : TupleEvent<T, TT>(tupleSpaceName, isBefore, Effect.WRITING, tuple, null)

    data class Reading<T : Tuple<T>, TT : Template<T>>(
            override val tupleSpaceName: String,
            override val isBefore: Boolean = true,
            override val tuple: T
    ) : TupleEvent<T, TT>(tupleSpaceName, isBefore, Effect.READING, tuple, null)

    data class Taking<T : Tuple<T>, TT : Template<T>>(
            override val tupleSpaceName: String,
            override val isBefore: Boolean = true,
            override val tuple: T
    ) : TupleEvent<T, TT>(tupleSpaceName, isBefore, Effect.TAKING, tuple, null)

    data class Missing<T : Tuple<T>, TT : Template<T>>(
            override val tupleSpaceName: String,
            override val isBefore: Boolean = true,
            override val template: TT,
            override val tuple: T? = null
    ) : TupleEvent<T, TT>(tupleSpaceName, isBefore, Effect.MISSING, tuple, template) {

        val counterExample
            get() = tuple
    }

    companion object {

        @JvmStatic
        fun <X : Tuple<X>, Y : Template<X>> of(tupleSpaceName: String, before: Boolean, effect: Effect, tuple: X?, template: Y?): TupleEvent<X, Y> =
                when (effect) {
                    Effect.WRITING -> Writing(tupleSpaceName, before, tuple!!)
                    Effect.READING -> Reading(tupleSpaceName, before, tuple!!)
                    Effect.TAKING -> Taking(tupleSpaceName, before, tuple!!)
                    Effect.MISSING -> Missing(tupleSpaceName, before, template!!, tuple)
                }

        @JvmStatic
        fun <X : Tuple<X>, Y : Template<X>> beforeWriting(tupleSpaceName: String, tuple: X): Writing<X, Y> {
            return Writing(tupleSpaceName, true, tuple)
        }

        @JvmStatic
        fun <X : Tuple<X>, Y : Template<X>> afterWriting(tupleSpaceName: String, tuple: X): Writing<X, Y> {
            return Writing(tupleSpaceName, false, tuple)
        }

        @JvmStatic
        fun <X : Tuple<X>, Y : Template<X>> beforeTaking(tupleSpaceName: String, tuple: X): Taking<X, Y> {
            return Taking(tupleSpaceName, false, tuple)
        }

        @JvmStatic
        fun <X : Tuple<X>, Y : Template<X>> afterTaking(tupleSpaceName: String, tuple: X): Taking<X, Y> {
            return Taking(tupleSpaceName, true, tuple)
        }

        @JvmStatic
        fun <X : Tuple<X>, Y : Template<X>> beforeReading(tupleSpaceName: String, tuple: X): Reading<X, Y> {
            return Reading(tupleSpaceName, false, tuple)
        }

        @JvmStatic
        fun <X : Tuple<X>, Y : Template<X>> afterReading(tupleSpaceName: String, tuple: X): Reading<X, Y> {
            return Reading(tupleSpaceName, true, tuple)
        }

        @JvmStatic
        fun <X : Tuple<X>, Y : Template<X>> beforeAbsent(tupleSpaceName: String, template: Y): Missing<X, Y> {
            return Missing(tupleSpaceName, false, template)
        }

        @JvmStatic
        fun <X : Tuple<X>, Y : Template<X>> afterAbsent(tupleSpaceName: String, template: Y): Missing<X, Y> {
            return Missing(tupleSpaceName, true, template)
        }

        @JvmStatic
        fun <X : Tuple<X>, Y : Template<X>> beforeAbsent(tupleSpaceName: String, template: Y, counterexample: X): Missing<X, Y> {
            return Missing(tupleSpaceName, false, template, counterexample)
        }

        @JvmStatic
        fun <X : Tuple<X>, Y : Template<X>> afterAbsent(tupleSpaceName: String, template: Y, counterexample: X): Missing<X, Y> {
            return Missing(tupleSpaceName, true, template, counterexample)
        }
    }
}