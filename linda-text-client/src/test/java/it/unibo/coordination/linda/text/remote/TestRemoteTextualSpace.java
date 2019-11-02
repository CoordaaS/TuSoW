package it.unibo.coordination.linda.text.remote;

import it.unibo.coordination.linda.test.TestTupleSpace;
import it.unibo.coordination.linda.text.RegexTemplate;
import it.unibo.coordination.linda.text.RegularMatch;
import it.unibo.coordination.linda.text.StringTuple;
import it.unibo.coordination.tusow.Service;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.stream.IntStream;

@RunWith(Parameterized.class)
public class TestRemoteTextualSpace extends TestTupleSpace<StringTuple, RegexTemplate, Object, String, RegularMatch, RemoteTextualSpace> {

    private static final int PORT = 10002;

    private static Service service;
    private final int testIndex;
    private static int testCaseIndex = 0;

    public TestRemoteTextualSpace(Integer i) {
        super(new TextualTupleTemplateFactory());
        this.testIndex = i;
    }

    @BeforeClass
    public static void setUpClass() throws InterruptedException, ExecutionException {
        service = Service.start("-p", Integer.toString(PORT)).awaitDeployment();
        testCaseIndex = 0;
    }

    @AfterClass
    public static void tearDownClass() throws InterruptedException, ExecutionException {
        service.stop();
        service.awaitTermination();
    }

    @Before
    @Override
    public void setUp() throws Exception {
        testCaseIndex++;
        super.setUp();
    }



    @Override
    protected RemoteTextualSpace getTupleSpace(ExecutorService executor) {
        return RemoteTextualSpace.of("localhost", PORT, TestRemoteTextualSpace.class.getSimpleName() + "-" + testIndex + "-" + testCaseIndex);
    }

    @Parameterized.Parameters
    public static Object[][] getParams() {
        return IntStream.range(0, 1).mapToObj(i -> new Object[] { i }).toArray(Object[][]::new);
    }
}
