package it.unibo.coordination.linda.core

import it.unibo.coordination.linda.core.events.OperationEvent
import it.unibo.coordination.linda.core.events.TupleEvent
import it.unibo.coordination.utils.events.EventSource

interface InspectableLindaTupleSpace<T : Tuple, TT : Template, K, V> : LindaTupleSpace<T, TT, K, V> {
    val operationInvoked: EventSource<OperationEvent<T, TT>>

    val operationCompleted: EventSource<OperationEvent<T, TT>>

    val tupleSpaceChanged: EventSource<TupleEvent<T, TT>>
}
