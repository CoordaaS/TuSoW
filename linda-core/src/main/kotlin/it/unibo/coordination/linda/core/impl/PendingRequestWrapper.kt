package it.unibo.coordination.linda.core.impl

import it.unibo.coordination.linda.core.PendingRequest
import it.unibo.coordination.linda.core.RequestTypes
import it.unibo.coordination.linda.core.Template
import it.unibo.coordination.linda.core.Tuple

data class PendingRequestWrapper<T : Tuple<T>, TT : Template<T>>(override val id: String, override val requestType: RequestTypes, override val template: TT)
    : PendingRequest<T, TT>