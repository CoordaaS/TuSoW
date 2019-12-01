package it.unibo.coordination.linda.text;

import it.unibo.coordination.linda.test.TestTupleSpace;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.concurrent.ExecutorService;
import java.util.stream.IntStream;

@RunWith(Parameterized.class)
public class TestTextualSpace extends TestTupleSpace<StringTuple, RegexTemplate, Object, String, RegularMatch, TextualSpace> {

    public TestTextualSpace(Integer i) {
        super(new TextualTupleTemplateFactory());
    }

    @Override
    protected TextualSpace getTupleSpace(ExecutorService executor) {
        return TextualSpace.local(executor);
    }

    @Parameterized.Parameters
    public static Object[][] getParams() {
        return IntStream.range(0, 5).mapToObj(i -> new Object[] { i }).toArray(Object[][]::new);
    }
}
