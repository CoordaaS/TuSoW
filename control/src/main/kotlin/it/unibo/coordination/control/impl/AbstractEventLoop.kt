package it.unibo.coordination.control.impl

import it.unibo.coordination.control.Activity
import it.unibo.coordination.control.EventLoop
import java.util.*

abstract class AbstractEventLoop<E> : EventLoop<E> {

    private val eventDeque: Deque<E> = LinkedList()

    @Volatile
    private var onResume: (() -> Unit)? = null

    override val eventQueue: List<E>
        get() = eventDeque.toList()

    override fun schedule(event: E) {
        synchronized(eventDeque) {
            eventDeque.addLast(event)
            onResume?.invoke()
        }
    }

    override fun scheduleFirst(event: E) {
        synchronized(eventDeque) {
            eventDeque.addFirst(event)
            onResume?.invoke()
        }
    }

    override fun onStep(input: Nothing, lastData: E, controller: Activity.Controller<Nothing, E, Unit>) {
        synchronized(eventDeque) {
            if (eventDeque.isEmpty()) {
                onResume = { resume(controller) }
                controller.pause()
            } else {
                val event = eventDeque.pollFirst()
                onEvent(event)
                controller.`continue`(event)
            }
        }
    }

    private fun resume(controller: Activity.Controller<Nothing, E, Unit>) {
        controller.resume()
        onResume = null
    }
}