package it.unibo.coordination.testing;

import java.util.Collection;
import java.util.concurrent.Future;
import java.util.function.Predicate;

public interface IConcurrentTestHelper {
    void await() throws Exception;

    void done();

    void fail(Exception t);

    void fail(String message, Exception t);

    void fail(String message);

    void fail();

    void success();

    void assertTrue(boolean condition);

    void assertTrue(boolean condition, String message);

    <T> void assertEquals(T actual, T expected, String message);

    <T> void assertEquals(T actual, T expected);

    <T> void assertEquals(Future<T> actualFuture, T expected);

    <T> void assertTrue(Future<T> actualFuture, Predicate<T> p);

    <T> void assertEquals(Future<T> actualFuture, T expected, String message);

    <T> void assertOneOf(Future<T> actualFuture, T expected1,
                         @SuppressWarnings("unchecked") T... expected);

    <T> void assertOneOf(Future<T> actualFuture, Collection<? extends T> expected);

    <T> void assertOneOf(Future<T> actualFuture, Collection<? extends T> expected, String message);

    void assertBlocksIndefinitely(Future<?> future, String message);

    void assertBlocksIndefinitely(Future<?> future);

    void assertEventuallyReturns(Future<?> future, String message);

    void assertEventuallyReturns(Future<?> future);

    @FunctionalInterface
    public interface ThrowableRunnable {
        void run() throws Exception;
    }
}
