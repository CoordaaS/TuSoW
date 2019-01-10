package it.unibo.coordination.linda.strings;

import it.unibo.coordination.linda.string.RegexTemplate;
import it.unibo.coordination.linda.string.StringSpace;
import it.unibo.coordination.linda.string.StringTuple;
import it.unibo.coordination.linda.test.TestTupleSpace;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.multiset.HashMultiSet;
import org.javatuples.Pair;
import org.javatuples.Quartet;

import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TestStringSpace extends TestTupleSpace<StringTuple, RegexTemplate, Object, String, StringSpace> {

    @Override
    protected StringSpace getTupleSpace(ExecutorService executor) {
        return StringSpace.deterministic(executor);
    }

    @Override
    protected RegexTemplate getATemplate() {
        return RegexTemplate.of("[Ff][Oo]+\\s+[Bb][Aa][Rr]");
    }

    @Override
    protected StringTuple getATuple() {
        return StringTuple.of("Foo Bar");
    }

    @Override
    protected Pair<StringTuple, RegexTemplate> getATupleAndATemplateMatchingIt() {
        return Pair.with(
            StringTuple.of("abba"),
            RegexTemplate.of("ab+a")
        );
    }

    private static String escape(String string) {
        return string.replace("'", "\\'");
    }

    @Override
    protected StringTuple messageTuple(String recipient, String payload) {
        return StringTuple.of(String.format("'%s'; '%s'", escape(recipient), escape(payload)));
    }

    @Override
    protected RegexTemplate messageTemplate(String recipient) {
        return RegexTemplate.of(String.format("\\s*'%s'\\s*;\\s*'(?<payload>.*?)'\\s*", escape(recipient)));
    }

    @Override
    protected RegexTemplate getGeneralMessageTemplate() {
        return RegexTemplate.of("\\s*'(?<recipient>.*?)'\\s*;\\s*'(?<payload>.*?)'\\s*");
    }

    @Override
    protected MultiSet<StringTuple> getSomeTuples() {
        return Stream.of(
                "a", "b", "c", "d", "e"
        ).map(StringTuple::of)
        .collect(Collectors.toCollection(HashMultiSet::new));
    }

    @Override
    protected Quartet<MultiSet<StringTuple>, RegexTemplate, MultiSet<StringTuple>, RegexTemplate> getSomeTuplesOfTwoSorts() {
        var tuples1 = IntStream.range(0, 5)
                .mapToObj(i -> new String(new char[] { (char)(i + 'a') }))
                .map(StringTuple::of)
                .collect(Collectors.toCollection(HashMultiSet::new));
        var tuples2 = IntStream.range(1, 6)
                .mapToObj(String::valueOf)
                .map(StringTuple::of)
                .collect(Collectors.toCollection(HashMultiSet::new));
        return Quartet.with(tuples1, RegexTemplate.of("(?<letters>[A-Za-z]+)"), tuples2, RegexTemplate.of("(?<digits>\\d+)"));
    }

    @Override
    protected Pair<MultiSet<StringTuple>, RegexTemplate> getSomeTuplesOfOneSort() {
        return Pair.with(
                IntStream.range(1, 6)
                        .mapToObj(i -> "x=" + i)
                        .map(StringTuple::of)
                        .collect(Collectors.toCollection(HashMultiSet::new)),
                RegexTemplate.of("(?<var>[A-Za-z]+)=(?<val>\\d+)")
        );
    }
}
