package it.unibo.coordination.linda.text;

import it.unibo.coordination.linda.core.Tuple;

import java.util.Objects;

public interface StringTuple extends Tuple<StringTuple> {

    @Override
    String getValue();

    static StringTuple of(String string) {
        return new StringTupleImpl(string);
    }

    default int compareTo(StringTuple other) {
        return getValue().compareTo(other.getValue());
    }

    static boolean equals(StringTuple t1, StringTuple t2) {
        if (t1 == t2) return true;
        if (t1 == null || t2 == null) return false;
        return Objects.equals(t1.getValue(), t2.getValue());
    }

    static int hashCode(StringTuple t) {
        return Objects.hashCode(t.getValue());
    }


}
