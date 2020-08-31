package it.unibo.cooordination.respect.core

import it.unibo.coordination.linda.core.Template
import it.unibo.coordination.linda.core.Tuple


interface ReactionContext<T : Tuple<T>, TT : Template<T>> {
    val event: InternalEvent<T, TT>
    val causedBy: ReactionContext<T, TT>?

    val pathToRoot: Sequence<ReactionContext<T, TT>>
    val root: ReactionContext<T, TT>?

    val isRoot: Boolean
}