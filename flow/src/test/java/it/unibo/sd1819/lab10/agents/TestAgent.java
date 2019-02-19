package it.unibo.sd1819.lab10.agents;

import alice.tuprolog.Term;
import it.unibo.coordination.flow.AgentId;
import it.unibo.coordination.flow.DistributedEnvironment;
import it.unibo.coordination.flow.Environment;
import it.unibo.sd1819.lab10.agents.behaviours.Behaviours;
import it.unibo.sd1819.lab10.agents.behaviours.messages.Message;
import it.unibo.sd1819.lab10.ts.logic.LogicTuple;
import it.unibo.sd1819.lab10.tusow.TuSoWService;
import it.unibo.sd1819.test.ConcurrentTestHelper;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@RunWith(Parameterized.class)
public class TestAgent {

    private static final Duration MAX_WAIT = Duration.ofSeconds(3);
    private final int testIndex;
    protected ConcurrentTestHelper test;
    protected Random rand;
    protected Environment mas;


    public TestAgent(Integer i) {
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

    @Test
    public void testOneShot() throws Exception {
        final List<Integer> xs = new LinkedList<>();

        mas.registerAgent(new Agent("testOneShot-" + testIndex) {
            @Override
            public void onBegin() {
                Behaviours.of(() -> xs.add(1))
                        .andThen(AgentController::stop)
                        .addTo(this);
            }
        }, true);

        mas.awaitAllAgentsStop(MAX_WAIT);

        Assert.assertEquals(xs, Collections.singletonList(1));
    }

    @Test
    public void testSequence1() throws Exception {
        final List<Integer> xs = new LinkedList<>();

        mas.registerAgent(new Agent("testSequence1-" + testIndex) {
            @Override
            public void onBegin() {
                Behaviours.sequence(
                        Behaviours.of(() -> xs.add(1)),
                        Behaviours.of(() -> xs.add(2)),
                        Behaviours.of(() -> xs.add(3))
                    ).andThen(AgentController::stop)
                    .addTo(this);
            }
        }, true);

        mas.awaitAllAgentsStop(MAX_WAIT);

        Assert.assertEquals(Arrays.asList(1, 2, 3), xs);
    }

    @Test
    public void testSequence2() throws Exception {
        final List<Integer> xs = new LinkedList<>();

        mas.registerAgent(new Agent("testSequence2-" + testIndex) {
            @Override
            public void onBegin() {
                Behaviours.of(() -> xs.add(1))
                        .andThen(() -> xs.add(2))
                        .andThen(() -> xs.add(3))
                        .andThen(AgentController::stop)
                        .addTo(this);
            }
        }, true);

        mas.awaitAllAgentsStop(MAX_WAIT);

        Assert.assertEquals(Arrays.asList(1, 2, 3), xs);
    }

    @Test
    public void testJoin() throws Exception {
        final List<Object> xs = new LinkedList<>();

        mas.registerAgent(new Agent("testJoin-" + testIndex) {
            @Override
            public void onBegin() {
                Behaviours.allOf(
                        Behaviours.of(() -> xs.add(1)).andThen(() -> xs.add(2)).andThen(() -> xs.add(3)),
                        Behaviours.of(() -> xs.add("a")).andThen(() -> xs.add("b")).andThen(() -> xs.add("c")).andThen(() -> xs.add("d"))
                    ).andThen(AgentController::stop)
                    .addTo(this);
            }
        }, true);

        mas.awaitAllAgentsStop(MAX_WAIT);

        Assert.assertEquals(Arrays.asList(1, "a", 2, "b", 3, "c", "d"), xs);
    }

    @Test
    public void testParallel() throws Exception {
        final List<Object> xs = new LinkedList<>();

        mas.registerAgent(new Agent("testParallel-" + testIndex) {
            @Override
            public void onBegin() {
                Behaviours.anyOf(
                        Behaviours.of(() -> xs.add(1)).andThen(() -> xs.add(2)),
                        Behaviours.of(() -> xs.add("a")).andThen(() -> xs.add("b")).andThen(() -> xs.add("c")).andThen(() -> xs.add("d"))
                    ).andThen(AgentController::stop)
                    .addTo(this);
            }
        }, true);

        mas.awaitAllAgentsStop(MAX_WAIT);

        Assert.assertEquals(Arrays.asList(1, "a", 2), xs);
    }

    @Test
    public void testDoWhile() throws Exception {
        final List<Object> xs = new LinkedList<>();

        mas.registerAgent(new Agent("testDoWhile-" + testIndex) {
            @Override
            public void onBegin() {
                Behaviours.of(() -> xs.add(1)).andThen(() -> xs.add(2)).andThen(() -> xs.add(3))
                        .repeatWhile(() -> xs.size() < 7)
                        .andThen(AgentController::stop)
                        .addTo(this);
            }
        }, true);

        mas.awaitAllAgentsStop(Duration.ofMillis(Long.MAX_VALUE));

        Assert.assertEquals(Arrays.asList(1, 2, 3, 1, 2, 3, 1, 2, 3), xs);
    }

    @Test
    public void testWait() throws Exception {
        final Duration toWait = Duration.ofSeconds(1);

        final OffsetDateTime start = OffsetDateTime.now();

        mas.registerAgent(new Agent("testWait-" + testIndex) {

            @Override
            public void onBegin() {
                Behaviours.wait(toWait)
                        .andThen(AgentController::stop)
                        .addTo(this);
            }
        }, true);

        mas.awaitAllAgentsStop(Duration.ofMillis(Long.MAX_VALUE));

        Assert.assertTrue(ChronoUnit.MILLIS.between(start, OffsetDateTime.now()) >= toWait.toMillis());
    }

    @Test
    public void testDoForAWhile() throws Exception {
        final Duration toWait = Duration.ofSeconds(1);
        final List<Integer> xs = new LinkedList<>();
        final OffsetDateTime start = OffsetDateTime.now();
        final AtomicInteger i = new AtomicInteger(0);

        mas.registerAgent(new Agent("testDoForAWhile-" + testIndex) {

            @Override
            public void onBegin() {
                Behaviours.anyOf(
                        Behaviours.wait(toWait),
                        Behaviours.of(() -> xs.add(i.getAndIncrement())).repeatForEver()
                    ).andThen(AgentController::stop)
                    .addTo(this);
            }
        }, true);

        mas.awaitAllAgentsStop(Duration.ofMillis(Long.MAX_VALUE));

        Assert.assertTrue(ChronoUnit.MILLIS.between(start, OffsetDateTime.now()) >= toWait.toMillis());
        Assert.assertTrue(i.get() > 0);
        Assert.assertEquals(IntStream.range(0, i.get()).boxed().collect(Collectors.toList()), xs);
    }

    @Test
    public void testLinda() throws Exception {
        final List<Object> xs = new LinkedList<>();

        mas.registerAgent(new Agent("testLinda-Alice-" + testIndex) {
            @Override
            public void onBegin() {
                Behaviours.linda("testLinda-" + testIndex,
                        tupleSpace -> tupleSpace.write("msg(payload)"),
                        writtenTuple -> xs.add(writtenTuple)
                    ).andThen(AgentController::stop)
                    .addTo(this);
            }
        }, true);

        mas.registerAgent(new Agent("testLinda-Bob-" + testIndex) {
            @Override
            public void onBegin() {
                Behaviours.linda("testLinda-" + testIndex,
                        tupleSpace -> tupleSpace.take("msg(X)"),
                        takenTuple -> xs.add(takenTuple)
                    ).andThen(AgentController::stop)
                    .addTo(this);
            }
        }, true);

        mas.awaitAllAgentsStop(Duration.ofMillis(Long.MAX_VALUE));

        Assert.assertEquals(
                Arrays.asList(new LogicTuple("msg(payload)"), new LogicTuple("msg(payload)")),
                xs
        );
    }

    @Test
    public void testSendReceive() throws Exception {
        final List<Message> xs = new LinkedList<>();

        mas.registerAgent(new Agent("testSendReceive-Alice-" + testIndex) {
            @Override
            public void onBegin() {
                Behaviours.send("testSendReceive-Bob-" + testIndex, "hello")
                        .andThen(AgentController::stop)
                        .addTo(this);
            }
        }, true);

        mas.registerAgent(new Agent("testSendReceive-Bob-" + testIndex) {
            @Override
            public void onBegin() {
                Behaviours.receive(msg -> xs.add(msg))
                        .andThen(AgentController::stop)
                        .addTo(this);
            }
        }, true);

        mas.awaitAllAgentsStop(Duration.ofMillis(Long.MAX_VALUE));

        Assert.assertEquals(
                Collections.singletonList(
                        new Message(
                                AgentId.parse("testSendReceive-Alice-" + testIndex + "@" + mas.getName()),
                                AgentId.parse("testSendReceive-Bob-" + testIndex + "@" + mas.getName()),
                                Term.createTerm("hello")
                        )
                ),
                xs
        );
    }

    @Test
    public void testPingPong1() throws Exception {
        final List<Message> xs = new LinkedList<>();

        mas.registerAgent(new Agent("testPingPong1-Ping-" + testIndex) {
            @Override
            public void onBegin() {
                Behaviours.send("testPingPong1-Pong-" + testIndex, "ping")
                        .andThen(Behaviours.receive(msg -> {
                            if (!msg.getPayloadAsString().equals("pong")) {
                                throw new IllegalStateException();
                            }
                            xs.add(msg);
                        }))
                        .andThen(AgentController::stop)
                        .addTo(this);
            }
        }, true);

        mas.registerAgent(new Agent("testPingPong1-Pong-" + testIndex) {
            @Override
            public void onBegin() {
                Behaviours.receive(msg -> {
                        if (!msg.getPayloadAsString().equals("ping")) {
                            throw new IllegalStateException();
                        }
                        xs.add(msg);
                    }).andThen(Behaviours.send("testPingPong1-Ping-" + testIndex, "pong"))
                    .andThen(AgentController::stop)
                    .addTo(this);
            }
        }, true);

        mas.awaitAllAgentsStop(Duration.ofMillis(Long.MAX_VALUE));

        Assert.assertEquals(2, xs.size());

        Assert.assertEquals(
                Arrays.asList("ping", "pong"),
                xs.stream().map(Message::getPayload).map(Term::toString).collect(Collectors.toList())
        );
    }

    @Test
    public void testPingPongN() throws Exception {
        final int n = 5;
        final List<Message> xs = new LinkedList<>();

        mas.registerAgent(new Agent("testPingPongN-Ping-" + testIndex) {
            int i = 0;

            @Override
            public void onBegin() {
                Behaviours.send("testPingPongN-Pong-" + testIndex, "ping")
                        .andThen(Behaviours.receive(msg -> {
                            if (!msg.getPayloadAsString().equals("pong")) {
                                throw new IllegalStateException();
                            }
                            xs.add(msg);
                        }))
                        .repeatWhile(() -> ++i < n)
                        .andThen(AgentController::stop)
                        .addTo(this);
            }
        }, true);

        mas.registerAgent(new Agent("testPingPongN-Pong-" + testIndex) {
            int j = 0;

            @Override
            public void onBegin() {
                Behaviours.receive(msg -> {
                        if (!msg.getPayloadAsString().equals("ping")) {
                            throw new IllegalStateException();
                        }
                        xs.add(msg);
                    }).andThen(Behaviours.send("testPingPongN-Ping-" + testIndex, "pong"))
                    .repeatWhile(() -> ++j < n)
                    .andThen(AgentController::stop)
                    .addTo(this);
            }
        }, true);

        mas.awaitAllAgentsStop(Duration.ofMillis(Long.MAX_VALUE));

        Assert.assertEquals(10, xs.size());

        Assert.assertEquals(
                IntStream.range(0, 5).boxed().flatMap(i -> Stream.of("ping", "pong")).collect(Collectors.toList()),
                xs.stream().map(Message::getPayload).map(Term::toString).collect(Collectors.toList())
        );
    }

    @Test
    public void testWhitePages() throws Exception {

        final Agent first = mas.registerAgent(new Agent("testWhitePages-First-" + testIndex) {

            @Override
            public void setup() {
                Behaviours.queryWhitePages(ids -> {
                    AgentId agentId = ids.stream()
                            .filter(it -> !it.equals(this.getAgentId()))
                            .findFirst()
                            .get();

                    Behaviours.send(agentId, "hello")
                            .andThen(this::stop)
                            .addTo(this);
                }).addTo(this);

            }
        }, false);

        mas.registerAgent(new Agent("testWhitePages-Second-" + testIndex) {

            Message msg;

            @Override
            public void setup() {
                Behaviours.of(first::start)
                        .andThen(Behaviours.receive(msg -> {
                            if (msg.getPayloadAsString().equalsIgnoreCase("hello")) {
                                this.msg = msg;
                            } else {
                                this.msg = null;
                            }
                        })).repeatUntil(() -> msg != null)
                        .andThen(this::stop)
                        .addTo(this);

            }
        }, true);

        mas.awaitAllAgentsStop(Duration.ofMillis(Long.MAX_VALUE));
    }

    @Test
    public void testAgentCreationRequiresAConstructorAcceptingAnAgentId1() throws Exception {

        final MyAgent1 agent = mas.createAgent(MyAgent1.class, "testAgentCreationRequiresAConstructorAcceptingAnAgentId1" + testIndex, true);

        Assert.assertEquals(
                mas.generateAgentId("testAgentCreationRequiresAConstructorAcceptingAnAgentId1" + testIndex),
                agent.getAgentId()
        );

        mas.awaitAllAgentsStop(MAX_WAIT);

        Assert.assertTrue(true);
    }

    @Test
    public void testAgentDoesNothingByDefault() throws Exception {
        final Agent lazy = mas.registerAgent(new Agent("testAgentDoesNothingByDefault" + testIndex) {
            @Override
            public void setup() { }
        }, true);

        try {
            lazy.await(MAX_WAIT);
            Assert.fail();

        } catch (InterruptedException | TimeoutException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testAgentCreationRequiresAConstructorAcceptingAnAgentId2() {
        try {
            mas.createAgent(MyAgent2.class, "testAgentCreationRequiresAConstructorAcceptingAnAgentId2" + testIndex, true);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        }
    }

    static class MyAgent1 extends Agent {

        public MyAgent1(AgentId id) {
            super(id);

            Behaviours.of(this::stop).addTo(this);
        }
    }

    static class MyAgent2 extends Agent {

        public MyAgent2() {
            super("my-name");

            Behaviours.of(this::stop).addTo(this);
        }
    }

    @Test
    public void testPublishSubscribe() throws Exception {
        final String content1 = "a cat falls down";
        final String content2 = "lol the dog is scared by pussies";
        final String content3 = "aaaawwww :3";

        final Map<String, String> xs = new HashMap<>();

        mas.registerAgent(new Agent("testPublishSubscribe-Pub-" + testIndex) {

            long amountOfAgents = 0;

            @Override
            public void setup() {
                Behaviours.queryWhitePages((ctl, ids) -> {
                        amountOfAgents = ids.stream()
                                .filter(it -> !it.equals(this.getAgentId()))
                                .count();
                    }).repeatWhile(() -> amountOfAgents < 3)
                    .andThen(Behaviours.wait(Duration.ofSeconds(1))) // give subscribers some time to subscribe
                    .andThen(Behaviours.publish("animals(cats(funny))", content1))
                    .andThen(Behaviours.publish("animals(cats(catsVSdogs))", content2))
                    .andThen(Behaviours.publish("animals(dogs(puppies))", content3))
                    .andThen(this::stop)
                    .addTo(this);

            }
        }, true);

        mas.registerAgent(new Agent("testWhitePages-Sub1-" + testIndex) {

            int notifications = 0;

            @Override
            public void setup() {
                Behaviours.subscribe("animals(cats(_))", (ctl, n) -> {
                        xs.merge(n.getTopic().toString(), n.getContent().toString(), (a, b) -> a + "\n" + b);
                        if (++notifications >= 2) {
                            stop();
                        }
                    }).addTo(this);
            }
        }, true);

        mas.registerAgent(new Agent("testWhitePages-Sub2-" + testIndex) {

            @Override
            public void setup() {
                Behaviours.subscribe("animals(cats(funny))", (ctl, n) -> {
                    xs.merge(n.getTopic().toString(), n.getContent().toString(), (a, b) -> a + "\n" + b);
                    stop();
                }).addTo(this);
            }
        }, true);

        mas.registerAgent(new Agent("testWhitePages-Sub3-" + testIndex) {

            int notifications = 0;

            @Override
            public void setup() {
                Behaviours.subscribe("animals(_)", (ctl, n) -> {
                    xs.merge(n.getTopic().toString(), n.getContent().toString(), (a, b) -> a + "\n" + b);
                    if (++notifications >= 3) {
                        stop();
                    }
                }).addTo(this);
            }
        }, true);

        mas.awaitAllAgentsStop(Duration.ofMillis(Long.MAX_VALUE));

        Assert.assertEquals(3, xs.size());
        Assert.assertEquals(String.format("'%s'\n'%s'\n'%s'", content1, content1, content1), xs.get("animals(cats(funny))"));
        Assert.assertEquals(String.format("'%s'\n'%s'", content2, content2), xs.get("animals(cats(catsVSdogs))"));
        Assert.assertEquals(String.format("'%s'", content3), xs.get("animals(dogs(puppies))"));
    }
}
