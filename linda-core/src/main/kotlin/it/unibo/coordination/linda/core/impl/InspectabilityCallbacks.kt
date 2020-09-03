package it.unibo.coordination.linda.core.impl

import it.unibo.coordination.linda.core.PendingRequest
import it.unibo.coordination.linda.core.Template
import it.unibo.coordination.linda.core.Tuple
import it.unibo.coordination.linda.core.events.PendingRequestEvent
import it.unibo.coordination.linda.core.events.TupleEvent

class InspectabilityCallbacks<T : Tuple<T>, TT : Template<T>>(
        private val name: String,
        private val emitters: InspectabilityEmitters<T, TT>
) : TupleSpaceInteralCallbacks<T, TT> {

    override fun onSuspending(request: PendingRequest<T, TT>) {
        emitters.pendingRequestEventEmitter.syncEmit(
                PendingRequestEvent.of(name, PendingRequestEvent.Effect.SUSPENDING, request)
        )
    }

    override fun onResuming(request: PendingRequest<T, TT>) {
        emitters.pendingRequestEventEmitter.syncEmit(
                PendingRequestEvent.of(name, PendingRequestEvent.Effect.RESUMING, request)
        )
    }

    override fun onTaking(tuple: T) {
        emitters.tupleEventEmitter.syncEmit(TupleEvent.beforeTaking(name, tuple))
    }

    override fun onTaken(tuple: T) {
        emitters.tupleEventEmitter.syncEmit(TupleEvent.afterTaking(name, tuple))
    }

    override fun onReading(tuple: T) {
        emitters.tupleEventEmitter.syncEmit(TupleEvent.beforeReading(name, tuple))
    }

    override fun onRead(tuple: T) {
        emitters.tupleEventEmitter.syncEmit(TupleEvent.afterReading(name, tuple))
    }

    override fun onWriting(tuple: T) {
        emitters.tupleEventEmitter.syncEmit(TupleEvent.beforeWriting(name, tuple))
    }

    override fun onWritten(tuple: T) {
        emitters.tupleEventEmitter.syncEmit(TupleEvent.afterWriting(name, tuple))
    }

    override fun onMissing(template: TT) {
        emitters.tupleEventEmitter.syncEmit(TupleEvent.beforeAbsent(name, template))
    }

    override fun onMissing(template: TT, counterExample: T) {
        emitters.tupleEventEmitter.syncEmit(TupleEvent.beforeAbsent(name, template, counterExample))
    }

    override fun onMissed(template: TT) {
        emitters.tupleEventEmitter.syncEmit(TupleEvent.afterAbsent(name, template))
    }

    override fun onMissed(template: TT, counterExample: T) {
        emitters.tupleEventEmitter.syncEmit(TupleEvent.afterAbsent(name, template, counterExample))
    }
}