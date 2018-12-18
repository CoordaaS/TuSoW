package it.unibo.coordination.tuples.core;

import it.unibo.coordination.tuples.core.events.OperationEvent;
import it.unibo.coordination.tuples.core.events.TupleEvent;
import it.unibo.coordination.utils.events.EventSource;

public interface InspectableTupleSpace<T extends Tuple, TT extends Template> extends TupleSpace<T, TT> {
    EventSource<OperationEvent<T, TT>> operationInvoked();

    EventSource<OperationEvent<T, TT>> operationCompleted();

    EventSource<TupleEvent<T, TT>> tupleSpaceChanged();
}
