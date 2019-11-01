package it.unibo.coordination.linda.core

interface InspectableBulkTupleSpace<T : Tuple, TT : Template, K, V, M : Match<T, TT, K, V>>
    : BulkTupleSpace<T, TT, K, V, M>,
        InspectableLindaTupleSpace<T, TT, K, V, M>