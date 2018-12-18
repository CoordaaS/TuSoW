package it.unibo.coordination.tuples.core;

import it.unibo.coordination.tuples.core.events.TupleEvent;
import it.unibo.coordination.tuples.core.events.TupleSpaceEvent;
import it.unibo.coordination.utils.events.EventSource;

public interface InspectableTupleSpace<T extends Tuple, TT extends Template> extends TupleSpace<T, TT> {
    EventSource<TupleSpaceEvent<T, TT>> operationInvoked();

    EventSource<TupleSpaceEvent<T, TT>> operationCompleted();

    EventSource<TupleEvent<T, TT>> tupleSpaceChanged();
}
