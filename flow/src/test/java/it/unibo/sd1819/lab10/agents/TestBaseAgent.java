package it.unibo.sd1819.lab10.agents;

import it.unibo.coordination.flow.*;
import it.unibo.sd1819.lab10.tusow.TuSoWService;
import it.unibo.sd1819.test.ConcurrentTestHelper;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RunWith(Parameterized.class)
public class TestBaseAgent {

    private static final Duration MAX_WAIT = Duration.ofSeconds(3);
    private final int testIndex;
    protected ConcurrentTestHelper test;
    protected Random rand;
    protected Environment mas;


    public TestBaseAgent(Integer i) {
        testIndex = i;
    }

    @Parameterized.Parameters
    public static Iterable<Integer> data() {
        return IntStream.range(0, 10).boxed().collect(Collectors.toList());
    }

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
        // TODO notice that all agents are executed by a single thread in this test suite!
        mas = new DistributedEnvironment(Executors.newSingleThreadExecutor(), "localhost", 8080, "tuple-spaces");
    }

    @After
    public void tearDown() throws InterruptedException {
        mas.shutdown().awaitShutdown(MAX_WAIT);
    }

    // TODO readme
    @Test
    public void testAgentsFlow() throws Exception {
        final List<Integer> xs = new LinkedList<>();

        mas.registerAgent(new BaseAgent("Alice") {
            int x = 0;

            @Override
            public void onBegin() {
                xs.add(-1);
                throw new RuntimeException("Ignore me");
            }

            @Override
            public void onRun() throws Exception {
                if (x < 10) {
                    xs.add(x++);
                } else {
                    throw new Exception("Stop the BaseAgent now!");
                }
            }

            @Override
            public Continuation onUncaughtError(Exception e) {
                if (e instanceof RuntimeException) {
                    xs.add(-1);
                    return Continuation.CONTINUE;
                } else {
                    xs.add(x++);
                    return Continuation.STOP;
                }
            }

            @Override
            public void onEnd() {
                xs.add(x);
            }

        }, true);

        mas.awaitAllAgentsStop(MAX_WAIT);

        Assert.assertEquals(
                Arrays.asList(-1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11),
                xs
        );
    }

    // TODO readme
    @Test
    public void testAgentsRestart() throws Exception {
        final List<Integer> xs = new LinkedList<>();

        mas.registerAgent(new BaseAgent("Alice") {
            int x = -1;

            @Override
            public void onBegin() {
                xs.add(x);
                x += 2;
                if (x == 1) {
                    throw new RuntimeException("Restart the agent!");
                }
            }

            @Override
            public void onRun() throws Exception {
                if (x == 10) {
                    xs.add(x++);
                    throw new RuntimeException("Restart the agent!");
                } else if (x == 15) {
                    xs.add(x++);
                    throw new Exception("Stop the agent!");
                } else {
                    xs.add(x++);
                }
            }

            @Override
            public Continuation onUncaughtError(Exception e) {
                if (e instanceof RuntimeException) {
                    xs.add(-1);
                    return Continuation.RESTART;
                } else {
                    xs.add(x++);
                    return Continuation.STOP;
                }
            }

            @Override
            public void onEnd() {
                xs.add(x);
            }

        }, true);

        mas.awaitAllAgentsStop(MAX_WAIT);

        Assert.assertEquals(
                Arrays.asList(-1, -1, 1, 3, 4, 5, 6, 7, 8, 9, 10, -1, 11, 13, 14, 15, 16, 17),
                xs
        );
    }

    // TODO readme
    @Test
    public void testAgentsRunOnTheSameExecutor() throws Exception {
        final List<String> xs = new LinkedList<>();

        mas.registerAgent(new BaseAgent("Bob") {

            @Override
            public void onRun() throws Exception {
                xs.add("b1");
                getEnvironment().getTupleSpace("testAgentsRunOnTheSameExecutor-" + testIndex).take("signal(When)").get();
                xs.add("b2");
                stop();
            }
        }, true);

        mas.registerAgent(new BaseAgent("Alice") {
            @Override
            public void onRun() throws Exception {
                xs.add("a1");
                getEnvironment().getTupleSpace("testAgentsRunOnTheSameExecutor-" + testIndex).write("signal(now)").get();
                xs.add("a2");
                stop();
            }
        }, true);

        try {
            mas.awaitAllAgentsStop(MAX_WAIT);
            Assert.fail();
        } catch (TimeoutException e) {
            Assert.assertEquals(
                    Collections.singletonList("b1"),
                    xs
            );
        }
    }

    @Test
    public void testAgentCreationRequiresAConstructorAcceptingAnAgentId1() throws Exception {
        final MyAgent1 agent = mas.createAgent(MyAgent1.class, "testAgentCreation", true);

        Assert.assertEquals(
                mas.generateAgentId("testAgentCreation"),
                agent.getAgentId()
        );

        mas.awaitAllAgentsStop(MAX_WAIT);

        Assert.assertTrue(true);
    }

    @Test
    public void testAgentCreationRequiresAConstructorAcceptingAnAgentId2() {
        try {
            mas.createAgent(MyAgent2.class, "testAgentCreation", true);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        }
    }

    // TODO readme
    @Test
    public void testAgentsPause() throws Exception {
        final List<String> xs = new LinkedList<>();

        mas.registerAgent(new BaseAgent("Bob") {

            boolean signalReceived = false;

            @Override
            public void onRun() {
                if (!signalReceived) {
                    xs.add("b1");
                    getEnvironment().getTupleSpace("testAgentsRunOnTheSameExecutor-" + testIndex).take("signal(When)")
                            .thenRunAsync(() -> {
                                xs.add("b4");
                                signalReceived = true;
                                resume();
                            }, getEngine());
                    xs.add("b2");
                    pause();
                    xs.add("b3");
                } else {
                    xs.add("b5");
                    stop();
                }
            }
        }, true);

        mas.registerAgent(new BaseAgent("Alice") {
            @Override
            public void onRun() throws Exception {
                xs.add("a1");
                getEnvironment().getTupleSpace("testAgentsRunOnTheSameExecutor-" + testIndex).write("signal(now)").get();
                xs.add("a2");
                stop();
            }
        }, true);

        mas.awaitAllAgentsStop(MAX_WAIT);

        Assert.assertEquals(
                Arrays.asList("b1", "b2", "b3", "a1", "a2", "b4", "b5"),
                xs
        );
    }

    static class MyAgent1 extends BaseAgent {

        public MyAgent1(AgentId id) {
            super(id);
        }

        @Override
        public void onRun() {
            stop();
        }
    }

    static class MyAgent2 extends BaseAgent {

        public MyAgent2() {
            super("my-name");
        }

        @Override
        public void onRun() {
            stop();
        }
    }
}
