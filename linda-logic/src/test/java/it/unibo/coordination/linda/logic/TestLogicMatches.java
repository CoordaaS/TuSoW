package it.unibo.coordination.linda.logic;

import alice.tuprolog.Term;
import it.unibo.coordination.linda.test.TestMatch;
import org.junit.Assert;
import org.junit.Test;

public class TestLogicMatches extends TestMatch<LogicTuple, LogicTemplate, String, Term, LogicMatch> {

    public TestLogicMatches() {
        super(new LogicTupleTemplateFactory());
    }

    @Test
    public void testVarRetrievalSuccessfulMatch() {
        final LogicTuple tuple = LogicTuple.of("f(1, \"2\", '3', d, e(f), g(h, [4, i]), [x, y, Z])");
        final LogicTemplate template = LogicTemplate.of("f(1, A, B, C, D, g(E, [F | G]), H)");
        final LogicMatch match = template.matchWith(tuple);

        Assert.assertTrue(template.matches(tuple));
        Assert.assertEquals(template.matchWith(tuple), match);

        Assert.assertEquals(template, match.getTemplate());
        Assert.assertTrue(match.isMatching());
        Assert.assertTrue(match.getTuple().isPresent());
        Assert.assertEquals(tuple, match.getTuple().get());

        Assert.assertTrue(match.get("A").isPresent());
        Assert.assertEquals(Term.createTerm("\"2\""), match.get("A").get());
        Assert.assertTrue(match.toMap().containsKey("A"));
        Assert.assertEquals(Term.createTerm("\"2\""), match.toMap().get("A"));

        Assert.assertTrue(match.get("B").isPresent());
        Assert.assertEquals(Term.createTerm("'3'"), match.get("B").get());
        Assert.assertTrue(match.toMap().containsKey("B"));
        Assert.assertEquals(Term.createTerm("'3'"), match.toMap().get("B"));

        Assert.assertTrue(match.get("C").isPresent());
        Assert.assertEquals(Term.createTerm("d"), match.get("C").get());
        Assert.assertTrue(match.toMap().containsKey("C"));
        Assert.assertEquals(Term.createTerm("d"), match.toMap().get("C"));

        Assert.assertTrue(match.get("D").isPresent());
        Assert.assertEquals(Term.createTerm("e(f)"), match.get("D").get());
        Assert.assertTrue(match.toMap().containsKey("D"));
        Assert.assertEquals(Term.createTerm("e(f)"), match.toMap().get("D"));

        Assert.assertTrue(match.get("E").isPresent());
        Assert.assertEquals(Term.createTerm("h"), match.get("E").get());
        Assert.assertTrue(match.toMap().containsKey("E"));
        Assert.assertEquals(Term.createTerm("h"), match.toMap().get("E"));

        Assert.assertTrue(match.get("F").isPresent());
        Assert.assertEquals(Term.createTerm("4"), match.get("F").get());
        Assert.assertTrue(match.toMap().containsKey("F"));
        Assert.assertEquals(Term.createTerm("4"), match.toMap().get("F"));

        Assert.assertTrue(match.get("G").isPresent());
        Assert.assertEquals(Term.createTerm("[i]"), match.get("G").get());
        Assert.assertTrue(match.toMap().containsKey("G"));
        Assert.assertEquals(Term.createTerm("[i]"), match.toMap().get("G"));

        Assert.assertTrue(match.get("H").isPresent());
        Assert.assertEquals(Term.createTerm("[x, y, Z]"), match.get("H").get());
        Assert.assertTrue(match.toMap().containsKey("H"));
        Assert.assertEquals(Term.createTerm("[x, y, Z]"), match.toMap().get("H"));

        Assert.assertFalse(match.get("Z").isPresent());
        Assert.assertFalse(match.toMap().containsKey("Z"));

        Assert.assertFalse(match.get("UNUSED_VARIABLE").isPresent());
        Assert.assertFalse(match.toMap().containsKey("UNUSED_VARIABLE"));
    }
}
