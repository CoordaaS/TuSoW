package it.unibo.sd1819.lab10.ts.logic;

import it.unibo.coordination.flow.BaseAgent;
import it.unibo.coordination.flow.Continuation;
import it.unibo.coordination.flow.DistributedEnvironment;
import it.unibo.coordination.flow.Environment;
import it.unibo.sd1819.lab10.tusow.TuSoWService;
import it.unibo.sd1819.test.ConcurrentTestHelper;
import org.junit.*;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestLogicTupleSpace {

    private static final Duration MAX_WAIT = Duration.ofSeconds(3);

    protected ConcurrentTestHelper test;
    protected Random rand;
    protected Environment mas;

    @BeforeClass
    public static void setUpClass() {
        TuSoWService.start("-p", "8080", "-r", "tuple-spaces");
    }

    @AfterClass
    public static void tearDownClass() {
        TuSoWService.stop();
    }

    @Before
    public void setUp() {
        test = new ConcurrentTestHelper();
        rand = new Random();
        mas = new DistributedEnvironment("localhost", 8080, "tuple-spaces");
    }

    @After
    public void tearDown() throws InterruptedException {
        mas.shutdown().awaitShutdown(MAX_WAIT);
    }

    @Test
    public void testInitiallyEmpty() throws Exception {
        test.setThreadCount(1);

        BaseAgent alice = mas.registerAgent(new TestBaseAgent("Alice", "testInitiallyEmpty") {
            @Override
            public void onRun() {
                test.assertEquals(tupleSpace.getSize(), 0);
                stop();
            }

        }, true);


        test.await();
        alice.await();
    }


    @Test
    public void testReadSuspensiveSemantics() throws Exception {
        test.setThreadCount(1);

        BaseAgent alice = mas.registerAgent(new TestBaseAgent("Alice", "testReadSuspensiveSemantics") {
            @Override
            public void onRun() {
                test.assertBlocksIndefinitely(tupleSpace.read("f(X)"), "A read operation should block if no tuple matching the requested template is available");
                stop();
            }
        }, true);

        test.await();
        alice.await();
    }


    @Test
    public void testTakeSuspensiveSemantics() throws Exception {
        test.setThreadCount(1);

        BaseAgent alice = mas.registerAgent(new TestBaseAgent("Alice", "testTakeSuspensiveSemantics") {
            @Override
            public void onRun() {
                test.assertBlocksIndefinitely(tupleSpace.take("f(x)"), "A take operation should block if no tuple matching the requested template is available");
                stop();
            }
        }, true);

        test.await();
        alice.await();
    }


    @Test
    public void testWriteGenerativeSemantics() throws Exception {
        test.setThreadCount(1);

        BaseAgent alice = mas.registerAgent(new TestBaseAgent("Alice", "testWriteGenerativeSemantics") {
            @Override
            public void onRun() {
                test.assertEquals(tupleSpace.getSize(), 0, "The tuple space must initially be empty");
                test.assertEquals(tupleSpace.write("s(z)"), new LogicTuple("s(z)"), "A write operation eventually return the same tuple it received as argument");
                test.assertEquals(tupleSpace.getSize(), 1, "After a tuple was written, the tuple space size should increase");
                stop();
            }
        }, true);

        test.await();
        alice.await();
    }


    @Test
    public void testReadIsIdempotent1() throws Exception {
        test.setThreadCount(2);

        final LogicTuple tuple = new LogicTuple("s(z)");


        BaseAgent bob = mas.registerAgent(new TestBaseAgent("Bob", "testReadIsIdempotent1") {

            int i = rand.nextInt(10) + 1;

            @Override
            public void onRun() {
                if (i-- >= 0) {
                    test.assertEquals(tupleSpace.read("s(X)"), tuple);
                } else {
                    test.assertEquals(tupleSpace.read("s(Y)"), tuple);
                    stop();
                }
            }

        }, false);

        BaseAgent alice = mas.registerAgent(new TestBaseAgent("Alice", "testReadIsIdempotent1") {
            @Override
            public void onRun() {
                test.assertEventuallyReturns(tupleSpace.write(tuple));
                bob.start();
                stop();
            }
        }, true);

        test.await();
        alice.await();
        bob.await();
    }

    @Test
    public void testReadIsIdempotent2() throws Exception {
        test.setThreadCount(2);

        final LogicTuple tuple = new LogicTuple("s(z)");

        BaseAgent alice = mas.registerAgent(new TestBaseAgent("Alice", "testReadIsIdempotent2") {
            @Override
            public void onRun() {
                test.assertEventuallyReturns(tupleSpace.write(tuple));
                stop();
            }
        }, false);

        BaseAgent bob = mas.registerAgent(new TestBaseAgent("Bob", "testReadIsIdempotent2") {
            @Override
            public void onRun() {
                final Future<LogicTuple> toBeRead1 = tupleSpace.read("s(X)");
                final Future<LogicTuple> toBeRead2 = tupleSpace.read("s(Y)");

                alice.start();

                test.assertEquals(toBeRead1, tuple);
                test.assertEquals(toBeRead2, tuple);

                stop();
            }
        }, true);

        test.await();
        alice.await();
        bob.await();
    }

    @Test
    public void testTakeIsNotIdempotent1() throws Exception {
        test.setThreadCount(2);

        final LogicTuple tuple = new LogicTuple("foo(bar)");

        BaseAgent bob = mas.registerAgent(new TestBaseAgent("Bob", "testTakeIsNotIdempotent1") {
            @Override
            public void onRun() {
                test.assertEquals(tupleSpace.take("foo(X)"), tuple);
                test.assertBlocksIndefinitely(tupleSpace.take("foo(_)"));
                stop();
            }
        }, false);

        BaseAgent alice = mas.registerAgent(new TestBaseAgent("Alice", "testTakeIsNotIdempotent1") {
            @Override
            public void onRun() {
                test.assertEventuallyReturns(tupleSpace.write(tuple));
                bob.start();
                stop();
            }
        }, true);

        test.await();
        alice.await();
        bob.await();
    }

    @Test
    public void testTakeIsNotIdempotent2() throws Exception {
        test.setThreadCount(2);

        final LogicTuple tuple = new LogicTuple("foo(bar)");

        BaseAgent alice = mas.registerAgent(new TestBaseAgent("Alice", "testTakeIsNotIdempotent2") {
            @Override
            public void onRun() {
                test.assertEventuallyReturns(tupleSpace.write(tuple));
                stop();
            }
        }, false);

        BaseAgent bob = mas.registerAgent(new TestBaseAgent("Bob", "testTakeIsNotIdempotent2") {
            @Override
            public void onRun() {
                Future<LogicTuple> toBeWritten = tupleSpace.take("foo(X)");
                alice.start();
                test.assertEquals(toBeWritten, tuple);
                test.assertBlocksIndefinitely(tupleSpace.take("foo(_)"));
                stop();
            }
        }, true);

        test.await();
        alice.await();
        bob.await();
    }

    @Test
    public void testAssociativeAccess() throws Exception {
        test.setThreadCount(3);

        final LogicTuple tuple4Bob = new LogicTuple("msg(to(bob), hi_bob)");
        final LogicTuple tuple4Carl = new LogicTuple("msg(to(carl), hi_carl)");

        BaseAgent carl = mas.registerAgent(new TestBaseAgent("Carl", "testAssociativeAccess") {
            @Override
            public void onRun() {
                test.assertEquals(tupleSpace.read("msg(to(carl), M)"), tuple4Carl);
                stop();
            }
        }, true);

        BaseAgent bob = mas.registerAgent(new TestBaseAgent("Bob", "testAssociativeAccess") {
            @Override
            public void onRun() {
                test.assertEquals(tupleSpace.read("msg(to(bob), M)"), tuple4Bob);
                stop();
            }
        }, true);

        BaseAgent alice = mas.registerAgent(new TestBaseAgent("Alice", "testAssociativeAccess") {
            @Override
            public void onRun() {
                test.assertEventuallyReturns(tupleSpace.write(tuple4Bob));
                test.assertEventuallyReturns(tupleSpace.write(tuple4Carl));

                test.assertOneOf(tupleSpace.take("msg(to(_), M)"), tuple4Bob, tuple4Carl);
                test.assertOneOf(tupleSpace.take("msg(to(_), M)"), tuple4Bob, tuple4Carl);
                stop();
            }
        }, true);

        test.await();
        alice.await();
        bob.await();
        carl.await();
    }

    @Test
    public void testGetSize() throws Exception {
        test.setThreadCount(1);

        BaseAgent alice = mas.registerAgent(new TestBaseAgent("Alice", "testGetSize") {
            @Override
            public void onRun() {
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
        }, true);

        test.await();
        alice.await();
    }

    @Test
    public void testGetAll() throws Exception {
        test.setThreadCount(1);

        final List<LogicTuple> expected = new ArrayList<>(Arrays.asList(
                new LogicTuple("b"),
                new LogicTuple("c"),
                new LogicTuple("a"),
                new LogicTuple("b")
        ));

        BaseAgent alice = mas.registerAgent(new TestBaseAgent("Alice", "testGetAll") {

            Queue<LogicTuple> queue = new LinkedList<>(expected);

            @Override
            public void onRun() {
                if (queue.isEmpty()) {
                    test.assertEquals(tupleSpace.get(), expected);
                    stop();
                } else {
                    test.assertEventuallyReturns(tupleSpace.write(queue.poll()));
                }
            }
        }, true);

        test.await();
        alice.await();
    }


    @Test
    public void testWriteAll() throws Exception {
        test.setThreadCount(1);

        final List<LogicTuple> tuples = Arrays.asList(
                new LogicTuple("b(2)"),
                new LogicTuple("c(3)"),
                new LogicTuple("a(1)"),
                new LogicTuple("b(2)")
        );

        BaseAgent alice = mas.registerAgent(new TestBaseAgent("Alice", "testWriteAll") {
            @Override
            public void onRun() {
                test.assertEquals(tupleSpace.getSize(), 0);
                test.assertEquals(tupleSpace.writeAll(tuples), tuples);
                test.assertEquals(tupleSpace.getSize(), tuples.size());
                test.assertEquals(tupleSpace.get(), tuples);

                stop();
            }
        }, true);

        test.await();
        alice.await();
    }

    @Test
    public void testWriteAllResumesSuspendedOperations() throws Exception {
        test.setThreadCount(2);

        final List<LogicTuple> tuples = Stream.of("f(x)", "f(y)", "g(x)", "g(y)")
                .map(LogicTuple::new)
                .collect(Collectors.toList());

        BaseAgent alice = mas.registerAgent(new TestBaseAgent("Alice", "testWriteAllResumesSuspendedOperations") {
            @Override
            public void onRun() {
                test.assertEventuallyReturns(tupleSpace.writeAll(tuples));

                stop();
            }
        }, false);

        BaseAgent bob = mas.registerAgent(new TestBaseAgent("Bob", "testWriteAllResumesSuspendedOperations") {
            @Override
            public void onRun() {
                final Future<LogicTuple> toBeRead = tupleSpace.read("f(A)");
                final Future<LogicTuple> toBeTaken = tupleSpace.take("g(A)");

                alice.start();

                test.assertOneOf(toBeRead, tuples.get(0), tuples.get(1));
                test.assertOneOf(toBeTaken, tuples.get(2), tuples.get(3));

                stop();
            }
        }, true);

        test.await();
        alice.await();
        bob.await();
    }

    @Test
    public void testReadAll() throws Exception {
        test.setThreadCount(1);

        final List<LogicTuple> tuples = Arrays.asList(
                new LogicTuple("a(2)"),
                new LogicTuple("a(3)"),
                new LogicTuple("a(1)"),
                new LogicTuple("a(4)"),
                new LogicTuple("b(5)")
        );

        final Collection<LogicTuple> expected = tuples.subList(0, 4);

        BaseAgent alice = mas.registerAgent(new TestBaseAgent("Alice", "testReadAll") {
            @Override
            public void onRun() {
                test.assertEquals(tupleSpace.getSize(), 0);
                test.assertEventuallyReturns(tupleSpace.writeAll(tuples));
                test.assertEquals(tupleSpace.getSize(), tuples.size());
                test.assertEquals(tupleSpace.readAll("a(N)"), expected);
                test.assertEquals(tupleSpace.readAll("a(N)"), expected);
                test.assertEquals(tupleSpace.getSize(), tuples.size());

                stop();
            }
        }, true);

        test.await();
        alice.await();
    }

    @Test
    public void testTryRead() throws Exception {
        test.setThreadCount(1);

        BaseAgent alice = mas.registerAgent(new TestBaseAgent("Alice", "testTryRead") {
            @Override
            public void onRun() {
                test.assertEquals(tupleSpace.getSize(), 0);
                test.assertEventuallyReturns(tupleSpace.write("p(a)"));
                test.assertEquals(tupleSpace.getSize(), 1);
                test.assertEquals(tupleSpace.tryRead("p(Z)"), Optional.of(new LogicTuple("p(a)")));
                test.assertEquals(tupleSpace.getSize(), 1);
                test.assertEquals(tupleSpace.tryRead("p(Z)"), Optional.of(new LogicTuple("p(a)")));
                test.assertEquals(tupleSpace.getSize(), 1);

                stop();
            }
        }, true);

        test.await();
        alice.await();
    }

    @Test
    public void testTryTake() throws Exception {
        test.setThreadCount(1);

        BaseAgent alice = mas.registerAgent(new TestBaseAgent("Alice", "testTryTake") {
            @Override
            public void onRun() {
                test.assertEquals(tupleSpace.getSize(), 0);
                test.assertEventuallyReturns(tupleSpace.write("p(a)"));
                test.assertEquals(tupleSpace.getSize(), 1);
                test.assertEquals(tupleSpace.tryTake("p(Z)"), Optional.of(new LogicTuple("p(a)")));
                test.assertEquals(tupleSpace.getSize(), 0);
                test.assertEquals(tupleSpace.tryTake("p(Z)"), Optional.empty());

                stop();
            }
        }, true);


        test.await();
        alice.await();
    }

    @Test
    public void testTakeAll() throws Exception {
        test.setThreadCount(1);

        final List<LogicTuple> tuples = Arrays.asList(
                new LogicTuple("a(2)"),
                new LogicTuple("a(3)"),
                new LogicTuple("a(1)"),
                new LogicTuple("a(4)"),
                new LogicTuple("b(5)")
        );

        final Collection<LogicTuple> expected = tuples.subList(0, 4);


        BaseAgent alice = mas.registerAgent(new TestBaseAgent("Alice", "testTakeAll") {
            @Override
            public void onRun() {
                test.assertEquals(tupleSpace.getSize(), 0);
                test.assertEventuallyReturns(tupleSpace.writeAll(tuples));
                test.assertEquals(tupleSpace.getSize(), tuples.size());
                test.assertEquals(tupleSpace.takeAll("a(N)"), expected);
                test.assertEquals(tupleSpace.takeAll("a(N)"), Collections.emptySet());
                test.assertEquals(tupleSpace.getSize(), 1);

                stop();
            }
        }, true);

        test.await();
        alice.await();
    }

    private abstract class TestBaseAgent extends BaseAgent {

        private final String tupleSpaceName;
        protected LogicTupleSpace tupleSpace;

        protected TestBaseAgent(String name, String tupleSpaceName) {
            super(name);
            this.tupleSpaceName = tupleSpaceName;
        }

        @Override
        public void onBegin() {
            tupleSpace = getEnvironment().getTupleSpace(tupleSpaceName);
        }

        @Override
        public Continuation onUncaughtError(Exception e) {
            test.fail(e);
            return Continuation.STOP;
        }

        @Override
        public void onEnd() {
            TestLogicTupleSpace.this.test.done();
        }
    }

}
