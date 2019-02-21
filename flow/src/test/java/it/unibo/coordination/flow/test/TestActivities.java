package it.unibo.coordination.flow.test;

import it.unibo.coordination.flow.Activities;
import it.unibo.coordination.flow.Activity;
import it.unibo.coordination.flow.Continuation;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class TestActivities {

    @Test
    public void testSyncActivity() throws ExecutionException, InterruptedException {
        var r = Activities.runSynchronously(1, new Activity<Integer, Integer, Integer>() {
            int start;

            @Override
            public Continuation<Integer> onBegin(Integer argument) throws Exception {
                start = argument;
                return Continuation.next(0);
            }

            @Override
            public Continuation<Integer> onStep(Integer i) throws Exception {
                if ( i < 10) {
                    return Continuation.next(i + 1);
                } else {
                    return Continuation.stop(i);
                }
            }

            @Override
            public Integer onEnd(Integer context, Optional<Exception> exception) throws Exception {
                return context + start;
            }
        });

        Assert.assertTrue(r.isDone());

        Assert.assertEquals(Integer.valueOf(11), r.get());
    }

    @Test
    public void testActivityInBackground() throws ExecutionException, InterruptedException {

        final var semaphore = new Semaphore(0);

        var r = Activities.runInBackground(1, new Activity<Integer, Integer, Integer>() {
            int start;

            @Override
            public Continuation<Integer> onBegin(Integer argument) throws Exception {
                semaphore.acquire();
                start = argument;
                return Continuation.next(0);
            }

            @Override
            public Continuation<Integer> onStep(Integer i) throws Exception {
                if ( i < 10) {
                    return Continuation.next(i + 1);
                } else {
                    return Continuation.stop(i);
                }
            }

            @Override
            public Integer onEnd(Integer context, Optional<Exception> exception) throws Exception {
                return context + start;
            }
        });

        Assert.assertFalse(r.isDone());

        semaphore.release();

        Assert.assertEquals(Integer.valueOf(11), r.get());
    }

    @Test
    public void testActivityOnExecutor() throws ExecutionException, InterruptedException {
        final var executor = Executors.newSingleThreadScheduledExecutor();

        final var semaphore = new Semaphore(0);

        var r = Activities.runOnExecutor(executor, 1, new Activity<Integer, Integer, Integer>() {
            int start;

            @Override
            public Continuation<Integer> onBegin(Integer argument) throws Exception {
                semaphore.acquire();
                start = argument;
                return Continuation.next(0);
            }

            @Override
            public Continuation<Integer> onStep(Integer i) throws Exception {
                if ( i < 10) {
                    return Continuation.next(i + 1);
                } else {
                    return Continuation.stop(i);
                }
            }

            @Override
            public Integer onEnd(Integer context, Optional<Exception> exception) throws Exception {
                return context + start;
            }
        });

        Assert.assertFalse(r.isDone());

        semaphore.release();

        Assert.assertEquals(Integer.valueOf(11), r.get());

        executor.shutdown();

        executor.awaitTermination(1, TimeUnit.SECONDS);
    }
}
