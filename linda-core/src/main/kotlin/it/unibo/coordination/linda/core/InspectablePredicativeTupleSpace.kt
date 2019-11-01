package it.unibo.coordination.linda.core

interface InspectablePredicativeTupleSpace<T : Tuple, TT : Template, K, V, M : Match<T, TT, K, V>>
    : PredicativeTupleSpace<T, TT, K, V, M>,
        InspectableLindaTupleSpace<T, TT, K, V, M>
