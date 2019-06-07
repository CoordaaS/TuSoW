package it.unibo.coordination.linda.logic.remote;

import alice.tuprolog.Term;
import io.vertx.core.Future;
import it.unibo.coordination.linda.logic.LogicMatch;
import it.unibo.coordination.linda.logic.LogicSpace;
import it.unibo.coordination.linda.logic.LogicTemplate;
import it.unibo.coordination.linda.logic.LogicTuple;
import it.unibo.coordination.linda.test.TestTupleSpace;
import it.unibo.coordination.tusow.Service;
import org.apache.commons.cli.ParseException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.stream.IntStream;

@RunWith(Parameterized.class)
public class TestLogicSpace extends TestTupleSpace<LogicTuple, LogicTemplate, String, Term, LogicMatch, LogicSpace> {

    private static Service service;
    private final int i;

    public TestLogicSpace(Integer i) {
        super(new LogicTupleTemplateFactory());
        this.i = i;
    }

    @BeforeClass
    public static void setUpClass() throws ParseException {
        service = Service.start("-p", "8080");
    }

    @AfterClass
    public static void tearDownClass() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Future<Void> future = Future.future();
        future.setHandler(x -> latch.countDown());
        service.stop(future);
        latch.await();
    }

    @Override
    protected LogicSpace getTupleSpace(ExecutorService executor) {
        return RemoteLogicSpace.of("localhost", 8080, TestLogicSpace.class.getName() + "." + i);
    }

    @Parameterized.Parameters
    public static Object[][] getParams() {
        return IntStream.range(0, 5).mapToObj(i -> new Object[] { i }).toArray(Object[][]::new);
    }
}
