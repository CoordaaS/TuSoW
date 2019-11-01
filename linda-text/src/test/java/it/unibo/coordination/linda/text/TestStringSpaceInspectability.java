package it.unibo.coordination.linda.text;

import it.unibo.coordination.linda.test.TestTupleSpaceInspectability;

import java.util.concurrent.ExecutorService;

public class TestStringSpaceInspectability extends TestTupleSpaceInspectability<StringTuple, RegexTemplate, Object, String, RegularMatch, InspectableStringSpace> {

    public TestStringSpaceInspectability() {
        super(new TextualTupleTemplateFactory());
    }

    @Override
    protected InspectableStringSpace getTupleSpace(ExecutorService executor) {
        return InspectableStringSpace.deterministic(executor);
    }


}
