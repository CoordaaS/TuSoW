package it.unibo.coordination.utils;

public interface Tuple4<A, B, C, D> extends Tuple {
	A getFirst();
    B getSecond();
    C getThird();
    D getFourth();
}
