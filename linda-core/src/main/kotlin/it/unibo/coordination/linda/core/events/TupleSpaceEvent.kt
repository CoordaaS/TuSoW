package it.unibo.coordination.linda.core.events

import it.unibo.coordination.linda.core.InspectableTupleSpace
import it.unibo.coordination.linda.core.Template
import it.unibo.coordination.linda.core.Tuple
import java.util.*

abstract class TupleSpaceEvent<T : Tuple, TT : Template>
    internal constructor(val tupleSpace: InspectableTupleSpace<T, TT, *, *, *>) {

    val tupleSpaceName: String
        get() = tupleSpace.name

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
