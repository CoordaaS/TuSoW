package it.unibo.coordination.linda.core

interface PendingRequest<T : Tuple<T>, TT : Template<T>> {
    val requestType: RequestTypes
    val template: TT
    val id: String

    companion object {

        @JvmStatic
        fun <X : Tuple<X>, Y : Template<X>> wrap(pendingRequest: PendingRequest<X, Y>) =
                object : PendingRequest<X, Y> {
                    override val id = pendingRequest.id
                    override val template = pendingRequest.template
                    override val requestType = pendingRequest.requestType
                }
    }
}