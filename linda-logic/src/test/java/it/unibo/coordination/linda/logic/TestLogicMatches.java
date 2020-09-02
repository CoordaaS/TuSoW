package it.unibo.coordination.linda.logic;


import it.unibo.coordination.linda.test.TestMatch;
import it.unibo.tuprolog.core.Term;
import it.unibo.tuprolog.core.parsing.TermParser;
import org.junit.Test;

import static it.unibo.coordination.linda.logic.LogicTestsUtils.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestLogicMatches extends TestMatch<LogicTuple, LogicTemplate, String, Term, LogicMatch> {

    public TestLogicMatches() {
        super(new LogicTupleTemplateFactory());
    }

    private Term parseTerm(String string) {
        return TermParser.getWithDefaultOperators().parseTerm(string);
    }

    @Test
    public void testVarRetrievalSuccessfulMatch() {
        final LogicTuple tuple = LogicTuple.of("f(1, \"2\", '3', d, e(f), g(h, [4, i]), [x, y, Z])");
        final LogicTemplate template = LogicTemplate.of("f(1, A, B, C, D, g(E, [F | G]), H)");
        final LogicMatch match = template.matchWith(tuple);

        assertTrue(template.matches(tuple));
        assertEquals(template.matchWith(tuple), match);

        assertEquals(template, match.getTemplate());
        assertTrue(match.isMatching());
        assertTrue(match.getTuple().isPresent());
        assertEquals(tuple, match.getTuple().get());

        assertTrue(match.get("A").isPresent());
        assertEquals(parseTerm("\"2\""), match.get("A").get());
        assertTrue(match.toMap().containsKey("A"));
        assertEquals(parseTerm("\"2\""), match.toMap().get("A"));

        assertTrue(match.get("B").isPresent());
        assertEquals(parseTerm("'3'"), match.get("B").get());
        assertTrue(match.toMap().containsKey("B"));
        assertEquals(parseTerm("'3'"), match.toMap().get("B"));

        assertTrue(match.get("C").isPresent());
        assertEquals(parseTerm("d"), match.get("C").get());
        assertTrue(match.toMap().containsKey("C"));
        assertEquals(parseTerm("d"), match.toMap().get("C"));

        assertTrue(match.get("D").isPresent());
        assertEquals(parseTerm("e(f)"), match.get("D").get());
        assertTrue(match.toMap().containsKey("D"));
        assertEquals(parseTerm("e(f)"), match.toMap().get("D"));

        assertTrue(match.get("E").isPresent());
        assertEquals(parseTerm("h"), match.get("E").get());
        assertTrue(match.toMap().containsKey("E"));
        assertEquals(parseTerm("h"), match.toMap().get("E"));

        assertTrue(match.get("F").isPresent());
        assertEquals(parseTerm("4"), match.get("F").get());
        assertTrue(match.toMap().containsKey("F"));
        assertEquals(parseTerm("4"), match.toMap().get("F"));

        assertTrue(match.get("G").isPresent());
        assertEquals(parseTerm("[i]"), match.get("G").get());
        assertTrue(match.toMap().containsKey("G"));
        assertEquals(parseTerm("[i]"), match.toMap().get("G"));

        assertTrue(match.get("H").isPresent());
        assertEquals(parseTerm("[x, y, Z]"), match.get("H").get());
        assertTrue(match.toMap().containsKey("H"));
        assertEquals(parseTerm("[x, y, Z]"), match.toMap().get("H"));

        assertFalse(match.get("Z").isPresent());
        assertFalse(match.toMap().containsKey("Z"));

        assertFalse(match.get("UNUSED_VARIABLE").isPresent());
        assertFalse(match.toMap().containsKey("UNUSED_VARIABLE"));
    }
}
