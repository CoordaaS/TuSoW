package it.unibo.coordination.utils.events

interface EventEmitter<T> {
    val eventSource: EventSource<T>

    fun emit(event: T)
}