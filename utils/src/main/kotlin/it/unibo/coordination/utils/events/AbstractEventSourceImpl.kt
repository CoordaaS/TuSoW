package it.unibo.coordination.utils.events

import java.util.concurrent.CompletableFuture

internal abstract class AbstractEventSourceImpl<T> : EventSource<T>, SyncEventEmitter<T>, AsyncEventEmitter<T> {

    protected abstract val eventListeners: MutableCollection<EventListener<T>>

    override fun bind(listener: EventListener<T>) {
        eventListeners.add(listener)
    }

    override fun unbind(listener: EventListener<T>) {
        eventListeners.remove(listener)
    }

    override fun unbindAll() {
        eventListeners.clear()
    }

    abstract override fun syncEmit(data: T): T

    abstract override fun asyncEmit(event: T): CompletableFuture<T>

    override val eventSource: EventSource<T>
        get() = this
}