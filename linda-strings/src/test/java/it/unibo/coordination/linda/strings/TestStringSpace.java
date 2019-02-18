package it.unibo.coordination.linda.strings;

import it.unibo.coordination.linda.string.RegexTemplate;
import it.unibo.coordination.linda.string.RegularMatch;
import it.unibo.coordination.linda.string.StringSpace;
import it.unibo.coordination.linda.string.StringTuple;
import it.unibo.coordination.linda.test.TestTupleSpace;

import java.util.concurrent.ExecutorService;

public class TestStringSpace extends TestTupleSpace<StringTuple, RegexTemplate, Object, String, RegularMatch, StringSpace> {

    public TestStringSpace() {
        super(new TextualTupleTemplateFactory());
    }

    @Override
    protected StringSpace getTupleSpace(ExecutorService executor) {
        return StringSpace.deterministic(executor);
    }


}
