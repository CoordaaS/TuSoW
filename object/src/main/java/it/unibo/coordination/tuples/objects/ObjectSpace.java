package it.unibo.coordination.tuples.objects;

import it.unibo.coordination.tuples.core.ExtendedTupleSpace;
import it.unibo.coordination.tuples.core.InspectableTupleSpace;

public interface ObjectSpace<T> extends ExtendedTupleSpace<ObjectTuple<T>, ObjectTemplate<T>>, InspectableTupleSpace<ObjectTuple<T>, ObjectTemplate<T>> {

}
