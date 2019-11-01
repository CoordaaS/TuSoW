package it.unibo.coordination.linda.core

interface TupleSpace<T : Tuple, TT : Template, K, V, M : Match<T, TT, K, V>>
    : BulkTupleSpace<T, TT, K, V, M>,
        PredicativeTupleSpace<T, TT, K, V, M>,
        NegatedTupleSpace<T, TT, K, V, M>
