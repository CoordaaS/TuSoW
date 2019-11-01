package it.unibo.coordination.linda.test;

import it.unibo.coordination.linda.core.Match;
import it.unibo.coordination.linda.core.Template;
import it.unibo.coordination.linda.core.Tuple;
import it.unibo.coordination.linda.core.TupleSpace;
import it.unibo.coordination.testing.ActiveObject;
import it.unibo.coordination.testing.ConcurrentTestHelper;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.multiset.HashMultiSet;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class TestTupleSpace<T extends Tuple, TT extends Template, K, V, M extends Match<T, TT, K, V>, TS extends TupleSpace<T, TT, K, V>> extends TestBaseLinda<T, TT, K, V, M> {

    protected ExecutorService executor;
    protected TupleSpace<T, TT, K, V> tupleSpace;
    protected ConcurrentTestHelper test;
    protected Random rand;

    public TestTupleSpace(TupleTemplateFactory<T, TT, K, V, M> tupleTemplateFactory) {
        super(tupleTemplateFactory);
    }

    protected abstract TS getTupleSpace(ExecutorService executor);

    @Before
    public void setUp() throws Exception {
        executor = Executors.newSingleThreadExecutor();
        tupleSpace = getTupleSpace(executor);
        test = new ConcurrentTestHelper();
        rand = new Random();
    }

    @Test
    public void testInitiallyEmpty() throws Exception {
        test.setThreadCount(1);

        final ActiveObject alice = new ActiveObject("Alice") {

            @Override
            protected void loop() throws Exception {
                test.assertEquals(tupleSpace.getSize(), 0, "The tuple space must initially be empty");
                stop();
            }

            @Override
            protected void onEnd() {
                test.done();
            }

        }.start();

        test.await();
        alice.await();
    }

    @Test
    public void testReadSuspensiveSemantics() throws Exception {
        test.setThreadCount(1);

        final ActiveObject alice = new ActiveObject("Alice") {

            @Override
            protected void loop() throws Exception {
                test.assertBlocksIndefinitely(tupleSpace.readTuple(getATemplate()),
                        "A read operation should block if no tuple matching the requested template is available");
                stop();
            }

            @Override
            protected void onEnd() {
                test.done();
            }

        }.start();

        test.await();
        alice.await();
    }

    @Test
    public void testTakeSuspensiveSemantics() throws Exception {
        test.setThreadCount(1);

        final ActiveObject alice = new ActiveObject("Alice") {

            @Override
            protected void loop() throws Exception {
                test.assertBlocksIndefinitely(tupleSpace.takeTuple(getATemplate()),
                        "A take operation should block if no tuple matching the requested template is available");
                stop();
            }

            @Override
            protected void onEnd() {
                test.done();
            }

        }.start();

        test.await();
        alice.await();
    }

    @Test
    public void testWriteGenerativeSemantics() throws Exception {
        test.setThreadCount(1);

        final var tuple = getATuple();

        final ActiveObject alice = new ActiveObject("Alice") {

            @Override
            protected void loop() throws Exception {
                test.assertEquals(tupleSpace.getSize(), 0, "The tuple space should be initially empty");
                test.assertEquals(tupleSpace.write(tuple), tuple, "A write operation should always return the tuple it has inserted");
                test.assertEquals(tupleSpace.getSize(), 1, "After a write, the tuple space should contain one more tuple");
                stop();
            }

            @Override
            protected void onEnd() {
                test.done();
            }

        }.start();

        test.await();
        alice.await();
    }

    @Test
    public void testReadIsIdempotent1() throws Exception {
        test.setThreadCount(2);

        final var tupleAndTemplate = getATupleAndATemplateMatchingIt();

        final ActiveObject bob = new ActiveObject("Bob") {

            @Override
            protected void loop() throws Exception {
                for (int i = rand.nextInt(10) + 1; i >= 0; i--) {
                    test.assertEquals(tupleSpace.readTuple(tupleAndTemplate.getValue1()), tupleAndTemplate.getValue0());
                }
                test.assertEquals(tupleSpace.readTuple(tupleAndTemplate.getValue1()), tupleAndTemplate.getValue0());
                stop();
            }

            @Override
            protected void onEnd() {
                test.done();
            }

        };

        final ActiveObject alice = new ActiveObject("Alice") {

            @Override
            protected void loop() throws Exception {
                test.assertEventuallyReturns(tupleSpace.write(tupleAndTemplate.getValue0()));
                bob.start();
                stop();
            }

            @Override
            protected void onEnd() {
                test.done();
            }

        }.start();

        test.await();
        alice.await();
        bob.await();
    }

    @Test
    public void testReadIsIdempotent2() throws Exception {
        test.setThreadCount(2);

        final var tupleAndTemplate = getATupleAndATemplateMatchingIt();

        final ActiveObject alice = new ActiveObject("Alice") {

            @Override
            protected void loop() throws Exception {
                test.assertEventuallyReturns(tupleSpace.write(tupleAndTemplate.getValue0()));
                stop();
            }

            @Override
            protected void onEnd() {
                test.done();
            }

        };

        final ActiveObject bob = new ActiveObject("Bob") {

            @Override
            protected void loop() throws Exception {
                final Future<T> toBeRead1 = tupleSpace.readTuple(tupleAndTemplate.getValue1());
                final Future<T> toBeRead2 = tupleSpace.readTuple(tupleAndTemplate.getValue1());

                alice.start();

                test.assertEquals(toBeRead1, tupleAndTemplate.getValue0());
                test.assertEquals(toBeRead2, tupleAndTemplate.getValue0());

                stop();
            }

            @Override
            protected void onEnd() {
                test.done();
            }

        }.start();

        test.await();
        alice.await();
        bob.await();
    }

    @Test
    public void testTakeIsNotIdempotent1() throws Exception {
        test.setThreadCount(2);

        final var tupleAndTemplate = getATupleAndATemplateMatchingIt();

        final ActiveObject bob = new ActiveObject("Bob") {

            @Override
            protected void loop() throws Exception {
                test.assertEquals(tupleSpace.takeTuple(tupleAndTemplate.getValue1()), tupleAndTemplate.getValue0());
                test.assertBlocksIndefinitely(tupleSpace.takeTuple(tupleAndTemplate.getValue1()));
                stop();
            }

            @Override
            protected void onEnd() {
                test.done();
            }

        };

        final ActiveObject alice = new ActiveObject("Alice") {

            @Override
            protected void loop() throws Exception {
                test.assertEventuallyReturns(tupleSpace.write(tupleAndTemplate.getValue0()));
                bob.start();
                stop();
            }

            @Override
            protected void onEnd() {
                test.done();
            }

        }.start();

        test.await();
        alice.await();
        bob.await();
    }

    @Test
    public void testTakeIsNotIdempotent2() throws Exception {
        test.setThreadCount(2);

        final var tupleAndTemplate = getATupleAndATemplateMatchingIt();

        final ActiveObject alice = new ActiveObject("Alice") {

            @Override
            protected void loop() throws Exception {
                test.assertEventuallyReturns(tupleSpace.write(tupleAndTemplate.getValue0()));
                stop();
            }

            @Override
            protected void onEnd() {
                test.done();
            }

        };

        final ActiveObject bob = new ActiveObject("Bob") {

            @Override
            protected void loop() throws Exception {
                final var toBeWritten = tupleSpace.takeTuple(tupleAndTemplate.getValue1());
                alice.start();
                test.assertEquals(toBeWritten, tupleAndTemplate.getValue0());
                test.assertBlocksIndefinitely(tupleSpace.takeTuple(tupleAndTemplate.getValue1()));
                stop();
            }

            @Override
            protected void onEnd() {
                test.done();
            }

        }.start();

        test.await();
        alice.await();
        bob.await();
    }

    @Test
    public void testAssociativeAccess() throws Exception {
        test.setThreadCount(3);

        final T tuple4Bob = getMessageTuple("Bob", "hi Bob");
        final T tuple4Carl = getMessageTuple("Carl", "hi Carl");

        final ActiveObject carl = new ActiveObject("Carl") {

            @Override
            protected void loop() throws Exception {
                test.assertEquals(tupleSpace.readTuple(getMessageTemplate("Carl")), tuple4Carl, "The tuple read by Carl should be equal to " + tuple4Carl);
                stop();
            }

            @Override
            protected void onEnd() {
                test.done();
            }

        }.start();

        final ActiveObject bob = new ActiveObject("Bob") {

            @Override
            protected void loop() throws Exception {
                test.assertEquals(tupleSpace.readTuple(getMessageTemplate("Bob")), tuple4Bob, "The tuple read by Bob should be equal to " + tuple4Bob);
                stop();
            }

            @Override
            protected void onEnd() {
                test.done();
            }

        }.start();

        final ActiveObject alice = new ActiveObject("Alice") {

            @Override
            protected void loop() throws Exception {
                test.assertEventuallyReturns(tupleSpace.write(tuple4Bob), "Alice should eventually be able to insert " + tuple4Bob);
                test.assertEventuallyReturns(tupleSpace.write(tuple4Carl), "Alice should eventually be able to insert " + tuple4Carl);

                final Set<T> ts = Set.of(tuple4Bob, tuple4Carl);

                test.assertOneOf(tupleSpace.takeTuple(getGeneralMessageTemplate()), ts, "The first tuple taken by Alice should be equal to any of" + ts);
                test.assertOneOf(tupleSpace.takeTuple(getGeneralMessageTemplate()), ts, "The second tuple taken by Alice should be equal to any of" + ts);

                stop();
            }

            @Override
            protected void onEnd() {
                test.done();
            }

        }.start();

        test.await();
        alice.await();
        bob.await();
        carl.await();
    }

    @Test
    public void testGetSize() throws Exception {
        test.setThreadCount(1);

        final var tupleAndTemplate = getATupleAndATemplateMatchingIt();

        final ActiveObject alice = new ActiveObject("Alice") {

            @Override
            protected void loop() throws Exception {
                test.assertEquals(tupleSpace.getSize(), 0);
                test.assertEventuallyReturns(tupleSpace.write(tupleAndTemplate.getValue0()));
                test.assertEquals(tupleSpace.getSize(), 1);
                test.assertEventuallyReturns(tupleSpace.write(tupleAndTemplate.getValue0()));
                test.assertEquals(tupleSpace.getSize(), 2);
                test.assertEventuallyReturns(tupleSpace.write(tupleAndTemplate.getValue0()));
                test.assertEquals(tupleSpace.getSize(), 3);

                test.assertEventuallyReturns(tupleSpace.takeTuple(tupleAndTemplate.getValue1()));
                test.assertEquals(tupleSpace.getSize(), 2);

                stop();
            }

            @Override
            protected void onEnd() {
                test.done();
            }

        }.start();

        test.await();
        alice.await();
    }

    @Test
    public void testGetAll() throws Exception {
        test.setThreadCount(1);

        final MultiSet<T> expected = getSomeTuples();

        final ActiveObject alice = new ActiveObject("Alice") {

            @Override
            protected void loop() throws Exception {

                for (var tuple : expected) {
                    test.assertEventuallyReturns(tupleSpace.write(tuple));
                }

                test.assertEquals(tupleSpace.getSize(), expected.size());

                test.assertEquals(tupleSpace.get(), expected);

                stop();
            }

            @Override
            protected void onEnd() {
                test.done();
            }

        }.start();

        test.await();
        alice.await();
    }

    @Test
    public void testWriteAll() throws Exception {
        test.setThreadCount(1);

        final MultiSet<T> tuples = getSomeTuples();

        final ActiveObject alice = new ActiveObject("Alice") {

            @Override
            protected void loop() throws Exception {
                test.assertEquals(tupleSpace.getSize(), 0, "The tuple space should initially be empty");
                test.assertEquals(tupleSpace.writeAll(tuples), tuples, "The write all operation should return all the inserted tuples");
                test.assertEquals(tupleSpace.getSize(), tuples.size(), "The tuple space size should now be equal to the amount of inserted tuples");
                test.assertEquals(tupleSpace.get(), tuples, "The get primitive should now retrieve all the tuples inserted so far");

                stop();
            }

            @Override
            protected void onEnd() {
                test.done();
            }

        }.start();

        test.await();
        alice.await();
    }

    @Test
    public void testWriteAllResumesSuspendedOperations() throws Exception {
        test.setThreadCount(2);

        final var someTuplesOfTwoSorts = getSomeTuplesOfTwoSorts();
        final MultiSet<T> tuples1 = someTuplesOfTwoSorts.getValue0();
        final MultiSet<T> tuples2 = someTuplesOfTwoSorts.getValue2();
        final MultiSet<T> tuples = new HashMultiSet<>(tuples1);
        tuples.addAll(tuples2);
        final TT template1 = someTuplesOfTwoSorts.getValue1();
        final TT template2 = someTuplesOfTwoSorts.getValue3();

        final ActiveObject alice = new ActiveObject("Alice") {

            @Override
            protected void loop() throws Exception {
                test.assertEventuallyReturns(tupleSpace.writeAll(tuples));

                stop();
            }

            @Override
            protected void onEnd() {
                test.done();
            }

        };

        final ActiveObject bob = new ActiveObject("Bob") {

            @Override
            protected void loop() throws Exception {
                final var toBeRead = tupleSpace.readTuple(template1);
                final var toBeTaken = tupleSpace.takeTuple(template2);

                alice.start();

                test.assertOneOf(toBeRead, tuples1);
                test.assertOneOf(toBeTaken, tuples2);

                stop();
            }

            @Override
            protected void onEnd() {
                test.done();
            }

        }.start();

        test.await();
        alice.await();
        bob.await();
    }

    @Test
    public void testReadAll() throws Exception {
        test.setThreadCount(1);

        final var someTuplesOfTwoSorts = getSomeTuplesOfTwoSorts();

        final MultiSet<T> tuples = new HashMultiSet<>(someTuplesOfTwoSorts.getValue0());
        tuples.addAll(someTuplesOfTwoSorts.getValue2());

        final TT template = someTuplesOfTwoSorts.getValue1();
        final MultiSet<T> expected = someTuplesOfTwoSorts.getValue0();

        final ActiveObject alice = new ActiveObject("Alice") {

            @Override
            protected void loop() throws Exception {
                test.assertEquals(tupleSpace.getSize(), 0);
                test.assertEventuallyReturns(tupleSpace.writeAll(tuples));
                test.assertEquals(tupleSpace.getSize(), tuples.size());
                test.assertEquals(tupleSpace.readAllTuples(template), expected);
                test.assertEquals(tupleSpace.readAllTuples(template), expected);
                test.assertEquals(tupleSpace.getSize(), tuples.size());

                stop();
            }

            @Override
            protected void onEnd() {
                test.done();
            }

        }.start();

        test.await();
        alice.await();
    }

    @Test
    public void testTryRead() throws Exception {
        test.setThreadCount(1);

        final var tupleAndTemplate = getATupleAndATemplateMatchingIt();
        final var tuple = tupleAndTemplate.getValue0();
        final var template = tupleAndTemplate.getValue1();

        final ActiveObject alice = new ActiveObject("Alice") {

            @Override
            protected void loop() throws Exception {
                test.assertEquals(tupleSpace.getSize(), 0);
                test.assertEventuallyReturns(tupleSpace.write(tuple));
                test.assertEquals(tupleSpace.getSize(), 1);
                test.assertEquals(tupleSpace.tryReadTuple(template), Optional.of(tuple));
                test.assertEquals(tupleSpace.getSize(), 1);
                test.assertEquals(tupleSpace.tryReadTuple(template), Optional.of(tuple));
                test.assertEquals(tupleSpace.getSize(), 1);
                stop();
            }

            @Override
            protected void onEnd() {
                test.done();
            }

        }.start();

        test.await();
        alice.await();
    }

    @Test
    public void testTryTake() throws Exception {
        test.setThreadCount(1);

        final var tupleAndTemplate = getATupleAndATemplateMatchingIt();
        final var tuple = tupleAndTemplate.getValue0();
        final var template = tupleAndTemplate.getValue1();

        final ActiveObject alice = new ActiveObject("Alice") {

            @Override
            protected void loop() throws Exception {
                test.assertEquals(tupleSpace.getSize(), 0);
                test.assertEventuallyReturns(tupleSpace.write(tuple));
                test.assertEquals(tupleSpace.getSize(), 1);
                test.assertEquals(tupleSpace.tryTakeTuple(template), Optional.of(tuple));
                test.assertEquals(tupleSpace.getSize(), 0);
                test.assertEquals(tupleSpace.tryTakeTuple(template), Optional.empty());
                stop();
            }

            @Override
            protected void onEnd() {
                test.done();
            }

        }.start();

        test.await();
        alice.await();
    }

    @Test
    public void testTakeAll() throws Exception {
        test.setThreadCount(1);

        final var someTuplesOfTwoSorts = getSomeTuplesOfTwoSorts();

        final MultiSet<T> tuples = new HashMultiSet<>(someTuplesOfTwoSorts.getValue0());
        tuples.addAll(someTuplesOfTwoSorts.getValue2());

        final TT template = someTuplesOfTwoSorts.getValue1();
        final MultiSet<T> expected = someTuplesOfTwoSorts.getValue0();

        final ActiveObject alice = new ActiveObject("Alice") {

            @Override
            protected void loop() throws Exception {
                test.assertEquals(tupleSpace.getSize(), 0);
                test.assertEventuallyReturns(tupleSpace.writeAll(tuples));
                test.assertEquals(tupleSpace.getSize(), tuples.size());
                test.assertEquals(tupleSpace.takeAllTuples(template), expected);
                test.assertEquals(tupleSpace.takeAllTuples(template), new HashMultiSet<>());
                test.assertEquals(tupleSpace.getSize(), tuples.size() - expected.size());

                stop();
            }

            @Override
            protected void onEnd() {
                test.done();
            }

        }.start();


        alice.await();
        test.await();
    }

    @Test
    public void testAbsentReturns() throws Exception {
        test.setThreadCount(1);

        final ActiveObject alice = new ActiveObject("Alice") {

            @Override
            protected void loop() throws Exception {
                test.assertEventuallyReturns(tupleSpace.absent(getATemplate()));
                stop();
            }

            @Override
            protected void onEnd() {
                test.done();
            }

        }.start();

        test.await();
        alice.await();
    }

    @Test
    public void testAbsentSuspends() throws Exception {
        test.setThreadCount(1);

        final var tupleAndTemplate = getATupleAndATemplateMatchingIt();
        final var tuple = tupleAndTemplate.getValue0();
        final var template = tupleAndTemplate.getValue1();

        final ActiveObject alice = new ActiveObject("Alice") {

            @Override
            protected void loop() throws Exception {
                test.assertEventuallyReturns(tupleSpace.write(tuple));
                test.assertBlocksIndefinitely(tupleSpace.absent(template));
                stop();
            }

            @Override
            protected void onEnd() {
                test.done();
            }

        }.start();

        test.await();
        alice.await();
    }

    @Test
    public void testTryAbsentSucceeds() throws Exception {
        test.setThreadCount(1);

        final ActiveObject alice = new ActiveObject("Alice") {

            @Override
            protected void loop() throws Exception {
                test.assertTrue(tupleSpace.tryAbsentTuple(getATemplate()), opt -> !opt.isPresent());
                stop();
            }

            @Override
            protected void onEnd() {
                test.done();
            }

        }.start();

        test.await();
        alice.await();
    }

    @Test
    public void testTryAbsentFails() throws Exception {
        test.setThreadCount(1);

        final var tupleAndTemplate = getATupleAndATemplateMatchingIt();
        final var tuple = tupleAndTemplate.getValue0();
        final var template = tupleAndTemplate.getValue1();

        final ActiveObject alice = new ActiveObject("Alice") {

            @Override
            protected void loop() throws Exception {
                test.assertEventuallyReturns(tupleSpace.write(tuple));
                test.assertEquals(tupleSpace.tryAbsentTuple(template), Optional.of(tuple));

                stop();
            }

            @Override
            protected void onEnd() {
                test.done();
            }

        }.start();

        test.await();
        alice.await();
    }

    @Test
    public void testTakeResumesAbsent() throws Exception {
        test.setThreadCount(2);

        final var tupleAndTemplate = getATupleAndATemplateMatchingIt();
        final var tuple = tupleAndTemplate.getValue0();
        final var template = tupleAndTemplate.getValue1();

        final ActiveObject bob = new ActiveObject("Bob") {

            @Override
            protected void loop() throws Exception {
                test.assertEquals(tupleSpace.takeTuple(template), tuple);
                stop();
            }

            @Override
            protected void onEnd() {
                test.done();
            }

        };

        final ActiveObject alice = new ActiveObject("Alice") {

            @Override
            protected void loop() throws Exception {
                test.assertEventuallyReturns(tupleSpace.write(tuple));
                final Future<?> toBeAbsent = tupleSpace.absent(template);
                bob.start();
                test.assertEventuallyReturns(toBeAbsent);
                stop();
            }

            @Override
            protected void onEnd() {
                test.done();
            }

        }.start();

        test.await();
        alice.await();
        bob.await();
    }

    @Test
    public void testTryTakeResumesAbsent() throws Exception {
        test.setThreadCount(2);

        final var tupleAndTemplate = getATupleAndATemplateMatchingIt();
        final var tuple = tupleAndTemplate.getValue0();
        final var template = tupleAndTemplate.getValue1();

        final ActiveObject bob = new ActiveObject("Bob") {

            @Override
            protected void loop() throws Exception {
                test.assertEquals(tupleSpace.tryTakeTuple(template), Optional.of(tuple));
                stop();
            }

            @Override
            protected void onEnd() {
                test.done();
            }

        };

        final ActiveObject alice = new ActiveObject("Alice") {

            @Override
            protected void loop() throws Exception {
                test.assertEventuallyReturns(tupleSpace.write(tuple));
                final Future<?> toBeAbsent = tupleSpace.absent(template);
                bob.start();
                test.assertEventuallyReturns(toBeAbsent);
                stop();
            }

            @Override
            protected void onEnd() {
                test.done();
            }

        }.start();

        test.await();
        alice.await();
        bob.await();
    }

    @Test
    public void testTakeAllResumesAbsent() throws Exception {
        test.setThreadCount(2);

        final var someTuplesOfASort = getSomeTuplesOfOneSort();
        final MultiSet<T> tuples = someTuplesOfASort.getValue0();
        final TT template = someTuplesOfASort.getValue1();

        final ActiveObject bob = new ActiveObject("Bob") {

            @Override
            protected void loop() throws Exception {
                test.assertEquals(tupleSpace.takeAllTuples(template), tuples);
                stop();
            }

            @Override
            protected void onEnd() {
                test.done();
            }

        };

        final ActiveObject alice = new ActiveObject("Alice") {

            @Override
            protected void loop() throws Exception {
                test.assertEquals(tupleSpace.writeAll(tuples), tuples);
                final Future<?> toBeAbsent = tupleSpace.absent(template);
                bob.start();
                test.assertEventuallyReturns(toBeAbsent);
                stop();
            }

            @Override
            protected void onEnd() {
                test.done();
            }

        }.start();

        test.await();
        alice.await();
        bob.await();
    }
}
