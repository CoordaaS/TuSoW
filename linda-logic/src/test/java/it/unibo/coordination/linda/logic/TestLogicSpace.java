package it.unibo.coordination.linda.logic;

import alice.tuprolog.Term;
import it.unibo.coordination.linda.test.TestTupleSpace;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.concurrent.ExecutorService;
import java.util.stream.IntStream;

@RunWith(Parameterized.class)
public class TestLogicSpace extends TestTupleSpace<LogicTuple, LogicTemplate, String, Term, LogicMatch, LogicSpace> {

    @Override
    protected LogicSpace getTupleSpace(ExecutorService executor) {
        return LogicSpace.deterministic(executor);
    }

    public TestLogicSpace(Integer i) {
        super(new LogicTupleTemplateFactory());
    }

    @Parameterized.Parameters
    public static Object[][] getParams() {
        return IntStream.range(0, 30).mapToObj(i -> new Object[] { i }).toArray(Object[][]::new);
    }
}
