package it.unibo.coordination.utils.events

interface EventListener<Arg> {
    fun onEvent(data: Arg)
}