package it.unibo.coordination.tuples.objects;

import it.unibo.coordination.linda.core.Template;
import it.unibo.coordination.linda.core.Tuple;

import java.util.Map;
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
    public ObjectMatch<T> matchWith(Tuple tuple) {
        return new ObjectMatchImpl(tuple);
    }

    public ObjectTemplate<T> where(Predicate<T> predicate) {
        return new ObjectTemplate<>(type, this.predicate.and(predicate));
    }

    private class ObjectMatchImpl implements ObjectMatch<T> {

        private final Tuple tuple;

        private ObjectMatchImpl(Tuple property) {
            this.tuple = property;
        }


        @Override
        public Optional<ObjectTuple<T>> getTuple() {
            return tuple instanceof ObjectTuple ? Optional.of((ObjectTuple<T>)tuple) : Optional.empty();
        }

        @Override
        public ObjectTemplate<T> getTemplate() {
            return ObjectTemplate.this;
        }

        @Override
        public boolean isMatching() {
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
        public Optional<Object> get(Object key) {
            return Optional.empty();
        }

        @Override
        public Map<Object, Object> toMap() {
            return Map.of();
        }

    }
}
