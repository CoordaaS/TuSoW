package it.unibo.coordination.utils;

public interface Tuple5<A, B, C, D, E> extends Tuple {
	A getFirst();
    B getSecond();
    C getThird();
    D getFourth();
    E getFifth();
}
