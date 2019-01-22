package it.unibo.coordination.linda.core;

public interface InspectableBulkTupleSpace<T extends Tuple, TT extends Template, K, V>
        extends BulkTupleSpace<T, TT, K, V>, InspectableTupleSpace<T, TT, K, V> {

}