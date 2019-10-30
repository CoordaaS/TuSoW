package it.unibo.coordination.linda.core

interface InspectablePredicativeTupleSpace<T : Tuple, TT : Template, K, V> : PredicativeTupleSpace<T, TT, K, V>, InspectableTupleSpace<T, TT, K, V>
