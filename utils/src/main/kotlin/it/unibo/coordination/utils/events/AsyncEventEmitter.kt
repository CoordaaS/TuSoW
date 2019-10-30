package it.unibo.coordination.utils.events

import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService

interface AsyncEventEmitter<Arg> : EventEmitter<Arg> {

    fun asyncEmit(event: Arg): CompletableFuture<Arg>

    companion object {

        @JvmStatic
        fun <A> ordered(executor: ExecutorService): AsyncEventEmitter<A> {
            return AsyncOrderedEventSourceImpl(executor)
        }
    }
}