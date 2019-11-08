package it.unibo.coordination.linda.core

import it.unibo.coordination.Promise

data class PendingRequest<T : Tuple<T>, TT : Template<T>, M : Match<T, TT, * ,*>>(
        val requestType: RequestTypes,
        val template: TT,
        val promise: Promise<M>
)