package it.unibo.coordination.linda.core

interface InspectableNegatedTupleSpace<T : Tuple, TT : Template, K, V, M : Match<T, TT, K, V>>
    : NegatedTupleSpace<T, TT, K, V, M>,
        InspectableLindaTupleSpace<T, TT, K, V, M>