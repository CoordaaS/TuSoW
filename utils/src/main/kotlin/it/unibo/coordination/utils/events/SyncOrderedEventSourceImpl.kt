package it.unibo.coordination.utils.events

import java.util.*
import java.util.concurrent.CompletableFuture

internal class SyncOrderedEventSourceImpl<T> : AbstractEventSourceImpl<T>() {

    override val eventListeners = LinkedList<EventListener<T>>()

    override fun syncEmit(data: T): T {
        for (listener in eventListeners) {
            listener(data)
        }
        return data
    }

    override fun asyncEmit(event: T): CompletableFuture<T> {
        return CompletableFuture.completedFuture(syncEmit(event))
    }

}