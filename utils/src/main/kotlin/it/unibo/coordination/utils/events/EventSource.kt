package it.unibo.coordination.utils.events

interface EventSource<T> {
    fun bind(listener: EventListener<T>)

    @JvmDefault
    operator fun plusAssign(listener: EventListener<T>) =
            bind(listener)

    fun unbind(listener: EventListener<T>)

    @JvmDefault
    operator fun minusAssign(listener: EventListener<T>) =
            unbind(listener)

    fun unbindAll()

    fun mergeWith(other: EventSource<T>, vararg others: EventSource<T>): EventSource<T>

    fun <U> map(f: (T) -> U): EventSource<U>

    companion object {
        @JvmStatic
        fun <X> merge(first: EventSource<X>, second: EventSource<X>, vararg others: EventSource<X>): EventSource<X> {
            return first.mergeWith(second, *others)
        }
    }
}