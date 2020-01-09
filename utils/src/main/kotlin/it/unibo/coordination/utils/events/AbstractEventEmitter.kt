package it.unibo.coordination.utils.events

import java.util.concurrent.CompletableFuture

internal abstract class AbstractEventEmitter<T> : AbstractEventSource<T>(), SyncEventEmitter<T>, AsyncEventEmitter<T>  {

    abstract override fun syncEmit(event: T): T

    abstract override fun asyncEmit(event: T): CompletableFuture<T>

    override val eventSource: EventSource<T>
        get() = this

    override fun emit(event: T) {
        throw NotImplementedError()
    }
}