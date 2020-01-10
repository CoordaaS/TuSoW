package it.unibo.coordination.linda.core

import it.unibo.coordination.linda.core.impl.PendingRequestWrapper

interface PendingRequest<T : Tuple<T>, TT : Template<T>> {
    val requestType: RequestTypes
    val template: TT
    val id: String

    companion object {

        @JvmStatic
        fun <X : Tuple<X>, Y : Template<X>> wrap(pendingRequest: PendingRequest<X, Y>) =
                with(pendingRequest) {
                    PendingRequestWrapper(id, requestType, template)
                }

        @JvmStatic
        fun <X : Tuple<X>, Y : Template<X>> of(id: String, requestType: RequestTypes, template: Y) =
                PendingRequestWrapper(id, requestType, template)

    }
}