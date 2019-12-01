package it.unibo.coordination.linda.core

import it.unibo.coordination.Promise
import it.unibo.coordination.linda.core.events.OperationEvent
import it.unibo.coordination.linda.core.events.PendingRequestEvent
import it.unibo.coordination.linda.core.events.TupleEvent
import it.unibo.coordination.utils.events.EventSource

interface InspectableLindaTupleSpace<T : Tuple<T>, TT : Template<T>, K, V, M : Match<T, TT, K, V>> : LindaTupleSpace<T, TT, K, V, M> {
    val operationInvoked: EventSource<OperationEvent.Invocation<T, TT>>

    val operationCompleted: EventSource<OperationEvent.Completion<T, TT>>

    val tupleSpaceChanged: EventSource<TupleEvent<T, TT>>

    val operationSuspended: EventSource<PendingRequestEvent.Suspending<T, TT>>

    val operationResumed: EventSource<PendingRequestEvent.Resuming<T, TT>>

    fun getAllPendingRequests(): Promise<PendingRequest<T, TT>>
}
