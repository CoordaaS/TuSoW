package it.unibo.coordination.linda.core.events

import it.unibo.coordination.linda.core.LindaTupleSpace
import it.unibo.coordination.linda.core.Template
import it.unibo.coordination.linda.core.Tuple
import it.unibo.coordination.linda.core.TupleSpace
import java.util.*

abstract class TupleSpaceEvent<T : Tuple, TT : Template>
    internal constructor(val tupleSpace: LindaTupleSpace<T, TT, *, *>) {

    val tupleSpaceName: String
        get() = tupleSpace.name

    protected val extendedTupleSpace: TupleSpace<T, TT, *, *>
        get() = tupleSpace as TupleSpace<T, TT, *, *>

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as TupleSpaceEvent<*, *>?
        return tupleSpace == that!!.tupleSpace
    }

    override fun hashCode(): Int {
        return Objects.hash(tupleSpace)
    }

    override fun toString(): String {
        return "TupleSpaceEvent{" +
                "tupleSpace=" + tupleSpaceName +
                "}"
    }
}
