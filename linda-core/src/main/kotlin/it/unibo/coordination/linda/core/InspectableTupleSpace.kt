package it.unibo.coordination.linda.core

interface InspectableTupleSpace<T : Tuple, TT : Template, K, V> : TupleSpace<T, TT, K, V>, InspectableLindaTupleSpace<T, TT, K, V>