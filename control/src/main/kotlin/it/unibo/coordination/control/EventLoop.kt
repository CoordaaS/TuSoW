package it.unibo.coordination.control

import it.unibo.coordination.control.impl.AbstractEventLoop
import java.util.*

interface EventLoop<E> : Activity<Unit, Optional<E>, Unit> {

    val eventQueue: List<E>

    val isIdle: Boolean

    fun onEvent(event: E)

    fun schedule(event: E)

    fun scheduleFirst(event: E)

    companion object {
        fun <E> of(handler: (E) -> Unit): EventLoop<E> =
                object : AbstractEventLoop<E>() {
                    override fun onEvent(event: E) = handler(event)
                }
    }
}