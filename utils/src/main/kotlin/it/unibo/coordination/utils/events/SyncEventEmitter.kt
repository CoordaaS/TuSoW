package it.unibo.coordination.utils.events

interface SyncEventEmitter<Arg> : EventEmitter<Arg> {

    fun syncEmit(data: Arg): Arg

    companion object {

        @JvmStatic
        fun <X> ordered(): SyncEventEmitter<X> {
            return SyncOrderedEventSourceImpl()
        }
    }

}