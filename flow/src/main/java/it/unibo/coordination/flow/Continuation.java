package it.unibo.coordination.flow;

//public enum Continuation {
//    CONTINUE, RESTART, PAUSE, STOP
//}

import java.util.Objects;

public abstract class Continuation<T> {

    private Continuation() {}

    abstract T getValue();

    public boolean isStart() {
        return false;
    }

    public boolean isNext() {
        return false;
    }

    public boolean isPause() {
        return false;
    }

    public boolean isStop() {
        return false;
    }

    public static <X> Start<X> start(X value) {
        return new Start<>() {

            @Override
            X getValue() {
                return value;
            }
        };
    }

    public static abstract class Start<T> extends Continuation<T> {

        private Start() {}

        @Override
        public boolean isStart() {
            return true;
        }
    }

    public static <X> Next<X> next(X value) {
        return new Next<>() {

            @Override
            X getValue() {
                return value;
            }
        };
    }

    public static abstract class Next<T> extends Continuation<T> {

        private Next() {}

        @Override
        public boolean isNext() {
            return true;
        }
    }

    public static <X> Pause<X> pause(X value) {
        return new Pause<>() {

            @Override
            X getValue() {
                return value;
            }
        };
    }

    public static abstract class Pause<T> extends Continuation<T> {

        private Pause() {}

        @Override
        public boolean isPause() {
            return true;
        }
    }

    public static <X> Stop<X> stop(X value) {
        return new Stop<>() {

            @Override
            X getValue() {
                return value;
            }
        };
    }

    public static abstract class Stop<T> extends Continuation<T> {

        private Stop() {}

        @Override
        public boolean isStop() {
            return true;
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null) return false;
        if (Objects.equals(other.getClass(), getClass())) {
            return Objects.equals(getValue(), ((Continuation<T>) other).getValue());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), getValue());
    }

    @Override
    public String toString() {
        return String.format("%s{%s}", getClass().getSimpleName(), getValue());
    }
}