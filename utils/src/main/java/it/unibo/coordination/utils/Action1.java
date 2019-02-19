package it.unibo.coordination.utils;

@FunctionalInterface
public interface Action1<T, E extends Exception> {
    void execute(T arg) throws E;
}