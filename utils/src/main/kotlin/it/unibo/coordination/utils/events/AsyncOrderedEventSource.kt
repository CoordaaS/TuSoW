package it.unibo.coordination.utils.events

import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService

internal class AsyncOrderedEventSource<T>(private val engine: ExecutorService) : AbstractEventEmitter<T>() {

    override val publicEventListeners = LinkedList<EventListener<T>>()
    override val privateEventListeners = LinkedList<EventListener<T>>()

    override fun syncEmit(event: T): T {
        try {
            return asyncEmit(event).get()
        } catch (e: InterruptedException) {
            throw IllegalStateException(e)
        } catch (e: ExecutionException) {
            throw IllegalStateException(e)
        }

    }

    override fun asyncEmit(event: T): CompletableFuture<T> {
        val emitterPromise = CompletableFuture<T>()

        submitNotifications(event, allEventListeners.toList(), 0, emitterPromise)

        return emitterPromise
    }

    private fun submitNotifications(data: T, eventListeners: List<EventListener<T>>, i: Int, promise: CompletableFuture<T>) {
        if (i == eventListeners.size) {
            promise.complete(data)
        } else {
            engine.submit {
                eventListeners[i](data)
                submitNotifications(data, eventListeners, i + 1, promise)
            }
        }
    }

    override fun emit(event: T) {
        asyncEmit(event)
    }

    override fun <U> newPrivateEmitter(): EventEmitter<U> = AsyncOrderedEventSource(engine)
}