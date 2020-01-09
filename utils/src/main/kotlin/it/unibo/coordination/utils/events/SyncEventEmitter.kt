package it.unibo.coordination.utils.events

interface SyncEventEmitter<T> : EventEmitter<T> {

    fun syncEmit(event: T): T

    @JvmDefault
    override fun emit(event: T) {
        syncEmit(event)
    }

    companion object {

        @JvmStatic
        fun <X> ordered(): SyncEventEmitter<X> {
            return SyncOrderedEventSource()
        }
    }

}