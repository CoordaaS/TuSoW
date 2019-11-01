package it.unibo.coordination.linda.text.remote;

import it.unibo.coordination.linda.text.RegexTemplate;
import it.unibo.coordination.linda.text.RegularMatch;
import it.unibo.coordination.linda.text.StringTuple;
import it.unibo.coordination.linda.test.TupleTemplateFactory;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.multiset.HashMultiSet;
import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.javatuples.Triplet;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TextualTupleTemplateFactory implements TupleTemplateFactory<StringTuple, RegexTemplate, Object, String, RegularMatch> {
    @Override
    public RegexTemplate getATemplate() {
        return RegexTemplate.of("[Ff][Oo]+\\s+[Bb][Aa][Rr]");
    }

    @Override
    public StringTuple getATuple() {
        return StringTuple.of("Foo Bar");
    }

    @Override
    public Pair<StringTuple, RegexTemplate> getATupleAndATemplateMatchingIt() {
        return Pair.with(
                StringTuple.of("abba"),
                RegexTemplate.of("ab+a")
        );
    }

    @Override
    public Triplet<StringTuple, RegexTemplate, RegularMatch> getSuccessfulMatch() {
        final var tuple = StringTuple.of("name: Giovanni, surname: Ciatto");
        final var template = RegexTemplate.of("name: ([A-Za-z]+), surname: (?<surname>[A-Za-z]+)");

        return Triplet.with(tuple, template, template.matchWith(tuple));
    }

    @Override
    public Triplet<StringTuple, RegexTemplate, RegularMatch> getFailedMatch() {
        final var tuple = StringTuple.of("name: G10v4nn1, surname: C14tt0");
        final var template = RegexTemplate.of("name: ([A-Za-z]+), surname: (?<surname>[A-Za-z]+)");

        return Triplet.with(tuple, template, template.matchWith(tuple));
    }

    @Override
    public Pair<RegexTemplate, RegularMatch> getEmptyMatch() {
        final var template = RegexTemplate.of("name: ([A-Za-z]+), surname: (?<surname>[A-Za-z]+)");

        return Pair.with(template, RegularMatch.failed(template));
    }

    private static String escape(String string) {
        return string.replace("'", "\\'");
    }

    @Override
    public StringTuple getMessageTuple(String recipient, String payload) {
        return StringTuple.of(String.format("'%s'; '%s'", escape(recipient), escape(payload)));
    }

    @Override
    public RegexTemplate getMessageTemplate(String recipient) {
        return RegexTemplate.of(String.format("\\s*'%s'\\s*;\\s*'(?<payload>.*?)'\\s*", escape(recipient)));
    }

    @Override
    public RegexTemplate getGeneralMessageTemplate() {
        return RegexTemplate.of("\\s*'(?<recipient>.*?)'\\s*;\\s*'(?<payload>.*?)'\\s*");
    }

    @Override
    public MultiSet<StringTuple> getSomeTuples() {
        return Stream.of(
                    "a", "b", "c", "d", "e"
            ).map(StringTuple::of)
            .collect(Collectors.toCollection(HashMultiSet::new));
    }

    @Override
    public Quartet<MultiSet<StringTuple>, RegexTemplate, MultiSet<StringTuple>, RegexTemplate> getSomeTuplesOfTwoSorts() {
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
    public Pair<MultiSet<StringTuple>, RegexTemplate> getSomeTuplesOfOneSort() {
        return Pair.with(
                IntStream.range(1, 6)
                        .mapToObj(i -> "x=" + i)
                        .map(StringTuple::of)
                        .collect(Collectors.toCollection(HashMultiSet::new)),
                RegexTemplate.of("(?<var>[A-Za-z]+)=(?<val>\\d+)")
        );
    }
}
