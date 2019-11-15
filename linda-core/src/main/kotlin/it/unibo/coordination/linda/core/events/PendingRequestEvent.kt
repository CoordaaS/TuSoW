package it.unibo.coordination.linda.core.events

import it.unibo.coordination.linda.core.PendingRequest
import it.unibo.coordination.linda.core.Template
import it.unibo.coordination.linda.core.Tuple

sealed class PendingRequestEvent<T : Tuple<T>, TT : Template<T>>(
        override val tupleSpaceName: String,
        val effect: Effect,
        open val pendingRequest: PendingRequest<T, TT>
) : TupleSpaceEvent<T, TT> {

    enum class Effect {
        SUSPENDING, RELEASING
    }

    data class Suspending<T : Tuple<T>, TT : Template<T>>(
            override val tupleSpaceName: String,
            override val pendingRequest: PendingRequest<T, TT>
    ) : PendingRequestEvent<T, TT>(tupleSpaceName, Effect.SUSPENDING, pendingRequest)

    data class Releasing<T : Tuple<T>, TT : Template<T>>(
            override val tupleSpaceName: String,
            override val pendingRequest: PendingRequest<T, TT>
    ) : PendingRequestEvent<T, TT>(tupleSpaceName, Effect.RELEASING, pendingRequest)

    companion object {

        @JvmStatic
        fun <X : Tuple<X>, Y : Template<X>> of(tupleSpaceName: String, effect: Effect, pendingRequest: PendingRequest<X, Y>): PendingRequestEvent<X, Y> =
                when (effect) {
                    Effect.SUSPENDING -> Suspending(tupleSpaceName, pendingRequest)
                    Effect.RELEASING -> Releasing(tupleSpaceName, pendingRequest)
                }
    }
}