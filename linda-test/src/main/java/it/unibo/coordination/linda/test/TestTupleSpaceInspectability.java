package it.unibo.coordination.linda.test;

import it.unibo.coordination.linda.core.InspectableExtendedTupleSpace;
import it.unibo.coordination.linda.core.OperationType;
import it.unibo.coordination.linda.core.Template;
import it.unibo.coordination.linda.core.Tuple;
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

public abstract class TestTupleSpaceInspectability<T extends Tuple, TT extends Template, K, V, TS extends InspectableExtendedTupleSpace<T, TT, K, V>> extends TestBaseLinda<T, TT> {

    protected ExecutorService executor;
    protected InspectableExtendedTupleSpace<T, TT, K, V> tupleSpace;
    protected ConcurrentTestHelper test;
    protected Random rand;

    public TestTupleSpaceInspectability(TupleTemplateFactory<T, TT> tupleTemplateFactory) {
        super(tupleTemplateFactory);
    }

    protected abstract TS getTupleSpace(ExecutorService executor);

    @Before
    public void setUp() throws Exception {
        executor = Executors.newSingleThreadExecutor();
        tupleSpace = getTupleSpace(executor);
        test = new ConcurrentTestHelper();
    }

    private static final Duration MAX_WAIT = Duration.ofSeconds(2);

    private static <T> T await(Future<T> future) throws InterruptedException, ExecutionException, TimeoutException {
        return future.get(MAX_WAIT.toMillis(), TimeUnit.MILLISECONDS);
    }

    private static boolean await(ExecutorService engine) throws InterruptedException {
        return engine.awaitTermination(MAX_WAIT.toMillis(), TimeUnit.MILLISECONDS);
    }

    @After
    public void tearDown() throws Exception {
        executor.shutdown();
    }

    @Test
    public void testReadInvocation() throws Exception {
        final List<TupleSpaceEvent<T, TT>> observableBehaviour = new LinkedList<>();

        final var template = getATemplate();

        final var expectedEvent = OperationEvent.templateAcceptingInvocation(tupleSpace, OperationType.READ, template);

        tupleSpace.operationInvoked().bind(observableBehaviour::add);
        tupleSpace.tupleSpaceChanged().bind(observableBehaviour::add);
        tupleSpace.operationCompleted().bind(observableBehaviour::add);

        try {
            await(tupleSpace.readTuple(template));
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
        final List<TupleSpaceEvent<T, TT>> observableBehaviour = new LinkedList<>();

        final var template = getATemplate();

        final var expectedEvent = OperationEvent.templateAcceptingInvocation(tupleSpace, OperationType.TAKE, template);

        tupleSpace.operationInvoked().bind(observableBehaviour::add);
        tupleSpace.tupleSpaceChanged().bind(observableBehaviour::add);
        tupleSpace.operationCompleted().bind(observableBehaviour::add);

        try {
            await(tupleSpace.takeTuple(template));
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
        final List<TupleSpaceEvent<T, TT>> observableBehaviour = new LinkedList<>();

        final var tuple = getATuple();

        final var expectedEvent1 = OperationEvent.tupleAcceptingInvocation(tupleSpace, OperationType.WRITE, tuple);
        final var expectedEvent2 = TupleEvent.afterWriting(tupleSpace, tuple);
        final var expectedEvent3 = expectedEvent1.toTupleReturningCompletion(tuple);


        tupleSpace.operationInvoked().bind(observableBehaviour::add);
        tupleSpace.tupleSpaceChanged().bind(observableBehaviour::add);
        tupleSpace.operationCompleted().bind(observableBehaviour::add);

        await(tupleSpace.write(tuple));

        await(executor);

        Assert.assertEquals(3, observableBehaviour.size());
        Assert.assertEquals(
                List.of(expectedEvent1, expectedEvent2, expectedEvent3),
                observableBehaviour
        );
    }


    @Test
    public void testReadCompletion1() throws Exception {
        final List<TupleSpaceEvent<T, TT>> observableBehaviour = new LinkedList<>();

        final var tupleTemplate = getATupleAndATemplateMatchingIt();

        final var expectedEvent1 = OperationEvent.tupleAcceptingInvocation(tupleSpace, OperationType.WRITE, tupleTemplate.getValue0());
        final var expectedEvent2 = TupleEvent.afterWriting(tupleSpace, tupleTemplate.getValue0());
        final var expectedEvent3 = expectedEvent1.toTupleReturningCompletion(tupleTemplate.getValue0());
        final var expectedEvent4 = OperationEvent.templateAcceptingInvocation(tupleSpace, OperationType.READ, tupleTemplate.getValue1());
        final var expectedEvent5 = TupleEvent.afterReading(tupleSpace, tupleTemplate.getValue0());
        final var expectedEvent6 = expectedEvent4.toTupleReturningCompletion(tupleTemplate.getValue0());

        tupleSpace.operationInvoked().bind(observableBehaviour::add);
        tupleSpace.tupleSpaceChanged().bind(observableBehaviour::add);
        tupleSpace.operationCompleted().bind(observableBehaviour::add);

        await(tupleSpace.write(tupleTemplate.getValue0()));
        await(tupleSpace.readTuple(tupleTemplate.getValue1()));

        Assert.assertEquals(6, observableBehaviour.size());
        Assert.assertEquals(
                List.of(expectedEvent1, expectedEvent2, expectedEvent3, expectedEvent4, expectedEvent5, expectedEvent6),
                observableBehaviour
        );

    }


    @Test
    public void testReadCompletion2() throws Exception {
        final List<TupleSpaceEvent<T, TT>> observableBehaviour = new LinkedList<>();

        final var tupleTemplate = getATupleAndATemplateMatchingIt();

        final var expectedEvent1 = OperationEvent.templateAcceptingInvocation(tupleSpace, OperationType.READ, tupleTemplate.getValue1());
        final var expectedEvent2 = OperationEvent.tupleAcceptingInvocation(tupleSpace, OperationType.WRITE, tupleTemplate.getValue0());
        final var expectedEvent3 = TupleEvent.afterWriting(tupleSpace, tupleTemplate.getValue0());
        final var expectedEvent4 = TupleEvent.afterReading(tupleSpace, tupleTemplate.getValue0());
        final var expectedEvent5 = expectedEvent2.toTupleReturningCompletion(tupleTemplate.getValue0());
        final var expectedEvent6 = expectedEvent1.toTupleReturningCompletion(tupleTemplate.getValue0());

        tupleSpace.operationInvoked().bind(observableBehaviour::add);
        tupleSpace.tupleSpaceChanged().bind(observableBehaviour::add);
        tupleSpace.operationCompleted().bind(observableBehaviour::add);

        var read = tupleSpace.readTuple(tupleTemplate.getValue1());
        await(tupleSpace.write(tupleTemplate.getValue0()));
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
        final List<TupleSpaceEvent<T, TT>> observableBehaviour = new LinkedList<>();

        final var template = getATemplate();

        final var expectedEvent1 = OperationEvent.templateAcceptingInvocation(tupleSpace, OperationType.TRY_READ, template);
        final var expectedEvent2 = expectedEvent1.toTuplesReturningCompletion();

        tupleSpace.operationInvoked().bind(observableBehaviour::add);
        tupleSpace.tupleSpaceChanged().bind(observableBehaviour::add);
        tupleSpace.operationCompleted().bind(observableBehaviour::add);

        await(tupleSpace.tryReadTuple(template));

        Assert.assertEquals(2, observableBehaviour.size());
        Assert.assertEquals(
                List.of(expectedEvent1, expectedEvent2),
                observableBehaviour
        );

    }


    @Test
    public void testTakeCompletion1() throws Exception {
        final List<TupleSpaceEvent<T, TT>> observableBehaviour = new LinkedList<>();

        final var tupleTemplate = getATupleAndATemplateMatchingIt();

        final var expectedEvent1 = OperationEvent.tupleAcceptingInvocation(tupleSpace, OperationType.WRITE, tupleTemplate.getValue0());
        final var expectedEvent2 = TupleEvent.afterWriting(tupleSpace, tupleTemplate.getValue0());
        final var expectedEvent3 = expectedEvent1.toTupleReturningCompletion(tupleTemplate.getValue0());
        final var expectedEvent4 = OperationEvent.templateAcceptingInvocation(tupleSpace, OperationType.TAKE, tupleTemplate.getValue1());
        final var expectedEvent5 = TupleEvent.afterTaking(tupleSpace, tupleTemplate.getValue0());
        final var expectedEvent6 = expectedEvent4.toTupleReturningCompletion(tupleTemplate.getValue0());

        tupleSpace.operationInvoked().bind(observableBehaviour::add);
        tupleSpace.tupleSpaceChanged().bind(observableBehaviour::add);
        tupleSpace.operationCompleted().bind(observableBehaviour::add);

        await(tupleSpace.write(tupleTemplate.getValue0()));
        await(tupleSpace.takeTuple(tupleTemplate.getValue1()));

        Assert.assertEquals(6, observableBehaviour.size());
        Assert.assertEquals(
                List.of(expectedEvent1, expectedEvent2, expectedEvent3, expectedEvent4, expectedEvent5, expectedEvent6),
                observableBehaviour
        );
    }

    @Test
    public void testTakeCompletion2() throws Exception {
        final List<TupleSpaceEvent<T, TT>> observableBehaviour = new LinkedList<>();

        final var tupleTemplate = getATupleAndATemplateMatchingIt();

        final var expectedEvent1 = OperationEvent.templateAcceptingInvocation(tupleSpace, OperationType.TAKE, tupleTemplate.getValue1());
        final var expectedEvent2 = OperationEvent.tupleAcceptingInvocation(tupleSpace, OperationType.WRITE, tupleTemplate.getValue0());
        final var expectedEvent3 = TupleEvent.afterWriting(tupleSpace, tupleTemplate.getValue0());
        final var expectedEvent4 = TupleEvent.afterTaking(tupleSpace, tupleTemplate.getValue0());
        final var expectedEvent5 = expectedEvent2.toTupleReturningCompletion(tupleTemplate.getValue0());
        final var expectedEvent6 = expectedEvent1.toTupleReturningCompletion(tupleTemplate.getValue0());

        tupleSpace.operationInvoked().bind(observableBehaviour::add);
        tupleSpace.tupleSpaceChanged().bind(observableBehaviour::add);
        tupleSpace.operationCompleted().bind(observableBehaviour::add);

        var take = tupleSpace.takeTuple(tupleTemplate.getValue1());
        await(tupleSpace.write(tupleTemplate.getValue0()));
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
        final List<TupleSpaceEvent<T, TT>> observableBehaviour = new LinkedList<>();

        final var tupleTemplate = getATupleAndATemplateMatchingIt();

        final var expectedEvent1 = OperationEvent.tupleAcceptingInvocation(tupleSpace, OperationType.WRITE, tupleTemplate.getValue0());
        final var expectedEvent2 = TupleEvent.afterWriting(tupleSpace, tupleTemplate.getValue0());
        final var expectedEvent3 = expectedEvent1.toTupleReturningCompletion(tupleTemplate.getValue0());
        final var expectedEvent4 = OperationEvent.templateAcceptingInvocation(tupleSpace, OperationType.TRY_TAKE, tupleTemplate.getValue1());
        final var expectedEvent5 = TupleEvent.afterTaking(tupleSpace, tupleTemplate.getValue0());
        final var expectedEvent6 = expectedEvent4.toTupleReturningCompletion(tupleTemplate.getValue0());

        tupleSpace.operationInvoked().bind(observableBehaviour::add);
        tupleSpace.tupleSpaceChanged().bind(observableBehaviour::add);
        tupleSpace.operationCompleted().bind(observableBehaviour::add);

        await(tupleSpace.write(tupleTemplate.getValue0()));
        await(tupleSpace.tryTakeTuple(tupleTemplate.getValue1()));

        Assert.assertEquals(6, observableBehaviour.size());
        Assert.assertEquals(
                List.of(expectedEvent1, expectedEvent2, expectedEvent3, expectedEvent4, expectedEvent5, expectedEvent6),
                observableBehaviour
        );

    }

    @Test
    public void testTryTakeFail() throws Exception {
        final List<TupleSpaceEvent<T, TT>> observableBehaviour = new LinkedList<>();

        final var template = getATemplate();

        final var expectedEvent1 = OperationEvent.templateAcceptingInvocation(tupleSpace, OperationType.TRY_READ, template);
        final var expectedEvent2 = expectedEvent1.toTuplesReturningCompletion();

        tupleSpace.operationInvoked().bind(observableBehaviour::add);
        tupleSpace.tupleSpaceChanged().bind(observableBehaviour::add);
        tupleSpace.operationCompleted().bind(observableBehaviour::add);

        await(tupleSpace.tryReadTuple(template));

        Assert.assertEquals(2, observableBehaviour.size());
        Assert.assertEquals(
                List.of(expectedEvent1, expectedEvent2),
                observableBehaviour
        );

    }

}
