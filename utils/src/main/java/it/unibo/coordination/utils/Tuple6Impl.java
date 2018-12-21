package it.unibo.coordination.utils;

class Tuple6Impl<A, B, C, D, E, F> extends Tuple5Impl<A, B, C, D, E> implements Tuple6<A, B, C, D, E, F> {

    public Tuple6Impl(A a, B b, C c, D d, E e) {
        super(a, b, c, d, e);
    }

    protected Tuple6Impl(Object... items) {
        super(items);
    }

    @Override
    public F getSixth() {
        return get(5);
    }
}
