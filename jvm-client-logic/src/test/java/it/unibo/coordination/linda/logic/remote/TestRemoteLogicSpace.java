package it.unibo.coordination.linda.logic.remote;

import alice.tuprolog.Term;
import io.vertx.core.Future;
import it.unibo.coordination.linda.logic.LogicMatch;
import it.unibo.coordination.linda.logic.LogicTemplate;
import it.unibo.coordination.linda.logic.LogicTuple;
import it.unibo.coordination.linda.test.TestTupleSpace;
import it.unibo.coordination.tusow.Service;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.stream.IntStream;

@RunWith(Parameterized.class)
public class TestRemoteLogicSpace extends TestTupleSpace<LogicTuple, LogicTemplate, String, Term, LogicMatch, RemoteLogicSpace> {

    private static Service service;
    private final int testIndex;
    private static int testCaseIndex = 0;

    public TestRemoteLogicSpace(Integer i) {
        super(new LogicTupleTemplateFactory());
        this.testIndex = i;
    }

    @BeforeClass
    public static void setUpClass() {
        service = Service.start("-p", "10001");
        testCaseIndex = 0;
    }

    @AfterClass
    public static void tearDownClass() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Future<Void> future = Future.future();
        future.setHandler(x -> latch.countDown());
        service.stop(future);
        latch.await();
    }

    @Before
    @Override
    public void setUp() throws Exception {
        testCaseIndex++;
        super.setUp();
    }



    @Override
    protected RemoteLogicSpace getTupleSpace(ExecutorService executor) {
        return RemoteLogicSpace.of("localhost", 8080, TestRemoteLogicSpace.class.getSimpleName() + "-" + testIndex + "-" + testCaseIndex);
    }

    @Parameterized.Parameters
    public static Object[][] getParams() {
        return IntStream.range(0, 5).mapToObj(i -> new Object[] { i }).toArray(Object[][]::new);
    }
}
