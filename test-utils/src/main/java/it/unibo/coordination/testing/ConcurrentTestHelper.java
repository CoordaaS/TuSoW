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

public class ConcurrentTestHelper {

    private static final Duration BLOCKING_THRESHOLD = Duration.ofSeconds(3);
    private static final Duration GET_THRESHOLD = Duration.ofSeconds(2);

    @FunctionalInterface
    public interface ThrowableRunnable {
        void run() throws Exception;
    }

    private final List<ThrowableRunnable> toDoList = Collections.synchronizedList(new ArrayList<>());
    private CountDownLatch latch;

    public void setThreadCount(final int n) {
        this.latch = new CountDownLatch(n);
    }

    public void await() throws Exception {
        latch.await();
        for (final ThrowableRunnable throwableRunnable : toDoList) {
            throwableRunnable.run();
        }
    }

    public void done() {
        latch.countDown();
    }

    public void fail(final Exception t) {
        toDoList.add(() -> {
            throw new AssertionError(t);
        });
    }

    public void fail(final String message, final Exception t) {
        toDoList.add(() -> {
            throw new AssertionError(message, t);
        });
    }

    public void fail(final String message) {
        toDoList.add(() -> Assert.fail(message));
    }

    public void fail() {
        toDoList.add(() -> Assert.fail());

    }

    public void success() {
        toDoList.add(() -> Assert.assertTrue(true));
    }

    public void assertTrue(final boolean condition) {
        toDoList.add(() -> Assert.assertTrue(condition));
    }

    public void assertTrue(final boolean condition, final String message) {
        toDoList.add(() -> Assert.assertTrue(message, condition));
    }

    public <T> void assertEquals(final T actual, final T expected, final String message) {
        assertTrue(expected.equals(actual), message);
    }

    public <T> void assertEquals(final T actual, final T expected) {
        assertTrue(expected.equals(actual),
                String.format("Failed assertion: %s must be equals to %s", actual, expected));
    }

    public <T> void assertEquals(final Future<T> actualFuture, final T expected) {
        try {
            final T actual = actualFuture.get(GET_THRESHOLD.toMillis(), TimeUnit.MILLISECONDS);
            assertEquals(actual, expected);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            fail(e);
        }
    }

    public <T> void assertTrue(final Future<T> actualFuture, final Predicate<T> p) {
        try {
            final T actual = actualFuture.get(GET_THRESHOLD.toMillis(), TimeUnit.MILLISECONDS);
            assertTrue(p.test(actual));
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            fail(e);
        }
    }

    public <T> void assertEquals(final Future<T> actualFuture, final T expected, final String message) {
        try {
            final T actual = actualFuture.get(GET_THRESHOLD.toMillis(), TimeUnit.MILLISECONDS);
            assertEquals(actual, expected, message);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            fail(message, e);
        }
    }

    public <T> void assertOneOf(final Future<T> actualFuture, final T expected1,
            @SuppressWarnings("unchecked") final T... expected) {
        assertOneOf(actualFuture, Stream.concat(Stream.of(expected1), Stream.of(expected)).collect(Collectors.toSet()), null);
    }

    public <T> void assertOneOf(final Future<T> actualFuture, final Collection<? extends T> expected) {
        assertOneOf(actualFuture, expected, null);
    }

    public <T> void assertOneOf(final Future<T> actualFuture, final Collection<? extends T> expected, String message) {
        try {
            final T actual = actualFuture.get(GET_THRESHOLD.toMillis(), TimeUnit.MILLISECONDS);
            if (message == null)
                assertTrue(expected.contains(actual));
            else
                assertTrue(expected.contains(actual), message);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            fail(e);
        }
    }

    public void assertBlocksIndefinitely(final Future<?> future, final String message) {
        try {
            future.get(BLOCKING_THRESHOLD.toMillis(), TimeUnit.MILLISECONDS);
            fail(message);
        } catch (InterruptedException | ExecutionException e) {
            fail(e);
        } catch (final TimeoutException e) {
            success();
        }
    }

    public void assertBlocksIndefinitely(final Future<?> future) {
        try {
            future.get(BLOCKING_THRESHOLD.toMillis(), TimeUnit.MILLISECONDS);
            fail();
        } catch (InterruptedException | ExecutionException e) {
            fail(e);
        } catch (final TimeoutException e) {
            success();
        }
    }

    public void assertEventuallyReturns(final Future<?> future, final String message) {
        try {
            future.get(BLOCKING_THRESHOLD.toMillis(), TimeUnit.MILLISECONDS);
            success();
        } catch (InterruptedException | ExecutionException e) {
            fail(e);
        } catch (final TimeoutException e) {
            fail(message);
        }
    }

    public void assertEventuallyReturns(final Future<?> future) {
        try {
            future.get(BLOCKING_THRESHOLD.toMillis(), TimeUnit.MILLISECONDS);
            success();
        } catch (InterruptedException | ExecutionException e) {
            fail(e);
        } catch (final TimeoutException e) {
            fail();
        }
    }

}
