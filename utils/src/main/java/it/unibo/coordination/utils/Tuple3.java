package it.unibo.coordination.utils;

public interface Tuple3<A, B, C> extends Tuple {
	A getFirst();
    B getSecond();
    C getThird();
}
