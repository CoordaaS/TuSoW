package it.unibo.coordination.testing;

import java.util.Collection;
import java.util.concurrent.Future;
import java.util.function.Predicate;

public abstract class TestAgent extends ActiveObject<TestAgent> implements IConcurrentTestHelper {
    private final IConcurrentTestHelper test;

    public TestAgent(String name, ConcurrentTestHelper test) {
        super(name);
        this.test = test;
    }

    public TestAgent(ConcurrentTestHelper test) {
        this.test = test;
    }

    @Override
    protected void onEnd() {
        test.done();
    }

    @Override
    protected void onUncaughtException(Exception e) {
        // silently ignores
    }

    protected abstract void main() throws Exception;

    @Override
    protected void loop() throws Exception {
        main();
        stop();
    }

    @Override
    public void await() throws Exception {
        test.await();
    }

    public void done() {
        test.done();
    }

    public void fail(Exception t) {
        test.fail(t);
    }

    public void fail(String message, Exception t) {
        test.fail(message, t);
    }

    public void fail(String message) {
        test.fail(message);
    }

    public void fail() {
        test.fail();
    }

    public void success() {
        test.success();
    }

    public void assertTrue(boolean condition) {
        test.assertTrue(condition);
    }

    public void assertTrue(boolean condition, String message) {
        test.assertTrue(condition, message);
    }

    public <T> void assertEquals(T actual, T expected, String message) {
        test.assertEquals(actual, expected, message);
    }

    public <T> void assertEquals(T actual, T expected) {
        test.assertEquals(actual, expected);
    }

    public <T> void assertEquals(Future<T> actualFuture, T expected) {
        test.assertEquals(actualFuture, expected);
    }

    public <T> void assertTrue(Future<T> actualFuture, Predicate<T> p) {
        test.assertTrue(actualFuture, p);
    }

    public <T> void assertEquals(Future<T> actualFuture, T expected, String message) {
        test.assertEquals(actualFuture, expected, message);
    }

    public <T> void assertOneOf(Future<T> actualFuture, T expected1, T... expected) {
        test.assertOneOf(actualFuture, expected1, expected);
    }

    public <T> void assertOneOf(Future<T> actualFuture, Collection<? extends T> expected) {
        test.assertOneOf(actualFuture, expected);
    }

    public <T> void assertOneOf(Future<T> actualFuture, Collection<? extends T> expected, String message) {
        test.assertOneOf(actualFuture, expected, message);
    }

    public void assertBlocksIndefinitely(Future<?> future, String message) {
        test.assertBlocksIndefinitely(future, message);
    }

    public void assertBlocksIndefinitely(Future<?> future) {
        test.assertBlocksIndefinitely(future);
    }

    public void assertEventuallyReturns(Future<?> future, String message) {
        test.assertEventuallyReturns(future, message);
    }

    public void assertEventuallyReturns(Future<?> future) {
        test.assertEventuallyReturns(future);
    }
}
