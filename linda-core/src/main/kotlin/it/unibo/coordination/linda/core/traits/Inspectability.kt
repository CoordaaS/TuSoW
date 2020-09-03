package it.unibo.coordination.linda.core.traits

import it.unibo.coordination.linda.core.Template
import it.unibo.coordination.linda.core.Tuple
import it.unibo.coordination.linda.core.events.OperationEvent
import it.unibo.coordination.linda.core.events.PendingRequestEvent
import it.unibo.coordination.linda.core.events.TupleEvent
import it.unibo.coordination.utils.events.EventSource

interface Inspectability<T : Tuple<T>, TT : Template<T>> {
    val operationInvoked: EventSource<OperationEvent.Invocation<T, TT>>

    val operationEvent: EventSource<OperationEvent<T, TT>>

    val operationCompleted: EventSource<OperationEvent.Completion<T, TT>>

    val tupleWriting: EventSource<TupleEvent.Writing<T, TT>>

    val tupleReading: EventSource<TupleEvent.Reading<T, TT>>

    val tupleTaking: EventSource<TupleEvent.Taking<T, TT>>

    val tupleMissing: EventSource<TupleEvent.Missing<T, TT>>

    val tupleEvent: EventSource<TupleEvent<T, TT>>

    val tupleWritten: EventSource<TupleEvent.Writing<T, TT>>

    val tupleRead: EventSource<TupleEvent.Reading<T, TT>>

    val tupleTaken: EventSource<TupleEvent.Taking<T, TT>>

    val tupleMissed: EventSource<TupleEvent.Missing<T, TT>>

    val pendingRequestSuspended: EventSource<PendingRequestEvent.Suspending<T, TT>>

    val pendingRequestEvent: EventSource<PendingRequestEvent<T, TT>>

    val pendingRequestResumed: EventSource<PendingRequestEvent.Resuming<T, TT>>

}