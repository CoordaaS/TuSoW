package it.unibo.coordination.linda.core

interface InspectableBulkTupleSpace<T : Tuple, TT : Template, K, V> : BulkTupleSpace<T, TT, K, V>, InspectableTupleSpace<T, TT, K, V>