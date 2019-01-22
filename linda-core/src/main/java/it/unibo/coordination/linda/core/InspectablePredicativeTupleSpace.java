package it.unibo.coordination.linda.core;

public interface InspectablePredicativeTupleSpace<T extends Tuple, TT extends Template, K, V>
        extends PredicativeTupleSpace<T, TT, K, V>, InspectableTupleSpace<T, TT, K, V> {

}
