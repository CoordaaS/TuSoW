package it.unibo.coordination.linda.core

interface InspectableNegatedTupleSpace<T : Tuple, TT : Template, K, V> : NegatedTupleSpace<T, TT, K, V>, InspectableTupleSpace<T, TT, K, V>