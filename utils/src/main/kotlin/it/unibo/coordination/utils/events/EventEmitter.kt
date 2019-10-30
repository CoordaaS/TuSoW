package it.unibo.coordination.utils.events

interface EventEmitter<Arg> {
    val eventSource: EventSource<Arg>
}