package it.unibo.sd1819.test;

import org.junit.Assert;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ConcurrentTestHelper {

    private static final Duration BLOCKING_THRESHOLD = Duration.ofSeconds(2);
    private static final Duration GET_THRESHOLD = Duration.ofSeconds(2);
    private static final Duration MAX_WAIT_THRESHOLD = Duration.ofSeconds(3);
    private final List<ThrowableRunnable> toDoList = Collections.synchronizedList(new ArrayList<>());
    private CountDownLatch latch;

    public void setThreadCount(int n) {
        this.latch = new CountDownLatch(n);
    }

    public void await() throws Exception {
        if (!latch.await(MAX_WAIT_THRESHOLD.toMillis(), TimeUnit.MILLISECONDS)) {
            throw new TimeoutException();
        }
        for (ThrowableRunnable throwableRunnable : toDoList) {
            throwableRunnable.run();
        }
    }

    public void done() {
        latch.countDown();
    }

    public void fail(Exception t) {
        toDoList.add(() -> {
            throw new AssertionError(t);
        });
    }

    public void fail(String message, Exception t) {
        toDoList.add(() -> {
            throw new AssertionError(message, t);
        });
    }

    public void fail(String message) {
        toDoList.add(() -> Assert.fail(message));
    }

    public void fail() {
        toDoList.add(() -> Assert.fail());

    }

    public void success() {
        toDoList.add(() -> Assert.assertTrue(true));
    }

    public void assertTrue(boolean condition) {
        toDoList.add(() -> Assert.assertTrue(condition));
    }

    public void assertTrue(boolean condition, String message) {
        toDoList.add(() -> Assert.assertTrue(message, condition));
    }

    public void assertEquals(Object actual, Object expected, String message) {
        assertTrue(expected.equals(actual), message);
    }

    public void assertEquals(Object actual, Object expected) {
        assertTrue(expected.equals(actual), String.format("Failed assertion: %s must be equals to %s", actual, expected));
    }

    public <T> void assertEquals(Future<T> actualFuture, T expected) {
        try {
            final T actual = actualFuture.get(GET_THRESHOLD.toMillis(), TimeUnit.MILLISECONDS);
            assertEquals(actual, expected);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            fail(e);
        }
    }

    public <T> void assertEquals(Future<Collection<? extends T>> actualFuture, Collection<? extends T> expected) {
        try {
            final Collection<? extends T> actual = actualFuture.get(GET_THRESHOLD.toMillis(), TimeUnit.MILLISECONDS);
            final List<T> actualList = actual.stream().sorted().collect(Collectors.toList());
            final List<T> expectedList = expected.stream().sorted().collect(Collectors.toList());
            assertEquals(actualList, expectedList);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            fail(e);
        }
    }

    public <T> void assertEquals(Future<Set<? extends T>> actualFuture, Set<? extends T> expected) {
        try {
            final Set<? extends T> actual = actualFuture.get(GET_THRESHOLD.toMillis(), TimeUnit.MILLISECONDS);
            assertEquals(actual, expected);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            fail(e);
        }
    }

    public <T> void assertEquals(Future<List<? extends T>> actualFuture, List<? extends T> expected) {
        try {
            final List<? extends T> actual = actualFuture.get(GET_THRESHOLD.toMillis(), TimeUnit.MILLISECONDS);
            assertEquals(actual, expected);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            fail(e);
        }
    }

    public <T> void assertTrue(Future<T> actualFuture, Predicate<T> p) {
        try {
            final T actual = actualFuture.get(GET_THRESHOLD.toMillis(), TimeUnit.MILLISECONDS);
            assertTrue(p.test(actual));
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            fail(e);
        }
    }

    public <T> void assertEquals(Future<T> actualFuture, T expected, String message) {
        try {
            final T actual = actualFuture.get(GET_THRESHOLD.toMillis(), TimeUnit.MILLISECONDS);
            assertEquals(actual, expected, message);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            fail(e);
        }
    }

    public <T> void assertOneOf(Future<T> actualFuture, T expected1, @SuppressWarnings("unchecked") T... expected) {
        try {
            final T actual = actualFuture.get(GET_THRESHOLD.toMillis(), TimeUnit.MILLISECONDS);
            final Set<T> set = new HashSet<>(Arrays.asList(expected));
            set.add(expected1);
            assertTrue(set.contains(actual));
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            fail(e);
        }
    }

    public void assertBlocksIndefinitely(Future<?> future, String message) {
        try {
            future.get(BLOCKING_THRESHOLD.toMillis(), TimeUnit.MILLISECONDS);
            fail(message);
        } catch (InterruptedException | ExecutionException e) {
            fail(e);
        } catch (TimeoutException e) {
            success();
        }
    }

    public void assertBlocksIndefinitely(Future<?> future) {
        try {
            future.get(BLOCKING_THRESHOLD.toMillis(), TimeUnit.MILLISECONDS);
            fail();
        } catch (InterruptedException | ExecutionException e) {
            fail(e);
        } catch (TimeoutException e) {
            success();
        }
    }

    public void assertEventuallyReturns(Future<?> future, String message) {
        try {
            future.get(BLOCKING_THRESHOLD.toMillis(), TimeUnit.MILLISECONDS);
            success();
        } catch (InterruptedException | ExecutionException e) {
            fail(e);
        } catch (TimeoutException e) {
            fail(message);
        }
    }

    public void assertEventuallyReturns(Future<?> future) {
        try {
            future.get(BLOCKING_THRESHOLD.toMillis(), TimeUnit.MILLISECONDS);
            success();
        } catch (InterruptedException | ExecutionException e) {
            fail(e);
        } catch (TimeoutException e) {
            fail();
        }
    }

    @FunctionalInterface
    public interface ThrowableRunnable {
        void run() throws Exception;
    }

}
