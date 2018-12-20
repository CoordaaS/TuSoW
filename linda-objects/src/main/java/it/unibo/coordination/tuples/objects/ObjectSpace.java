package it.unibo.coordination.tuples.objects;

import it.unibo.coordination.linda.core.ExtendedTupleSpace;
import it.unibo.coordination.linda.core.InspectableTupleSpace;

public interface ObjectSpace<T> extends ExtendedTupleSpace<ObjectTuple<T>, ObjectTemplate<T>>, InspectableTupleSpace<ObjectTuple<T>, ObjectTemplate<T>> {

}
