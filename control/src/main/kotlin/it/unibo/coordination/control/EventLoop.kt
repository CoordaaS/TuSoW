package it.unibo.coordination.control

import it.unibo.coordination.control.impl.AbstractEventLoop

interface EventLoop<E> : Activity<Unit, E, Unit> {

    val eventQueue: List<E>

    val isIdle: Boolean

    fun onEvent(event: E)

    fun schedule(event: E)

    fun scheduleFirst(event: E)

    override fun onBegin(input: Unit, controller: Activity.Controller<Unit, E, Unit>) = Unit

    override fun onEnd(input: Unit, lastData: E, result: Unit, controller: Activity.Controller<Unit, E, Unit>) = Unit

    companion object {
        fun <E> of(handler: (E) -> Unit): EventLoop<E> =
                object : AbstractEventLoop<E>() {
                    override fun onEvent(event: E) = handler(event)
                }
    }
}