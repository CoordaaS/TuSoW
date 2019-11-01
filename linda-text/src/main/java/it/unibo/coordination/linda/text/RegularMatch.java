package it.unibo.coordination.linda.text;

import it.unibo.coordination.linda.core.Match;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public interface RegularMatch extends Match<StringTuple, RegexTemplate, Object, String> {

    static RegularMatch failed(RegexTemplate template) {
        return new RegularMatchImpl(template, null, null);
    }

    static RegularMatch wrap(Match<StringTuple, RegexTemplate, Object, String> match) {
        if (match instanceof RegularMatch) {
            return (RegularMatch) match;
        } else {
            return new RegularMatch() {
                @Override
                public Optional<StringTuple> getTuple() {
                    return match.getTuple();
                }

                @Override
                public RegexTemplate getTemplate() {
                    return match.getTemplate();
                }

                @Override
                public boolean isMatching() {
                    return match.isMatching();
                }

                @Override
                public Optional<String> get(Object key) {
                    return match.get(key);
                }

                @Override
                public Map<Object, String> toMap() {
                    return match.toMap();
                }

                @Override
                public boolean equals(Object o) {
                    return o instanceof RegularMatch && Match.equals(this, (RegularMatch) o);
                }

                @Override
                public int hashCode() {
                    return Match.hashCode(this);
                }

                @Override
                public String toString() {
                    return RegularMatch.toString(this);
                }
            };
        }
    }

    static String toString(RegularMatch match) {
        return "{ match: " + match.isMatching()
                + ", template: " + match.getTemplate()
                + ", tuple: " + match.getTuple().map(StringTuple::toString).orElse("null")
                + ", map: "
                + match.toMap().entrySet().stream()
                    .map(kv -> String.format("%s: %s", kv.getKey(), kv.getValue()))
                    .collect(Collectors.joining(", ", "{ ", " }"))
                + " }";
    }
}
