package it.unibo.coordination.linda.test;

import it.unibo.coordination.linda.core.*;
import it.unibo.coordination.linda.core.events.OperationEvent;
import it.unibo.coordination.linda.core.events.TupleEvent;
import it.unibo.coordination.linda.core.events.TupleSpaceEvent;
import it.unibo.coordination.testing.ConcurrentTestHelper;
import it.unibo.coordination.utils.PowerSet;
import org.apache.commons.collections4.MultiSet;
import org.javatuples.Pair;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public abstract class TestTupleSpaceInspectability<T extends Tuple, TT extends Template, K, V, M extends Match<T, TT, K, V>,  TS extends InspectableExtendedTupleSpace<T, TT, K, V>> extends TestBaseLinda<T, TT, K, V, M> {

    private ExecutorService executor;
    private InspectableExtendedTupleSpace<T, TT, K, V> tupleSpace;
    private ConcurrentTestHelper test;
    protected Random rand;

    public TestTupleSpaceInspectability(TupleTemplateFactory<T, TT, K, V, M> tupleTemplateFactory) {
        super(tupleTemplateFactory);
    }

    protected abstract TS getTupleSpace(ExecutorService executor);

    @Before
    public void setUp() {
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
    public void tearDown() {
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
    public void testTryReadSuccess() throws Exception {
        final List<TupleSpaceEvent<T, TT>> observableBehaviour = new LinkedList<>();

        final var tupleTemplate = getATupleAndATemplateMatchingIt();

        final var expectedEvent1 = OperationEvent.tupleAcceptingInvocation(tupleSpace, OperationType.WRITE, tupleTemplate.getValue0());
        final var expectedEvent2 = TupleEvent.afterWriting(tupleSpace, tupleTemplate.getValue0());
        final var expectedEvent3 = expectedEvent1.toTupleReturningCompletion(tupleTemplate.getValue0());
        final var expectedEvent4 = OperationEvent.templateAcceptingInvocation(tupleSpace, OperationType.TRY_READ, tupleTemplate.getValue1());
        final var expectedEvent5 = TupleEvent.afterReading(tupleSpace, tupleTemplate.getValue0());
        final var expectedEvent6 = expectedEvent4.toTupleReturningCompletion(tupleTemplate.getValue0());

        tupleSpace.operationInvoked().bind(observableBehaviour::add);
        tupleSpace.tupleSpaceChanged().bind(observableBehaviour::add);
        tupleSpace.operationCompleted().bind(observableBehaviour::add);

        await(tupleSpace.write(tupleTemplate.getValue0()));
        await(tupleSpace.tryRead(tupleTemplate.getValue1()));

        Assert.assertEquals(6, observableBehaviour.size());
        Assert.assertEquals(
                List.of(expectedEvent1, expectedEvent2, expectedEvent3, expectedEvent4, expectedEvent5, expectedEvent6),
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

        final var expectedEvent1 = OperationEvent.templateAcceptingInvocation(tupleSpace, OperationType.TRY_TAKE, template);
        final var expectedEvent2 = expectedEvent1.toTuplesReturningCompletion();

        tupleSpace.operationInvoked().bind(observableBehaviour::add);
        tupleSpace.tupleSpaceChanged().bind(observableBehaviour::add);
        tupleSpace.operationCompleted().bind(observableBehaviour::add);

        await(tupleSpace.tryTakeTuple(template));

        Assert.assertEquals(2, observableBehaviour.size());
        Assert.assertEquals(
                List.of(expectedEvent1, expectedEvent2),
                observableBehaviour
        );

    }

    @Test
    public void testWriteAll() throws Exception {

        final List<TupleSpaceEvent<T, TT>> observableBehaviour = new LinkedList<>();

        final MultiSet<T> tuples = getSomeTuples();


        final var expectedEvent1 = OperationEvent.tuplesAcceptingInvocation(tupleSpace, OperationType.WRITE_ALL, tuples);
        final List<TupleSpaceEvent<T, TT>> expectedEvents2 = tuples.stream().map(t -> TupleEvent.afterWriting(tupleSpace, t)).collect(Collectors.toList()); //for each tuple a tuple event

        final var expectedEvent3 = expectedEvent1.toTuplesReturningCompletion(tuples);


        tupleSpace.operationInvoked().bind(observableBehaviour::add);
        tupleSpace.tupleSpaceChanged().bind(observableBehaviour::add);
        tupleSpace.operationCompleted().bind(observableBehaviour::add);

        await(tupleSpace.writeAll(tuples));

        await(executor);

        var writeAllEvents = expectedEvents2;
        writeAllEvents.add(0, expectedEvent1);
        writeAllEvents.add(expectedEvent3);

        Assert.assertEquals(writeAllEvents.size(), observableBehaviour.size());

        Assert.assertEquals(
                writeAllEvents,
                observableBehaviour
        );
    }

    @Test
    public void testGet() throws Exception {

        final List<TupleSpaceEvent<T, TT>> observableBehaviour = new LinkedList<>();

        final MultiSet<T> tuples = getSomeTuples();


        final var expectedEvent1 = OperationEvent.tuplesAcceptingInvocation(tupleSpace, OperationType.WRITE_ALL, tuples);
        final List<TupleSpaceEvent<T, TT>> expectedEvents2 = tuples.stream().map(t -> TupleEvent.afterWriting(tupleSpace, t)).collect(Collectors.toList()); //for each tuple a tuple event
        final var expectedEvent3 = expectedEvent1.toTuplesReturningCompletion(tuples);
        final var expectedEvent4 = OperationEvent.nothingAcceptingInvocation(tupleSpace, OperationType.GET);
        final List<TupleSpaceEvent<T, TT>> expectedEvents5 = tuples.stream().map(t -> TupleEvent.afterReading(tupleSpace, t)).collect(Collectors.toList()); //for each tuple a tuple event
        final var expectedEvent6 = expectedEvent4.toTuplesReturningCompletion(tuples);


        tupleSpace.operationInvoked().bind(observableBehaviour::add);
        tupleSpace.tupleSpaceChanged().bind(observableBehaviour::add);
        tupleSpace.operationCompleted().bind(observableBehaviour::add);

        await(tupleSpace.writeAll(tuples));
        await(tupleSpace.get());

        var getEvents = expectedEvents2;
        getEvents.add(0, expectedEvent1);
        getEvents.addAll(List.of(expectedEvent3, expectedEvent4));
        getEvents.addAll(expectedEvents5);
        getEvents.add(expectedEvent6);

        Assert.assertEquals(getEvents.size(), observableBehaviour.size());

        Assert.assertEquals(
                getEvents,
                observableBehaviour
        );
    }

    @Test
    public void testReadAllCompletation1() throws Exception {

        final List<TupleSpaceEvent<T, TT>> observableBehaviour = new LinkedList<>();

        final Pair<MultiSet<T>, TT> tuples = getSomeTuplesOfOneSort();


        final var expectedEvent1 = OperationEvent.tuplesAcceptingInvocation(tupleSpace, OperationType.WRITE_ALL, tuples.getValue0());
        final List<TupleSpaceEvent<T, TT>> expectedEvents2 = tuples.getValue0().stream().map(t -> TupleEvent.afterWriting(tupleSpace, t)).collect(Collectors.toList()); //for each tuple a tuple event
        final var expectedEvent3 = expectedEvent1.toTuplesReturningCompletion(tuples.getValue0());
        final var expectedEvent4 = OperationEvent.templateAcceptingInvocation(tupleSpace, OperationType.READ_ALL, tuples.getValue1());
        final List<TupleSpaceEvent<T, TT>> expectedEvents5 = tuples.getValue0().stream().map(t -> TupleEvent.afterReading(tupleSpace, t)).collect(Collectors.toList()); //for each tuple a tuple event
        final var expectedEvent6 = expectedEvent4.toTuplesReturningCompletion(tuples.getValue0());


        tupleSpace.operationInvoked().bind(observableBehaviour::add);
        tupleSpace.tupleSpaceChanged().bind(observableBehaviour::add);
        tupleSpace.operationCompleted().bind(observableBehaviour::add);

        await(tupleSpace.writeAll(tuples.getValue0()));
        await(tupleSpace.readAllTuples(tuples.getValue1()));

        var readAllEvents = expectedEvents2;
        readAllEvents.add(0, expectedEvent1);
        readAllEvents.addAll(List.of(expectedEvent3, expectedEvent4));
        readAllEvents.addAll(expectedEvents5);
        readAllEvents.add(expectedEvent6);

        Assert.assertEquals(readAllEvents.size(), observableBehaviour.size());

        Assert.assertEquals(
                readAllEvents,
                observableBehaviour
        );
    }

    @Test
    public void testReadAllCompletation2() throws Exception {

        final List<TupleSpaceEvent<T, TT>> observableBehaviour = new LinkedList<>();

        final Pair<MultiSet<T>, TT> tuples = getSomeTuplesOfOneSort();


        final var expectedEvent1 = OperationEvent.tuplesAcceptingInvocation(tupleSpace, OperationType.WRITE_ALL, tuples.getValue0());
        final List<TupleSpaceEvent<T, TT>>  expectedEvents2 = tuples.getValue0().stream().map(t -> TupleEvent.afterWriting(tupleSpace, t)).collect(Collectors.toList()); //for each tuple a tuple event
        final var expectedEvent3 = expectedEvent1.toTuplesReturningCompletion(tuples.getValue0());
        final var expectedEvent4 = OperationEvent.templateAcceptingInvocation(tupleSpace, OperationType.READ_ALL, tuples.getValue1());
        final List<TupleSpaceEvent<T, TT>> expectedEvents5 = tuples.getValue0().stream().map(t -> TupleEvent.afterReading(tupleSpace, t)).collect(Collectors.toList()); //for each tuple a tuple event
        //final var expectedEvent6 = expectedEvent4.toTuplesReturningCompletion(tuples.getValue0());


        tupleSpace.operationInvoked().bind(observableBehaviour::add);
        tupleSpace.tupleSpaceChanged().bind(observableBehaviour::add);
        tupleSpace.operationCompleted().bind(observableBehaviour::add);

        var read = tupleSpace.readAllTuples(tuples.getValue1());
        await(tupleSpace.writeAll(tuples.getValue0()));
        await(read);


        Assert.assertTrue(observableBehaviour.size() >= 4 + expectedEvents2.size());
        Assert.assertTrue(observableBehaviour.size() <= 4 + expectedEvents2.size() + expectedEvents5.size());

        Assert.assertTrue (observableBehaviour.contains(expectedEvent1));
        expectedEvents2.forEach(e -> Assert.assertTrue(observableBehaviour.contains(e))); //not ordered
        Assert.assertTrue (observableBehaviour.contains(expectedEvent3));
        Assert.assertTrue (observableBehaviour.contains(expectedEvent4));

        //expectedEvents5 could be partly ma also not
        if(observableBehaviour.size() > 4 + expectedEvents2.size()) {
            //expectedEvents5 is contained
            int count = 0;
            for(var e : expectedEvents5) {
                if(observableBehaviour.contains(e)) {
                    count++;
                }
            }
            Assert.assertEquals(count, observableBehaviour.size() - (4 + expectedEvents2.size()));
        }

        //expectedEvents6 is there but with a particular subset of tuples -> use PowerSet
        boolean isExpectedEvent6 = false;
        for(Set<T> l : PowerSet.powerSet(tuples.getValue0().stream().collect(Collectors.toSet()))) {
            if(!isExpectedEvent6 && observableBehaviour.contains(expectedEvent4.toTuplesReturningCompletion(l))) {
                isExpectedEvent6 = true;
            } else if(isExpectedEvent6 && observableBehaviour.contains(expectedEvent4.toTuplesReturningCompletion(l))) {
                Assert.fail();
            }
        }
        Assert.assertTrue(isExpectedEvent6);

        Assert.assertTrue (observableBehaviour.indexOf(expectedEvent1) > observableBehaviour.indexOf(expectedEvent4));
    }

    @Test
    public void testTakeAllCompletation1() throws Exception {

        final List<TupleSpaceEvent<T, TT>> observableBehaviour = new LinkedList<>();

        final Pair<MultiSet<T>, TT> tuples = getSomeTuplesOfOneSort();


        final var expectedEvent1 = OperationEvent.tuplesAcceptingInvocation(tupleSpace, OperationType.WRITE_ALL, tuples.getValue0());
        final List<TupleSpaceEvent<T, TT>>  expectedEvents2 = tuples.getValue0().stream().map(t -> TupleEvent.afterWriting(tupleSpace, t)).collect(Collectors.toList()); //for each tuple a tuple event
        final var expectedEvent3 = expectedEvent1.toTuplesReturningCompletion(tuples.getValue0());
        final var expectedEvent4 = OperationEvent.templateAcceptingInvocation(tupleSpace, OperationType.TAKE_ALL, tuples.getValue1());
        final List<TupleSpaceEvent<T, TT>>  expectedEvents5 = tuples.getValue0().stream().map(t -> TupleEvent.afterTaking(tupleSpace, t)).collect(Collectors.toList()); //for each tuple a tuple event
        final var expectedEvent6 = expectedEvent4.toTuplesReturningCompletion(tuples.getValue0());


        tupleSpace.operationInvoked().bind(observableBehaviour::add);
        tupleSpace.tupleSpaceChanged().bind(observableBehaviour::add);
        tupleSpace.operationCompleted().bind(observableBehaviour::add);

        await(tupleSpace.writeAll(tuples.getValue0()));
        await(tupleSpace.takeAllTuples(tuples.getValue1()));

        var takeAllEvents = expectedEvents2;
        takeAllEvents.add(0, expectedEvent1);
        takeAllEvents.addAll(List.of(expectedEvent3, expectedEvent4));
        takeAllEvents.addAll(expectedEvents5);
        takeAllEvents.add(expectedEvent6);

        Assert.assertEquals(takeAllEvents.size(), observableBehaviour.size());

        Assert.assertEquals(
                takeAllEvents,
                observableBehaviour
        );
    }

    @Test
    public void testTakeAllCompletation2() throws Exception {

        final List<TupleSpaceEvent<T, TT>> observableBehaviour = new LinkedList<>();

        final Pair<MultiSet<T>, TT> tuples = getSomeTuplesOfOneSort();


        final var expectedEvent1 = OperationEvent.tuplesAcceptingInvocation(tupleSpace, OperationType.WRITE_ALL, tuples.getValue0());
        final List<TupleSpaceEvent<T, TT>>  expectedEvents2 = tuples.getValue0().stream().map(t -> TupleEvent.afterWriting(tupleSpace, t)).collect(Collectors.toList()); //for each tuple a tuple event
        final var expectedEvent3 = expectedEvent1.toTuplesReturningCompletion(tuples.getValue0());
        final var expectedEvent4 = OperationEvent.templateAcceptingInvocation(tupleSpace, OperationType.TAKE_ALL, tuples.getValue1());
        final List<TupleSpaceEvent<T, TT>> expectedEvents5 = tuples.getValue0().stream().map(t -> TupleEvent.afterTaking(tupleSpace, t)).collect(Collectors.toList()); //for each tuple a tuple event
        //final var expectedEvent6 = expectedEvent4.toTuplesReturningCompletion(tuples.getValue0());


        tupleSpace.operationInvoked().bind(observableBehaviour::add);
        tupleSpace.tupleSpaceChanged().bind(observableBehaviour::add);
        tupleSpace.operationCompleted().bind(observableBehaviour::add);

        var read = tupleSpace.takeAllTuples(tuples.getValue1());
        await(tupleSpace.writeAll(tuples.getValue0()));
        await(read);


        Assert.assertTrue(observableBehaviour.size() >= 4 + expectedEvents2.size());
        Assert.assertTrue(observableBehaviour.size() <= 4 + expectedEvents2.size() + expectedEvents5.size());

        Assert.assertTrue (observableBehaviour.contains(expectedEvent1));
        expectedEvents2.forEach(e -> Assert.assertTrue(observableBehaviour.contains(e))); //not ordered
        Assert.assertTrue (observableBehaviour.contains(expectedEvent3));
        Assert.assertTrue (observableBehaviour.contains(expectedEvent4));

        //expectedEvents5 could be partly ma also not
        if(observableBehaviour.size() > 4 + expectedEvents2.size()) {
            //expectedEvents5 is contained
            int count = 0;
            for(var e : expectedEvents5) {
                if(observableBehaviour.contains(e)) {
                    count++;
                }
            }
            Assert.assertEquals(count, observableBehaviour.size() - (4 + expectedEvents2.size()));
        }

        //expectedEvents6 is there but with a particular subset of tuples -> use PowerSet
        boolean isExpectedEvent6 = false;
        for(Set<T> l : PowerSet.powerSet(tuples.getValue0().stream().collect(Collectors.toSet()))) {
            if(!isExpectedEvent6 && observableBehaviour.contains(expectedEvent4.toTuplesReturningCompletion(l))) {
                isExpectedEvent6 = true;
            } else if(isExpectedEvent6 && observableBehaviour.contains(expectedEvent4.toTuplesReturningCompletion(l))) {
                Assert.fail();
            }
        }
        Assert.assertTrue(isExpectedEvent6);

        Assert.assertTrue (observableBehaviour.indexOf(expectedEvent1) > observableBehaviour.indexOf(expectedEvent4));
    }

    @Test
    public void testAbsentInvocation() throws Exception {
        final List<TupleSpaceEvent<T, TT>> observableBehaviour = new LinkedList<>();

        final var tupleTemplate = getATupleAndATemplateMatchingIt();

        final var expectedEvent1 = OperationEvent.tupleAcceptingInvocation(tupleSpace, OperationType.WRITE, tupleTemplate.getValue0());
        final var expectedEvent2 = TupleEvent.afterWriting(tupleSpace, tupleTemplate.getValue0());
        final var expectedEvent3 = expectedEvent1.toTupleReturningCompletion(tupleTemplate.getValue0());
        final var expectedEvent4 = OperationEvent.templateAcceptingInvocation(tupleSpace, OperationType.ABSENT, tupleTemplate.getValue1());

        tupleSpace.operationInvoked().bind(observableBehaviour::add);
        tupleSpace.tupleSpaceChanged().bind(observableBehaviour::add);
        tupleSpace.operationCompleted().bind(observableBehaviour::add);

        await(tupleSpace.write(tupleTemplate.getValue0()));
        try {
            //pending request because there is a tuple with T as template
            await(tupleSpace.absentTemplate(tupleTemplate.getValue1()));
            Assert.fail();
        } catch (TimeoutException e) {
            Assert.assertEquals(4, observableBehaviour.size());
            Assert.assertEquals(
                    List.of(expectedEvent1, expectedEvent2, expectedEvent3, expectedEvent4),
                    observableBehaviour
            );
        }
    }

    @Test
    public void testAbsentCompletation() throws Exception {
        final List<TupleSpaceEvent<T, TT>> observableBehaviour = new LinkedList<>();

        final var template = getATemplate();

        final var expectedEvent1 = OperationEvent.templateAcceptingInvocation(tupleSpace, OperationType.ABSENT, template);
        final var expectedEvent2 = TupleEvent.afterAbsent(tupleSpace, template);
        final var expectedEvent3 = expectedEvent1.toTemplateReturningCompletion(template);

        tupleSpace.operationInvoked().bind(observableBehaviour::add);
        tupleSpace.tupleSpaceChanged().bind(observableBehaviour::add);
        tupleSpace.operationCompleted().bind(observableBehaviour::add);

        await(tupleSpace.absentTemplate(template));

        Assert.assertEquals(3, observableBehaviour.size());
        Assert.assertEquals(
                List.of(expectedEvent1, expectedEvent2, expectedEvent3),
                observableBehaviour
        );
    }

    @Test
    public void testAbsentInvocationAndCompletation() throws Exception {
        final List<TupleSpaceEvent<T, TT>> observableBehaviour = new LinkedList<>();

        final var tupleTemplate = getATupleAndATemplateMatchingIt();

        final var expectedEvent1 = OperationEvent.tupleAcceptingInvocation(tupleSpace, OperationType.WRITE, tupleTemplate.getValue0());
        final var expectedEvent2 = TupleEvent.afterWriting(tupleSpace, tupleTemplate.getValue0());
        final var expectedEvent3 = expectedEvent1.toTupleReturningCompletion(tupleTemplate.getValue0());
        final var expectedEvent4 = OperationEvent.templateAcceptingInvocation(tupleSpace, OperationType.ABSENT, tupleTemplate.getValue1());
        final var expectedEvent5 = TupleEvent.afterAbsent(tupleSpace, tupleTemplate.getValue1());
        final var expectedEvent6 = expectedEvent4.toTemplateReturningCompletion(tupleTemplate.getValue1());
        final var expectedEvent7 = OperationEvent.templateAcceptingInvocation(tupleSpace, OperationType.TAKE, tupleTemplate.getValue1());
        final var expectedEvent8 = TupleEvent.afterTaking(tupleSpace, tupleTemplate.getValue0());
        final var expectedEvent9 = expectedEvent7.toTupleReturningCompletion(tupleTemplate.getValue0());

        tupleSpace.operationInvoked().bind(observableBehaviour::add);
        tupleSpace.tupleSpaceChanged().bind(observableBehaviour::add);
        tupleSpace.operationCompleted().bind(observableBehaviour::add);

        await(tupleSpace.write(tupleTemplate.getValue0()));
        var absent = tupleSpace.absentTemplate(tupleTemplate.getValue1());
        await(tupleSpace.takeTuple(tupleTemplate.getValue1()));
        await(absent);

        Assert.assertEquals(9, observableBehaviour.size());
        Assert.assertEquals(
                Set.of(expectedEvent1, expectedEvent2, expectedEvent3,
                        expectedEvent4, expectedEvent5, expectedEvent6,
                        expectedEvent7, expectedEvent8, expectedEvent9),
                Set.copyOf(observableBehaviour)
        );
        //standard write
        Assert.assertEquals(List.of(expectedEvent1, expectedEvent2, expectedEvent3),
                observableBehaviour.subList(0, 3));
        // absent invokation before take invokation
        Assert.assertTrue(observableBehaviour.indexOf(expectedEvent4) < observableBehaviour.indexOf(expectedEvent7));
        // take completation is not always before absent completation
    }


    @Test
    public void testTryAbsentSuccess() throws Exception {
        final List<TupleSpaceEvent<T, TT>> observableBehaviour = new LinkedList<>();

        final var template = getATemplate();

        final var expectedEvent1 = OperationEvent.templateAcceptingInvocation(tupleSpace, OperationType.TRY_ABSENT, template);
        final var expectedEvent2 = expectedEvent1.toTuplesReturningCompletion();

        tupleSpace.operationInvoked().bind(observableBehaviour::add);
        tupleSpace.tupleSpaceChanged().bind(observableBehaviour::add);
        tupleSpace.operationCompleted().bind(observableBehaviour::add);

        await(tupleSpace.tryAbsent(template));

        Assert.assertEquals(2, observableBehaviour.size());
        Assert.assertEquals(
                List.of(expectedEvent1, expectedEvent2),
                observableBehaviour
        );

    }

    @Test
    public void testTryAbsentFail() throws Exception {
        final List<TupleSpaceEvent<T, TT>> observableBehaviour = new LinkedList<>();

        final var tupleTemplate = getATupleAndATemplateMatchingIt();

        final var expectedEvent1 = OperationEvent.tupleAcceptingInvocation(tupleSpace, OperationType.WRITE, tupleTemplate.getValue0());
        final var expectedEvent2 = TupleEvent.afterWriting(tupleSpace, tupleTemplate.getValue0());
        final var expectedEvent3 = expectedEvent1.toTupleReturningCompletion(tupleTemplate.getValue0());
        final var expectedEvent4 = OperationEvent.templateAcceptingInvocation(tupleSpace, OperationType.TRY_ABSENT, tupleTemplate.getValue1());
        final var expectedEvent5 = TupleEvent.afterAbsent(tupleSpace, tupleTemplate.getValue1(), tupleTemplate.getValue0());
        final var expectedEvent6 = expectedEvent4.toTupleReturningCompletion(tupleTemplate.getValue0());

        tupleSpace.operationInvoked().bind(observableBehaviour::add);
        tupleSpace.tupleSpaceChanged().bind(observableBehaviour::add);
        tupleSpace.operationCompleted().bind(observableBehaviour::add);

        await(tupleSpace.write(tupleTemplate.getValue0()));
        await(tupleSpace.tryAbsent(tupleTemplate.getValue1()));

        Assert.assertEquals(6, observableBehaviour.size());
        Assert.assertEquals(
                List.of(expectedEvent1, expectedEvent2, expectedEvent3, expectedEvent4, expectedEvent5, expectedEvent6),
                observableBehaviour
        );

    }
}
