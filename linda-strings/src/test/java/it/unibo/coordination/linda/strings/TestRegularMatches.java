package it.unibo.coordination.linda.strings;

import it.unibo.coordination.linda.string.RegexTemplate;
import it.unibo.coordination.linda.string.RegularMatch;
import it.unibo.coordination.linda.string.StringTuple;
import it.unibo.coordination.linda.test.TestMatch;

public class TestRegularMatches extends TestMatch<StringTuple, RegexTemplate, Object, String, RegularMatch> {

    public TestRegularMatches() {
        super(new TextualTupleTemplateFactory());
    }
}
