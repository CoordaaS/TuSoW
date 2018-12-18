package it.unibo.coordination.tuples.objects;


import it.unibo.coordination.tuples.core.Template;
import it.unibo.coordination.tuples.core.Tuple;

import java.util.Optional;
import java.util.function.Predicate;

public final class ObjectTemplate<T> implements Template, Predicate<Tuple> {

    private final Class<T> type;
    private final Predicate<T> predicate;

    private ObjectTemplate(Class<T> type, Predicate<T> predicate) {
        this.type = type;
        this.predicate = x -> predicate.test((T) x);
    }

    public static <X> ObjectTemplate<X> anyOfType(Class<X> type, Predicate<X> predicate) {
        return new ObjectTemplate<>(type, predicate);
    }

    public static <X> ObjectTemplate<X> anyOfType(Class<X> type) {
        return new ObjectTemplate<>(type, x -> true);
    }

    @Override
    public Match matchWith(Tuple tuple) {
        return new ObjectMatch(tuple);
    }

    public ObjectTemplate<T> where(Predicate<T> predicate) {
        return new ObjectTemplate<>(type, this.predicate.and(predicate));
    }

    private class ObjectMatch implements Match {

        private final Tuple tuple;

        private ObjectMatch(Tuple property) {
            this.tuple = property;
        }


        @Override
        public boolean isSuccess() {
            if (tuple instanceof ObjectTuple) {
                final ObjectTuple<?> objectTuple = (ObjectTuple<?>) tuple;
                if (type.isAssignableFrom(objectTuple.get().getClass())) {
                    final ObjectTuple<T> objectTupleOfT = (ObjectTuple<T>) tuple;
                    return predicate.test(objectTupleOfT.get());
                }
            }
            return false;
        }

        @Override
        public <X> Optional<X> get(Object key) {
            return Optional.empty();
        }
    }
}
