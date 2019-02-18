package it.unibo.coordination.linda.test;

import it.unibo.coordination.linda.core.Match;
import it.unibo.coordination.linda.core.Template;
import it.unibo.coordination.linda.core.Tuple;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.Optional;

public class TestMatch<T extends Tuple, TT extends Template, K, V, M extends Match<T, TT, K, V>> extends TestBaseLinda<T, TT, K, V, M> {


    public TestMatch(TupleTemplateFactory<T, TT, K, V, M> tupleTemplateFactory) {
        super(tupleTemplateFactory);
    }

    @Test
    public void testEmptyMatch() {
        final var emtpyMatch = getEmptyMatch();
        final TT template = emtpyMatch.getValue0();
        final M match = emtpyMatch.getValue1();

        Assert.assertEquals(template, match.getTemplate());
        Assert.assertFalse(match.isMatching());
        Assert.assertFalse(match.getTuple().isPresent());
        Assert.assertEquals(Map.of(), match.toMap());
    }

    @Test
    public void testFailedMatch() {
        final var failedMatch = getFailedMatch();
        final T tuple = failedMatch.getValue0();
        final TT template = failedMatch.getValue1();
        final M match = failedMatch.getValue2();

        Assert.assertFalse(template.matches(tuple));
        Assert.assertEquals(template.matchWith(tuple), match);

        Assert.assertEquals(template, match.getTemplate());
        Assert.assertFalse(match.isMatching());
        Assert.assertTrue(match.getTuple().isPresent());
        Assert.assertEquals(tuple, match.getTuple().get());
        Assert.assertEquals(Map.of(), match.toMap());
    }

    @Test
    public void testSuccessfulMatch() {
        final var successfulMatch = getSuccessfulMatch();
        final T tuple = successfulMatch.getValue0();
        final TT template = successfulMatch.getValue1();
        final M match = successfulMatch.getValue2();

        Assert.assertTrue(template.matches(tuple));
        Assert.assertEquals(template.matchWith(tuple), match);

        Assert.assertEquals(template, match.getTemplate());
        Assert.assertTrue(match.isMatching());
        Assert.assertTrue(match.getTuple().isPresent());
        Assert.assertEquals(tuple, match.getTuple().get());
        Assert.assertEquals(template.matchWith(tuple).toMap(), match.toMap());

        for (var kv : template.matchWith(tuple).toMap().entrySet()) {
            Assert.assertEquals(Optional.of(kv.getValue()), match.get((K) kv.getKey()));
        }
    }
}
