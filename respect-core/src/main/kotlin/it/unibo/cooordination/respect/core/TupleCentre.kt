package it.unibo.cooordination.respect.core

import it.unibo.coordination.linda.core.Match
import it.unibo.coordination.linda.core.Template
import it.unibo.coordination.linda.core.Tuple

interface TupleCentre<
        T : Tuple<T>, // f(1)
        TT : Template<T>, // f(X)
        K,
        V,
        M : Match<T, TT, K, V>,
        ST : SpecificationTuple<T, TT, ST>,
        STT : SpecificationTemplate<T, TT, ST>,
        SM : Match<ST, STT, K, V>
        > : TupleCentreExternalAPI<T, TT, K, V, M, ST, STT, SM> {
    val id: TupleCentreID
}