package it.unibo.coordination.linda.core.impl

import it.unibo.coordination.linda.core.Template
import it.unibo.coordination.linda.core.Tuple
import it.unibo.coordination.linda.core.events.OperationEvent
import java.util.concurrent.CompletableFuture

data class TupleSpaceInternalEvent<T : Tuple<T>, TT : Template<T>>(
        val cause: OperationEvent<T, TT>,
        val promise: CompletableFuture<*>
)