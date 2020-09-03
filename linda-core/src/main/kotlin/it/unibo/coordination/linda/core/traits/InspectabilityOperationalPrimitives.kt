package it.unibo.coordination.linda.core.traits

import it.unibo.coordination.Promise
import it.unibo.coordination.linda.core.PendingRequest
import it.unibo.coordination.linda.core.Template
import it.unibo.coordination.linda.core.Tuple

interface InspectabilityOperationalPrimitives<T : Tuple<T>, TT : Template<T>>
    : OperationalPrimitives, Inspectability<T, TT> {

    fun getAllPendingRequests(): Promise<Collection<PendingRequest<T, TT>>>
}