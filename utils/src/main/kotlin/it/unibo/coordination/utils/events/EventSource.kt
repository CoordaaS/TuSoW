package it.unibo.coordination.utils.events

interface EventSource<Arg> {
    fun bind(listener: EventListener<Arg>)

    @JvmDefault
    operator fun plusAssign(listener: EventListener<Arg>) =
            bind(listener)

    fun unbind(listener: EventListener<Arg>)

    @JvmDefault
    operator fun minusAssign(listener: EventListener<Arg>) =
            unbind(listener)

    fun unbindAll()
}