package it.unibo.coordination.linda.core;

public interface InspectableExtendedTupleSpace<T extends Tuple, TT extends Template, K, V>
        extends ExtendedTupleSpace<T, TT, K, V>, InspectableTupleSpace<T, TT, K, V> {

}