package it.unibo.coordination.linda.logic;

import it.unibo.coordination.linda.core.PendingRequest;
import it.unibo.coordination.linda.core.events.OperationEvent;
import it.unibo.coordination.linda.core.events.PendingRequestEvent;
import it.unibo.coordination.linda.core.events.TupleEvent;
import it.unibo.presentation.Deserializer;
import it.unibo.presentation.Serializer;
import it.unibo.tuprolog.core.Term;
import org.apache.commons.collections4.MultiSet;
import org.junit.Assert;

import java.util.List;

public class LogicTestsUtils {
    public static void assertEquals(String message, Term expected, Term actual) {
        if (expected == null && actual == null) return;
        else if (expected == null || actual == null) {
            Assert.fail(message);
            return;
        }
        Assert.assertTrue(message, expected.equals(actual, false));
    }

    public static void assertEquals(Term expected, Term actual) {
        final String message = String.format("Expected %s, found %s", expected, actual);
        assertEquals(message, expected, actual);
    }

    public static void assertEquals(LogicTemplate expected, LogicTemplate actual) {
        final String message = String.format("Expected %s, found %s", expected, actual);
        assertEquals(message, expected, actual);
    }

    public static void assertEquals(String message, LogicTemplate expected, LogicTemplate actual) {
        if (expected == null && actual == null) return;
        else if (expected == null || actual == null) {
            Assert.fail(message);
            return;
        }
        assertEquals(message, expected.asTerm(), actual.asTerm());
    }

    public static void assertEquals(LogicTuple expected, LogicTuple actual) {
        final String message = String.format("Expected %s, found %s", expected, actual);
        assertEquals(message, expected, actual);
    }

    public static void assertEquals(String message, LogicTuple expected, LogicTuple actual) {
        if (expected == null && actual == null) return;
        else if (expected == null || actual == null) {
            Assert.fail(message);
            return;
        }
        assertEquals(message, expected.asTerm(), actual.asTerm());
    }

    public static void assertEquals(LogicMatch expected, LogicMatch actual) {
        final String message = String.format("Expected %s, found %s", expected, actual);
        assertEquals(message, expected, actual);
    }

    public static void assertEquals(String message, LogicMatch expected, LogicMatch actual) {
        if (expected == null && actual == null) return;
        else if (expected == null || actual == null) {
            Assert.fail(message);
            return;
        }
        assertEquals(message, expected.asTerm(), actual.asTerm());

    }

    private static void assertTuplesHaveSameOrder(List<LogicTuple> expected, List<LogicTuple> actual) {
        final String message = String.format("Expected %s, found %s", expected, actual);
        assertTuplesHaveSameOrder(message, expected, actual);
    }

    private static void assertTuplesHaveSameOrder(String message, List<LogicTuple> expected, List<LogicTuple> actual) {
        if (expected == null && actual == null) return;
        else if (expected == null || actual == null) {
            Assert.fail(message);
            return;
        }
        Assert.assertEquals(message, expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            Assert.assertEquals(message, expected.get(i), actual.get(i));
        }
    }

    private static void assertSameTuples(MultiSet<LogicTuple> expected, MultiSet<LogicTuple> actual) {
        final String message = String.format("Expected %s, found %s", expected, actual);
        assertSameTuples(message, expected, actual);
    }

    private static void assertSameTuples(String message, MultiSet<LogicTuple> expected, MultiSet<LogicTuple> actual) {
        if (expected == null && actual == null) return;
        else if (expected == null || actual == null) {
            Assert.fail(message);
            return;
        }
        Assert.assertEquals(message, expected.size(), actual.size());
        for (LogicTuple logicTuple : expected) {
            if (actual.stream().noneMatch(it -> it.asTerm().equals(logicTuple.asTerm(), false))) {
                Assert.fail(message);
            }
        }
        for (LogicTuple logicTuple : actual) {
            if (expected.stream().noneMatch(it -> it.asTerm().equals(logicTuple.asTerm(), false))) {
                Assert.fail(message);
            }
        }
    }

    private static void assertTemplatesHaveSameOrder(List<LogicTemplate> expected, List<LogicTemplate> actual) {
        final String message = String.format("Expected %s, found %s", expected, actual);
        assertTemplatesHaveSameOrder(message, expected, actual);
    }

    private static void assertTemplatesHaveSameOrder(String message, List<LogicTemplate> expected, List<LogicTemplate> actual) {
        if (expected == null && actual == null) return;
        else if (expected == null || actual == null) {
            Assert.fail(message);
            return;
        }
        Assert.assertEquals(message, expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(message, expected.get(i), actual.get(i));
        }
    }

    private static void assertSameTemplates(MultiSet<LogicTemplate> expected, MultiSet<LogicTemplate> actual) {
        final String message = String.format("Expected %s, found %s", expected, actual);
        assertSameTemplates(message, expected, actual);
    }

    private static void assertSameTemplates(String message, MultiSet<LogicTemplate> expected, MultiSet<LogicTemplate> actual) {
        if (expected == null && actual == null) return;
        else if (expected == null || actual == null) {
            Assert.fail(message);
            return;
        }
        Assert.assertEquals(message, expected.size(), actual.size());
        for (LogicTemplate logicTemplate : expected) {
            if (actual.stream().noneMatch(it -> it.asTerm().equals(logicTemplate.asTerm(), false))) {
                Assert.fail(message);
            }
        }
        for (LogicTemplate logicTemplate : actual) {
            if (expected.stream().noneMatch(it -> it.asTerm().equals(logicTemplate.asTerm(), false))) {
                Assert.fail(message);
            }
        }
    }

    public static <OE extends OperationEvent<LogicTuple, LogicTemplate>> void assertEquals(OE expected, OE actual) {
        final String message = String.format("Expected %s, found %s", expected, actual);
        if (expected == null && actual == null) return;
        else if (expected == null || actual == null) {
            Assert.fail(message);
            return;
        }
        Assert.assertEquals(message, expected.getClass(), actual.getClass());
        Assert.assertEquals(message, expected.getTupleSpaceName(), actual.getTupleSpaceName());
        Assert.assertEquals(message, expected.getOperationType(), actual.getOperationType());
        Assert.assertEquals(message, expected.getOperationPhase(), actual.getOperationPhase());
        assertTuplesHaveSameOrder(message, expected.getArgumentTuples(), actual.getArgumentTuples());
        assertTemplatesHaveSameOrder(message, expected.getArgumentTemplates(), actual.getArgumentTemplates());
        assertSameTuples(message, expected.getResultTuples(), actual.getResultTuples());
        assertSameTemplates(message, expected.getResultTemplates(), actual.getResultTemplates());
    }

    public static <TE extends TupleEvent<LogicTuple, LogicTemplate>> void assertEquals(TE expected, TE actual) {
        final String message = String.format("Expected %s, found %s", expected, actual);
        if (expected == null && actual == null) return;
        else if (expected == null || actual == null) {
            Assert.fail(message);
            return;
        }
        Assert.assertEquals(message, expected.getClass(), actual.getClass());
        Assert.assertEquals(message, expected.getTupleSpaceName(), actual.getTupleSpaceName());
        Assert.assertEquals(message, expected.isBefore(), actual.isBefore());
        Assert.assertEquals(message, expected.getEffect(), actual.getEffect());
        assertEquals(message, expected.getTuple(), actual.getTuple());
        assertEquals(message, expected.getTemplate(), actual.getTemplate());
    }

    public static <PR extends PendingRequest<LogicTuple, LogicTemplate>> void assertEquals(PR expected, PR actual) {
        final String message = String.format("Expected %s, found %s", expected, actual);
        assertEquals(message, expected, actual);
    }

    public static <PR extends PendingRequest<LogicTuple, LogicTemplate>> void assertEquals(String message, PR expected, PR actual) {
        if (expected == null && actual == null) return;
        else if (expected == null || actual == null) {
            Assert.fail(message);
            return;
        }
        Assert.assertEquals(message, expected.getClass(), actual.getClass());
        Assert.assertEquals(message, expected.getId(), actual.getId());
        Assert.assertEquals(message, expected.getRequestType(), actual.getRequestType());
        assertEquals(message, expected.getTemplate(), actual.getTemplate());
    }

    public static <TSE extends PendingRequestEvent<LogicTuple, LogicTemplate>> void assertEquals(TSE expected, TSE actual) {
        final String message = String.format("Expected %s, found %s", expected, actual);
        if (expected == null && actual == null) return;
        else if (expected == null || actual == null) {
            Assert.fail(message);
            return;
        }
        Assert.assertEquals(message, expected.getClass(), actual.getClass());
        Assert.assertEquals(message, expected.getTupleSpaceName(), actual.getTupleSpaceName());
        Assert.assertEquals(message, expected.getEffect(), actual.getEffect());
        assertEquals(message, expected.getPendingRequest(), actual.getPendingRequest());
    }

    public static <OE extends OperationEvent<LogicTuple, LogicTemplate>> void assertSerializationWorks(OE value, Serializer<OE> serializer, Deserializer<OE> deserializer) {
        final String serialized = serializer.toString(value);
        final OE deserialized = deserializer.fromString(serialized);
        assertEquals(value, deserialized);
    }

    public static <TE extends TupleEvent<LogicTuple, LogicTemplate>> void assertSerializationWorks(TE value, Serializer<TE> serializer, Deserializer<TE> deserializer) {
        final String serialized = serializer.toString(value);
        final TE deserialized = deserializer.fromString(serialized);
        assertEquals(value, deserialized);
    }

    public static <TSE extends PendingRequestEvent<LogicTuple, LogicTemplate>> void assertSerializationWorks(TSE value, Serializer<TSE> serializer, Deserializer<TSE> deserializer) {
        final String serialized = serializer.toString(value);
        final TSE deserialized = deserializer.fromString(serialized);
        assertEquals(value, deserialized);
    }

    public static <PR extends PendingRequest<LogicTuple, LogicTemplate>> void assertSerializationWorks(PR value, Serializer<PR> serializer, Deserializer<PR> deserializer) {
        final String serialized = serializer.toString(value);
        final PR deserialized = deserializer.fromString(serialized);
        assertEquals(value, deserialized);
    }
}
