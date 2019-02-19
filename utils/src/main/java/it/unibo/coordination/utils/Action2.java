package it.unibo.coordination.utils;

@FunctionalInterface
public interface Action2<T1, T2, E extends Exception> {
    void execute(T1 arg1, T2 arg2) throws E;
}