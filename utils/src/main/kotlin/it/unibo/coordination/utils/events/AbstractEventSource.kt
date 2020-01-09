package it.unibo.coordination.utils.events

internal abstract class AbstractEventSource<T> : EventSource<T> {

    protected abstract val publicEventListeners: MutableCollection<EventListener<T>>
    protected abstract val privateEventListeners: MutableCollection<EventListener<T>>

    protected val allEventListeners: Sequence<EventListener<T>>
        get() = privateEventListeners.asSequence() + publicEventListeners.asSequence()

    override fun bind(listener: EventListener<T>) {
        publicEventListeners.add(listener)
    }

    override fun unbind(listener: EventListener<T>) {
        publicEventListeners.remove(listener)
    }

    override fun unbindAll() {
        publicEventListeners.clear()
    }

    protected abstract fun <X> newPrivateEmitter(): EventEmitter<X>

    override fun mergeWith(other: EventSource<T>, vararg others: EventSource<T>): EventSource<T> {
        val result = newPrivateEmitter<T>()
        val propagator = { event: T -> result.emit(event) }

        sequenceOf(this, other, *others).forEach { source ->
            if (source is AbstractEventSource<T>) {
                source.privateEventListeners.add(propagator)
            } else {
                source.bind(propagator)
            }
        }

        return result.eventSource
    }

    override fun <U> map(f: (T) -> U): EventSource<U> {
        val result = newPrivateEmitter<U>()
        val propagator = { event: T -> result.emit(f(event)) }
        privateEventListeners.add(propagator)
        return result.eventSource
    }

    override fun filter(predicate: (T) -> Boolean): EventSource<T> {
        val result = newPrivateEmitter<T>()
        val propagator = { event: T -> if (predicate(event)) result.emit(event) }
        privateEventListeners.add(propagator)
        return result.eventSource
    }
}