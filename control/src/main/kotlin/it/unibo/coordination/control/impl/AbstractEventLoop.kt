package it.unibo.coordination.control.impl

import it.unibo.coordination.control.Activity
import it.unibo.coordination.control.EventLoop
import java.util.*

abstract class AbstractEventLoop<E> : EventLoop<E> {

    private val eventDeque: Deque<E> = LinkedList()

    override val isIdle: Boolean
        get() = synchronized(eventDeque) {
            eventDeque.isEmpty()
        }

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

    private val emptyData = Optional.empty<E>()

    override fun onBegin(input: Unit, controller: Activity.Controller<Unit, Optional<E>, Unit>) {
        onStep(input, Optional.empty(), controller)
    }

    override fun onStep(input: Unit, lastData: Optional<E>, controller: Activity.Controller<Unit, Optional<E>, Unit>) {
        synchronized(eventDeque) {
            if (eventDeque.isEmpty()) {
                onResume = { resume(controller) }
                controller.pause()
            } else {
                val event = eventDeque.pollFirst()
                onEvent(event)
                controller.`continue`(Optional.of(event))
            }
        }
    }

    private fun resume(controller: Activity.Controller<Unit, Optional<E>, Unit>) {
        controller.resume()
        onResume = null
    }
}