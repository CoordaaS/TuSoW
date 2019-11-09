package it.unibo.coordination.linda.core.impl

import it.unibo.coordination.Promise
import it.unibo.coordination.linda.core.*
import it.unibo.coordination.utils.readableHashString

data class LocalPendingRequest<T : Tuple<T>, TT : Template<T>, M : Match<T, TT, *, *>>(
        override val requestType: RequestTypes,
        override val template: TT,
        val promise: Promise<M>
) : PendingRequest<T, TT> {
    override val id: String
        get() = promise.readableHashString
}