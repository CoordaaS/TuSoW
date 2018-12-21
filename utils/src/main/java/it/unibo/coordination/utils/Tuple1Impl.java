package it.unibo.coordination.utils;

class Tuple1Impl<A> extends TupleImpl implements Tuple1<A> {
    public Tuple1Impl(A a) {
        super(a);
    }

    protected Tuple1Impl(Object... items) {
        super(items);
    }

    public A getFirst() {
        return get(0);
    }
}
