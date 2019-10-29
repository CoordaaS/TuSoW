package it.unibo.coordination.linda.strings;

import it.unibo.coordination.linda.string.RegexTemplate;
import it.unibo.coordination.linda.string.RegularMatch;
import it.unibo.coordination.linda.string.StringSpace;
import it.unibo.coordination.linda.string.StringTuple;
import it.unibo.coordination.linda.test.TestTupleSpace;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.concurrent.ExecutorService;
import java.util.stream.IntStream;

@RunWith(Parameterized.class)
public class TestStringSpace extends TestTupleSpace<StringTuple, RegexTemplate, Object, String, RegularMatch, StringSpace> {

    public TestStringSpace(Integer i) {
        super(new TextualTupleTemplateFactory());
    }

    @Override
    protected StringSpace getTupleSpace(ExecutorService executor) {
        return StringSpace.deterministic(executor);
    }

    @Parameterized.Parameters
    public static Object[][] getParams() {
        return IntStream.range(0, 5).mapToObj(i -> new Object[] { i }).toArray(Object[][]::new);
    }
}
