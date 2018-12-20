package it.unibo.coordination.linda.core;

public interface ExtendedTupleSpace<T extends Tuple, TT extends Template>
        extends BulkTupleSpace<T, TT>, PredicativeTupleSpace<T, TT>, NegatedTupleSpace<T, TT> {

}
