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

    @JvmDefault
    fun <U> cast(): EventSource<U> = map { it as U }

    fun filter(predicate: (T) -> Boolean): EventSource<T>

    companion object {
        @JvmStatic
        fun <X> merge(first: EventSource<X>, second: EventSource<X>, vararg others: EventSource<X>): EventSource<X> {
            return first.mergeWith(second, *others)
        }
    }
}

inline fun <reified U : T, T> EventSource<T>.filterByType(): EventSource<U> = filter { it is U }.map { it as U }

inline fun <reified U : T, T> EventSource<T>.filterByType(klass: Class<U>): EventSource<U> =
        filter { klass is U }.map { klass.cast(it) }