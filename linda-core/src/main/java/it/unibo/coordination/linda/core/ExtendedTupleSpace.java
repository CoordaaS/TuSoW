package it.unibo.coordination.linda.core;

public interface ExtendedTupleSpace<T extends Tuple, TT extends Template, K, V>
        extends BulkTupleSpace<T, TT, K, V>,
        PredicativeTupleSpace<T, TT, K, V>,
        NegatedTupleSpace<T, TT, K, V>/*,
        ExoticTupleSpace<T, TT, K, V>*/ { }
