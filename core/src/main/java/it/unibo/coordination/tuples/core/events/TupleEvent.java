package it.unibo.coordination.tuples.core.events;

import it.unibo.coordination.tuples.core.Template;
import it.unibo.coordination.tuples.core.Tuple;
import it.unibo.coordination.tuples.core.TupleSpace;

import java.util.Objects;

public class TupleEvent<T extends Tuple, TT extends Template> extends TupleSpaceEvent<T, TT> {

    private static final String EFFECT_WRITE = "write";
    private static final String EFFECT_TAKE = "take";

    private final String effect;
    private final boolean before;
    private final T tuple;

    private TupleEvent(TupleSpace<T, TT> tupleSpace, boolean before, String effect, T tuple) {
        super(tupleSpace);
        this.before = before;
        this.effect = Objects.requireNonNull(effect);
        this.tuple = Objects.requireNonNull(tuple);
    }

    public static <X extends Tuple, Y extends Template> TupleEvent<X, Y> beforeWriting(TupleSpace<X, Y> tupleSpace, X tuple) {
        return new TupleEvent<>(tupleSpace, true, EFFECT_WRITE, tuple);
    }

    public static <X extends Tuple, Y extends Template> TupleEvent<X, Y> afterWriting(TupleSpace<X, Y> tupleSpace, X tuple) {
        return new TupleEvent<>(tupleSpace, false, EFFECT_WRITE, tuple);
    }

    public static <X extends Tuple, Y extends Template> TupleEvent<X, Y> beforeTaking(TupleSpace<X, Y> tupleSpace, X tuple) {
        return new TupleEvent<>(tupleSpace, true, EFFECT_TAKE, tuple);
    }

    public static <X extends Tuple, Y extends Template> TupleEvent<X, Y> afterTaking(TupleSpace<X, Y> tupleSpace, X tuple) {
        return new TupleEvent<>(tupleSpace, false, EFFECT_TAKE, tuple);
    }

    public boolean isWritten() {
        return EFFECT_WRITE.equals(effect);
    }

    public boolean isTaken() {
        return EFFECT_TAKE.equals(effect);
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
    public String toString() {
        return "TupleEvent{" +
                "effect='" + effect + '\'' +
                ", before=" + before +
                ", tuple=" + tuple +
                '}';
    }
}
