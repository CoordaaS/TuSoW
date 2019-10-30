package it.unibo.coordination.utils.events

interface EventSource<Arg> {
    fun bind(listener: EventListener<Arg>)

    fun unbind(listener: EventListener<Arg>)

    fun unbindAll()
}