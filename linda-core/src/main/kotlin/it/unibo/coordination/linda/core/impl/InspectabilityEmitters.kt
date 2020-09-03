package it.unibo.coordination.linda.core.impl

import it.unibo.coordination.linda.core.Template
import it.unibo.coordination.linda.core.Tuple
import it.unibo.coordination.linda.core.events.OperationEvent
import it.unibo.coordination.linda.core.events.PendingRequestEvent
import it.unibo.coordination.linda.core.events.TupleEvent
import it.unibo.coordination.linda.core.traits.Inspectability
import it.unibo.coordination.utils.events.EventSource
import it.unibo.coordination.utils.events.SyncEventEmitter
import it.unibo.coordination.utils.events.filterByType

class InspectabilityEmitters<T : Tuple<T>, TT : Template<T>> : Inspectability<T, TT> {

    val tupleEventEmitter: SyncEventEmitter<TupleEvent<T, TT>> =
            SyncEventEmitter.ordered()

    override val tupleEvent: EventSource<TupleEvent<T, TT>>
        get() = tupleEventEmitter.eventSource

    val operationEventEmitter: SyncEventEmitter<OperationEvent<T, TT>> =
            SyncEventEmitter.ordered()

    override val operationEvent: EventSource<OperationEvent<T, TT>>
        get() = operationEventEmitter.eventSource

    val pendingRequestEventEmitter: SyncEventEmitter<PendingRequestEvent<T, TT>> =
            SyncEventEmitter.ordered()

    override val pendingRequestEvent: EventSource<PendingRequestEvent<T, TT>>
        get() = pendingRequestEventEmitter.eventSource

    override val operationInvoked: EventSource<OperationEvent.Invocation<T, TT>> =
            operationEvent.filterByType()

    override val operationCompleted: EventSource<OperationEvent.Completion<T, TT>> =
            operationEvent.filterByType()

    override val tupleWriting: EventSource<TupleEvent.Writing<T, TT>> =
            tupleEvent.filter { it.isBefore }.filterByType()

    override val tupleReading: EventSource<TupleEvent.Reading<T, TT>> =
            tupleEvent.filter { it.isBefore }.filterByType()

    override val tupleTaking: EventSource<TupleEvent.Taking<T, TT>> =
            tupleEvent.filter { it.isBefore }.filterByType()

    override val tupleMissing: EventSource<TupleEvent.Missing<T, TT>> =
            tupleEvent.filter { it.isBefore }.filterByType()

    override val tupleWritten: EventSource<TupleEvent.Writing<T, TT>> =
            tupleEvent.filter { it.isAfter }.filterByType()

    override val tupleRead: EventSource<TupleEvent.Reading<T, TT>> =
            tupleEvent.filter { it.isAfter }.filterByType()

    override val tupleTaken: EventSource<TupleEvent.Taking<T, TT>> =
            tupleEvent.filter { it.isAfter }.filterByType()

    override val tupleMissed: EventSource<TupleEvent.Missing<T, TT>> =
            tupleEvent.filter { it.isAfter }.filterByType()

    override val pendingRequestSuspended: EventSource<PendingRequestEvent.Suspending<T, TT>> =
            pendingRequestEvent.filterByType()

    override val pendingRequestResumed: EventSource<PendingRequestEvent.Resuming<T, TT>> =
            pendingRequestEvent.filterByType()
}