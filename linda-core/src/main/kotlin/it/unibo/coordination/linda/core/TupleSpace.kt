package it.unibo.coordination.linda.core

interface TupleSpace<T : Tuple, TT : Template, K, V> : BulkTupleSpace<T, TT, K, V>, PredicativeTupleSpace<T, TT, K, V>, NegatedTupleSpace<T, TT, K, V>
