package it.unibo.coordination.tusow.presentation;

import it.unibo.coordination.linda.core.Tuple;
import it.unibo.coordination.linda.logic.LogicTuple;

public interface TupleRepresentation extends Tuple, Representation {
    static TupleRepresentation wrap(Tuple tuple) {
        if (tuple instanceof TupleRepresentation) {
            return (TupleRepresentation) tuple;
        } else if (tuple instanceof LogicTuple) {
            return LogicTupleRepresentation.wrap((LogicTuple) tuple);
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
