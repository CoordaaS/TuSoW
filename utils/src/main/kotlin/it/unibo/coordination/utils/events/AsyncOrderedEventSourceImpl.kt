package it.unibo.coordination.utils.events

import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService

internal class AsyncOrderedEventSourceImpl<T>(private val engine: ExecutorService) : AbstractEventSourceImpl<T>() {

    override val eventListeners = LinkedList<EventListener<T>>()

    override fun syncEmit(data: T): T {
        try {
            return asyncEmit(data).get()
        } catch (e: InterruptedException) {
            throw IllegalStateException(e)
        } catch (e: ExecutionException) {
            throw IllegalStateException(e)
        }

    }

    override fun asyncEmit(event: T): CompletableFuture<T> {
        val emitterPromise = CompletableFuture<T>()

        submitNotifications(event, eventListeners.toList(), 0, emitterPromise)

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
}