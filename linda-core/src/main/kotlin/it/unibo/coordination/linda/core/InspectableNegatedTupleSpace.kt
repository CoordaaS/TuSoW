package it.unibo.coordination.linda.core

interface InspectableNegatedTupleSpace<T : Tuple, TT : Template, K, V> : NegatedTupleSpace<T, TT, K, V>, InspectableLindaTupleSpace<T, TT, K, V>