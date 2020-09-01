package it.unibo.coordination.linda.test;

import it.unibo.coordination.linda.core.Match;
import it.unibo.coordination.linda.core.Template;
import it.unibo.coordination.linda.core.Tuple;
import it.unibo.coordination.linda.core.TupleSpace;
import it.unibo.coordination.testing.ConcurrentTestHelper;
import it.unibo.coordination.testing.TestAgent;
import it.unibo.coordination.utils.CollectionUtils;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.multiset.HashMultiSet;
import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@SuppressWarnings("RedundantThrows")
public abstract class TestTupleSpace<T extends Tuple<T>, TT extends Template<T>, K, V, M extends Match<T, TT, K, V>, TS extends TupleSpace<T, TT, K, V, M>> extends TestBaseLinda<T, TT, K, V, M> {

    protected ExecutorService executor;
    protected TupleSpace<T, TT, K, V, M> tupleSpace;
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

        final TestAgent alice = new TestAgent("Alice", test) {
            @Override
            protected void main() throws Exception {
                assertEquals(tupleSpace.getSize(), 0, "The tuple space must initially be empty");
            }
        }.start();

        test.await();
        alice.awaitTermination();
    }

    @Test
    public void testReadSuspensiveSemantics() throws Exception {
        test.setThreadCount(1);

        final TestAgent alice = new TestAgent("Alice", test) {

            @Override
            protected void main() throws Exception {
                assertBlocksIndefinitely(tupleSpace.readTuple(getATemplate()),
                        "A read operation should block if no tuple matching the requested template is available");
            }

        }.start();

        test.await();
        alice.awaitTermination();
    }

    @Test
    public void testTakeSuspensiveSemantics() throws Exception {
        test.setThreadCount(1);

        final TestAgent alice = new TestAgent("Alice", test) {
            @Override
            protected void main() throws Exception {
                assertBlocksIndefinitely(tupleSpace.takeTuple(getATemplate()),
                        "A take operation should block if no tuple matching the requested template is available");
            }
        }.start();

        test.await();
        alice.awaitTermination();
    }

    @Test
    public void testWriteGenerativeSemantics() throws Exception {
        test.setThreadCount(1);

        final T tuple = getATuple();

        final TestAgent alice = new TestAgent("Alice", test) {

            @Override
            protected void main() throws Exception {
                assertEquals(tupleSpace.getSize(), 0, "The tuple space should be initially empty");
                assertEquals(tupleSpace.write(tuple), tuple, "A write operation should always return the tuple it has inserted");
                assertEquals(tupleSpace.getSize(), 1, "After a write, the tuple space should contain one more tuple");
            }

        }.start();

        test.await();
        alice.awaitTermination();
    }

    @Test
    public void testReadIsIdempotent1() throws Exception {
        test.setThreadCount(2);

        final Pair<T, TT> tupleAndTemplate = getATupleAndATemplateMatchingIt();

        final TestAgent bob = new TestAgent("Bob", test) {

            @Override
            protected void main() throws Exception {
                for (int i = rand.nextInt(10) + 1; i >= 0; i--) {
                    assertEquals(tupleSpace.readTuple(tupleAndTemplate.getValue1()), tupleAndTemplate.getValue0());
                }
                assertEquals(tupleSpace.readTuple(tupleAndTemplate.getValue1()), tupleAndTemplate.getValue0());
            }

        };

        final TestAgent alice = new TestAgent("Alice", test) {

            @Override
            protected void main() throws Exception {
                assertEventuallyReturns(tupleSpace.write(tupleAndTemplate.getValue0()));
                bob.start();
            }

        }.start();

        test.await();
        alice.awaitTermination();
        bob.awaitTermination();
    }

    @Test
    public void testReadIsIdempotent2() throws Exception {
        test.setThreadCount(2);

        final Pair<T, TT> tupleAndTemplate = getATupleAndATemplateMatchingIt();

        final TestAgent alice = new TestAgent("Alice", test) {

            @Override
            protected void main() throws Exception {
                assertEventuallyReturns(tupleSpace.write(tupleAndTemplate.getValue0()));
            }

        };

        final TestAgent bob = new TestAgent("Bob", test) {

            @Override
            protected void main() throws Exception {
                final Future<T> toBeRead1 = tupleSpace.readTuple(tupleAndTemplate.getValue1());
                final Future<T> toBeRead2 = tupleSpace.readTuple(tupleAndTemplate.getValue1());

                alice.start();

                assertEquals(toBeRead1, tupleAndTemplate.getValue0());
                assertEquals(toBeRead2, tupleAndTemplate.getValue0());
            }

        }.start();

        test.await();
        alice.awaitTermination();
        bob.awaitTermination();
    }

    @Test
    public void testTakeIsNotIdempotent1() throws Exception {
        test.setThreadCount(2);

        final Pair<T, TT> tupleAndTemplate = getATupleAndATemplateMatchingIt();

        final TestAgent bob = new TestAgent("Bob", test) {

            @Override
            protected void main() throws Exception {
                assertEquals(tupleSpace.takeTuple(tupleAndTemplate.getValue1()), tupleAndTemplate.getValue0());
                assertBlocksIndefinitely(tupleSpace.takeTuple(tupleAndTemplate.getValue1()));
            }

        };

        final TestAgent alice = new TestAgent("Alice", test) {

            @Override
            protected void main() throws Exception {
                assertEventuallyReturns(tupleSpace.write(tupleAndTemplate.getValue0()));
                bob.start();
            }

        }.start();

        test.await();
        alice.awaitTermination();
        bob.awaitTermination();
    }

    @Test
    public void testTakeIsNotIdempotent2() throws Exception {
        test.setThreadCount(2);

        final Pair<T, TT> tupleAndTemplate = getATupleAndATemplateMatchingIt();

        final TestAgent alice = new TestAgent("Alice", test) {

            @Override
            protected void main() throws Exception {
                assertEventuallyReturns(tupleSpace.write(tupleAndTemplate.getValue0()));
            }

        };

        final TestAgent bob = new TestAgent("Bob", test) {

            @Override
            protected void main() throws Exception {
                final CompletableFuture<T> toBeWritten = tupleSpace.takeTuple(tupleAndTemplate.getValue1());
                alice.start();
                assertEquals(toBeWritten, tupleAndTemplate.getValue0());
                assertBlocksIndefinitely(tupleSpace.takeTuple(tupleAndTemplate.getValue1()));
            }

        }.start();

        test.await();
        alice.awaitTermination();
        bob.awaitTermination();
    }

    @Test
    public void testAssociativeAccess() throws Exception {
        test.setThreadCount(3);

        final T tuple4Bob = getMessageTuple("Bob", "hi Bob");
        final T tuple4Carl = getMessageTuple("Carl", "hi Carl");

        final TestAgent carl = new TestAgent("Carl", test) {

            @Override
            protected void main() throws Exception {
                assertEquals(tupleSpace.readTuple(getMessageTemplate("Carl")), tuple4Carl, "The tuple read by Carl should be equal to " + tuple4Carl);
            }

        }.start();

        final TestAgent bob = new TestAgent("Bob", test) {

            @Override
            protected void main() throws Exception {
                assertEquals(tupleSpace.readTuple(getMessageTemplate("Bob")), tuple4Bob, "The tuple read by Bob should be equal to " + tuple4Bob);
            }

        }.start();

        final TestAgent alice = new TestAgent("Alice", test) {

            @Override
            protected void main() throws Exception {
                assertEventuallyReturns(tupleSpace.write(tuple4Bob), "Alice should eventually be able to insert " + tuple4Bob);
                assertEventuallyReturns(tupleSpace.write(tuple4Carl), "Alice should eventually be able to insert " + tuple4Carl);

                final Set<T> ts = CollectionUtils.setOf(tuple4Bob, tuple4Carl);

                assertOneOf(tupleSpace.takeTuple(getGeneralMessageTemplate()), ts, "The first tuple taken by Alice should be equal to any of" + ts);
                assertOneOf(tupleSpace.takeTuple(getGeneralMessageTemplate()), ts, "The second tuple taken by Alice should be equal to any of" + ts);
            }

        }.start();

        test.await();
        alice.awaitTermination();
        bob.awaitTermination();
        carl.awaitTermination();
    }

    @Test
    public void testGetSize() throws Exception {
        test.setThreadCount(1);

        final Pair<T, TT> tupleAndTemplate = getATupleAndATemplateMatchingIt();

        final TestAgent alice = new TestAgent("Alice", test) {

            @Override
            protected void main() throws Exception {
                assertEquals(tupleSpace.getSize(), 0);
                assertEventuallyReturns(tupleSpace.write(tupleAndTemplate.getValue0()));
                assertEquals(tupleSpace.getSize(), 1);
                assertEventuallyReturns(tupleSpace.write(tupleAndTemplate.getValue0()));
                assertEquals(tupleSpace.getSize(), 2);
                assertEventuallyReturns(tupleSpace.write(tupleAndTemplate.getValue0()));
                assertEquals(tupleSpace.getSize(), 3);

                assertEventuallyReturns(tupleSpace.takeTuple(tupleAndTemplate.getValue1()));
                assertEquals(tupleSpace.getSize(), 2);
            }

        }.start();

        test.await();
        alice.awaitTermination();
    }

    @Test
    public void testGetAll() throws Exception {
        test.setThreadCount(1);

        final MultiSet<T> expected = getSomeTuples();

        final TestAgent alice = new TestAgent("Alice", test) {

            @Override
            protected void main() throws Exception {

                for (T tuple : expected) {
                    assertEventuallyReturns(tupleSpace.write(tuple));
                }

                assertEquals(tupleSpace.getSize(), expected.size());
                assertEquals(tupleSpace.get(), expected);
            }

        }.start();

        test.await();
        alice.awaitTermination();
    }

    @Test
    public void testWriteAll() throws Exception {
        test.setThreadCount(1);

        final MultiSet<T> tuples = getSomeTuples();

        final TestAgent alice = new TestAgent("Alice", test) {

            @Override
            protected void main() throws Exception {
                assertEquals(tupleSpace.getSize(), 0, "The tuple space should initially be empty");
                assertEquals(tupleSpace.writeAll(tuples), tuples, "The write all operation should return all the inserted tuples");
                assertEquals(tupleSpace.getSize(), tuples.size(), "The tuple space size should now be equal to the amount of inserted tuples");
                assertEquals(tupleSpace.get(), tuples, "The get primitive should now retrieve all the tuples inserted so far");
            }

        }.start();

        test.await();
        alice.awaitTermination();
    }

    @Test
    public void testWriteAllResumesSuspendedOperations() throws Exception {
        test.setThreadCount(2);

        final Quartet<MultiSet<T>, TT, MultiSet<T>, TT> someTuplesOfTwoSorts = getSomeTuplesOfTwoSorts();
        final MultiSet<T> tuples1 = someTuplesOfTwoSorts.getValue0();
        final MultiSet<T> tuples2 = someTuplesOfTwoSorts.getValue2();
        final MultiSet<T> tuples = new HashMultiSet<>(tuples1);
        tuples.addAll(tuples2);
        final TT template1 = someTuplesOfTwoSorts.getValue1();
        final TT template2 = someTuplesOfTwoSorts.getValue3();

        final TestAgent alice = new TestAgent("Alice", test) {

            @Override
            protected void main() throws Exception {
                assertEventuallyReturns(tupleSpace.writeAll(tuples));
            }

        };

        final TestAgent bob = new TestAgent("Bob", test) {

            @Override
            protected void main() throws Exception {
                final CompletableFuture<T> toBeRead = tupleSpace.readTuple(template1);
                final CompletableFuture<T> toBeTaken = tupleSpace.takeTuple(template2);

                alice.start();

                assertOneOf(toBeRead, tuples1);
                assertOneOf(toBeTaken, tuples2);
            }

        }.start();

        test.await();
        alice.awaitTermination();
        bob.awaitTermination();
    }

    @Test
    public void testReadAll() throws Exception {
        test.setThreadCount(1);

        final Quartet<MultiSet<T>, TT, MultiSet<T>, TT> someTuplesOfTwoSorts = getSomeTuplesOfTwoSorts();

        final MultiSet<T> tuples = new HashMultiSet<>(someTuplesOfTwoSorts.getValue0());
        tuples.addAll(someTuplesOfTwoSorts.getValue2());

        final TT template = someTuplesOfTwoSorts.getValue1();
        final MultiSet<T> expected = someTuplesOfTwoSorts.getValue0();

        final TestAgent alice = new TestAgent("Alice", test) {

            @Override
            protected void main() throws Exception {
                assertEquals(tupleSpace.getSize(), 0);
                assertEventuallyReturns(tupleSpace.writeAll(tuples));
                assertEquals(tupleSpace.getSize(), tuples.size());
                assertEquals(tupleSpace.readAllTuples(template), expected);
                assertEquals(tupleSpace.readAllTuples(template), expected);
                assertEquals(tupleSpace.getSize(), tuples.size());
            }

        }.start();

        test.await();
        alice.awaitTermination();
    }

    @Test
    public void testTryRead() throws Exception {
        test.setThreadCount(1);

        final Pair<T, TT> tupleAndTemplate = getATupleAndATemplateMatchingIt();
        final T tuple = tupleAndTemplate.getValue0();
        final TT template = tupleAndTemplate.getValue1();

        final TestAgent alice = new TestAgent("Alice", test) {

            @Override
            protected void main() throws Exception {
                assertEquals(tupleSpace.getSize(), 0);
                assertEventuallyReturns(tupleSpace.write(tuple));
                assertEquals(tupleSpace.getSize(), 1);
                assertEquals(tupleSpace.tryReadTuple(template), Optional.of(tuple));
                assertEquals(tupleSpace.getSize(), 1);
                assertEquals(tupleSpace.tryReadTuple(template), Optional.of(tuple));
                assertEquals(tupleSpace.getSize(), 1);
            }

        }.start();

        test.await();
        alice.awaitTermination();
    }

    @Test
    public void testTryTake() throws Exception {
        test.setThreadCount(1);

        final Pair<T, TT> tupleAndTemplate = getATupleAndATemplateMatchingIt();
        final T tuple = tupleAndTemplate.getValue0();
        final TT template = tupleAndTemplate.getValue1();

        final TestAgent alice = new TestAgent("Alice", test) {

            @Override
            protected void main() throws Exception {
                assertEquals(tupleSpace.getSize(), 0);
                assertEventuallyReturns(tupleSpace.write(tuple));
                assertEquals(tupleSpace.getSize(), 1);
                assertEquals(tupleSpace.tryTakeTuple(template), Optional.of(tuple));
                assertEquals(tupleSpace.getSize(), 0);
                assertEquals(tupleSpace.tryTakeTuple(template), Optional.empty());
            }

        }.start();

        test.await();
        alice.awaitTermination();
    }

    @Test
    public void testTakeAll() throws Exception {
        test.setThreadCount(1);

        final Quartet<MultiSet<T>, TT, MultiSet<T>, TT> someTuplesOfTwoSorts = getSomeTuplesOfTwoSorts();

        final MultiSet<T> tuples = new HashMultiSet<>(someTuplesOfTwoSorts.getValue0());
        tuples.addAll(someTuplesOfTwoSorts.getValue2());

        final TT template = someTuplesOfTwoSorts.getValue1();
        final MultiSet<T> expected = someTuplesOfTwoSorts.getValue0();

        final TestAgent alice = new TestAgent("Alice", test) {

            @Override
            protected void main() throws Exception {
                assertEquals(tupleSpace.getSize(), 0);
                assertEventuallyReturns(tupleSpace.writeAll(tuples));
                assertEquals(tupleSpace.getSize(), tuples.size());
                assertEquals(tupleSpace.takeAllTuples(template), expected);
                assertEquals(tupleSpace.takeAllTuples(template), new HashMultiSet<>());
                assertEquals(tupleSpace.getSize(), tuples.size() - expected.size());
            }

        }.start();


        alice.awaitTermination();
        test.await();
    }

    @Test
    public void testAbsentReturns() throws Exception {
        test.setThreadCount(1);

        final TestAgent alice = new TestAgent("Alice", test) {

            @Override
            protected void main() throws Exception {
                assertEventuallyReturns(tupleSpace.absent(getATemplate()));
            }

        }.start();

        test.await();
        alice.awaitTermination();
    }

    @Test
    public void testAbsentSuspends() throws Exception {
        test.setThreadCount(1);

        final Pair<T, TT> tupleAndTemplate = getATupleAndATemplateMatchingIt();
        final T tuple = tupleAndTemplate.getValue0();
        final TT template = tupleAndTemplate.getValue1();

        final TestAgent alice = new TestAgent("Alice", test) {

            @Override
            protected void main() throws Exception {
                assertEventuallyReturns(tupleSpace.write(tuple));
                assertBlocksIndefinitely(tupleSpace.absent(template));
            }

        }.start();

        test.await();
        alice.awaitTermination();
    }

    @Test
    public void testTryAbsentSucceeds() throws Exception {
        test.setThreadCount(1);

        final TestAgent alice = new TestAgent("Alice", test) {

            @Override
            protected void main() throws Exception {
                assertTrue(tupleSpace.tryAbsentTuple(getATemplate()), opt -> !opt.isPresent());
            }

        }.start();

        test.await();
        alice.awaitTermination();
    }

    @Test
    public void testTryAbsentFails() throws Exception {
        test.setThreadCount(1);

        final Pair<T, TT> tupleAndTemplate = getATupleAndATemplateMatchingIt();
        final T tuple = tupleAndTemplate.getValue0();
        final TT template = tupleAndTemplate.getValue1();

        final TestAgent alice = new TestAgent("Alice", test) {

            @Override
            protected void main() throws Exception {
                assertEventuallyReturns(tupleSpace.write(tuple));
                assertEquals(tupleSpace.tryAbsentTuple(template), Optional.of(tuple));
            }

        }.start();

        test.await();
        alice.awaitTermination();
    }

    @Test
    public void testTakeResumesAbsent() throws Exception {
        test.setThreadCount(2);

        final Pair<T, TT> tupleAndTemplate = getATupleAndATemplateMatchingIt();
        final T tuple = tupleAndTemplate.getValue0();
        final TT template = tupleAndTemplate.getValue1();

        final TestAgent bob = new TestAgent("Bob", test) {

            @Override
            protected void main() {
                assertEquals(tupleSpace.takeTuple(template), tuple);
            }

        };

        final TestAgent alice = new TestAgent("Alice", test) {

            @Override
            protected void main() throws Exception {
                assertEventuallyReturns(tupleSpace.write(tuple));
                final Future<?> toBeAbsent = tupleSpace.absent(template);
                bob.start();
                assertEventuallyReturns(toBeAbsent);
            }

        }.start();

        test.await();
        alice.awaitTermination();
        bob.awaitTermination();
    }

    @Test
    public void testTryTakeResumesAbsent() throws Exception {
        test.setThreadCount(2);

        final Pair<T, TT> tupleAndTemplate = getATupleAndATemplateMatchingIt();
        final T tuple = tupleAndTemplate.getValue0();
        final TT template = tupleAndTemplate.getValue1();

        final TestAgent bob = new TestAgent("Bob", test) {

            @Override
            protected void main() throws Exception {
                assertEquals(tupleSpace.tryTakeTuple(template), Optional.of(tuple));
            }

        };

        final TestAgent alice = new TestAgent("Alice", test) {

            @Override
            protected void main() throws Exception {
                assertEventuallyReturns(tupleSpace.write(tuple));
                final Future<?> toBeAbsent = tupleSpace.absent(template);
                bob.start();
                assertEventuallyReturns(toBeAbsent);
            }

        }.start();

        test.await();
        alice.awaitTermination();
        bob.awaitTermination();
    }

    @Test
    public void testTakeAllResumesAbsent() throws Exception {
        test.setThreadCount(2);

        final Pair<MultiSet<T>, TT> someTuplesOfASort = getSomeTuplesOfOneSort();
        final MultiSet<T> tuples = someTuplesOfASort.getValue0();
        final TT template = someTuplesOfASort.getValue1();

        final TestAgent bob = new TestAgent("Bob", test) {

            @Override
            protected void main() throws Exception {
                assertEquals(tupleSpace.takeAllTuples(template), tuples);
            }

        };

        final TestAgent alice = new TestAgent("Alice", test) {

            @Override
            protected void main() throws Exception {
                assertEquals(tupleSpace.writeAll(tuples), tuples);
                final Future<?> toBeAbsent = tupleSpace.absent(template);
                bob.start();
                assertEventuallyReturns(toBeAbsent);
            }

        }.start();

        test.await();
        alice.awaitTermination();
        bob.awaitTermination();
    }
}
