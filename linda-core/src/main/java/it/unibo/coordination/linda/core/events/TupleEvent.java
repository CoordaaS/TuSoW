package it.unibo.coordination.linda.core.events;

import it.unibo.coordination.linda.core.Template;
import it.unibo.coordination.linda.core.Tuple;
import it.unibo.coordination.linda.core.TupleSpace;

import java.util.Objects;

public class TupleEvent<T extends Tuple, TT extends Template> extends TupleSpaceEvent<T, TT> {

    public enum Effect { WRITTEN, READ, TAKEN, ABSENT };

    private final Effect effect;
    private final boolean before;
    private final T tuple;
    private final TT template;

    private TupleEvent(TupleSpace<T, TT> tupleSpace, boolean before, Effect effect, T tuple, TT template) {
        super(tupleSpace);
        this.before = before;
        this.effect = Objects.requireNonNull(effect);
        this.tuple = tuple;
        this.template = template;
    }

    public static <X extends Tuple, Y extends Template> TupleEvent<X, Y> beforeWriting(TupleSpace<X, Y> tupleSpace, X tuple) {
        return new TupleEvent<>(tupleSpace, true, Effect.WRITTEN, tuple, null);
    }

    public static <X extends Tuple, Y extends Template> TupleEvent<X, Y> afterWriting(TupleSpace<X, Y> tupleSpace, X tuple) {
        return new TupleEvent<>(tupleSpace, false, Effect.WRITTEN, tuple, null);
    }

    public static <X extends Tuple, Y extends Template> TupleEvent<X, Y> beforeTaking(TupleSpace<X, Y> tupleSpace, X tuple) {
        return new TupleEvent<>(tupleSpace, true, Effect.TAKEN, tuple, null);
    }

    public static <X extends Tuple, Y extends Template> TupleEvent<X, Y> afterTaking(TupleSpace<X, Y> tupleSpace, X tuple) {
        return new TupleEvent<>(tupleSpace, false, Effect.TAKEN, tuple, null);
    }

    public static <X extends Tuple, Y extends Template> TupleEvent<X, Y> beforeReading(TupleSpace<X, Y> tupleSpace, X tuple) {
        return new TupleEvent<>(tupleSpace, true, Effect.READ, tuple, null);
    }

    public static <X extends Tuple, Y extends Template> TupleEvent<X, Y> afterReading(TupleSpace<X, Y> tupleSpace, X tuple) {
        return new TupleEvent<>(tupleSpace, false, Effect.READ, tuple, null);
    }

    public static <X extends Tuple, Y extends Template> TupleEvent<X, Y> beforeAbsent(TupleSpace<X, Y> tupleSpace, Y template) {
        return new TupleEvent<>(tupleSpace, true, Effect.ABSENT, null, template);
    }

    public static <X extends Tuple, Y extends Template> TupleEvent<X, Y> afterAbsent(TupleSpace<X, Y> tupleSpace, Y template) {
        return new TupleEvent<>(tupleSpace, false, Effect.ABSENT, null, template);
    }

    public static <X extends Tuple, Y extends Template> TupleEvent<X, Y> beforeAbsent(TupleSpace<X, Y> tupleSpace, Y template, X counterexample) {
        return new TupleEvent<>(tupleSpace, true, Effect.ABSENT, counterexample, template);
    }

    public static <X extends Tuple, Y extends Template> TupleEvent<X, Y> afterAbsent(TupleSpace<X, Y> tupleSpace, Y template, X counterexample) {
        return new TupleEvent<>(tupleSpace, false, Effect.ABSENT, counterexample, template);
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

    public TT getTemplate() {
        return template;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TupleEvent<?, ?> that = (TupleEvent<?, ?>) o;
        return before == that.before &&
                Objects.equals(effect, that.effect) &&
                Objects.equals(tuple, that.tuple) &&
                Objects.equals(template, that.template);
    }

    @Override
    public String toString() {
        return "TupleEvent{" +
                "tupleSpace=" + getTupleSpaceName() +
                ", effect='" + effect + '\'' +
                ", before=" + before +
                ", tuple=" + tuple +
                ", template=" + template +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(effect, before, tuple, template);
    }
}
