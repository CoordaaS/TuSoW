package it.unibo.coordination.utils;

class Tuple5Impl<A, B, C, D, E> extends Tuple4Impl<A, B, C, D> implements Tuple5<A, B, C, D, E> {

    public Tuple5Impl(A a, B b, C c, D d, E e) {
        super(a, b, c, d, e);
    }

    protected Tuple5Impl(Object... items) {
        super(items);
    }

    @Override
    public E getFifth() {
        return get(4);
    }
}
