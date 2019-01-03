package it.unibo.coordination.linda.string;

import it.unibo.coordination.linda.core.Template;
import it.unibo.coordination.linda.core.Tuple;

import java.util.Objects;
import java.util.regex.Pattern;

public interface RegexTemplate extends Template {

    static RegexTemplate of(String pattern) {
        return new RegexTemplateImpl(com.google.code.regexp.Pattern.compile(pattern));
    }

    static RegexTemplate of(Pattern pattern) {
        return new RegexTemplateImpl(com.google.code.regexp.Pattern.compile(pattern.pattern()));
    }

    Pattern getTemplate();

    @Override
    RegularMatch matchWith(Tuple tuple);

    @Override
    boolean equals(Object other);

    @Override
    int hashCode();

    static boolean equals(RegexTemplate t1, RegexTemplate t2) {
        if (t1 == t2) return true;
        if (t1 == null || t2 == null) return false;
        return Objects.equals(t1.getTemplate().pattern(), t2.getTemplate().pattern());
    }

    static int hashCode(RegexTemplate t) {
        return Objects.hashCode(t.getTemplate().pattern());
    }
}
