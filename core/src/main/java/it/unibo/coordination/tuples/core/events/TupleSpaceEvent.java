package it.unibo.coordination.tuples.core.events;

import it.unibo.coordination.tuples.core.ExtendedTupleSpace;
import it.unibo.coordination.tuples.core.Template;
import it.unibo.coordination.tuples.core.Tuple;
import it.unibo.coordination.tuples.core.TupleSpace;

import java.util.Objects;

public class TupleSpaceEvent<T extends Tuple, TT extends Template> {

    private final TupleSpace<T, TT> tupleSpace;

    TupleSpaceEvent(TupleSpace<T, TT> tupleSpace) {
        this.tupleSpace = Objects.requireNonNull(tupleSpace);
    }

    public String getTupleSpaceName() {
        return tupleSpace.getName();
    }

    public TupleSpace<T, TT> getTupleSpace() {
        return tupleSpace;
    }

    public ExtendedTupleSpace<T, TT> getExtendedTupleSpace() {
        return (ExtendedTupleSpace<T, TT>) tupleSpace;
    }

    @Override
    public String toString() {
        return "TupleSpaceEvent{" +
                "tupleSpace=" + getTupleSpaceName() +
                '}';
    }
}
