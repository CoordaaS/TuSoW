package it.unibo.coordination.linda.strings;

import it.unibo.coordination.linda.string.InspectableStringSpace;
import it.unibo.coordination.linda.string.RegexTemplate;
import it.unibo.coordination.linda.string.StringTuple;
import it.unibo.coordination.linda.test.TestTupleSpaceInspectability;

import java.util.concurrent.ExecutorService;

public class TestStringSpaceInspectability extends TestTupleSpaceInspectability<StringTuple, RegexTemplate, Object, String, InspectableStringSpace> {

    public TestStringSpaceInspectability() {
        super(new TextualTupleTemplateFactory());
    }

    @Override
    protected InspectableStringSpace getTupleSpace(ExecutorService executor) {
        return InspectableStringSpace.deterministic(executor);
    }


}
