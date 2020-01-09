package it.unibo.coordination.utils.events

import java.util.*
import java.util.concurrent.CompletableFuture

internal class SyncOrderedEventSource<T> : AbstractEventEmitter<T>() {

    override val publicEventListeners = LinkedList<EventListener<T>>()
    override val privateEventListeners = LinkedList<EventListener<T>>()

    override fun syncEmit(event: T): T {
        for (listener in allEventListeners) {
            listener(event)
        }
        return event
    }

    override fun asyncEmit(event: T): CompletableFuture<T> {
        return CompletableFuture.completedFuture(syncEmit(event))
    }

    override fun emit(event: T) {
        syncEmit(event)
    }

    override fun <U> newPrivateEmitter(): EventEmitter<U> = SyncOrderedEventSource()
}