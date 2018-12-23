package it.unibo.coordination.linda.core.events;

import it.unibo.coordination.linda.core.ExtendedTupleSpace;
import it.unibo.coordination.linda.core.Template;
import it.unibo.coordination.linda.core.Tuple;
import it.unibo.coordination.linda.core.TupleSpace;

import java.util.Objects;

public class TupleSpaceEvent<T extends Tuple, TT extends Template> {

    private final TupleSpace<T, TT, ?, ?> tupleSpace;

    TupleSpaceEvent(TupleSpace<T, TT, ?, ?> tupleSpace) {
        this.tupleSpace = Objects.requireNonNull(tupleSpace);
    }

    public String getTupleSpaceName() {
        return tupleSpace.getName();
    }

    public TupleSpace<T, TT, ?, ?> getTupleSpace() {
        return tupleSpace;
    }

    public ExtendedTupleSpace<T, TT, ?, ?> getExtendedTupleSpace() {
        return (ExtendedTupleSpace<T, TT, ?, ?>) tupleSpace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TupleSpaceEvent<?, ?> that = (TupleSpaceEvent<?, ?>) o;
        return Objects.equals(tupleSpace, that.tupleSpace);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tupleSpace);
    }

    @Override
    public String toString() {
        return "TupleSpaceEvent{" +
                "tupleSpace=" + getTupleSpaceName() +
                '}';
    }
}
