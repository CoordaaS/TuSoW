package it.unibo.coordination.testing;

import org.junit.Assert;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConcurrentTestHelper implements IConcurrentTestHelper {

    private static final Duration BLOCKING_THRESHOLD = Duration.ofSeconds(3);
    private static final Duration GET_THRESHOLD = Duration.ofSeconds(2);

    private final List<ThrowableRunnable> toDoList = Collections.synchronizedList(new ArrayList<>());
    private CountDownLatch latch;

    public void setThreadCount(final int n) {
        this.latch = new CountDownLatch(n);
    }

    @Override
    public void await() throws Exception {
        latch.await();
        for (final ThrowableRunnable throwableRunnable : toDoList) {
            throwableRunnable.run();
        }
    }

    @Override
    public void done() {
        latch.countDown();
    }

    @Override
    public void fail(final Exception t) {
        propagateExceptions(() -> {
            throw new AssertionError(t);
        });
    }

    @Override
    public void fail(final String message, final Exception t) {
        propagateExceptions(() -> {
            throw new AssertionError(message, t);
        });
    }

    @Override
    public void fail(final String message) {
        propagateExceptions(() -> Assert.fail(message));
    }

    @Override
    public void fail() {
        propagateExceptions(() -> Assert.fail());
    }

    @Override
    public void success() {
        propagateExceptions(() -> Assert.assertTrue(true));
    }

    @Override
    public void assertTrue(final boolean condition) {
        propagateExceptions(() -> Assert.assertTrue(condition));
    }

    @Override
    public void assertTrue(final boolean condition, final String message) {
        propagateExceptions(() -> Assert.assertTrue(message, condition));
    }

    private void propagateExceptions(ThrowableRunnable action) {
        try {
            action.run();
        } catch (Throwable e) {
            toDoList.add(() -> {
                throw e;
            });
            if (e instanceof RuntimeException) {
                throw ((RuntimeException) e);
            } else if (e instanceof Error) {
                throw ((Error) e);
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public <T> void assertEquals(final T actual, final T expected, final String message) {
        assertTrue(expected.equals(actual), message);
    }

    @Override
    public <T> void assertEquals(final T actual, final T expected) {
        assertTrue(expected.equals(actual),
                String.format("Failed assertion: %s must be equals to %s", actual, expected));
    }

    @Override
    public <T> void assertEquals(final Future<T> actualFuture, final T expected) {
        try {
            final T actual = actualFuture.get(GET_THRESHOLD.toMillis(), TimeUnit.MILLISECONDS);
            assertEquals(actual, expected, String.format("Expected %s, found %s", expected, actual));
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            fail(e);
        }
    }

    @Override
    public <T> void assertTrue(final Future<T> actualFuture, final Predicate<T> p) {
        try {
            final T actual = actualFuture.get(GET_THRESHOLD.toMillis(), TimeUnit.MILLISECONDS);
            assertTrue(p.test(actual), "Expected true, got false instead");
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            fail(e);
        }
    }

    @Override
    public <T> void assertEquals(final Future<T> actualFuture, final T expected, final String message) {
        try {
            final T actual = actualFuture.get(GET_THRESHOLD.toMillis(), TimeUnit.MILLISECONDS);
            assertEquals(actual, expected, message);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            fail(message, e);
        }
    }

    @Override
    public <T> void assertOneOf(final Future<T> actualFuture, final T expected1,
                                @SuppressWarnings("unchecked") final T... expected) {
        assertOneOf(actualFuture, Stream.concat(Stream.of(expected1), Stream.of(expected)).collect(Collectors.toSet()));
    }

    @Override
    public <T> void assertOneOf(final Future<T> actualFuture, final Collection<? extends T> expected) {
        assertOneOf(actualFuture, expected, null);
    }

    @Override
    public <T> void assertOneOf(final Future<T> actualFuture, final Collection<? extends T> expected, String message) {
        try {
            final T actual = actualFuture.get(GET_THRESHOLD.toMillis(), TimeUnit.MILLISECONDS);
            if (message == null)
                assertTrue(expected.contains(actual), String.format("Expecting %s is one of %s, but it is not", actual, expected));
            else
                assertTrue(expected.contains(actual), message);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            fail(e);
        }
    }

    @Override
    public void assertBlocksIndefinitely(final Future<?> future, final String message) {
        try {
            future.get(BLOCKING_THRESHOLD.toMillis(), TimeUnit.MILLISECONDS);
            fail(message == null ? "Async. operation terminated while it was expected to block indefinitely" : message);
        } catch (InterruptedException | ExecutionException e) {
            fail(e);
        } catch (final TimeoutException e) {
            success();
        }
    }

    @Override
    public void assertBlocksIndefinitely(final Future<?> future) {
        assertBlocksIndefinitely(future, null);
    }

    @Override
    public void assertEventuallyReturns(final Future<?> future, final String message) {
        try {
            future.get(BLOCKING_THRESHOLD.toMillis(), TimeUnit.MILLISECONDS);
            success();
        } catch (InterruptedException | ExecutionException e) {
            fail(e);
        } catch (final TimeoutException e) {
            fail(message == null ? "Async. operation should have returned a value in time, but it did not" : message);
        }
    }

    @Override
    public void assertEventuallyReturns(final Future<?> future) {
        assertEventuallyReturns(future, null);
    }

}
