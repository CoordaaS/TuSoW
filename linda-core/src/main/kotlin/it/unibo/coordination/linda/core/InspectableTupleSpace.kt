package it.unibo.coordination.linda.core

interface InspectableTupleSpace<T : Tuple, TT : Template, K, V, M : Match<T, TT, K, V>>
    : TupleSpace<T, TT, K, V, M>,
        InspectableLindaTupleSpace<T, TT, K, V, M>