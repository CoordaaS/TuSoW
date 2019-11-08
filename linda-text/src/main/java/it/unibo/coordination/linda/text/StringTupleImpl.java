package it.unibo.coordination.linda.text;

import java.util.Objects;

class StringTupleImpl implements StringTuple {

    private final String tuple;

    StringTupleImpl(String tuple) {
        this.tuple = Objects.requireNonNull(tuple);
    }

    @Override
    public String getValue() {
        return tuple;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof StringTuple && StringTuple.equals(this, (StringTuple) other);
    }

    @Override
    public int hashCode() {
        return StringTuple.hashCode(this);
    }

    @Override
    public String toString() {
        return "\"" + tuple + "\"";
    }
}
