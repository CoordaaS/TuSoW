package it.unibo.coordination.linda.core

import it.unibo.coordination.linda.core.traits.*

interface TupleSpace<T : Tuple<T>, TT : Template<T>, K, V, M : Match<T, TT, K, V>>
    : Naming,
        OperationalPrimitives,
        LindaPrimitives<T, TT, K, V, M>,
        BulkPrimitives<T, TT, K, V, M>,
        NegatedPrimitives<T, TT, K, V, M>,
        PredicativePrimitives<T, TT, K, V, M>,
        NegatedPredicativePrimitives<T, TT, K, V, M>
