package it.unibo.coordination.utils;

@FunctionalInterface
public interface Action3<T1, T2, T3, E extends Exception> {
    void execute(T1 arg1, T2 arg2, T3 arg3) throws E;
}