package it.unibo.coordination.utils;

public interface Tuple {

    int getSize();

    <X> X get(int i);

    Object[] toArray();

    static <A> Tuple1<A> of(A value) {
        return new Tuple1Impl<>(value);
    }

    static <A, B> Tuple2<A, B> of(A value1, B value2) {
        return new Tuple2Impl<>(value1, value2);
    }

    static <A, B, C> Tuple3<A, B, C> of(A value1, B value2, C value3) {
        return new Tuple3Impl<>(value1, value2, value3);
    }

    static <A, B, C, D> Tuple4<A, B, C, D> of(A value1, B value2, C value3, D value4) {
        return new Tuple4Impl<>(value1, value2, value3, value4);
    }

    static <A, B, C, D, E> Tuple5<A, B, C, D, E> of(A value1, B value2, C value3, D value4, E value5) {
        return new Tuple5Impl<>(value1, value2, value3, value4, value5);
    }

    static <A, B, C, D, E, F> Tuple6<A, B, C, D, E, F> of(A value1, B value2, C value3, D value4, E value5,  F value6) {
        return new Tuple6Impl<>(value1, value2, value3, value4, value5, value6);
    }

    static Tuple of(Object... args) {
        return new TupleImpl(args);
    }
}
