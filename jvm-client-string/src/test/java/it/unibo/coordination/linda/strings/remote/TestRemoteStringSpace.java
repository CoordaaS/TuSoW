package it.unibo.coordination.linda.strings.remote;

import io.vertx.core.Future;
import it.unibo.coordination.linda.string.RegexTemplate;
import it.unibo.coordination.linda.string.RegularMatch;
import it.unibo.coordination.linda.string.StringTuple;
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
public class TestRemoteStringSpace extends TestTupleSpace<StringTuple, RegexTemplate, Object, String, RegularMatch, RemoteStringSpace> {

    private static final int PORT = 10002;

    private static Service service;
    private final int testIndex;
    private static int testCaseIndex = 0;

    public TestRemoteStringSpace(Integer i) {
        super(new TextualTupleTemplateFactory());
        this.testIndex = i;
    }

    @BeforeClass
    public static void setUpClass() {
        service = Service.start("-p", Integer.toString(PORT));
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
    protected RemoteStringSpace getTupleSpace(ExecutorService executor) {
        return RemoteStringSpace.of("localhost", PORT, TestRemoteStringSpace.class.getSimpleName() + "-" + testIndex + "-" + testCaseIndex);
    }

    @Parameterized.Parameters
    public static Object[][] getParams() {
        return IntStream.range(0, 5).mapToObj(i -> new Object[] { i }).toArray(Object[][]::new);
    }
}
