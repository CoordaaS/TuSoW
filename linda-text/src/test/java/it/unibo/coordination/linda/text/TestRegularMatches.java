package it.unibo.coordination.linda.text;

import it.unibo.coordination.linda.test.TestMatch;
import org.junit.Assert;
import org.junit.Test;

public class TestRegularMatches extends TestMatch<StringTuple, RegexTemplate, Object, String, RegularMatch> {

    public TestRegularMatches() {
        super(new TextualTupleTemplateFactory());
    }

    @Test
    public void testVarRetrievalSuccessfulMatch() {
        final StringTuple tuple = StringTuple.of("name: Giovanni, surname: Ciatto");
        final RegexTemplate template = RegexTemplate.of("name: ([A-Za-z]+), surname: (?<surname>[A-Za-z]+)");
        final RegularMatch match = template.matchWith(tuple);

        Assert.assertTrue(template.matches(tuple));
        Assert.assertEquals(template.matchWith(tuple), match);

        Assert.assertEquals(template, match.getTemplate());
        Assert.assertTrue(match.isMatching());
        Assert.assertTrue(match.getTuple().isPresent());
        Assert.assertEquals(tuple, match.getTuple().get());

        Assert.assertTrue(match.get(0).isPresent());
        Assert.assertEquals("name: Giovanni, surname: Ciatto", match.get(0).get());
        Assert.assertTrue(match.toMap().containsKey(0));
        Assert.assertEquals("name: Giovanni, surname: Ciatto", match.toMap().get(0));

        Assert.assertTrue(match.get(1).isPresent());
        Assert.assertEquals("Giovanni", match.get(1).get());
        Assert.assertTrue(match.toMap().containsKey(1));
        Assert.assertEquals("Giovanni", match.toMap().get(1));

        Assert.assertTrue(match.get("surname").isPresent());
        Assert.assertEquals("Ciatto", match.get("surname").get());
        Assert.assertTrue(match.toMap().containsKey("surname"));
        Assert.assertEquals("Ciatto", match.toMap().get("surname"));
    }
}
