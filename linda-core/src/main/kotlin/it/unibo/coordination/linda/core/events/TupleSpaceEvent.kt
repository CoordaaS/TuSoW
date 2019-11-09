package it.unibo.coordination.linda.core.events

import it.unibo.coordination.linda.core.Template
import it.unibo.coordination.linda.core.Tuple

interface TupleSpaceEvent<T : Tuple<T>, TT : Template<T>> {
    val tupleSpaceName: String
}
