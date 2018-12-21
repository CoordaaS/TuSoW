package it.unibo.coordination.utils;

class Tuple2Impl<A, B> extends Tuple1Impl<A> implements Tuple2<A, B> {
    public Tuple2Impl(A a, B b) {
        super(a, b);
    }

    protected Tuple2Impl(Object... items) {
        super(items);
    }

    public B getSecond() {
        return get(1);
    }
}

