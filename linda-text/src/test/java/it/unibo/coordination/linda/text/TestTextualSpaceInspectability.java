package it.unibo.coordination.linda.text;

import it.unibo.coordination.linda.test.TestTupleSpaceInspectability;

import java.util.concurrent.ExecutorService;

public class TestTextualSpaceInspectability extends TestTupleSpaceInspectability<StringTuple, RegexTemplate, Object, String, RegularMatch, InspectableTextualSpace> {

    public TestTextualSpaceInspectability() {
        super(new TextualTupleTemplateFactory());
    }

    @Override
    protected InspectableTextualSpace getTupleSpace(ExecutorService executor) {
        return InspectableTextualSpace.deterministic(executor);
    }


}
