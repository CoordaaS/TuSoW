package it.unibo.coordination.linda.string;

import it.unibo.coordination.linda.core.Match;

import java.util.Map;
import java.util.Optional;

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
            };
        }
    }
}
