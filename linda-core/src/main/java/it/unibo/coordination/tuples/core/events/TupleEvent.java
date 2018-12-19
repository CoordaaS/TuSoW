package it.unibo.coordination.tuples.core.events;

import it.unibo.coordination.tuples.core.Template;
import it.unibo.coordination.tuples.core.Tuple;
import it.unibo.coordination.tuples.core.TupleSpace;

import java.util.Objects;

public class TupleEvent<T extends Tuple, TT extends Template> extends TupleSpaceEvent<T, TT> {

    public enum Effect { WRITTEN, READ, TAKEN, ABSENT };

    private final Effect effect;
    private final boolean before;
    private final T tuple;

    private TupleEvent(TupleSpace<T, TT> tupleSpace, boolean before, Effect effect, T tuple) {
        super(tupleSpace);
        this.before = before;
        this.effect = Objects.requireNonNull(effect);
        this.tuple = Objects.requireNonNull(tuple);
    }

    public static <X extends Tuple, Y extends Template> TupleEvent<X, Y> beforeWriting(TupleSpace<X, Y> tupleSpace, X tuple) {
        return new TupleEvent<>(tupleSpace, true, Effect.WRITTEN, tuple);
    }

    public static <X extends Tuple, Y extends Template> TupleEvent<X, Y> afterWriting(TupleSpace<X, Y> tupleSpace, X tuple) {
        return new TupleEvent<>(tupleSpace, false, Effect.WRITTEN, tuple);
    }

    public static <X extends Tuple, Y extends Template> TupleEvent<X, Y> beforeTaking(TupleSpace<X, Y> tupleSpace, X tuple) {
        return new TupleEvent<>(tupleSpace, true, Effect.TAKEN, tuple);
    }

    public static <X extends Tuple, Y extends Template> TupleEvent<X, Y> afterTaking(TupleSpace<X, Y> tupleSpace, X tuple) {
        return new TupleEvent<>(tupleSpace, false, Effect.TAKEN, tuple);
    }

    public static <X extends Tuple, Y extends Template> TupleEvent<X, Y> beforeReading(TupleSpace<X, Y> tupleSpace, X tuple) {
        return new TupleEvent<>(tupleSpace, true, Effect.READ, tuple);
    }

    public static <X extends Tuple, Y extends Template> TupleEvent<X, Y> afterReading(TupleSpace<X, Y> tupleSpace, X tuple) {
        return new TupleEvent<>(tupleSpace, false, Effect.READ, tuple);
    }

    public static <X extends Tuple, Y extends Template> TupleEvent<X, Y> beforeAbsent(TupleSpace<X, Y> tupleSpace, X tuple) {
        return new TupleEvent<>(tupleSpace, true, Effect.ABSENT, tuple);
    }

    public static <X extends Tuple, Y extends Template> TupleEvent<X, Y> afterAbsent(TupleSpace<X, Y> tupleSpace, X tuple) {
        return new TupleEvent<>(tupleSpace, false, Effect.ABSENT, tuple);
    }

    public Effect getEffect() {
        return effect;
    }

    public boolean isBefore() {
        return before;
    }

    public boolean isAfter() {
        return !before;
    }

    public T getTuple() {
        return tuple;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TupleEvent<?, ?> that = (TupleEvent<?, ?>) o;
        return before == that.before &&
                Objects.equals(effect, that.effect) &&
                Objects.equals(tuple, that.tuple);
    }

    @Override
    public String toString() {
        return "TupleEvent{" +
                "tupleSpace=" + getTupleSpaceName() +
                ", effect='" + effect + '\'' +
                ", before=" + before +
                ", tuple=" + tuple +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(effect, before, tuple);
    }
}
