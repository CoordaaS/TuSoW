package it.unibo.coordination.linda.core;

public interface InspectableNegatedTupleSpace<T extends Tuple, TT extends Template, K, V>
        extends NegatedTupleSpace<T, TT, K, V>, InspectableTupleSpace<T, TT, K, V> {

}