package it.unibo.coordination.testing;

public final class Chronometer {
    private long start;

    public Chronometer() {
        reset();
    }

    public Chronometer reset() {
        start = System.currentTimeMillis();
        return this;
    }

    public long getTime() {
        return System.currentTimeMillis() - start;
    }

    @Override
    public String toString() {
        return "Chronometer [start=" + start + ", time=" + getTime() + "]";
    }

}
