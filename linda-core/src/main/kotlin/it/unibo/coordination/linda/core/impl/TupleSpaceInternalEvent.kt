package it.unibo.coordination.linda.core.impl

import it.unibo.coordination.linda.core.Template
import it.unibo.coordination.linda.core.Tuple
import it.unibo.coordination.linda.core.events.TupleSpaceEvent
import java.util.concurrent.CompletableFuture

data class TupleSpaceInternalEvent<T : Tuple<T>, TT : Template<T>>(
        val cause: TupleSpaceEvent<T, TT>,
        val operation: CompletableFuture<*>
)