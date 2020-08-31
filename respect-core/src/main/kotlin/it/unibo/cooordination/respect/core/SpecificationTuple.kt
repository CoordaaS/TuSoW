package it.unibo.cooordination.respect.core

import it.unibo.coordination.linda.core.Template
import it.unibo.coordination.linda.core.Tuple

interface SpecificationTuple<T : Tuple<T>, TT : Template<T>, ST : SpecificationTuple<T, TT, ST>> : Tuple<ST> {

    val event: ExternalEvent<T, TT> // <primitive>(<args>) | <time_event> | <space_event>

    val guards: GuardSet

    @Throws(ReactionException::class)
    fun callback(
            reactionContext: ReactionContext<T, TT>,
            tupleCentre: TupleCentreInternalAPI<T, TT, *, *, *, ST, *, *>
    )

}


// reaction(E, G, B).

// out_s(E, G, B).