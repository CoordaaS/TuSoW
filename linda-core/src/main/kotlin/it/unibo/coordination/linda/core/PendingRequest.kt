package it.unibo.coordination.linda.core

interface PendingRequest<T : Tuple<T>, TT : Template<T>> {
    val requestType: RequestTypes
    val template: TT
    val id: String
}