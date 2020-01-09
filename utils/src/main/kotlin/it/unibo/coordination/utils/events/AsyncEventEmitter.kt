package it.unibo.coordination.utils.events

import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService

interface AsyncEventEmitter<T> : EventEmitter<T> {

    fun asyncEmit(event: T): CompletableFuture<T>

    @JvmDefault
    override fun emit(event: T) {
        asyncEmit(event)
    }

    companion object {

        @JvmStatic
        fun <A> ordered(executor: ExecutorService): AsyncEventEmitter<A> {
            return AsyncOrderedEventSource(executor)
        }
    }
}