package it.unibo.coordination.linda.logic;

import it.unibo.tuprolog.core.Term;
import org.junit.Assert;

public class LogicTestsUtils {
    public static void assertEquals(String message, Term expected, Term actual) {
        Assert.assertTrue(message, expected.equals(actual, false));
    }

    public static void assertEquals(Term expected, Term actual) {
        assertEquals(String.format("Expected %s, found %s", expected, actual), expected, actual);
    }

    public static void assertEquals(LogicTemplate expected, LogicTemplate actual) {
        assertEquals(String.format("Expected %s, found %s", expected, actual), expected.asTerm(), actual.asTerm());
    }

    public static void assertEquals(LogicTuple expected, LogicTuple actual) {
        assertEquals(String.format("Expected %s, found %s", expected, actual), expected.asTerm(), actual.asTerm());
    }

    public static void assertEquals(LogicMatch expected, LogicMatch actual) {
        assertEquals(String.format("Expected %s, found %s", expected, actual), expected.asTerm(), actual.asTerm());
    }
}
