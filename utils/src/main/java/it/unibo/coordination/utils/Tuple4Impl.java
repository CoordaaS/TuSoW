package it.unibo.coordination.utils;

class Tuple4Impl<A, B, C, D> extends Tuple3Impl<A, B, C> implements Tuple4<A, B, C, D> {

    public Tuple4Impl(A a, B b, C c, D d) {
        super(a, b, c, d);
    }

    protected Tuple4Impl(Object... items) {
        super(items);
    }

    @Override
    public D getFourth() {
        return get(3);
    }
}
