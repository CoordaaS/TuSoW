package it.unibo.coordination.utils;

class Tuple3Impl<A, B, C> extends Tuple2Impl<A, B> implements Tuple3<A, B, C> {
    public Tuple3Impl(A a, B b, C c) {
        super(a, b, c);
    }

    protected Tuple3Impl(Object... items) {
        super(items);
    }

    public C getThird() {
        return get(2);
    }
}
