package it.unibo.coordination.linda.logic;

import it.unibo.coordination.linda.core.OperationType;
import it.unibo.coordination.linda.core.events.OperationEvent;
import it.unibo.coordination.linda.core.events.TupleEvent;
import it.unibo.coordination.linda.core.events.TupleSpaceEvent;
import it.unibo.coordination.testing.ConcurrentTestHelper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.*;

public class TestLogicSpaceInspectability {

    protected ExecutorService executor;
    protected InspectableLogicSpace tupleSpace;
    protected ConcurrentTestHelper test;
    protected Random rand;

    private static final Duration MAX_WAIT = Duration.ofSeconds(2);

    private static <T> T await(Future<T> future) throws InterruptedException, ExecutionException, TimeoutException {
        return future.get(MAX_WAIT.toMillis(), TimeUnit.MILLISECONDS);
    }

    private static boolean await(ExecutorService engine) throws InterruptedException, ExecutionException, TimeoutException {
        return engine.awaitTermination(MAX_WAIT.toMillis(), TimeUnit.MILLISECONDS);
    }

    @Before
    public void setUp() throws Exception {
        executor = Executors.newCachedThreadPool();
        tupleSpace = InspectableLogicSpace.create(executor);
        test = new ConcurrentTestHelper();
        rand = new Random();
    }

    @After
    public void tearDown() throws Exception {
        executor.shutdown();
    }

    @Test
    public void testReadInvocation() throws Exception {
        final List<TupleSpaceEvent<LogicTuple, LogicTemplate>> observableBehaviour = new LinkedList<>();
        final var expectedEvent = OperationEvent.templateAcceptingInvocation(tupleSpace, OperationType.READ, LogicTemplate.of("f(X)"));

        tupleSpace.operationInvoked().bind(observableBehaviour::add);
        tupleSpace.tupleSpaceChanged().bind(observableBehaviour::add);
        tupleSpace.operationCompleted().bind(observableBehaviour::add);

        try {
            await(tupleSpace.readTuple("f(X)"));
            Assert.fail();
        } catch (TimeoutException e) {
            Assert.assertEquals(1, observableBehaviour.size());
            Assert.assertEquals(
                    List.of(expectedEvent),
                    observableBehaviour
            );
        }

    }


    @Test
    public void testTakeInvocation() throws Exception {
        final List<TupleSpaceEvent<LogicTuple, LogicTemplate>> observableBehaviour = new LinkedList<>();
        final var expectedEvent = OperationEvent.templateAcceptingInvocation(tupleSpace, OperationType.TAKE, LogicTemplate.of("f(X)"));

        tupleSpace.operationInvoked().bind(observableBehaviour::add);
        tupleSpace.tupleSpaceChanged().bind(observableBehaviour::add);
        tupleSpace.operationCompleted().bind(observableBehaviour::add);

        try {
            await(tupleSpace.takeTuple("f(X)"));
            Assert.fail();
        } catch (TimeoutException e) {
            Assert.assertEquals(1, observableBehaviour.size());
            Assert.assertEquals(
                    List.of(expectedEvent),
                    observableBehaviour
            );
        }
    }


    @Test
    public void testWrite() throws Exception {
        final List<TupleSpaceEvent<LogicTuple, LogicTemplate>> observableBehaviour = new LinkedList<>();

        final var expectedEvent1 = OperationEvent.tupleAcceptingInvocation(tupleSpace, OperationType.WRITE, LogicTuple.of("s(z)"));
        final var expectedEvent2 = TupleEvent.afterWriting(tupleSpace, LogicTuple.of("s(z)"));
        final var expectedEvent3 = expectedEvent1.toTupleReturningCompletion(LogicTuple.of("s(z)"));


        tupleSpace.operationInvoked().bind(observableBehaviour::add);
        tupleSpace.tupleSpaceChanged().bind(observableBehaviour::add);
        tupleSpace.operationCompleted().bind(observableBehaviour::add);

        await(tupleSpace.write("s(z)"));

        await(executor);

        Assert.assertEquals(3, observableBehaviour.size());
        Assert.assertEquals(
                List.of(expectedEvent1, expectedEvent2, expectedEvent3),
                observableBehaviour
        );
    }


    @Test
    public void testReadCompletion1() throws Exception {
        final List<TupleSpaceEvent<LogicTuple, LogicTemplate>> observableBehaviour = new LinkedList<>();

        final var expectedEvent1 = OperationEvent.tupleAcceptingInvocation(tupleSpace, OperationType.WRITE, LogicTuple.of("f(1)"));
        final var expectedEvent2 = TupleEvent.afterWriting(tupleSpace, LogicTuple.of("f(1)"));
        final var expectedEvent3 = expectedEvent1.toTupleReturningCompletion(LogicTuple.of("f(1)"));
        final var expectedEvent4 = OperationEvent.templateAcceptingInvocation(tupleSpace, OperationType.READ, LogicTemplate.of("f(X)"));
        final var expectedEvent5 = TupleEvent.afterReading(tupleSpace, LogicTuple.of("f(1)"));
        final var expectedEvent6 = expectedEvent4.toTupleReturningCompletion(LogicTuple.of("f(1)"));

        tupleSpace.operationInvoked().bind(observableBehaviour::add);
        tupleSpace.tupleSpaceChanged().bind(observableBehaviour::add);
        tupleSpace.operationCompleted().bind(observableBehaviour::add);

        await(tupleSpace.write("f(1)"));
        await(tupleSpace.readTuple("f(X)"));

        Assert.assertEquals(6, observableBehaviour.size());
        Assert.assertEquals(
                List.of(expectedEvent1, expectedEvent2, expectedEvent3, expectedEvent4, expectedEvent5, expectedEvent6),
                observableBehaviour
        );

    }


    @Test
    public void testReadCompletion2() throws Exception {
        final List<TupleSpaceEvent<LogicTuple, LogicTemplate>> observableBehaviour = new LinkedList<>();

        final var expectedEvent1 = OperationEvent.templateAcceptingInvocation(tupleSpace, OperationType.READ, LogicTemplate.of("f(X)"));
        final var expectedEvent2 = OperationEvent.tupleAcceptingInvocation(tupleSpace, OperationType.WRITE, LogicTuple.of("f(1)"));
        final var expectedEvent3 = TupleEvent.afterWriting(tupleSpace, LogicTuple.of("f(1)"));
        final var expectedEvent4 = TupleEvent.afterReading(tupleSpace, LogicTuple.of("f(1)"));
        final var expectedEvent5 = expectedEvent2.toTupleReturningCompletion(LogicTuple.of("f(1)"));
        final var expectedEvent6 = expectedEvent1.toTupleReturningCompletion(LogicTuple.of("f(1)"));

        tupleSpace.operationInvoked().bind(observableBehaviour::add);
        tupleSpace.tupleSpaceChanged().bind(observableBehaviour::add);
        tupleSpace.operationCompleted().bind(observableBehaviour::add);

        var read = tupleSpace.readTuple("f(X)");
        await(tupleSpace.write("f(1)"));
        await(read);

        Assert.assertEquals(6, observableBehaviour.size());
        Assert.assertEquals(
                Set.of(expectedEvent1, expectedEvent2, expectedEvent3, expectedEvent4, expectedEvent5, expectedEvent6),
                Set.copyOf(observableBehaviour)
        );

        Assert.assertTrue(
                observableBehaviour.indexOf(expectedEvent1) < observableBehaviour.indexOf(expectedEvent2)
        );
    }

    @Test
    public void testTryReadFail() throws Exception {
        final List<TupleSpaceEvent<LogicTuple, LogicTemplate>> observableBehaviour = new LinkedList<>();

        final var expectedEvent1 = OperationEvent.templateAcceptingInvocation(tupleSpace, OperationType.TRY_READ, LogicTemplate.of("f(X)"));
        final var expectedEvent2 = expectedEvent1.toTuplesReturningCompletion();

        tupleSpace.operationInvoked().bind(observableBehaviour::add);
        tupleSpace.tupleSpaceChanged().bind(observableBehaviour::add);
        tupleSpace.operationCompleted().bind(observableBehaviour::add);

        await(tupleSpace.tryReadTuple("f(X)"));

        Assert.assertEquals(2, observableBehaviour.size());
        Assert.assertEquals(
                List.of(expectedEvent1, expectedEvent2),
                observableBehaviour
        );

    }


    @Test
    public void testTakeCompletion1() throws Exception {
        final List<TupleSpaceEvent<LogicTuple, LogicTemplate>> observableBehaviour = new LinkedList<>();

        final var expectedEvent1 = OperationEvent.tupleAcceptingInvocation(tupleSpace, OperationType.WRITE, LogicTuple.of("f(1)"));
        final var expectedEvent2 = TupleEvent.afterWriting(tupleSpace, LogicTuple.of("f(1)"));
        final var expectedEvent3 = expectedEvent1.toTupleReturningCompletion(LogicTuple.of("f(1)"));
        final var expectedEvent4 = OperationEvent.templateAcceptingInvocation(tupleSpace, OperationType.TAKE, LogicTemplate.of("f(X)"));
        final var expectedEvent5 = TupleEvent.afterTaking(tupleSpace, LogicTuple.of("f(1)"));
        final var expectedEvent6 = expectedEvent4.toTupleReturningCompletion(LogicTuple.of("f(1)"));

        tupleSpace.operationInvoked().bind(observableBehaviour::add);
        tupleSpace.tupleSpaceChanged().bind(observableBehaviour::add);
        tupleSpace.operationCompleted().bind(observableBehaviour::add);

        await(tupleSpace.write("f(1)"));
        await(tupleSpace.takeTuple("f(X)"));

        Assert.assertEquals(6, observableBehaviour.size());
        Assert.assertEquals(
                List.of(expectedEvent1, expectedEvent2, expectedEvent3, expectedEvent4, expectedEvent5, expectedEvent6),
                observableBehaviour
        );
    }

    @Test
    public void testTakeCompletion2() throws Exception {
        final List<TupleSpaceEvent<LogicTuple, LogicTemplate>> observableBehaviour = new LinkedList<>();

        final var expectedEvent1 = OperationEvent.templateAcceptingInvocation(tupleSpace, OperationType.TAKE, LogicTemplate.of("f(X)"));
        final var expectedEvent2 = OperationEvent.tupleAcceptingInvocation(tupleSpace, OperationType.WRITE, LogicTuple.of("f(1)"));
        final var expectedEvent3 = TupleEvent.afterWriting(tupleSpace, LogicTuple.of("f(1)"));
        final var expectedEvent4 = TupleEvent.afterTaking(tupleSpace, LogicTuple.of("f(1)"));
        final var expectedEvent5 = expectedEvent2.toTupleReturningCompletion(LogicTuple.of("f(1)"));
        final var expectedEvent6 = expectedEvent1.toTupleReturningCompletion(LogicTuple.of("f(1)"));

        tupleSpace.operationInvoked().bind(observableBehaviour::add);
        tupleSpace.tupleSpaceChanged().bind(observableBehaviour::add);
        tupleSpace.operationCompleted().bind(observableBehaviour::add);

        var take = tupleSpace.takeTuple("f(X)");
        await(tupleSpace.write("f(1)"));
        await(take);

        Assert.assertEquals(6, observableBehaviour.size());
        Assert.assertEquals(
            Set.of(expectedEvent1, expectedEvent2, expectedEvent3, expectedEvent4, expectedEvent5, expectedEvent6),
            Set.copyOf(observableBehaviour)
        );

        Assert.assertTrue(
            observableBehaviour.indexOf(expectedEvent1) < observableBehaviour.indexOf(expectedEvent2)
        );
    }

    @Test
    public void testTryTakeSuccess() throws Exception {
        final List<TupleSpaceEvent<LogicTuple, LogicTemplate>> observableBehaviour = new LinkedList<>();

        final var expectedEvent1 = OperationEvent.tupleAcceptingInvocation(tupleSpace, OperationType.WRITE, LogicTuple.of("f(1)"));
        final var expectedEvent2 = TupleEvent.afterWriting(tupleSpace, LogicTuple.of("f(1)"));
        final var expectedEvent3 = expectedEvent1.toTupleReturningCompletion(LogicTuple.of("f(1)"));
        final var expectedEvent4 = OperationEvent.templateAcceptingInvocation(tupleSpace, OperationType.TRY_TAKE, LogicTemplate.of("f(X)"));
        final var expectedEvent5 = TupleEvent.afterTaking(tupleSpace, LogicTuple.of("f(1)"));
        final var expectedEvent6 = expectedEvent4.toTupleReturningCompletion(LogicTuple.of("f(1)"));

        tupleSpace.operationInvoked().bind(observableBehaviour::add);
        tupleSpace.tupleSpaceChanged().bind(observableBehaviour::add);
        tupleSpace.operationCompleted().bind(observableBehaviour::add);

        await(tupleSpace.write("f(1)"));
        await(tupleSpace.tryTakeTuple("f(X)"));

        Assert.assertEquals(6, observableBehaviour.size());
        Assert.assertEquals(
                List.of(expectedEvent1, expectedEvent2, expectedEvent3, expectedEvent4, expectedEvent5, expectedEvent6),
                observableBehaviour
        );

    }

    @Test
    public void testTryTakeFail() throws Exception {
        final List<TupleSpaceEvent<LogicTuple, LogicTemplate>> observableBehaviour = new LinkedList<>();

        final var expectedEvent1 = OperationEvent.templateAcceptingInvocation(tupleSpace, OperationType.TRY_READ, LogicTemplate.of("f(X)"));
        final var expectedEvent2 = expectedEvent1.toTuplesReturningCompletion();

        tupleSpace.operationInvoked().bind(observableBehaviour::add);
        tupleSpace.tupleSpaceChanged().bind(observableBehaviour::add);
        tupleSpace.operationCompleted().bind(observableBehaviour::add);

        await(tupleSpace.tryReadTuple("f(X)"));

        Assert.assertEquals(2, observableBehaviour.size());
        Assert.assertEquals(
                List.of(expectedEvent1, expectedEvent2),
                observableBehaviour
        );

    }

}
