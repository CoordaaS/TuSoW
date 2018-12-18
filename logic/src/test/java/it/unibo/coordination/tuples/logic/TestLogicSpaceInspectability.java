package it.unibo.coordination.tuples.logic;

import it.unibo.coordination.tuples.core.OperationType;
import it.unibo.coordination.tuples.core.events.OperationEvent;
import it.unibo.coordination.tuples.core.events.TupleSpaceEvent;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class TestLogicSpaceInspectability {

    protected ExecutorService executor;
    protected LogicSpace tupleSpace;
    protected ConcurrentTestHelper test;
    protected Random rand;

    private static final Duration MAX_WAIT = Duration.ofSeconds(2);

    private static <T> T await(Future<T> future) throws InterruptedException, ExecutionException, TimeoutException {
        return future.get(MAX_WAIT.toMillis(), TimeUnit.MILLISECONDS);

    }

    @Before
    public void setUp() throws Exception {
        executor = Executors.newSingleThreadExecutor();
        tupleSpace = LogicSpace.create(executor);
        test = new ConcurrentTestHelper();
        rand = new Random();
    }

    @After
    public void tearDown() throws Exception {
        executor.shutdown();
    }

    @Test
    public void testReadEvents() throws Exception {
        final List<TupleSpaceEvent<LogicTuple, LogicTemplate>> observableBehaviour = new LinkedList<>();

        tupleSpace.operationInvoked().bind(observableBehaviour::add);
        tupleSpace.tupleSpaceChanged().bind(observableBehaviour::add);
        tupleSpace.operationCompleted().bind(observableBehaviour::add);

        try {
            await(tupleSpace.read("f(X)"));
            Assert.fail();
        } catch (TimeoutException e) {
            Assert.assertEquals(1, observableBehaviour.size());
            Assert.assertEquals(
                    List.of(
                            OperationEvent.templateAcceptingInvocation(tupleSpace, OperationType.READ, LogicTemplate.of("f(X)"))
                    ),
                    observableBehaviour
            );
        }

    }

    /*
    @Test
    public void testTakeSuspensiveSemantics() throws Exception {
        test.setThreadCount(1);

        final ActiveObject alice = new ActiveObject("Alice") {

            @Override
            protected void loop() throws Exception {
                test.assertBlocksIndefinitely(tupleSpace.take("f(x)"),
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

        final ActiveObject alice = new ActiveObject("Alice") {

            @Override
            protected void loop() throws Exception {
                test.assertEquals(tupleSpace.getSize(), 0);
                test.assertEquals(tupleSpace.write("s(z)"), LogicTuple.of("s(z)"));
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
    public void testReadIsIdempotent1() throws Exception {
        test.setThreadCount(2);

        final LogicTuple tuple = LogicTuple.of("s(z)");

        final ActiveObject bob = new ActiveObject("Bob") {

            @Override
            protected void loop() throws Exception {
                for (int i = rand.nextInt(10) + 1; i >= 0; i--) {
                    test.assertEquals(tupleSpace.read("s(X)"), tuple);
                }
                test.assertEquals(tupleSpace.read("s(Y)"), tuple);
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

        final LogicTuple tuple = LogicTuple.of("s(z)");

        final ActiveObject alice = new ActiveObject("Alice") {

            @Override
            protected void loop() throws Exception {
                test.assertEventuallyReturns(tupleSpace.write(tuple));
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
                final Future<LogicTuple> toBeRead1 = tupleSpace.read("s(X)");
                final Future<LogicTuple> toBeRead2 = tupleSpace.read("s(Y)");

                alice.start();

                test.assertEquals(toBeRead1, tuple);
                test.assertEquals(toBeRead2, tuple);

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

        final LogicTuple tuple = LogicTuple.of("foo(bar)");

        final ActiveObject bob = new ActiveObject("Bob") {

            @Override
            protected void loop() throws Exception {
                test.assertEquals(tupleSpace.take("foo(X)"), tuple);
                test.assertBlocksIndefinitely(tupleSpace.take("foo(_)"));
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

        final LogicTuple tuple = LogicTuple.of("foo(bar)");

        final ActiveObject alice = new ActiveObject("Alice") {

            @Override
            protected void loop() throws Exception {
                test.assertEventuallyReturns(tupleSpace.write(tuple));
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
                final Future<LogicTuple> toBeWritten = tupleSpace.take("foo(X)");
                alice.start();
                test.assertEquals(toBeWritten, tuple);
                test.assertBlocksIndefinitely(tupleSpace.take("foo(_)"));
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

        final LogicTuple tuple4Bob = LogicTuple.of("msg(to(bob), hi_bob)");
        final LogicTuple tuple4Carl = LogicTuple.of("msg(to(carl), hi_carl)");

        final ActiveObject carl = new ActiveObject("Carl") {

            @Override
            protected void loop() throws Exception {
                test.assertEquals(tupleSpace.read("msg(to(carl), M)"), tuple4Carl);
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
                test.assertEquals(tupleSpace.read("msg(to(bob), M)"), tuple4Bob);
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
                test.assertEventuallyReturns(tupleSpace.write(tuple4Bob));
                test.assertEventuallyReturns(tupleSpace.write(tuple4Carl));

                test.assertOneOf(tupleSpace.take("msg(to(_), M)"), tuple4Bob, tuple4Carl);
                test.assertOneOf(tupleSpace.take("msg(to(_), M)"), tuple4Bob, tuple4Carl);

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

        final ActiveObject alice = new ActiveObject("Alice") {

            @Override
            protected void loop() throws Exception {
                test.assertEquals(tupleSpace.getSize(), 0);
                test.assertEventuallyReturns(tupleSpace.write("a"));
                test.assertEquals(tupleSpace.getSize(), 1);
                test.assertEventuallyReturns(tupleSpace.write("a"));
                test.assertEquals(tupleSpace.getSize(), 2);
                test.assertEventuallyReturns(tupleSpace.write("a"));
                test.assertEquals(tupleSpace.getSize(), 3);

                test.assertEventuallyReturns(tupleSpace.take("a"));
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

        final MultiSet<LogicTuple> expected = new HashMultiSet<>(
                Arrays.asList(LogicTuple.of("b"), LogicTuple.of("c"), LogicTuple.of("a"), LogicTuple.of("b")));

        final ActiveObject alice = new ActiveObject("Alice") {

            @Override
            protected void loop() throws Exception {
                test.assertEventuallyReturns(tupleSpace.write("a"));
                test.assertEventuallyReturns(tupleSpace.write("b"));
                test.assertEventuallyReturns(tupleSpace.write("b"));
                test.assertEventuallyReturns(tupleSpace.write("c"));

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

        final MultiSet<LogicTuple> tuples = new HashMultiSet<>(Arrays.asList(LogicTuple.of("b(2)"),
                LogicTuple.of("c(3)"), LogicTuple.of("a(1)"), LogicTuple.of("b(2)")));

        final ActiveObject alice = new ActiveObject("Alice") {

            @Override
            protected void loop() throws Exception {
                test.assertEquals(tupleSpace.getSize(), 0);
                test.assertEquals(tupleSpace.writeAll(tuples), tuples);
                test.assertEquals(tupleSpace.getSize(), tuples.size());
                test.assertEquals(tupleSpace.get(), tuples);

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

        final List<LogicTuple> tuples = Stream.of("f(x)", "f(y)", "g(x)", "g(y)").map(LogicTuple::of)
                .collect(Collectors.toList());

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
                final Future<LogicTuple> toBeRead = tupleSpace.read("f(A)");
                final Future<LogicTuple> toBeTaken = tupleSpace.take("g(A)");

                alice.start();

                test.assertOneOf(toBeRead, tuples.get(0), tuples.get(1));
                test.assertOneOf(toBeTaken, tuples.get(2), tuples.get(3));

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

        final List<LogicTuple> tuples = Arrays.asList(LogicTuple.of("a(2)"), LogicTuple.of("a(3)"),
                LogicTuple.of("a(1)"), LogicTuple.of("a(4)"), LogicTuple.of("b(5)"));

        final MultiSet<LogicTuple> expected = new HashMultiSet<>(tuples.subList(0, 4));

        final ActiveObject alice = new ActiveObject("Alice") {

            @Override
            protected void loop() throws Exception {
                test.assertEquals(tupleSpace.getSize(), 0);
                test.assertEventuallyReturns(tupleSpace.writeAll(tuples));
                test.assertEquals(tupleSpace.getSize(), tuples.size());
                test.assertEquals(tupleSpace.readAll("a(N)"), expected);
                test.assertEquals(tupleSpace.readAll("a(N)"), expected);
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

        final ActiveObject alice = new ActiveObject("Alice") {

            @Override
            protected void loop() throws Exception {
                test.assertEquals(tupleSpace.getSize(), 0);
                test.assertEventuallyReturns(tupleSpace.write("p(a)"));
                test.assertEquals(tupleSpace.getSize(), 1);
                test.assertEquals(tupleSpace.tryRead("p(Z)"), Optional.of(LogicTuple.of("p(a)")));
                test.assertEquals(tupleSpace.getSize(), 1);
                test.assertEquals(tupleSpace.tryRead("p(Z)"), Optional.of(LogicTuple.of("p(a)")));
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

        final ActiveObject alice = new ActiveObject("Alice") {

            @Override
            protected void loop() throws Exception {
                test.assertEquals(tupleSpace.getSize(), 0);
                test.assertEventuallyReturns(tupleSpace.write("p(a)"));
                test.assertEquals(tupleSpace.getSize(), 1);
                test.assertEquals(tupleSpace.tryTake("p(Z)"), Optional.of(LogicTuple.of("p(a)")));
                test.assertEquals(tupleSpace.getSize(), 0);
                test.assertEquals(tupleSpace.tryTake("p(Z)"), Optional.empty());
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

        final List<LogicTuple> tuples = Arrays.asList(LogicTuple.of("a(2)"), LogicTuple.of("a(3)"),
                LogicTuple.of("a(1)"), LogicTuple.of("a(4)"), LogicTuple.of("b(5)"));

        final MultiSet<LogicTuple> expected = new HashMultiSet<>(tuples.subList(0, 4));

        final ActiveObject alice = new ActiveObject("Alice") {

            @Override
            protected void loop() throws Exception {
                test.assertEquals(tupleSpace.getSize(), 0);
                test.assertEventuallyReturns(tupleSpace.writeAll(tuples));
                test.assertEquals(tupleSpace.getSize(), tuples.size());
                test.assertEquals(tupleSpace.takeAll("a(N)"), expected);
                test.assertEquals(tupleSpace.takeAll("a(N)"), new HashMultiSet<>());
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
    public void testAbsentReturns() throws Exception {
        test.setThreadCount(1);

        final ActiveObject alice = new ActiveObject("Alice") {

            @Override
            protected void loop() throws Exception {
                test.assertEventuallyReturns(tupleSpace.absent("f(X)"));
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

        final ActiveObject alice = new ActiveObject("Alice") {

            @Override
            protected void loop() throws Exception {
                test.assertEventuallyReturns(tupleSpace.write("f(1)"));
                test.assertBlocksIndefinitely(tupleSpace.absent("f(X)"));
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
                test.assertTrue(tupleSpace.tryAbsent("f(X)"), opt -> !opt.isPresent());
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

        final ActiveObject alice = new ActiveObject("Alice") {

            @Override
            protected void loop() throws Exception {
                test.assertEventuallyReturns(tupleSpace.write("f(1)"));
                test.assertEquals(tupleSpace.tryAbsent("f(X)"), Optional.of(LogicTuple.of("f(1)")));

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

        final LogicTuple tuple = LogicTuple.of("foo(bar)");

        final ActiveObject bob = new ActiveObject("Bob") {

            @Override
            protected void loop() throws Exception {
                test.assertEquals(tupleSpace.take("foo(B)"), tuple);
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
                final Future<LogicTemplate> toBeAbsent = tupleSpace.absent("foo(X)");
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

        final LogicTuple tuple = LogicTuple.of("foo(bar)");

        final ActiveObject bob = new ActiveObject("Bob") {

            @Override
            protected void loop() throws Exception {
                test.assertEquals(tupleSpace.tryTake("foo(B)"), Optional.of(tuple));
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
                final Future<LogicTemplate> toBeAbsent = tupleSpace.absent("foo(X)");
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

        final MultiSet<LogicTuple> tuples = new HashMultiSet<>(
                Arrays.asList(LogicTuple.of("foo(bar)"), LogicTuple.of("foo(baz)")));

        final ActiveObject bob = new ActiveObject("Bob") {

            @Override
            protected void loop() throws Exception {
                test.assertEquals(tupleSpace.takeAll("foo(B)"), tuples);
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
                final Future<LogicTemplate> toBeAbsent = tupleSpace.absent("foo(X)");
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

    */
}
