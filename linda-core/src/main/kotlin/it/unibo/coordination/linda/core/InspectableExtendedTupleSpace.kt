package it.unibo.coordination.linda.core

interface InspectableExtendedTupleSpace<T : Tuple, TT : Template, K, V> : ExtendedTupleSpace<T, TT, K, V>, InspectableTupleSpace<T, TT, K, V>