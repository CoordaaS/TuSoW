package it.unibo.coordination.linda.string;

import com.google.code.regexp.Matcher;
import it.unibo.coordination.linda.core.Match;
import it.unibo.coordination.linda.core.Tuple;

import java.util.*;

class RegularMatchImpl implements RegularMatch {

    private final RegexTemplate template;
    private final Matcher matcher;
    private final Tuple tuple;
    private Optional<Boolean> matchingCache = Optional.empty();
    private Map<Object, String> getCache = new HashMap<>();
    private Optional<Map<Object, String>> toMapCache = Optional.empty();




    RegularMatchImpl(RegexTemplate template, Matcher matcher, Tuple tuple) {
        this.template = Objects.requireNonNull(template);
        this.matcher = matcher;
        this.tuple = tuple;
    }

    @Override
    public Optional<StringTuple> getTuple() {
        return tuple instanceof StringTuple ? Optional.of((StringTuple) tuple) : Optional.empty();

    }

    @Override
    public RegexTemplate getTemplate() {
        return template;
    }

    @Override
    public boolean isMatching() {
        if (!matchingCache.isPresent()) {
            matchingCache = Optional.of(
                    matcher != null && getTuple().isPresent()
                        && matcher.matches()
                        && matcher.start() == 0
                        && matcher.end() == getTuple().get().getValue().length()
                );
        }
        return matchingCache.get();
    }

    @Override
    public Optional<String> get(Object key) {
        Optional<String> result = Optional.empty();
        try {
            if (isMatching()) {
                if (getCache.containsKey(key)) {
                    return Optional.of(getCache.get(key));
                } else if (key instanceof Integer) {
                    result = Optional.ofNullable(matcher.group((Integer) key));
                } else if (key instanceof String) {
                    result = Optional.ofNullable(matcher.group(key.toString()));
                }
            }
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            result = Optional.empty();
        } finally {
            result.ifPresent(r -> getCache.put(key, r));
        }
        return result;
    }

    @Override
    public Map<Object, String> toMap() {
        if (!toMapCache.isPresent()) {
            if (isMatching()) {
                final Map<Object, String> map = new HashMap<>();
                for (int i = 0; i < matcher.groupCount(); i++) {
                    map.put(i, matcher.group(i));
                }
                for (final var group : matcher.namedPattern().groupNames()) {
                    map.put(group, matcher.group(group));
                }
                toMapCache = Optional.of(Collections.unmodifiableMap(map));
            } else {
                toMapCache = Optional.of(Map.of());
            }
        }
        return toMapCache.get();
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
}
