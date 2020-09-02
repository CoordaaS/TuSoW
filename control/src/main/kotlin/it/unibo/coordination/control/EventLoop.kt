package it.unibo.coordination.control

import it.unibo.coordination.control.impl.AbstractEventLoop

interface EventLoop<E> : Activity<Nothing, E, Unit> {

    val eventQueue: List<E>

    val isIdle: Boolean
        get() = eventQueue.isEmpty()

    fun onEvent(event: E)

    fun schedule(event: E)

    fun scheduleFirst(event: E)

    override fun onBegin(input: Nothing, controller: Activity.Controller<Nothing, E, Unit>) = Unit

    override fun onEnd(input: Nothing, lastData: E, result: Unit, controller: Activity.Controller<Nothing, E, Unit>) = Unit

    companion object {
        fun <E> of(handler: (E) -> Unit): EventLoop<E> =
                object : AbstractEventLoop<E>() {
                    override fun onEvent(event: E) = handler(event)
                }
    }
}